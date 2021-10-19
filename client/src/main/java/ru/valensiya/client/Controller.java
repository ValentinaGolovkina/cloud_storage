package ru.valensiya.client;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
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

import ru.valensiya.core.*;

@Slf4j
public class Controller implements Initializable {

    public ListView<Item> clientView;
    public ListView<Item> serverView;
    public TextField clientPath;
    public TextField serverPath;
    private Path currentDir;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        clientPath.setFocusTraversable(false);
        try {
            String userDir = System.getProperty("user.name");
            currentDir = Paths.get("C:/Users", userDir).toAbsolutePath();
            log.info("Current user: {}", System.getProperty("user.name"));
            Socket socket = new Socket("localhost", 8189);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());

            refreshClientView();
            addNavigationListeners();

            Thread daemon = new Thread(() -> {
                try {
                    while (true) {
                        Command command = (Command) is.readObject();
                        log.debug("received: {}", command);
                        switch (command.getType()) {
                            case LIST_RESPONSE:
                                ListResponse response = (ListResponse) command;
                                List<Item> items = response.getItems();
                                refreshServerView(items);
                                break;
                            case PATH_RESPONSE:
                                PathResponse pathResponse = (PathResponse) command;
                                String path = pathResponse.getPath();
                                Platform.runLater(() -> serverPath.setText(path));
                                break;
                            case FILE_MESSAGE:
                                FileMessage message = (FileMessage) command;
                                Files.write(currentDir.resolve(message.getName()), message.getBytes());
                                refreshClientView();
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

    private void refreshClientView() throws IOException {
        clientPath.setText(currentDir.toString());
        List<Item> items = Files.list(currentDir)
                .map(p -> p.getFileName().toString())
                .map(name->{
                    if (Files.isDirectory(currentDir.resolve(name))) {
                        return new Item(name,"folder.png");
                    } else {
                        return new Item(name,"file.png");
                    }}).collect(Collectors.toList());
        Platform.runLater(() -> {
            clientView.getItems().clear();
            clientView.getItems().addAll(items);
            //Отображение иконок
            clientView.setCellFactory(l-> new ListCell<Item>() {
                @Override
                public void updateItem(Item item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText("");
                        setGraphic(null);
                    } else {
                        setText(item.getName());
                        ImageView image =new ImageView(getClass().getResource(item.getImage()).toExternalForm());
                        image.setFitHeight(20);
                        image.setFitWidth(20);
                        setGraphic(image);
                    }
                }
            });
        });
    }

    private void refreshServerView(List<Item> items) {
        Platform.runLater(() -> {
            serverView.getItems().clear();
            serverView.getItems().addAll(items);
            serverView.setCellFactory(l-> new ListCell<Item>() {
                @Override
                public void updateItem(Item item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText("");
                        setGraphic(null);
                    } else {
                        setText(item.getName());
                        ImageView image =new ImageView(getClass().getResource(item.getImage()).toExternalForm());
                        image.setFitHeight(20);
                        image.setFitWidth(20);
                        setGraphic(image);
                    }
                }
            });
        });
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        String fileName = clientView.getSelectionModel().getSelectedItem().getName();
        FileMessage message = new FileMessage(currentDir.resolve(fileName));
        os.writeObject(message);
        os.flush();
    }

    public void downLoad(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem().getName();
        os.writeObject(new FileRequest(fileName));
        os.flush();
    }

    public void clientPathUp(ActionEvent actionEvent) throws IOException {
        if (currentDir.getParent()!=null) {
            currentDir = currentDir.getParent();
            clientPath.setText(currentDir.toString());
            refreshClientView();
        }
    }

    public void serverPathUp(ActionEvent actionEvent) throws IOException {
        os.writeObject(new PathUpRequest());
        os.flush();
    }

    private void addNavigationListeners() {
        clientView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String item = clientView.getSelectionModel().getSelectedItem().getName();
                Path newPath = currentDir.resolve(item);
                if (Files.isDirectory(newPath)) {
                    currentDir = newPath;
                    try {
                        refreshClientView();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });

        serverView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String item = serverView.getSelectionModel().getSelectedItem().getName();
                try {
                    os.writeObject(new PathInRequest(item));
                    os.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }
}