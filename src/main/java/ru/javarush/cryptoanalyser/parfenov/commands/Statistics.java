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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Statistics extends AbstractCrypt implements Action {
    public Result execute(Map<ArgumentTypes, Object> arguments) {
        Path pathToEncryptedFile = (Path) arguments.get(ArgumentTypes.INPUT_FILE);
        Path pathToDecryptedFile = (Path) arguments.get(ArgumentTypes.OUTPUT_FILE);
        Path pathToDictFileWithShrunkChars = generateDictFileWithShrunkChars(arguments, pathToEncryptedFile);
        StatCollector inputStatCollector = new StatCollector();
        StatCollector dictStatCollector = new StatCollector();
        inputStatCollector.getStatistics(pathToEncryptedFile);
        dictStatCollector.getStatistics(pathToDictFileWithShrunkChars);
        deleteTempFile(pathToDictFileWithShrunkChars);
        return getResult(pathToEncryptedFile, pathToDecryptedFile, inputStatCollector, dictStatCollector);
    }

    private void deleteTempFile(Path pathToDictFileWithShrunkChars) {
        try {
            Files.deleteIfExists(pathToDictFileWithShrunkChars);
        } catch(IOException e) {
            throw new ApplicationException("Can't delete temp file", e);
        }
    }

    private Path generateDictFileWithShrunkChars(Map<ArgumentTypes, Object> arguments, Path pathToEncryptedFile) {
        String nameForDictFileWithShrunkChars = "cut_" + ((Path) arguments.get(ArgumentTypes.INPUT_FILE)).getFileName();
        Path cutDictPath = ArgumentExtractor.extractFileName(nameForDictFileWithShrunkChars);
        Map<ArgumentTypes, Object> argumentsForDictShrinking = generateArgumentsForDictShrinking(arguments, pathToEncryptedFile, cutDictPath);
        MainController mainController = new MainController();
        Application application = new Application(mainController);
        application.run(argumentsForDictShrinking);
        return cutDictPath;
    }

    private Map<ArgumentTypes, Object> generateArgumentsForDictShrinking(Map<ArgumentTypes, Object> arguments, Path pathToEncryptedFile, Path cutDictPath) {
        Action action = ArgumentExtractor.extractCommand("encrypt");
        String alphabet = getAlphabet(pathToEncryptedFile);
        return new HashMap<>() {{
            put(ArgumentTypes.COMMAND, action);
            put(ArgumentTypes.INPUT_FILE, arguments.get(ArgumentTypes.DICT_FILE));
            put(ArgumentTypes.OUTPUT_FILE, cutDictPath);
            put(ArgumentTypes.KEY, 0);
            put(ArgumentTypes.ALPHABET, alphabet);
            put(ArgumentTypes.TO_LOWER_CASE, true);
        }};
    }

    private String getAlphabet(Path inputPath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputPath.toFile()))) {
            StringBuilder alphabet = new StringBuilder();
            while (reader.ready()) {
                int intOfChar = reader.read();
                String stringMadeOfChar = String.valueOf((char)intOfChar);
                if(alphabet.indexOf(stringMadeOfChar) == -1) {
                    alphabet.append((char) intOfChar);
                }
            }
            return alphabet.toString();
        } catch (IOException e) {
            throw new ApplicationException("It's a problem with your file", e);
        }
    }

    public Result getResult(Path pathToEncryptedFile,
                            Path pathToDecryptedFile,
                            StatCollector inputStatCollector,
                            StatCollector dictStatCollector) {
        LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf = inputStatCollector.getDraftCharToDicCaf(dictStatCollector.getCharsFreq());
        try(BufferedReader reader = new BufferedReader(new FileReader(pathToEncryptedFile.toFile()));
            BufferedWriter writer = new BufferedWriter(new FileWriter(pathToDecryptedFile.toFile()))) {
            produceCorrectEncoding(DraftCharToDicCaf, reader, writer);
        } catch (IOException e) {
            throw new ApplicationException("It's a problem with your file", e);
        }
        return new Result(ResultCode.OK, "Done");
    }

    private void produceCorrectEncoding(LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf, BufferedReader reader, BufferedWriter writer) throws IOException {
        Scanner sc = new Scanner(System.in);
        reader.mark(1);
        while (true) {
            StringBuilder probe = probeCollector(DraftCharToDicCaf, reader);
            if (swappingIsDone(DraftCharToDicCaf, sc, probe)) break;
        }

        generateFinalText(DraftCharToDicCaf, reader, writer);
    }

    private void generateFinalText(LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf, BufferedReader reader, BufferedWriter writer) throws IOException {
        int character;
        while ((character = reader.read()) != -1) {
            if ((character = encrypting(character, DraftCharToDicCaf)) != -1) {
                writer.write(character);
            }
        }
    }

    private StringBuilder probeCollector(LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf, BufferedReader reader) throws IOException {
        int probeSize = 500;
        StringBuilder probe = new StringBuilder();
        while (reader.ready() && probe.length() < probeSize) {
            int character = reader.read();
            if ((character = encrypting(character, DraftCharToDicCaf)) != -1) {
                probe.append((char) character);
            }
        }
        reader.reset();
        return probe;
    }

    private boolean swappingIsDone(LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf, Scanner sc, StringBuilder probe) {//Вот этот ужас отрефакторить не успел =(
        Character[] charsToSwap = new Character[2];
        String answer;
        do {
            System.out.println(probe);
            System.out.println("Would you like to do char swapping (д/н)?");
            answer = sc.nextLine();
        } while(!answer.equals("д") && !answer.equals("н"));
        if(answer.equals("д")) {
            for (int i = 0; i < charsToSwap.length; i++) {
                do {
                    if(i == 0) {
                        System.out.println("Give me 1st letter to swap (if you want to swap a new line char to something, just press enter):");
                    }
                    else {
                        System.out.println("Give me 2nd letter to swap (if you want to swap a new line char to something, just press enter):");
                    }
                    answer = sc.nextLine();
                    if(answer.isEmpty()) answer = "\n";
                } while (answer.length() != 1 || !DraftCharToDicCaf.containsKey(answer.toCharArray()[0]));
                charsToSwap[i] = answer.toCharArray()[0];
            }
            swapChars(DraftCharToDicCaf, charsToSwap);
        } else {
            return true;
        }
        return false;
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
}
