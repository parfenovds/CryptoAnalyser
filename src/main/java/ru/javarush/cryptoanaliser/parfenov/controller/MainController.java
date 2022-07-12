package ru.javarush.cryptoanaliser.parfenov.controller;

import ru.javarush.cryptoanaliser.parfenov.commands.Action;
import ru.javarush.cryptoanaliser.parfenov.entity.Result;

public class MainController {


    public Result execute(String command, String[] parameters) {
        Action action = Actions.find(command);
        Result result = action.execute(parameters);
        return result;
    }
}
