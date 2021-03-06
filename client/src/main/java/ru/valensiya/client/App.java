package ru.valensiya.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;

public class App extends Application {
    private Scene loginScene;
    private Scene cloudScene;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("cloud.fxml"));
        cloudScene = new Scene(parent);
        cloudScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        Parent loginParent = FXMLLoader.load(getClass().getResource("login.fxml"));
        loginScene = new Scene(loginParent);
        loginScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Облачное хранилище");
        primaryStage.getIcons().add(new Image(getClass().getResource("cloud.png").toExternalForm()));
        primaryStage.show();
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.setMinWidth(primaryStage.getWidth());
    }
}
