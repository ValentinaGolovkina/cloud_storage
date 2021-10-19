package ru.valensiya.client;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class LoginController implements Initializable {

    public TextField login;
    public PasswordField password;
    public Button loginButton;
    /*private Stage prevStage;

    public void setPrevStage(Stage stage) {
        this.prevStage = stage;
    }*/

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //todo как убрать фокус призагрузке?
        login.setFocusTraversable(false);
        password.setFocusTraversable(false);
        loginButton.setFocusTraversable(false);

        login.setPromptText("Введите логин");
        password.setPromptText("Введите пароль");
    }

    public void login(ActionEvent actionEvent) throws IOException {
        ((Stage) login.getScene().getWindow()).close();
        Stage stage = new Stage();
        //stage.setTitle("Shop Management");
        Parent parent = FXMLLoader.load(getClass().getResource("cloud.fxml"));
        Scene scene = new Scene(parent);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
