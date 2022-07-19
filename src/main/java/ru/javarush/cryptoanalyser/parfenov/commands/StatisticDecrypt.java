package ru.javarush.cryptoanalyser.parfenov.commands;

import ru.javarush.cryptoanalyser.parfenov.app.Application;
import ru.javarush.cryptoanalyser.parfenov.arguments.ArgumentExtractor;
import ru.javarush.cryptoanalyser.parfenov.arguments.ArgumentTypes;
import ru.javarush.cryptoanalyser.parfenov.controller.MainController;
import ru.javarush.cryptoanalyser.parfenov.entity.Result;
import ru.javarush.cryptoanalyser.parfenov.entity.ResultCode;
import ru.javarush.cryptoanalyser.parfenov.exception.ApplicationException;
import ru.javarush.cryptoanalyser.parfenov.statCollecting.CharAndFrequency;
import ru.javarush.cryptoanalyser.parfenov.statCollecting.StatCollector;

import javax.management.StandardEmitterMBean;
import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class StatisticDecrypt extends AbstractCrypt implements Action {
    private final ArgumentTypes[] argumentTypes = { ArgumentTypes.INPUT_FILE, ArgumentTypes.OUTPUT_FILE, ArgumentTypes.DICT_FILE };
    private final ArgumentTypes[] altArgumentTypes = null;
    public Result execute(Map<ArgumentTypes, Object> arguments) {
        Path inputPath = (Path) arguments.get(ArgumentTypes.INPUT_FILE);
        Path outputPath = (Path) arguments.get(ArgumentTypes.OUTPUT_FILE);
        String nameForDictCut = "cut_" + ((Path) arguments.get(ArgumentTypes.INPUT_FILE)).getFileName();
        ArgumentExtractor argumentExtractor = new ArgumentExtractor();
        Path cutDictPath = argumentExtractor.extractFileName(nameForDictCut);
        Action action = argumentExtractor.extractCommand("encrypt");
        String alphabet = getAlphabet(inputPath);
        Map<ArgumentTypes, Object> argumentsForDictCutting = new HashMap<>() {{
            put(ArgumentTypes.COMMAND, action);
            put(ArgumentTypes.INPUT_FILE, arguments.get(ArgumentTypes.DICT_FILE));
            put(ArgumentTypes.OUTPUT_FILE, cutDictPath);
            put(ArgumentTypes.KEY, 0);
            put(ArgumentTypes.ALPHABET, alphabet);
            put(ArgumentTypes.TO_LOWER_CASE, true);
        }};
        MainController mainController = new MainController();
        Application application = new Application(mainController);
        application.run(argumentsForDictCutting);

        StatCollector inputStatCollector = new StatCollector();
        StatCollector dicStatCollector = new StatCollector();
        inputStatCollector.getStatistics(inputPath);
        dicStatCollector.getStatistics(cutDictPath);
        return getResult(inputPath, outputPath, inputStatCollector, dicStatCollector);
    }

    private String getAlphabet(Path inputPath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputPath.toFile()))) {
            int character;
            StringBuilder alphabet = new StringBuilder();
            while (reader.ready()) {
                character = reader.read();
                String ch = String.valueOf((char)character);
                if(alphabet.indexOf(ch) == -1) {
                    alphabet.append((char) character);
                }
            }
            return alphabet.toString();
        } catch (IOException e) {
            throw new ApplicationException("It's a problem with your file", e);
        }
    }

    public Result getResult(Path in, Path out, StatCollector inStats, StatCollector dicStats) {
        LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf = inStats.getDraftCharToDicCaf(dicStats.getCharsFreq());

        try (BufferedReader reader = new BufferedReader(new FileReader(in.toFile()));
             BufferedWriter writer = new BufferedWriter(new FileWriter(out.toFile()))) {

            produceCorrectEncoding(DraftCharToDicCaf, reader, writer);
        } catch (IOException e) {
            throw new ApplicationException("It's a problem with your file", e);
        }

        return new Result(ResultCode.OK, "Done");
    }

    private void produceCorrectEncoding(LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf, BufferedReader reader, BufferedWriter writer) throws IOException {

        int bufferSize = 500;
        Scanner sc = new Scanner(System.in);
        reader.mark(1);
        while (true) {
            int counter = 0;
            int character;
            StringBuilder buffer = new StringBuilder();
            while (reader.ready() && counter++ < bufferSize) {
                character = reader.read();
                if ((character = encrypting(character, DraftCharToDicCaf)) != -1) {
                    buffer.append((char) character);
                }
            }
            reader.reset();

            Character[] charsToSwap = new Character[2];
            String answer;
            do {
                System.out.println(buffer);
                System.out.println("Would you like to do char swapping (д/н)?");
                answer = sc.nextLine();
            } while(!answer.equals("д") && !answer.equals("н"));
            if(answer.equals("д")) {
                for (int i = 0; i < charsToSwap.length; i++) {
                    do {
                        System.out.println("Give me " + (i+1) + "st(nd) letter to swap (if you want to swap a new line char to something, just press enter):");
                        answer = sc.nextLine();
                        if(answer.isEmpty()) answer = "\n";
                    } while (answer.length() != 1 || !DraftCharToDicCaf.containsKey(answer.toCharArray()[0]));
                    charsToSwap[i] = answer.toCharArray()[0];
                }
                swapChars(DraftCharToDicCaf, charsToSwap);
            } else {
                break;
            }
        }

        int character;
        while ((character = reader.read()) != -1) {
            if ((character = encrypting(character, DraftCharToDicCaf)) != -1) {
                writer.write(character);
            }
        }
    }

    public int encrypting(int character, LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf) {
        return DraftCharToDicCaf.get((char) character).getCharacter();
    }
    private void swapChars(LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf, Character[] charsToSwap) {
        Character firstPartOfSwapBase = getBaseOfCafByChar(charsToSwap[0], DraftCharToDicCaf);
        Character secondPartOfSwapBase = getBaseOfCafByChar(charsToSwap[1], DraftCharToDicCaf);
        char firstCafChar = DraftCharToDicCaf.get(firstPartOfSwapBase).getCharacter();
        char secondCafChar = DraftCharToDicCaf.get(secondPartOfSwapBase).getCharacter();
        DraftCharToDicCaf.get(firstPartOfSwapBase).setCharacter(secondCafChar);
        DraftCharToDicCaf.get(secondPartOfSwapBase).setCharacter(firstCafChar);
    }
    private Character getBaseOfCafByChar(Character character, LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf) {
        for (Map.Entry<Character, CharAndFrequency> entry : DraftCharToDicCaf.entrySet()) {
            if(entry.getValue().getCharacter() == character) {
                return entry.getKey();
            }
        }
        return null;
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
