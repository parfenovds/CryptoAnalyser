package ru.javarush.cryptoanalyser.parfenov.arguments;

import ru.javarush.cryptoanalyser.parfenov.commands.Action;
import ru.javarush.cryptoanalyser.parfenov.controller.Actions;
import ru.javarush.cryptoanalyser.parfenov.exception.ApplicationException;
import ru.javarush.cryptoanalyser.parfenov.util.PathFinder;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class ArgumentExtractor {

    public Action extractCommand(String command) {
        try {
            return Actions.find(command);
        } catch(IllegalArgumentException e) {
            throw new ApplicationException(command +
                        " is not a valid command. You have to choose one of those: /n"
                    + Arrays.toString(Actions.values()) + "/nTry again.", e);
        }
    }

    public Path extractFileName(String parameter) {
        try {
            Path path = Paths.get(parameter);
            if (path.getRoot() == null) {
                return Path.of(PathFinder.getRoot() + parameter);
            } else {
                System.out.println("??");
            }
            throw new ApplicationException(parameter +
                    " !is not a proper file name. You have to enter only a file name (or a relative path to the file). Try again.");
        } catch (InvalidPathException | NullPointerException ex) {
            throw new ApplicationException(parameter +
                    " is not a proper file name. You have to enter only a file name (or a relative path to the file). Try again.");
        }
    }

    public int extractKey(String parameter) {
        try {
            return Integer.parseInt(parameter);
        } catch(NumberFormatException e) {
            throw new ApplicationException(parameter +
                    " is not a key for the Caesar cypher. The key has to be an integer./n Try again.", e);
        }
    }
}
