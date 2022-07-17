package ru.javarush.cryptoanalyser.parfenov.statCollecting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class StatCollector {
    private TreeMap<Character, CharAndFrequency> charsFreqCollect = new TreeMap<>();
    private TreeMap<CharAndFrequency, Character> charsFreq = new TreeMap<>();
    private TreeMap<Character, TreeMap<Character, CharAndFrequency>> nextCharsFreqCollect = new TreeMap<>();
    private TreeMap<Character, TreeMap<CharAndFrequency, Character>> nextCharsFreq = new TreeMap<>();
    private TreeMap<Character, TreeMap<Character, CharAndFrequency>> prevCharsFreqCollect = new TreeMap<>();
    private TreeMap<Character, TreeMap<CharAndFrequency, Character>> prevCharsFreq = new TreeMap<>();

    public void getStatistics(Path path) {
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
            mapReversingConveyor(nextCharsFreqCollect, nextCharsFreq);
            mapReversingConveyor(prevCharsFreqCollect, prevCharsFreq);
            charsFreq = getReversedMap(charsFreqCollect);

            System.out.println("*".repeat(20) + "mainStatistics");
            for (CharAndFrequency character : charsFreq.keySet()) {
                System.out.println("\"" + character.getCharacter() + "\"" + " is " + character.getFrequency());
            }
            System.out.println("*".repeat(20) + "nextStatistics:");
            for (Character character : nextCharsFreq.keySet()) {
                System.out.println("For " + character + ":");
                for (CharAndFrequency innerCh : nextCharsFreq.get(character).keySet()) {
                    System.out.println(innerCh.getCharacter() + " = " +
                            innerCh.getFrequency());
                }
            }
            System.out.println("*".repeat(20) + "previousStatistics:");
            for (Character character : prevCharsFreq.keySet()) {
                System.out.println("For " + character + ":");
                for (CharAndFrequency innerCh : prevCharsFreq.get(character).keySet()) {
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
            mapToPutReversedMaps.put(key, getReversedMap( mapToExtractAndReverseMaps.get(key)));
        }
    }

//TODO: find out how to generalize it with <K,V> or something like that
    private TreeMap<CharAndFrequency, Character> getReversedMap(TreeMap<Character, CharAndFrequency> mapToReverse) {
        TreeMap<CharAndFrequency, Character> reversedMap = new TreeMap<>();
        for(Map.Entry<Character, CharAndFrequency> entry : mapToReverse.entrySet()) {
            reversedMap.put(entry.getValue(), entry.getKey());
        }
        return reversedMap;
    }

    //TODO change char - CaF to CaF - CaF!!!!!!
    public TreeMap<CharAndFrequency, CharAndFrequency> getMapForDraft(TreeMap<CharAndFrequency, Character> dicCharsFreq) {
        TreeMap<CharAndFrequency, CharAndFrequency> result = new TreeMap<>();
        Set<Map.Entry<CharAndFrequency, Character>> encEntrySet = this.charsFreq.entrySet();
        Set<Map.Entry<CharAndFrequency, Character>> dicEntrySet = dicCharsFreq.entrySet();
        Iterator<Map.Entry<CharAndFrequency, Character>> iteratorEnc = encEntrySet.iterator();
        Iterator<Map.Entry<CharAndFrequency, Character>> iteratorDic = dicEntrySet.iterator();
        while(iteratorEnc.hasNext()) {
            result.put(iteratorEnc.next().getKey(), iteratorDic.next().getKey());
        }
        return result;
    }
    public TreeMap<Character, CharAndFrequency> getMapForDraftShorted(TreeMap<CharAndFrequency, Character> dicCharsFreq) {
        TreeMap<Character, CharAndFrequency> result = new TreeMap<>();
        Set<Map.Entry<CharAndFrequency, Character>> encEntrySet = this.charsFreq.entrySet();
        Set<Map.Entry<CharAndFrequency, Character>> dicEntrySet = dicCharsFreq.entrySet();
        Iterator<Map.Entry<CharAndFrequency, Character>> iteratorEnc = encEntrySet.iterator();
        Iterator<Map.Entry<CharAndFrequency, Character>> iteratorDic = dicEntrySet.iterator();
        while(iteratorEnc.hasNext()) {
            result.put(iteratorEnc.next().getValue(), iteratorDic.next().getKey());
        }
        return result;
    }
    public LinkedHashMap<Character, CharAndFrequency> getDraftCharToDicCaf(TreeMap<CharAndFrequency, Character> dicCharsFreq) {
        LinkedHashMap<Character, CharAndFrequency> result = new LinkedHashMap<>();
        Set<Map.Entry<CharAndFrequency, Character>> encEntrySet = this.charsFreq.entrySet();
        Set<Map.Entry<CharAndFrequency, Character>> dicEntrySet = dicCharsFreq.entrySet();
        Iterator<Map.Entry<CharAndFrequency, Character>> iteratorEnc = encEntrySet.iterator();
        Iterator<Map.Entry<CharAndFrequency, Character>> iteratorDic = dicEntrySet.iterator();
        while(iteratorEnc.hasNext()) {
            result.put(iteratorEnc.next().getValue(), iteratorDic.next().getKey());
        }
        return result;
    }

    public TreeMap<CharAndFrequency, Character> getCharsFreq() {
        return charsFreq;
    }

    public TreeMap<Character, TreeMap<CharAndFrequency, Character>> getNextCharsFreq() {
        return nextCharsFreq;
    }

    public TreeMap<Character, TreeMap<CharAndFrequency, Character>> getPrevCharsFreq() {
        return prevCharsFreq;
    }
}
