package ru.javarush.cryptoanalyser.parfenov.commands;

import ru.javarush.cryptoanalyser.parfenov.app.Application;
import ru.javarush.cryptoanalyser.parfenov.arguments.ArgumentExtractor;
import ru.javarush.cryptoanalyser.parfenov.arguments.ArgumentTypes;
import ru.javarush.cryptoanalyser.parfenov.constants.Alphabet;
import ru.javarush.cryptoanalyser.parfenov.controller.MainController;
import ru.javarush.cryptoanalyser.parfenov.entity.Result;
import ru.javarush.cryptoanalyser.parfenov.entity.ResultCode;
import ru.javarush.cryptoanalyser.parfenov.exception.ApplicationException;
import ru.javarush.cryptoanalyser.parfenov.util.SpellChecker;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class BruteForceDecrypt extends AbstractCrypt implements Action{
    private final ArgumentTypes[] argumentTypes = { ArgumentTypes.INPUT_FILE };
    private final ArgumentTypes[] altArgumentTypes = null;
    public Result execute(Map<ArgumentTypes, Object> arguments) {
        int bufferSize = 200;
        Path path = (Path) arguments.get(ArgumentTypes.INPUT_FILE);
        String alphabet = (String) arguments.get(ArgumentTypes.ALPHABET);
        try (BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            reader.mark(1);
            //List<Integer> probingResults = new ArrayList<>();
            int foundKey = 0;
            int lastMin = Integer.MAX_VALUE;
            for (int key = 0; key < alphabet.length(); key++) {
                int counter = 0;
                int character;
                StringBuilder buffer = new StringBuilder();
                while(reader.ready() && counter++ < bufferSize) {
                    character = reader.read();
                    if ((character = encrypting(character, -key, alphabet)) != -1) {
                        buffer.append((char)character);
                    }
                }
                //probingResults.add(SpellChecker.patternMatchingCounter(buffer));
                int amountOfMatchings = SpellChecker.patternMatchingCounter(buffer);
                if(lastMin > amountOfMatchings) {
                    foundKey = key;
                    lastMin = amountOfMatchings;
                }


//                if(probingResults.get(probingResults.size()-1) == 0) {
//                    System.out.println(buffer);
//                    System.out.println("\n********************");
//                    System.out.println("for the key of " + key + " it's " + probingResults.get(probingResults.size() - 1) + " found");
//                    System.out.println("\n********************");
//                }
                reader.reset();
            }
            //Collections.sort(probingResults);
            ArgumentExtractor argumentExtractor = new ArgumentExtractor();
            Action action = argumentExtractor.extractCommand("decrypt");
            int finalKey = foundKey;
            Map<ArgumentTypes, Object> argumentsForDictCutting = new HashMap<>() {{
                put(ArgumentTypes.COMMAND, action);
                put(ArgumentTypes.INPUT_FILE, arguments.get(ArgumentTypes.INPUT_FILE));
                put(ArgumentTypes.OUTPUT_FILE, arguments.get(ArgumentTypes.OUTPUT_FILE));
                put(ArgumentTypes.KEY, finalKey);
                put(ArgumentTypes.ALPHABET, alphabet);
                put(ArgumentTypes.TO_LOWER_CASE, false);
            }};
            MainController mainController = new MainController();
            Application application = new Application(mainController);
            application.run(argumentsForDictCutting);
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
