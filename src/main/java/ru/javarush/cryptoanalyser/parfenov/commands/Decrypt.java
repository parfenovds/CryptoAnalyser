package ru.javarush.cryptoanalyser.parfenov.commands;

import ru.javarush.cryptoanalyser.parfenov.arguments.ArgumentTypes;
import ru.javarush.cryptoanalyser.parfenov.entity.Result;
import ru.javarush.cryptoanalyser.parfenov.exception.ApplicationException;

import java.nio.file.Path;
import java.util.Map;

public class Decrypt extends AbstractCrypt implements Action{
    private final ArgumentTypes[] argumentTypes = { ArgumentTypes.INPUT_FILE, ArgumentTypes.OUTPUT_FILE, ArgumentTypes.KEY };
    private final ArgumentTypes[] altArgumentTypes = null;
    @Override
    public Result execute(Map<ArgumentTypes, Object> arguments) {
        try {
            Path inputFile = (Path) arguments.get(ArgumentTypes.INPUT_FILE);
            Path outputFile = (Path) arguments.get(ArgumentTypes.OUTPUT_FILE);
            String alphabet = (String) arguments.get(ArgumentTypes.ALPHABET);
            int key = (int) arguments.get(ArgumentTypes.KEY);
            key *= -1;
            return getResult(inputFile, outputFile, key, alphabet, false);
        } catch(ClassCastException e) {
            throw new ApplicationException("Something went wrong with casting: ", e);
        }
    }
    @Override
    public ArgumentTypes[] getArgumentTypes() {
        return argumentTypes;
    }
    @Override
    public int getArgumentAmount() {
        return argumentTypes.length;
    }
}
