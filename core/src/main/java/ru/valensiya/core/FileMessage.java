package ru.valensiya.core;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileMessage implements Serializable {
    private final String name;
    private final byte[] bytes;

    public FileMessage(Path path) throws IOException {
        this.name = path.getFileName().toString();
        this.bytes = Files.readAllBytes(path);
    }

    public String getName() {
        return name;
    }

    public byte[] getBytes() {
        return bytes;
    }
}
