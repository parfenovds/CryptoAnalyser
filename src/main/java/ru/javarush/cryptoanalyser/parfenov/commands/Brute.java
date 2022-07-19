package ru.javarush.cryptoanalyser.parfenov.commands;

import ru.javarush.cryptoanalyser.parfenov.app.Application;
import ru.javarush.cryptoanalyser.parfenov.arguments.ArgumentExtractor;
import ru.javarush.cryptoanalyser.parfenov.arguments.ArgumentTypes;
import ru.javarush.cryptoanalyser.parfenov.controller.MainController;
import ru.javarush.cryptoanalyser.parfenov.entity.Result;
import ru.javarush.cryptoanalyser.parfenov.entity.ResultCode;
import ru.javarush.cryptoanalyser.parfenov.exception.ApplicationException;
import ru.javarush.cryptoanalyser.parfenov.util.SpellChecker;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class Brute extends AbstractCrypt implements Action{
    public Result execute(Map<ArgumentTypes, Object> arguments) {
        Path pathOfInputFile = (Path) arguments.get(ArgumentTypes.INPUT_FILE);
        String alphabet = (String) arguments.get(ArgumentTypes.ALPHABET);
        return getResult(arguments, pathOfInputFile, alphabet);
    }
    private Result getResult(Map<ArgumentTypes, Object> arguments, Path pathOfInputFile, String alphabet) {
        try (BufferedReader reader = new BufferedReader(new FileReader(pathOfInputFile.toFile()))) {
            int foundKey = findKeyForDecrypting(alphabet, reader);
            decryptTextWithFoundKey(arguments, alphabet, foundKey);
        } catch (IOException e) {
            throw new ApplicationException("It's a problem with your file", e);
        }
        return new Result(ResultCode.OK, "Done");
    }
    private int findKeyForDecrypting(String alphabet, BufferedReader reader) throws IOException {
        int foundKey = 0;
        int minAmountOfErrorsFound = Integer.MAX_VALUE;
        reader.mark(1);//The reason I've used this method is because of readChar of RandomAccessFile did not work with my Cyrillic texts somehow
        for (int key = 0; key < alphabet.length(); key++) {
            StringBuilder probe = probeCollecting(alphabet, reader, key);
            int amountOfErrorsInProbe = SpellChecker.getAmountOfErrorsInProbe(probe);
            if(minAmountOfErrorsFound > amountOfErrorsInProbe) {
                foundKey = key;
                minAmountOfErrorsFound = amountOfErrorsInProbe;
            }
            reader.reset();
        }
        return foundKey;
    }
    private StringBuilder probeCollecting(String alphabet, BufferedReader reader, int key) throws IOException {
        int probeSize = 200;
        StringBuilder probeCollector = new StringBuilder();
        while(reader.ready() && probeCollector.length() < probeSize) {
            int intOfChar = reader.read();
            if ((intOfChar = encrypting(intOfChar, -key, alphabet)) != -1) {
                probeCollector.append((char)intOfChar);
            }
        }
        return probeCollector;
    }
    private void decryptTextWithFoundKey(Map<ArgumentTypes, Object> arguments, String alphabet, int foundKey) {
        Map<ArgumentTypes, Object> argumentsForDictCutting = prepareArgumentsForDecrypting(arguments, alphabet, foundKey);
        MainController mainController = new MainController();
        Application application = new Application(mainController);
        application.run(argumentsForDictCutting);
    }
    private Map<ArgumentTypes, Object> prepareArgumentsForDecrypting(Map<ArgumentTypes, Object> arguments, String alphabet, int key) {
        Action action = ArgumentExtractor.extractCommand("decrypt");
        return new HashMap<>() {{
            put(ArgumentTypes.COMMAND, action);
            put(ArgumentTypes.INPUT_FILE, arguments.get(ArgumentTypes.INPUT_FILE));
            put(ArgumentTypes.OUTPUT_FILE, arguments.get(ArgumentTypes.OUTPUT_FILE));
            put(ArgumentTypes.KEY, key);
            put(ArgumentTypes.ALPHABET, alphabet);
            put(ArgumentTypes.TO_LOWER_CASE, false);
        }};
    }
}
