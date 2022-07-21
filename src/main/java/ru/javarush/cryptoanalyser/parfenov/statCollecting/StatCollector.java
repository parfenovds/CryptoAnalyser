package ru.javarush.cryptoanalyser.parfenov.statCollecting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class StatCollector {
    private final TreeMap<Character, CharAndFrequency> charsFreqCollect = new TreeMap<>();
    private TreeMap<CharAndFrequency, Character> charsFreq = new TreeMap<>();
    private int amountOfCharsInText;
    public void getStatistics(Path path) {
        try(BufferedReader reader = new BufferedReader(new FileReader(path.toFile()))) {
            while(reader.ready()) {
                Character character = (char) reader.read();
                charsFreqCollect.putIfAbsent(character, new CharAndFrequency(character));
                charsFreqCollect.get(character).addFrequency();
            }
            setPercentages(charsFreqCollect);
            charsFreq = getReversedMap(charsFreqCollect);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setPercentages(TreeMap<Character, CharAndFrequency> charsFreqCollect) {
        for (Character key : charsFreqCollect.keySet()) {
            charsFreqCollect.get(key).setPercentageByAmountOfCharsInText(amountOfCharsInText);
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
    public LinkedHashMap<Character, CharAndFrequency> getDraftCharToDicCaf(TreeMap<CharAndFrequency, Character> dicCharsFreq) {
        LinkedHashMap<Character, CharAndFrequency> result = new LinkedHashMap<>();
        Set<Map.Entry<CharAndFrequency, Character>> encEntrySet = this.charsFreq.entrySet();
        Set<Map.Entry<CharAndFrequency, Character>> dicEntrySet = dicCharsFreq.entrySet();
        Iterator<Map.Entry<CharAndFrequency, Character>> iteratorEnc = encEntrySet.iterator();
        //Iterator<Map.Entry<CharAndFrequency, Character>> iteratorDic = dicEntrySet.iterator();
        while(iteratorEnc.hasNext()) {
            //result.put(iteratorEnc.next().getValue(), iteratorDic.next().getKey());
            Map.Entry<CharAndFrequency, Character> thisEntry = iteratorEnc.next();
            CharAndFrequency caf = thisEntry.getKey();
            double percentage = caf.getPercentage();
            result.put(thisEntry.getValue(), getCloseCafByPercentageAndRemoveItFromSet(percentage, dicEntrySet));
        }
        return result;
    }

    private CharAndFrequency getCloseCafByPercentageAndRemoveItFromSet(double percentage, Set<Map.Entry<CharAndFrequency, Character>> dicEntrySet) {
        Iterator<Map.Entry<CharAndFrequency, Character>> iteratorDic = dicEntrySet.iterator();
        //CharAndFrequency closestCaf = iteratorDic.next().getKey();
        Map.Entry<CharAndFrequency, Character> closestEntry = iteratorDic.next();
        double minDelta = getDelta(percentage, closestEntry.getKey());
        while(iteratorDic.hasNext()) {
            Map.Entry<CharAndFrequency, Character> candidate = iteratorDic.next();
            double deltaToCheck = getDelta(percentage, closestEntry.getKey());
            //CharAndFrequency candidate = iteratorDic.next().getKey();
            //double deltaToCheck = getDelta(percentage, candidate);
            if(deltaToCheck < minDelta) {
                closestEntry = candidate;
                minDelta = deltaToCheck;
            }
        }
        dicEntrySet.remove(closestEntry);
        return closestEntry.getKey();
    }

    private double getDelta(double percentage, CharAndFrequency candidate) {
        return Math.abs(candidate.getPercentage() - percentage);
    }

    public TreeMap<CharAndFrequency, Character> getCharsFreq() {
        return charsFreq;
    }
}
