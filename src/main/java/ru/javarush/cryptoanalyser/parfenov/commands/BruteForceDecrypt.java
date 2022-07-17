package ru.javarush.cryptoanalyser.parfenov.commands;

import ru.javarush.cryptoanalyser.parfenov.arguments.ArgumentTypes;
import ru.javarush.cryptoanalyser.parfenov.constants.Alphabet;
import ru.javarush.cryptoanalyser.parfenov.entity.Result;
import ru.javarush.cryptoanalyser.parfenov.entity.ResultCode;
import ru.javarush.cryptoanalyser.parfenov.exception.ApplicationException;
import ru.javarush.cryptoanalyser.parfenov.util.SpellChecker;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BruteForceDecrypt extends AbstractCrypt implements Action{
    public static void printIt() {
        System.out.println("Brute is here!");
    }
    private final ArgumentTypes[] argumentTypes = { ArgumentTypes.INPUT_FILE };
    public Result execute(Map<ArgumentTypes, Object> arguments) {
        //System.out.println(Files.getAttribute());
        int bufferSize = 200;
        Path path = (Path) arguments.get(ArgumentTypes.INPUT_FILE);
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            reader.mark(1);
            List<Integer> probingResults = new ArrayList<>();
            for (int key = 0; key < Alphabet.FULL_ALPHABET.length(); key++) {
                int counter = 0;
                int character;
                StringBuilder buffer = new StringBuilder();
                while(reader.ready() && counter++ < bufferSize) {
                    character = reader.read();
                //while ((character = reader.read()) != -1 && counter++ < bufferSize) {
                    if ((character = encrypting(character, -key)) != -1) {
                        buffer.append((char)character);
                    }
                }
                probingResults.add(SpellChecker.patternMatchingCounter(buffer));
                if(probingResults.get(probingResults.size()-1) == 0) {
                    System.out.println(buffer);
                    System.out.println("\n********************");
                    System.out.println("for the key of " + key + " it's " + probingResults.get(probingResults.size() - 1) + " found");
                    System.out.println("\n********************");
                }
                reader.reset();
            }
            Collections.sort(probingResults);
            System.out.println(probingResults);
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
