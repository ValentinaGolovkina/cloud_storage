package ru.valensiya.nio;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;

@Slf4j
public class Server {

    private ServerSocketChannel serverChannel;
    private Selector selector;
    private ByteBuffer buffer;
    private Path rootPath;

    public Server() throws IOException {

        buffer = ByteBuffer.allocate(256);
        serverChannel = ServerSocketChannel.open();
        selector = Selector.open();
        serverChannel.bind(new InetSocketAddress(8189));
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        log.debug("Server started...");
        rootPath = Paths.get("server", "root");

        while (serverChannel.isOpen()) {

            selector.select();

            Set<SelectionKey> keys = selector.selectedKeys();

            Iterator<SelectionKey> iterator = keys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                if (key.isAcceptable()) {
                    handleAccept(key);
                }
                if (key.isReadable()) {
                    log.debug("isReadable..");
                    handleRead(key);
                }
                iterator.remove();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();

        buffer.clear();
        int read = 0;
        StringBuilder msg = new StringBuilder();
        while (true) {
            if (read == -1) {
                log.debug("read == 1.." );
                channel.close();
                return;
            }
            read = channel.read(buffer);
            if (read == 0) {
                break;
            }
            buffer.flip();
            while (buffer.hasRemaining()) {
                msg.append((char) buffer.get());
            }
            buffer.clear();
        }
        String message = msg.toString();
        log.debug("message.." + message);
        doCommand(message, channel);
    }

    private void doCommand(String message, SocketChannel channel) {
        String[] s =  message.split(" ");
        if (s.length==1 && s[0].equals("ls\r\n")) {
            log.debug("ls");
            ls(channel);
        } else if (s.length==2 && s[0].equals("cat")) {
            sendMsg(message, channel);
            cat(s[1], channel);
        } else {
            sendMsg("ERROR command does not exist", channel);
        }
    }

    @SneakyThrows
    private void ls(SocketChannel channel) {
        channel.write(ByteBuffer.wrap(("[" + LocalDateTime.now() + "]\r\n").getBytes(StandardCharsets.UTF_8)));
        Files.walk(rootPath).forEach(path -> {
            try {
                channel.write(ByteBuffer.wrap((path.toString()+"\r\n").getBytes(StandardCharsets.UTF_8)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @SneakyThrows
    private void cat(String s, SocketChannel channel) {
        Path copy = Paths.get("server", "root", s);
        if (Files.exists(copy)) {
            Files.readAllLines(copy).forEach(line->sendMsg(line, channel));
        } else {
            sendMsg("file not found", channel);
        }
    }

    @SneakyThrows
    private void sendMsg(String msg, SocketChannel channel) {
        channel.write(ByteBuffer.wrap(("[" + LocalDateTime.now() + "]" + msg + "\r\n").getBytes(StandardCharsets.UTF_8)));
    }


    private void handleAccept(SelectionKey key) throws IOException {
        SocketChannel channel = serverChannel.accept();
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
        log.debug("Client accepted...");
    }
}
