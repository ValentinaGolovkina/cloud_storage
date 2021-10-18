package ru.valensiya.core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class ListResponse extends Command{

    private final List<Item> items;

    public ListResponse(Path path) throws IOException {
        items = Files.list(path)
                .map(p -> p.getFileName().toString())
                .map(name->{
                    if (Files.isDirectory(path.resolve(name))) {
                        return new Item(name,"folder.png");
                    } else {
                        return new Item(name,"file.png");
                    }}).collect(Collectors.toList());
    }

    public List<Item> getItems() {
        return items;
    }

    /*private final List<String> names;

    public ListResponse(Path path) throws IOException {
        names = Files.list(path)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
    }

    public List<String> getNames() {
        return names;
    }*/

    @Override
    public CommandType getType() {
        return CommandType.LIST_RESPONSE;
    }
}
