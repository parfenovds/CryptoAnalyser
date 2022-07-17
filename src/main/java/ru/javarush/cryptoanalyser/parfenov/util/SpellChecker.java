package ru.javarush.cryptoanalyser.parfenov.util;

import ru.javarush.cryptoanalyser.parfenov.constants.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpellChecker {
    public static int patternMatchingCounter(StringBuilder probe) {
        int result = 0;
        for (String regex : Patterns.regexBruteList) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(probe);
            while(matcher.find()) {
                result++;
            }
        }
        return result;
    }
    public static boolean patternMatchingChecker(StringBuilder probe, String regex) {
        boolean checked = false;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(probe);
        if(matcher.find()) {
            checked = true;
        }
        return checked;
    }
    public static boolean patternMatchingChecker(char probe, String regex) {
        String strProbe = "" + probe;
        boolean checked = false;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(strProbe);
        if(matcher.find()) {
            checked = true;
        }
        return checked;
    }
    public static int getIndexOfMatching(StringBuilder probe, String regex) {
        int index = -1;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(probe);
        if(matcher.find()) {
            index = matcher.start();
        }
        return index;
    }
}
