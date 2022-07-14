package ru.javarush.cryptoanaliser.parfenov.commands;

import ru.javarush.cryptoanaliser.parfenov.arguments.ArgumentTypes;
import ru.javarush.cryptoanaliser.parfenov.constants.Alphabet;
import ru.javarush.cryptoanaliser.parfenov.entity.Result;
import ru.javarush.cryptoanaliser.parfenov.entity.ResultCode;
import ru.javarush.cryptoanaliser.parfenov.exception.ApplicationException;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;

public class BruteForceDecrypt extends AbstractCrypt implements Action{
    public static void printIt() {
        System.out.println("Brute is here!");
    }
    private final ArgumentTypes[] argumentTypes = { ArgumentTypes.INPUT_FILE };
    public Result execute(Map<ArgumentTypes, Object> arguments) {
        Path path = (Path) arguments.get(ArgumentTypes.INPUT_FILE);
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            for (int key = 0; key < Alphabet.ALPHABET.size(); key++) {
                int character;
                while ((character = reader.read()) != -1 && character != '\n') {
                    if ((character = encrypting(character, -key)) != -1) {
                        System.out.print((char)(character));
                    }
                }
            }
        } catch (IOException e) {
            throw new ApplicationException("It's a problem with your file", e);
        }
        return new Result(ResultCode.OK, "Done");
    }
    private static boolean checker() {
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
