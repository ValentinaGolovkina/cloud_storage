package ru.valensiya.core;

public class PathRequest extends Command{
    @Override
    public CommandType getType() {
        return CommandType.PATH_REQUEST;
    }
}
