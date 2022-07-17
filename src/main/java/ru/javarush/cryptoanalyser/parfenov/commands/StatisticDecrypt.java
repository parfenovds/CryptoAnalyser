package ru.javarush.cryptoanalyser.parfenov.commands;

import ru.javarush.cryptoanalyser.parfenov.arguments.ArgumentTypes;
import ru.javarush.cryptoanalyser.parfenov.constants.Patterns;
import ru.javarush.cryptoanalyser.parfenov.entity.Result;
import ru.javarush.cryptoanalyser.parfenov.entity.ResultCode;
import ru.javarush.cryptoanalyser.parfenov.exception.ApplicationException;
import ru.javarush.cryptoanalyser.parfenov.statCollecting.CharAndFrequency;
import ru.javarush.cryptoanalyser.parfenov.statCollecting.StatCollector;
import ru.javarush.cryptoanalyser.parfenov.util.SpellChecker;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class StatisticDecrypt extends AbstractCrypt implements Action {

    public static void printIt() {
        System.out.println("StatisticDecrypt is here!");
    }
    private final ArgumentTypes[] argumentTypes = { ArgumentTypes.INPUT_FILE, ArgumentTypes.OUTPUT_FILE, ArgumentTypes.DICT_FILE };
    private Character lastChange = null;
    public Result execute(Map<ArgumentTypes, Object> arguments) {
        Path inputPath = (Path) arguments.get(ArgumentTypes.INPUT_FILE);
        Path outputPath = (Path) arguments.get(ArgumentTypes.OUTPUT_FILE);
        Path dictPath = (Path) arguments.get(ArgumentTypes.DICT_FILE);
        StatCollector inputStatCollector = new StatCollector();
        StatCollector dicStatCollector = new StatCollector();
        inputStatCollector.getStatistics(inputPath);
        dicStatCollector.getStatistics(dictPath);
        getResult(inputPath, outputPath, inputStatCollector, dicStatCollector);

        return new Result(ResultCode.OK, "Done");
    }

    @Override
    public ArgumentTypes[] getArgumentTypes() {
        return argumentTypes;
    }
    @Override
    public int getArgumentAmount() {
        return argumentTypes.length;
    }

    public Result getResult(Path in, Path out, StatCollector inStats, StatCollector dicStats) {
        //TreeMap<CharAndFrequency, CharAndFrequency> encCharToDicChar = inStats.getMapForDraft(dicStats.getCharsFreq());
        //TreeMap<Character, CharAndFrequency> encCharToDicCharShorted = inStats.getMapForDraftShorted(dicStats.getCharsFreq());
        LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf = inStats.getDraftCharToDicCaf(dicStats.getCharsFreq());


        int bufferSize = 2000;
        boolean checked = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(in.toFile()));
             BufferedWriter writer = new BufferedWriter(new FileWriter(out.toFile()))) {
            reader.mark(1);
            String[][] patternsToCheck = {{Patterns.INCORRECT_LONELY_LETTER, Patterns.VALID_LONELY_LETTER, "1"},
                    {Patterns.SPACE_AFTER_PUNCTUATION, Patterns.VALID_CHAR_INSTEAD_OF_PUNCTUATION, "0"}};
            int countToStop = 0;
            for (String[] patterns : patternsToCheck) {
                while (!checked && countToStop < 20) {
                    System.out.println(countToStop);
                    countToStop++;
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
                    checked = CorrectorConveyor(buffer, DraftCharToDicCaf, patterns);
                }
                checked = false;
                countToStop = 0;
                lastChange = null;
            }
            int character;
            while ((character = reader.read()) != -1) {
                //character = Character.toLowerCase(character);//******************!!!!!!!!!!!!!!!!!!!!!!
                if ((character = encrypting(character, DraftCharToDicCaf)) != -1) {
                    writer.write(character);
                }
            }
        } catch (IOException e) {
            throw new ApplicationException("It's a problem with your file", e);
        }

        return new Result(ResultCode.OK, "Done");
    }

    public int encrypting(int character, LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf) {
        return DraftCharToDicCaf.get((char) character).getCharacter();
    }
    private boolean CorrectorConveyor(StringBuilder buffer,
                                      LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf,
                                      String[] patterns) {
        boolean result;
        result = tableOfCorrespondingCharsCorrector(buffer,
                DraftCharToDicCaf,
                patterns[0],
                patterns[1],
                Integer.parseInt(patterns[2]));
        return result;
    }
    private boolean tableOfCorrespondingCharsCorrector(StringBuilder buffer,
                                                       LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf,
                                                       String patternForIncorrectness, String patternForValidity,
                                                       int indexMovement) {
        boolean checked = true;
        int index = SpellChecker.getIndexOfMatching(buffer, patternForIncorrectness);
        if(index != -1) {
            checked = false;
            //Character firstPartOfSwapBase = buffer.charAt(index + 1);
            Character firstPartOfSwapBase = getBaseOfCafByChar(buffer.charAt(index + indexMovement), DraftCharToDicCaf);
            Character secondPartOfSwapBase = getSecondPartOfSwapBase(firstPartOfSwapBase, DraftCharToDicCaf, patternForValidity);
            char firstCafChar = DraftCharToDicCaf.get(firstPartOfSwapBase).getCharacter();
            char secondCafChar = DraftCharToDicCaf.get(secondPartOfSwapBase).getCharacter();
            lastChange = secondCafChar;
            DraftCharToDicCaf.get(firstPartOfSwapBase).setCharacter(secondCafChar);
            DraftCharToDicCaf.get(secondPartOfSwapBase).setCharacter(firstCafChar);
        }
        return checked;
    }
    private Character getSecondPartOfSwapBase(Character firstPartOfSwapBase, LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf, String pattern) {
        ArrayList<CharAndFrequency> listOfCandidates = new ArrayList<>();
        ArrayList<Character> listOfBasesForCandidates = new ArrayList<>(DraftCharToDicCaf.keySet());
        Character secondPartOfSwapBase = null;
        //SortedMap<CharAndFrequency, CharAndFrequency> charStatHead = DraftCharToDicCaf.headMap(firstPartOfSwapBase);
        //SortedMap<CharAndFrequency, CharAndFrequency> charStatTail = DraftCharToDicCaf.tailMap(firstPartOfSwapBase);
        //charStatTail.remove(firstPartOfSwapBase);
        int indexOfFirstChar = listOfBasesForCandidates.indexOf(firstPartOfSwapBase);
        int radiusOfPicking = 3;
        int indexToBegin = Math.max(0, indexOfFirstChar - radiusOfPicking);
        int indexToStop = Math.min(listOfBasesForCandidates.size() - 1, indexOfFirstChar + radiusOfPicking);
        for (int i = indexToBegin; i < indexToStop; i++) {
            if(i != indexOfFirstChar) {
                listOfCandidates.add(DraftCharToDicCaf.get(listOfBasesForCandidates.get(i)));
            }
        }
        listOfCandidates.sort((o1, o2) -> {
            int d = DraftCharToDicCaf.get(firstPartOfSwapBase).getFrequency();
            return Math.abs(o1.getFrequency() - d) - Math.abs(o2.getFrequency() - d);
        });
        for (CharAndFrequency candidate : listOfCandidates) {
            if(SpellChecker.patternMatchingChecker(candidate.getCharacter(), pattern) && (lastChange == null || candidate.getCharacter() != lastChange)) {
                secondPartOfSwapBase = getCharByCaf(candidate, DraftCharToDicCaf);
                break;
            }
        }
        return secondPartOfSwapBase;
    }

    private Character getCharByCaf(CharAndFrequency candidate, LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf) {
        for (Map.Entry<Character, CharAndFrequency> entry : DraftCharToDicCaf.entrySet()) {
            if(entry.getValue().equals(candidate)) {
                return entry.getKey();
            }
        }
        return null;
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
