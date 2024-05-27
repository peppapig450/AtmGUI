package com.nick.atmInterface;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginApplication extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    // set up stage to show the window
    @Override
    public void start(Stage stage) throws IOException {
        LoginController controller = new LoginController();
        controller.showStage();
    }
}
