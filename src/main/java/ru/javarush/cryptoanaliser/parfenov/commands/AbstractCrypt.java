package ru.javarush.cryptoanaliser.parfenov.commands;

import ru.javarush.cryptoanaliser.parfenov.constants.Alphabet;
import ru.javarush.cryptoanaliser.parfenov.entity.Result;
import ru.javarush.cryptoanaliser.parfenov.entity.ResultCode;
import ru.javarush.cryptoanaliser.parfenov.exception.ApplicationException;

import java.io.*;
import java.nio.file.Path;

public abstract class AbstractCrypt {
    public Result getResult(Path in, Path out, int key) {
        try (BufferedReader reader = new BufferedReader(new FileReader(in.toFile()));
             BufferedWriter writer = new BufferedWriter(new FileWriter(out.toFile()))) {
            int character;
            while ((character = reader.read()) != -1) {
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
        int a = Alphabet.FULL_ALPHABET.indexOf((char)character);
        //if(a == -1) System.out.println((char) character);
        if(a == -1) return a;
        int b = Math.floorMod((a + (key % Alphabet.FULL_ALPHABET.length())), Alphabet.FULL_ALPHABET.length());
        return (int) Alphabet.FULL_ALPHABET.charAt(b);
    }
}
