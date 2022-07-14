package ru.javarush.cryptoanaliser.parfenov.commands;

import ru.javarush.cryptoanaliser.parfenov.arguments.ArgumentTypes;
import ru.javarush.cryptoanaliser.parfenov.entity.Result;

import java.util.Map;

public class BruteForceDecrypt implements Action{
    public static void printIt() {
        System.out.println("Brute is here!");
    }
    public Result execute(Map<ArgumentTypes, Object> arguments) {
        throw new UnsupportedOperationException();
    }
    public ArgumentTypes[] getArgumentTypes() {
        return argumentTypes;
    }
    @Override
    public int getArgumentAmount() {
        return argumentTypes.length;
    }
}
