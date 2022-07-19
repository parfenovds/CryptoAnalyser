package ru.javarush.cryptoanalyser.parfenov.util;

import ru.javarush.cryptoanalyser.parfenov.constants.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpellChecker {
    public static int getAmountOfErrorsInProbe(StringBuilder probe) {
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
}
