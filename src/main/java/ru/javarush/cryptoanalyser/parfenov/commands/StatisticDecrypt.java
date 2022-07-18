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
import java.util.*;

public class StatisticDecrypt extends AbstractCrypt implements Action {

    private Character lastFirstSwapBase = null;
    private Character lastSecondSwapBase = null;
    private HashMap<Character, HashSet<Character>> lastSwap = new HashMap<>();

    public static void printIt() {
        System.out.println("StatisticDecrypt is here!");
    }
    private final ArgumentTypes[] argumentTypes = { ArgumentTypes.INPUT_FILE, ArgumentTypes.OUTPUT_FILE, ArgumentTypes.DICT_FILE };
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


        int bufferSize = 500;
        boolean done = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(in.toFile()));
             BufferedWriter writer = new BufferedWriter(new FileWriter(out.toFile()));
             Scanner sc = new Scanner(System.in)) {
            reader.mark(1);
            do {
                while (!done) {
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
                                //System.out.println(answer.length());
                                //System.out.println(answer);
                            } while (answer.length() != 1 || !DraftCharToDicCaf.containsKey(answer.toCharArray()[0]));
                            charsToSwap[i] = answer.toCharArray()[0];
                        }
                        swapChars(DraftCharToDicCaf, charsToSwap);
                    } else {
                        done = true;
                    }
                }
            } while(!done);
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
            Character firstPartOfSwapBase = getBaseOfCafByChar(buffer.charAt(index + indexMovement), DraftCharToDicCaf);
            Character secondPartOfSwapBase = getSecondPartOfSwapBase(firstPartOfSwapBase, DraftCharToDicCaf, patternForValidity, null);
//            if(firstPartOfSwapBase.equals(lastFirstSwapBase)) {
//                swapMirrorsOfCharsWithNoFreq(DraftCharToDicCaf, lastSecondSwapBase, lastFirstSwapBase);
//                secondPartOfSwapBase = getSecondPartOfSwapBase(firstPartOfSwapBase, DraftCharToDicCaf, patternForValidity, lastSecondSwapBase);
//            }
            swapMirrorsOfCharsWithNoFreq(DraftCharToDicCaf, firstPartOfSwapBase, secondPartOfSwapBase);
//            if(!lastSwap.containsKey(DraftCharToDicCaf.get(firstPartOfSwapBase).getCharacter())) {
//                lastSwap.put(DraftCharToDicCaf.get(firstPartOfSwapBase).getCharacter(), new HashSet<>());
//            }
//            lastSwap.get(DraftCharToDicCaf.get(firstPartOfSwapBase).getCharacter()).add(DraftCharToDicCaf.get(secondPartOfSwapBase).getCharacter());

        }
        return checked;
    }

    private void swapMirrorsOfCharsWithNoFreq(LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf, Character firstPartOfSwapBase, Character secondPartOfSwapBase) {

        char firstCafChar = DraftCharToDicCaf.get(firstPartOfSwapBase).getCharacter();
        char secondCafChar = DraftCharToDicCaf.get(secondPartOfSwapBase).getCharacter();
        DraftCharToDicCaf.get(firstPartOfSwapBase).setCharacter(secondCafChar);
        DraftCharToDicCaf.get(secondPartOfSwapBase).setCharacter(firstCafChar);
    }
    private void swapChars(LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf, Character[] charsToSwap) {
        Character firstPartOfSwapBase = getBaseOfCafByChar(charsToSwap[0], DraftCharToDicCaf);
        Character secondPartOfSwapBase = getBaseOfCafByChar(charsToSwap[1], DraftCharToDicCaf);
        char firstCafChar = DraftCharToDicCaf.get(firstPartOfSwapBase).getCharacter();
        char secondCafChar = DraftCharToDicCaf.get(secondPartOfSwapBase).getCharacter();
        DraftCharToDicCaf.get(firstPartOfSwapBase).setCharacter(secondCafChar);
        DraftCharToDicCaf.get(secondPartOfSwapBase).setCharacter(firstCafChar);
    }

    private Character getSecondPartOfSwapBase(Character firstPartOfSwapBase,
                                              LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf,
                                              String pattern, Character excludeThisChar) {
        ArrayList<CharAndFrequency> listOfCandidates = new ArrayList<>();
        ArrayList<Character> listOfBasesForCandidates = new ArrayList<>(DraftCharToDicCaf.keySet());
        //listOfBasesForCandidates.remove(excludeThisChar);
        Character secondPartOfSwapBase = null;
        int indexOfFirstChar = listOfBasesForCandidates.indexOf(firstPartOfSwapBase);
        int radiusOfPicking = 3;
        int indexToBegin = Math.max(0, indexOfFirstChar - radiusOfPicking);
        int indexToStop = Math.min(listOfBasesForCandidates.size() - 1, indexOfFirstChar + radiusOfPicking);
        for (int i = indexToBegin; i <= indexToStop; i++) {
//        for (int i = 0; i < listOfBasesForCandidates.size(); i++) {
            Character key = firstPartOfSwapBase;
            //Character key = listOfBasesForCandidates.get(firstPartOfSwapBase);
            char charFromCaf = DraftCharToDicCaf.get(key).getCharacter();
            if(!lastSwap.containsKey(charFromCaf)) {
                lastSwap.put(charFromCaf, new HashSet<>());
            }
            if(i != indexOfFirstChar && !lastSwap.get(charFromCaf).contains(DraftCharToDicCaf.get(listOfBasesForCandidates.get(i)).getCharacter())) {//не пущу его в лист кандидатов, если он уже с этим символом менялся местами! - дописать эту идею здесь!
                listOfCandidates.add(DraftCharToDicCaf.get(listOfBasesForCandidates.get(i)));
            }
        }
        listOfCandidates.sort((o1, o2) -> {
            int d = DraftCharToDicCaf.get(firstPartOfSwapBase).getFrequency();
            return Math.abs(o1.getFrequency() - d) - Math.abs(o2.getFrequency() - d);
        });
        for (CharAndFrequency candidate : listOfCandidates) {
            if(SpellChecker.patternMatchingChecker(candidate.getCharacter(), pattern)) {
                secondPartOfSwapBase = getCharByCaf(candidate, DraftCharToDicCaf);
                char charFromCaf = DraftCharToDicCaf.get(firstPartOfSwapBase).getCharacter();
                lastSwap.get(charFromCaf).add(DraftCharToDicCaf.get(secondPartOfSwapBase).getCharacter());
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
