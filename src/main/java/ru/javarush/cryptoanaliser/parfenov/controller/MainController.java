package ru.javarush.cryptoanaliser.parfenov.controller;

import ru.javarush.cryptoanaliser.parfenov.arguments.ArgumentTypes;
import ru.javarush.cryptoanaliser.parfenov.commands.Action;
import ru.javarush.cryptoanaliser.parfenov.entity.Result;
import ru.javarush.cryptoanaliser.parfenov.exception.ApplicationException;

import java.util.Map;

public class MainController {


    public Result execute(Map<ArgumentTypes, Object> arguments) {
        Action action = null;
        try {
            action = (Action) arguments.get(ArgumentTypes.COMMAND);
        } catch (ClassCastException e) {
            throw new ApplicationException("Somehow command is not in action...");
        }
        Result result = action.execute(arguments);
        return result;
    }
}
