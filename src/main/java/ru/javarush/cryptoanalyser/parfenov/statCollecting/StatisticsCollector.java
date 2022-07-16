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
    private TreeMap<Character, TreeMap<Character, CharAndFrequency>> prevCharsFreqCollect = new TreeMap<>();
    private TreeMap<Character, TreeMap<CharAndFrequency, Character>> prevCharsFreqUsing = new TreeMap<>();

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
                    statisticCollector(previousCharacter, character, nextCharsFreqCollect);
                    statisticCollector(character, previousCharacter, prevCharsFreqCollect);
                }
                previousCharacter = character;

            }
            mapReversingConveyor(nextCharsFreqCollect, nextCharsFreqUsing);
            mapReversingConveyor(prevCharsFreqCollect, prevCharsFreqUsing);
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
            System.out.println("*".repeat(20));
            for (Character character : prevCharsFreqUsing.keySet()) {
                System.out.println("For " + character + ":");
                for (CharAndFrequency innerCh : prevCharsFreqUsing.get(character).keySet()) {
                    System.out.println(innerCh.getCharacter() + " = " +
                            innerCh.getFrequency());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void statisticCollector(Character previousCharacter, Character character,
                                    TreeMap<Character, TreeMap<Character, CharAndFrequency>> CharsFreqCollect) {
        if(!CharsFreqCollect.containsKey(previousCharacter)) {
            TreeMap<Character, CharAndFrequency> newEntryForStatistics = new TreeMap<>() {{
                put(character, new CharAndFrequency(character));
            }};
            CharsFreqCollect.put(previousCharacter, newEntryForStatistics);
        }
        CharsFreqCollect.get(previousCharacter).putIfAbsent(character, new CharAndFrequency(character));
        CharsFreqCollect.get(previousCharacter).get(character).addFrequency();
    }

    private void mapReversingConveyor(TreeMap<Character, TreeMap<Character, CharAndFrequency>> mapToExtractAndReverseMaps,
                                      TreeMap<Character, TreeMap<CharAndFrequency, Character>> mapToPutReversedMaps) {
        for (Character key :  mapToExtractAndReverseMaps.keySet()) {
            mapToPutReversedMaps.put(key, reverseMap( mapToExtractAndReverseMaps.get(key)));
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
}
