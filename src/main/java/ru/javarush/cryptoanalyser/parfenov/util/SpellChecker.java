package ru.javarush.cryptoanalyser.parfenov.util;

import ru.javarush.cryptoanalyser.parfenov.constants.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpellChecker {



    //    public static int lengthBetweenSpacesCounter(char[] probe) {
//        int result = 0;
//        int counter = 0;
//        for (int i = 0; i < probe.length; i++) {
//            if(!Character.isWhitespace(probe[i])) {
//                counter++;
//            } else {
//                counter = 0;
//            }
//            if(counter > 25) {
//                result++;
//                counter = 0;
//            }
//        }
//        return result;
//    }
//    public static int second(char[] probe) {
//        int result = 0;
//        for (int i = 0; i < probe.length; i++) {
//            if()
//        }
//    }


    public static int patternChecking(StringBuilder probe) {
        int result = 0;
        for (String regex : Patterns.regexList) {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(probe);
            while(matcher.find()) {
                result++;
            }
        }
        return result;
    }
}
