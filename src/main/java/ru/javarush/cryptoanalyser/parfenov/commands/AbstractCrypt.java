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
        TreeMap<Character, CharAndFrequency> encCharToDicChar = inStats.getMapForDraft(dicStats.getCharsFreq());

        int bufferSize = 200;
        boolean checked = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(in.toFile()));
        BufferedWriter writer = new BufferedWriter(new FileWriter(out.toFile()))) {
            reader.mark(1);
            while(!checked) {
                int counter = 0;
                int character;
                StringBuilder buffer = new StringBuilder();
                while(reader.ready() && counter++ < bufferSize) {
                    character = reader.read();
                    if ((character = encrypting(character, encCharToDicChar)) != -1) {
                        buffer.append((char)character);
                    }
                }
                reader.reset();
                checked = tableOfCorrespondingCharsCorrector(buffer, encCharToDicChar);
            }
            int character;
            while ((character = reader.read()) != -1) {
                //character = Character.toLowerCase(character);//******************!!!!!!!!!!!!!!!!!!!!!!
                if ((character = encrypting(character, encCharToDicChar)) != -1) {
                    writer.write(character);
                }
            }
        } catch (IOException e) {
            throw new ApplicationException("It's a problem with your file", e);
        }

        return new Result(ResultCode.OK, "Done");
    }

    public int encrypting(int character, TreeMap<Character, CharAndFrequency> encCharToDicChar) {
        System.out.println(encCharToDicChar.toString());
        System.out.println("!!!" + (char)character);
        return encCharToDicChar.get((char) character).getCharacter();
    }
    private boolean tableOfCorrespondingCharsCorrector(StringBuilder buffer, TreeMap<Character, CharAndFrequency> encCharToDicChar) {
        boolean checked = true;
        int index = SpellChecker.getIndexOfMatching(buffer, Patterns.INCORRECT_LONELY_LETTER);
        if(index != -1) {
            checked = false;
            CharAndFrequency firstPartOfSwapBase = new CharAndFrequency(buffer.charAt(index + 1));
            CharAndFrequency secondPartOfSwapBase = getSecondPartOfSwapBase(firstPartOfSwapBase, encCharToDicChar, Patterns.VALID_LONELY_LETTER);
            encCharToDicChar.get(firstPartOfSwapBase).setCharacter(secondPartOfSwapBase.getCharacter());
            encCharToDicChar.get(secondPartOfSwapBase).setCharacter(firstPartOfSwapBase.getCharacter());
        }
        return checked;
    }
    private CharAndFrequency getSecondPartOfSwapBase(CharAndFrequency firstPartOfSwapBase, TreeMap<Character, CharAndFrequency> encCharToDicChar, String pattern) {
        ArrayList<CharAndFrequency> listOfCandidates = new ArrayList<>();
        CharAndFrequency secondPartOfSwapBase = firstPartOfSwapBase;
        SortedMap<CharAndFrequency, CharAndFrequency> charStatHead = encCharToDicChar.headMap(firstPartOfSwapBase);
        SortedMap<CharAndFrequency, CharAndFrequency> charStatTail = encCharToDicChar.tailMap(firstPartOfSwapBase);
        charStatTail.remove(firstPartOfSwapBase);
        for (int i = 0; i < 4; i++) {//TODO make a good method instead
            if(!charStatHead.isEmpty()) {
                listOfCandidates.add(charStatHead.lastKey());
                charStatHead.remove(charStatHead.lastKey());
            }
            if(!charStatTail.isEmpty()) {
                listOfCandidates.add(charStatTail.firstKey());
                charStatTail.remove(charStatTail.firstKey());
            }
        }
        listOfCandidates.sort((o1, o2) -> {
            int d = firstPartOfSwapBase.getFrequency();
            return Math.abs(o1.getFrequency() - d) - Math.abs(o2.getFrequency() - d);
        });
        for (CharAndFrequency candidate : listOfCandidates) {
            if(SpellChecker.patternMatchingChecker(candidate.getCharacter(), pattern)) {
                secondPartOfSwapBase = candidate;
                break;
            }
        }
        return secondPartOfSwapBase;
    }
}
