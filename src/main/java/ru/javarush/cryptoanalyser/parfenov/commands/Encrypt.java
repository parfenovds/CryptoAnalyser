package ru.javarush.cryptoanalyser.parfenov.commands;

import ru.javarush.cryptoanalyser.parfenov.arguments.ArgumentTypes;
import ru.javarush.cryptoanalyser.parfenov.entity.Result;
import ru.javarush.cryptoanalyser.parfenov.exception.ApplicationException;

import java.nio.file.Path;
import java.util.Map;

public class Encrypt extends AbstractCrypt implements Action{
    @Override
    public Result execute(Map<ArgumentTypes, Object> arguments) {
        try {
            Path inputFile = (Path) arguments.get(ArgumentTypes.INPUT_FILE);
            Path outputFile = (Path) arguments.get(ArgumentTypes.OUTPUT_FILE);
            int key = (int) arguments.get(ArgumentTypes.KEY);

            String alphabet = (String) arguments.get(ArgumentTypes.ALPHABET);
            boolean toLower = (boolean) arguments.get(ArgumentTypes.TO_LOWER_CASE);
            return getResult(inputFile, outputFile, key, alphabet, toLower);
        } catch(ClassCastException e) {
            throw new ApplicationException("Something went wrong with casting: ", e);
        }
    }
}
