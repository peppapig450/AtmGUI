package com.nick.atmInterface;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.Optional;

import javafx.scene.control.TextInputDialog;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainMenuController {

    // initalize sql class
    JdbcDao database = new JdbcDao();

    // holds this controller's stage
    private Stage thisStage;

    // hold a reference to the first controller, allowing access to its methods
    private final LoginController loginController;

    @FXML
    private Label welcomeLabel;
    @FXML
    private Label dateTimeDisplay;
    @FXML
    private Button mainMenuBalanceButton;
    @FXML
    private Button mainMenuLogOutButton;

    @FXML
    protected void onBalanceButtonClick() {
        TextInputDialog pinEntry = new TextInputDialog();
        pinEntry.setTitle("Pin Confirmation");
        pinEntry.setHeaderText("Enter your pin.");
        pinEntry.setContentText("pin: ");

        Optional<String> result = pinEntry.showAndWait();
        result.ifPresent(pinCheck -> {
            int enteredPin = Integer.parseInt(pinCheck);
            String user = loginController.getLoginUsername();
            if (validatePin(enteredPin, user)) {
                int userid = database.getUserIDFromUser(user);
                int balance = database.getAccountBalance(userid);
                Double numParsed = Double.parseDouble(String.valueOf(balance));
                String info = String.format("$%,.2f", numParsed);
                showBalance(info);
            }
        });
    }

    @FXML
    protected void onDepositButtonClick() {
        TextInputDialog pinEntry = new TextInputDialog();
        pinEntry.setTitle("Pin Confirmation");
        pinEntry.setHeaderText("Enter your pin.");
        pinEntry.setContentText("pin: ");

        Optional<String> result = pinEntry.showAndWait();
        result.ifPresent(pinCheck -> {
            int enteredPin = Integer.parseInt(pinCheck);
            String user = loginController.getLoginUsername();
            if (validatePin(enteredPin, user)) {
                TextInputDialog depositEntry = new TextInputDialog();
                depositEntry.setTitle("Deposit");
                depositEntry.setHeaderText(("Enter amount to deposit."));
                depositEntry.setContentText("Amount: ");
                Optional<String> dep = depositEntry.showAndWait();
                dep.ifPresent(amountString -> {
                    int amount = Integer.parseInt(amountString);
                    int userid = database.getUserIDFromUser(loginController.getLoginUsername());
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    Double numberParsed = Double.parseDouble(amountString);
                    alert.setContentText(String.format("$%,.2f", numberParsed));
                    alert.setTitle("Deposit");
                    alert.setHeaderText("Are you sure you would like to deposit this much?");
                    Optional<ButtonType> option = alert.showAndWait();
                    option.ifPresent(type -> {
                        if (type == ButtonType.OK) {
                            database.depositAmount(amount, userid);
                        } else {
                            alert.close();
                        }
                    });
                });
            }
        });
    }

    @FXML
    protected void onWithdrawButtonClick() {
        TextInputDialog pinEntry = new TextInputDialog();
        pinEntry.setTitle("Pin Confirmation");
        pinEntry.setHeaderText("Enter your pin.");
        pinEntry.setContentText("pin: ");

        Optional<String> result = pinEntry.showAndWait();
        result.ifPresent(pinCheck -> {
            int enteredPin = Integer.parseInt(pinCheck);
            String user = loginController.getLoginUsername();
            if (validatePin(enteredPin, user)) {
                TextInputDialog withdrawEntry = new TextInputDialog();
                withdrawEntry.setTitle("Withdraw");
                withdrawEntry.setHeaderText("Enter amount to withdraw.");
                withdrawEntry.setContentText("Amount: ");
                Optional<String> wit = withdrawEntry.showAndWait();
                wit.ifPresent(amountString -> {
                    int amount = Integer.parseInt(amountString);
                    int userid = database.getUserIDFromUser(loginController.getLoginUsername());
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    Double numParsed = Double.parseDouble(amountString);
                    alert.setContentText(String.format("$%,.2f", numParsed));
                    alert.setTitle("Withdraw");
                    alert.setHeaderText("Are you sure you want to withdraw this much?");
                    Optional<ButtonType> option = alert.showAndWait();
                    option.ifPresent(type -> {
                        if (type == ButtonType.OK) {
                            if (database.getAccountBalance(userid) <= amount) {
                                Alert error = new Alert(AlertType.ERROR);
                                error.setTitle("Insufficient Funds");
                                error.setHeaderText("Can't withdraw amount");
                                error.setContentText(
                                        "Account doesn't contain sufficient funds to withdraw this amount.");
                                error.showAndWait();
                            } else {
                                database.withdrawAmount(amount, userid);
                            }
                        } else {
                            alert.close();
                        }
                    });
                });
            }
        });
    }

    @FXML
    protected void onTransferButtonClick() {
        TextInputDialog transferEntry = new TextInputDialog();
    }

    @FXML
    protected void onLogoutButtonClick() {
        Stage stage = (Stage) mainMenuLogOutButton.getScene().getWindow();
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Logout");
        alert.setHeaderText("Would you like to logout?");

        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");

        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(yes, no);
        Optional<ButtonType> option = alert.showAndWait();

        if (option.get() == yes) {
            stage.close();
        } else if (option.get() == no) {
            alert.close();
        }
    }

    public MainMenuController(LoginController loginController) {
        // make first controller usable
        this.loginController = loginController;

        thisStage = new Stage();

        // load FXML file
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("main-menu.fxml"));

            // set this class as controller
            loader.setController(this);

            // load the scene
            thisStage.setScene(new Scene(loader.load()));

            thisStage.setTitle("Main Menu");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * validate pin entered
     */
    public boolean validatePin(int enteredPin, String user) {
        int checkAgainstPin = database.getAccountPin(user);
        return (enteredPin == checkAgainstPin) ? true : false;
    }

    public static void showBalance(String infoMessage) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setContentText(infoMessage);
        alert.setTitle("Balance");
        alert.setHeaderText("Current Balance is..");
        alert.showAndWait();
    }

    /*
     * show the stage loaded in the constructor
     */
    public void showStage() {
        thisStage.showAndWait();
    }

    @FXML
    private void initialize() {
        // set the label to whatever the username of the logged in user was
        welcomeLabel.setText("Welcome " + loginController.getLoginFullName());

        Timeline clock = new Timeline(
                new KeyFrame(Duration.ZERO,
                        e -> dateTimeDisplay.setText(
                                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))),
                new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

}
