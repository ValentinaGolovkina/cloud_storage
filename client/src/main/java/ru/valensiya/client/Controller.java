package ru.valensiya.client;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import ru.valensiya.core.Command;
import ru.valensiya.core.FileMessage;
import ru.valensiya.core.ListResponse;
import ru.valensiya.core.PathResponse;

@Slf4j
public class Controller implements Initializable {

    private Path ROOT_DIR = Paths.get("client/root");
    private static byte[] buffer = new byte[1024];
    public ListView<String> listView;
    public TextField input;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;

    public void send(ActionEvent actionEvent) throws Exception {
        String fileName = input.getText();
        input.clear();
        sendFile(fileName);
    }

    private void sendFile(String fileName) throws IOException {
        Path file = ROOT_DIR.resolve(fileName);
        os.writeObject(new FileMessage(file));
        os.flush();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            fillFilesInCurrentDir();
            Socket socket = new Socket("localhost", 8189);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());
            Thread daemon = new Thread(() -> {
                try {
                    while (true) {
                        Command command = (Command) is.readObject();
                        log.debug("received: {}", command);
                        switch (command.getType()) {
                            case LIST_RESPONSE:
                                ListResponse response = (ListResponse) command;
                                List<String> names = response.getNames();
                                log.debug(names.toString());
                                break;
                            case PATH_RESPONSE:
                                PathResponse pathResponse = (PathResponse) command;
                                String path = pathResponse.getPath();
                                log.debug(path);
                                break;
                            case FILE_MESSAGE:
                                FileMessage message = (FileMessage) command;
                                Files.write(ROOT_DIR.resolve(message.getName()), message.getBytes());
                                fillFilesInCurrentDir();
                                break;
                        }
                    }
                } catch (Exception e) {
                    log.error("exception while read from input stream");
                }
            });
            daemon.setDaemon(true);
            daemon.start();
        } catch (IOException ioException) {
            log.error("e=", ioException);
        }
    }

    private void fillFilesInCurrentDir() throws IOException {
        listView.getItems().clear();
        listView.getItems().addAll(
                Files.list(ROOT_DIR)
                        .map(p -> p.getFileName().toString())
                        .collect(Collectors.toList())
        );
        listView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String item = listView.getSelectionModel().getSelectedItem();
                input.setText(item);
            }
        });
    }
}