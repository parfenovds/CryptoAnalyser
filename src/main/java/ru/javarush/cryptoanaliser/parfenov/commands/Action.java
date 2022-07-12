package ru.javarush.cryptoanaliser.parfenov.commands;

import ru.javarush.cryptoanaliser.parfenov.entity.Result;

public interface Action {
    Result execute(String[] args);
}
