package com.nick.atmInterface;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

// change to have password field
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.fxml.Initializable;
import javafx.stage.Window;

// add full name to javafx fxml
public class LoginController {

    // hold this controller stage
    private final Stage thisStage;

    // strings which hold css elements to easily re-use in the application
    protected String successMessage = String.format("-fx-text-fill: GREEN;");
    String errorMessage = String.format("-fx-text-fill: RED;");
    String errorStyle = String.format("-fx-border-color: RED; -fx-border-width: 2; -fx-border-radius: 5;");
    String successStyle = String.format("-fx-border-color: #A9A9A9; -fx-border-width: 2; -fx-border-radius: 5;");

    JdbcDao jdbcDao = new JdbcDao();

    // import application's control
    @FXML
    private Label invalidLoginCredentials;
    @FXML
    private Label invalidSignupCredentials;
    @FXML
    private Label welcomeLabelMainMenu;
    @FXML
    private Button cancelButton;
    @FXML
    private Button signUpButton;
    @FXML
    private Button loginButton;
    @FXML
    private TextField loginUsernameField;
    @FXML
    private PasswordField loginPasswordField;
    @FXML
    private TextField signUpFullNameTextField;
    @FXML
    private TextField signUpUsernameTextField;
    @FXML
    private TextField signUpPasswordPasswordField;
    @FXML
    private TextField signUpRepeatPasswordPasswordField;

    // creation of methods which are activated on events in the forms
    @FXML
    protected void onCancelButtonClick() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void onLoginButtonClick(ActionEvent event)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        Window owner = loginButton.getScene().getWindow();

        if (loginUsernameField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Please enter your username");
            loginUsernameField.setStyle(errorStyle);
            return;
        }

        if (loginPasswordField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!", "Please enter your password");
            loginPasswordField.setStyle(errorStyle);
            return;
        }

        String username = loginUsernameField.getText();
        String password = loginPasswordField.getText();

        PasswordSecurity pwHasher = new PasswordSecurity();
        pwHasher.setPbkdf2Iterations(200000);
        String storedPassword = jdbcDao.getUsernamePass(username);
        boolean flag = pwHasher.validatePassword(password, storedPassword);

        if (!flag) {
            infoBox("Please enter correct username and/or password.", null, "Failed");
        } else {
            BankGenerator gen = new BankGenerator();
            String accNumb = gen.generateAccountNumber();

            jdbcDao.addBankAccount(username, accNumb);
            infoBox("Login Successful!", null, "Success");
            openMainMenu();
        }
    }

    @FXML
    public void onSignUpButtonClick(ActionEvent event)
            throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        Window owner = signUpButton.getScene().getWindow();

        if (signUpFullNameTextField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!",
                    "Please enter your name");
            return;
        }

        if (signUpUsernameTextField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!",
                    "Please enter a username");
            return;
        }

        if (signUpPasswordPasswordField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, owner, "Form Error!",
                    "Please enter a password");
            return;
        }

        String fullName = signUpFullNameTextField.getText();
        String userName = signUpUsernameTextField.getText();
        String password = signUpPasswordPasswordField.getText();
        int randomPIN = (int) (Math.random() * 9000) + 1000;
        String pin = String.valueOf(randomPIN);

        PasswordSecurity pwHasher = new PasswordSecurity();
        pwHasher.setPbkdf2Iterations(200000);
        String hashedPassword = pwHasher.createHash(password);

        jdbcDao.registerUser(fullName, userName, hashedPassword, pin);

        showAlert(Alert.AlertType.CONFIRMATION, owner, "Registration Successful!",
                "Welcome " + signUpFullNameTextField.getText() + "\nYour pin is: " + pin);
    }

    public LoginController() {
        // create the new stage
        thisStage = new Stage();

        // load the FXML file
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-panel.fxml"));

            // set this class as the controller
            loader.setController(this);

            // load the scene
            thisStage.setScene(new Scene(loader.load()));

            // setup the window/stage
            thisStage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
     * Show the stage that was loaded in the constructor
     */
    public void showStage() {
        thisStage.showAndWait();
    }

    /*
     * opens the next screen
     */
    private void openMainMenu() {
        final Stage stage = (Stage) thisStage.getScene().getWindow();
        // create the next controller to pass variables to
        MainMenuController mainMenuController = new MainMenuController(this);

        // show new stage
        mainMenuController.showStage();
        stage.close();
    }

    /*
     * get username
     */
    public String getLoginUsername() {
        return loginUsernameField.getText();
    }

    /*
     * get Full Name
     */
    public String getLoginFullName() {
        return jdbcDao.getFullNameFromUser(loginUsernameField.getText());
    }

    /*
     * get user id
     */
    public int getLoginUserID() {
        return jdbcDao.getUserIDFromUser(loginUsernameField.getText());
    }

    /*
     * Allows other controllers to set the text of the layouts label
     */
    public void setTextFromMainMenu(String text) {
        welcomeLabelMainMenu.setText(text);
    }

    public static void infoBox(String infoMessage, String headerText, String title) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setContentText(infoMessage);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.showAndWait();
    }

    private static void showAlert(Alert.AlertType alertType, Window owner, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(owner);
        alert.show();
    }
}
