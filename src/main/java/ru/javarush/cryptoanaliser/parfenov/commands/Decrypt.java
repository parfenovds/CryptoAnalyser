package ru.javarush.cryptoanaliser.parfenov.commands;

import ru.javarush.cryptoanaliser.parfenov.arguments.ArgumentTypes;
import ru.javarush.cryptoanaliser.parfenov.entity.Result;
import ru.javarush.cryptoanaliser.parfenov.exception.ApplicationException;

import java.nio.file.Path;
import java.util.Map;

public class Decrypt extends AbstractCrypt implements Action{
    public static void printIt() {
        System.out.println("Decrypt is here!");
    }
    private final ArgumentTypes[] argumentTypes = { ArgumentTypes.INPUT_FILE, ArgumentTypes.OUTPUT_FILE, ArgumentTypes.KEY };
    public Result execute(Map<ArgumentTypes, Object> arguments) {
        try {
            Path inputFile = (Path) arguments.get(ArgumentTypes.INPUT_FILE);
            Path outputFile = (Path) arguments.get(ArgumentTypes.OUTPUT_FILE);
            int key = (int) arguments.get(ArgumentTypes.KEY);
            key *= -1;
            return getResult(inputFile, outputFile, key);
        } catch(ClassCastException e) {
            throw new ApplicationException("Something went wrong with casting: ", e);
        }
    }
    public ArgumentTypes[] getArgumentTypes() {
        return argumentTypes;
    }
    @Override
    public int getArgumentAmount() {
        return argumentTypes.length;
    }
}
