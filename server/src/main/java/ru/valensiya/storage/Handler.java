package ru.valensiya.storage;

import lombok.extern.slf4j.Slf4j;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;

@Slf4j
public class Handler implements Runnable {

    private final Socket socket;
    private static final String ROOT_DIR = "server/root/";
    private static final byte [] buffer = new byte[1024];

    public Handler(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        try (DataInputStream is = new DataInputStream(socket.getInputStream())
        ) {
            while (true) {
                String nameFile = is.readUTF();
                log.debug("Received: {}", nameFile);
                if (nameFile.startsWith("nameFile")) {
                    int size = is.readInt();
                    log.debug("Received size: {}", size);
                    File file = new File(ROOT_DIR + nameFile.substring(10));
                    file.createNewFile();
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        for(int i=0; i<size;i++){
                            fos.write(is.read());
                        }
                        log.debug("файл закончился");
                    }
                }
            }
        } catch (Exception e) {
            log.error("stacktrace: ", e);
        }
    }
}
