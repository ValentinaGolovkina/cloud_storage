package ru.valensiya.core;

public enum CommandType {
    FILE_MESSAGE,  //файл
    FILE_REQUEST,  //дай мне файл с именем
    LIST_REQUEST,  //дай мне список
    LIST_RESPONSE, //список
    PATH_REQUEST,  //сервер скажи где ты сейчас
    PATH_RESPONSE  //в какой директории сейчас сервер
}
