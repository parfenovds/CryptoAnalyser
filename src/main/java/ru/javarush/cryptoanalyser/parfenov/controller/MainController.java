package ru.javarush.cryptoanalyser.parfenov.controller;

import ru.javarush.cryptoanalyser.parfenov.arguments.ArgumentTypes;
import ru.javarush.cryptoanalyser.parfenov.commands.Action;
import ru.javarush.cryptoanalyser.parfenov.entity.Result;
import ru.javarush.cryptoanalyser.parfenov.exception.ApplicationException;

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
