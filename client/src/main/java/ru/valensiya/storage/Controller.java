package ru.valensiya.storage;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class Controller implements Initializable {

    public ListView<String> listView;
    public TextField input;
    private DataInputStream is;
    private DataOutputStream os;

    private static final byte [] buffer = new byte[1024];

    public void send(ActionEvent actionEvent) throws Exception {
        File src = new File("client/files/"+input.getText());

        os.writeUTF("nameFile: " + src.getName());
        os.writeInt((int) src.length());

        try (FileInputStream fis = new FileInputStream(src)) {
            int read;
            while ((read = fis.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            os.flush();
        } catch (Exception e) {
            log.error("e=", e);
        }
        input.clear();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        File filesDir = new File("client/files");
        for (String s : filesDir.list()) {
            listView.getItems().add(s);
        }
        try {
            Socket socket = new Socket("localhost", 8189);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
        }
        catch (Exception e) {
            log.debug("e: "+e);
        }
    }

    public void selectFile(MouseEvent mouseEvent) {
        input.setText(listView.getSelectionModel().getSelectedItem());
    }
}
