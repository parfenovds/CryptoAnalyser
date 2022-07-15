package ru.javarush.cryptoanalyser.parfenov.statCollecting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class StatisticsCollector {
    private TreeMap<Character, CharAndFrequency> charsFreqCollect = new TreeMap<>();
    private TreeMap<CharAndFrequency, Character> charsFreqUsing = new TreeMap<>();
    //private TreeMap<Character, CharAndFrequency> prepsForStatistics = new TreeMap<>();
    private TreeMap<Character, TreeMap<Character, CharAndFrequency>> nextCharsFreqCollect = new TreeMap<>();
    private TreeMap<Character, TreeMap<CharAndFrequency, Character>> nextCharsFreqUsing = new TreeMap<>();
    private TreeMap<Character, TreeMap<Character, CharAndFrequency>> statisticsForPreviousChar = new TreeMap<>();

    public void getStatistics(Path path) {
//        prepsForStatisticsGenerator();
//        statisticTreeGenerator();
        try(BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            Character previousCharacter = null;
            while(reader.ready()) {
                Character character = (char) reader.read();
                charsFreqCollect.putIfAbsent(character, new CharAndFrequency(character));
                charsFreqCollect.get(character).addFrequency();

                if(previousCharacter != null) {
                    if(!nextCharsFreqCollect.containsKey(previousCharacter)) {
                        TreeMap<Character, CharAndFrequency> newEntryForStatistics = new TreeMap<>() {{
                            put(character, new CharAndFrequency(character));
                        }};
                        nextCharsFreqCollect.put(previousCharacter, newEntryForStatistics);
                    }
                    nextCharsFreqCollect.get(previousCharacter).putIfAbsent(character, new CharAndFrequency(character));
                    nextCharsFreqCollect.get(previousCharacter).get(character).addFrequency();
                }
                previousCharacter = character;

            }
            mapReversingConveyor(nextCharsFreqCollect);
            charsFreqUsing = reverseMap(charsFreqCollect);

            for (CharAndFrequency character : charsFreqUsing.keySet()) {
                System.out.println("\"" + character.getCharacter() + "\"" + " is " + character.getFrequency());
            }
            for (Character character : nextCharsFreqUsing.keySet()) {
                System.out.println("For " + character + ":");
                for (CharAndFrequency innerCh : nextCharsFreqUsing.get(character).keySet()) {
                    System.out.println(innerCh.getCharacter() + " = " +
                            innerCh.getFrequency());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
//TODO: generalize this method (it should be used not for one map only)
    private void mapReversingConveyor(TreeMap<Character, TreeMap<Character, CharAndFrequency>> containerToExtractAndReverseMaps) {
        for (Character key : containerToExtractAndReverseMaps.keySet()) {
            nextCharsFreqUsing.put(key, reverseMap(containerToExtractAndReverseMaps.get(key)));
        }
    }

//TODO: find out how to generalize it with <K,V> or something like that
    private TreeMap<CharAndFrequency, Character> reverseMap(TreeMap<Character, CharAndFrequency> mapToReverse) {
        TreeMap<CharAndFrequency, Character> reversedMap = new TreeMap<>();
        for(Map.Entry<Character, CharAndFrequency> entry : mapToReverse.entrySet()) {
            reversedMap.put(entry.getValue(), entry.getKey());
        }
        return reversedMap;
    }

//    private void statisticTreeGenerator() {
//        for (int i = 0; i < Alphabet.TINY_ALPHA.length(); i++) {
//            CharAndFrequency charAndFrequency = new CharAndFrequency(Alphabet.TINY_ALPHA.charAt(i));
//            statisticsTree.put(charAndFrequency, statisticsForOneChar);
//        }
//    }
//
//    private void prepsForStatisticsGenerator() {
//        for (int i = 0; i < Alphabet.TINY_ALPHA.length(); i++) {
//            CharAndFrequency charAndFrequency = new CharAndFrequency(Alphabet.TINY_ALPHA.charAt(i));
//            statisticsForOneChar.add(charAndFrequency);
//        }
//    }
}
