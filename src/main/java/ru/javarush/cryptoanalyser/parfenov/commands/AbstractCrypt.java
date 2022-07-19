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

public abstract class AbstractCrypt {//здесь еще можно было попробовать объединить поведение brute и statistics, но не успел
    public Result getResult(Path in, Path out, int key, String alphabet, boolean toLower) {
        try (BufferedReader reader = new BufferedReader(new FileReader(in.toFile()));
             BufferedWriter writer = new BufferedWriter(new FileWriter(out.toFile()))) {
            int character;
            while ((character = reader.read()) != -1) {
                if(toLower) {
                    character = Character.toLowerCase(character);
                }
                if ((character = encrypting(character, key, alphabet)) != -1) {
                    writer.write(character);
                }
            }
        } catch (IOException e) {
            throw new ApplicationException("It's a problem with your file", e);
        }
        return new Result(ResultCode.OK, "Done");
    }
    public int encrypting(int character, int key, String alphabet) {
        int a = alphabet.indexOf((char)character);
        if(a == -1) return a;
        int b = Math.floorMod((a + (key % alphabet.length())), alphabet.length());
        return alphabet.charAt(b);
    }
}
