package ru.javarush.cryptoanaliser.parfenov.arguments;

import ru.javarush.cryptoanaliser.parfenov.commands.Action;
import ru.javarush.cryptoanaliser.parfenov.controller.Actions;
import ru.javarush.cryptoanaliser.parfenov.exception.ApplicationException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ArgumentHandler {
    private final String[] args;
    private Map<ArgumentTypes, Object> arguments = new HashMap<>();
    public ArgumentHandler(String[] args) {
        this.args = args;
    }

    public Map<ArgumentTypes, Object> getMapOfArguments() {//TODO: refactor this shit!
        ArgumentChecker argumentChecker = new ArgumentChecker();
        ArgumentExtractor argumentExtractor = new ArgumentExtractor();
        int amountOfArguments = argumentChecker.getAmountOfArguments(args);
        Action action = argumentExtractor.extractCommand(args[0]);
        argumentChecker.hasExactAmountOfArguments(action, amountOfArguments);
        arguments.put(ArgumentTypes.COMMAND, action);
        for(int i = 0; i < action.getArgumentTypes().length; ++i) {
            if(action.getArgumentTypes()[i] == ArgumentTypes.KEY) {
                int key = argumentExtractor.extractKey(args[i+1]);
                arguments.put(action.getArgumentTypes()[i], key);
            } else {
                Path path = argumentExtractor.extractFileName(args[i+1]);
                arguments.put(action.getArgumentTypes()[i], path);
            }
        }
        return arguments;
    }

    public Map<ArgumentTypes, Object> getArguments() {
        return arguments;
    }
}
