package ru.javarush.cryptoanalyser.parfenov.commands;

import ru.javarush.cryptoanalyser.parfenov.constants.Alphabet;
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

public abstract class AbstractCrypt {
    public Result getResult(Path in, Path out, int key) {
        try (BufferedReader reader = new BufferedReader(new FileReader(in.toFile()));
             BufferedWriter writer = new BufferedWriter(new FileWriter(out.toFile()))) {
            int character;
            while ((character = reader.read()) != -1) {
                character = Character.toLowerCase(character);//******************!!!!!!!!!!!!!!!!!!!!!!
                if ((character = encrypting(character, key)) != -1) {
                    writer.write(character);
                }
            }
        } catch (IOException e) {
            throw new ApplicationException("It's a problem with your file", e);
        }
        return new Result(ResultCode.OK, "Done");
    }




    public int encrypting(int character, int key) {
        //Character ch = (char)character;
        //String alphabet = Alphabet.FULL_ALPHABET;
        String alphabet = Alphabet.TINY_ALPHA;
        int a = alphabet.indexOf((char)character);
        //if(a == -1) System.out.println((char) character);
        if(a == -1) return a;
        int b = Math.floorMod((a + (key % alphabet.length())), alphabet.length());
        return (int) alphabet.charAt(b);
    }

    public Result getResult(Path in, Path out, StatCollector inStats, StatCollector dicStats) {
        //TreeMap<CharAndFrequency, CharAndFrequency> encCharToDicChar = inStats.getMapForDraft(dicStats.getCharsFreq());
        //TreeMap<Character, CharAndFrequency> encCharToDicCharShorted = inStats.getMapForDraftShorted(dicStats.getCharsFreq());
        LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf = inStats.getDraftCharToDicCaf(dicStats.getCharsFreq());


        int bufferSize = 800;
        boolean checked = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(in.toFile()));
        BufferedWriter writer = new BufferedWriter(new FileWriter(out.toFile()))) {
            reader.mark(1);
            int countToStop = 0;
            while(!checked && countToStop < 20) {
                System.out.println(countToStop);
                countToStop++;
                int counter = 0;
                int character;
                StringBuilder buffer = new StringBuilder();
                while(reader.ready() && counter++ < bufferSize) {
                    character = reader.read();
                    if ((character = encrypting(character, DraftCharToDicCaf)) != -1) {
                        buffer.append((char)character);
                    }
                }
                reader.reset();
                checked = tableOfCorrespondingCharsCorrector(buffer, DraftCharToDicCaf);
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
    private boolean tableOfCorrespondingCharsCorrector(StringBuilder buffer, LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf) {
        boolean checked = true;
        int index = SpellChecker.getIndexOfMatching(buffer, Patterns.INCORRECT_LONELY_LETTER);
        if(index != -1) {
            checked = false;
            //Character firstPartOfSwapBase = buffer.charAt(index + 1);
            Character firstPartOfSwapBase = getBaseOfCafByChar(buffer.charAt(index + 1), DraftCharToDicCaf);
            Character secondPartOfSwapBase = getSecondPartOfSwapBase(firstPartOfSwapBase, DraftCharToDicCaf, Patterns.VALID_LONELY_LETTER);
            char firstCafChar = DraftCharToDicCaf.get(firstPartOfSwapBase).getCharacter();
            char secondCafChar = DraftCharToDicCaf.get(secondPartOfSwapBase).getCharacter();
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
            if(SpellChecker.patternMatchingChecker(candidate.getCharacter(), pattern)) {
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
