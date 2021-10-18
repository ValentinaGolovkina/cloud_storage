package ru.valensiya.core;
import lombok.Data;

import java.io.Serializable;

public class Item implements Serializable {
    private final String name;
    private final String image;

    public Item(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }
}
