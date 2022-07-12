package ru.javarush.cryptoanaliser.parfenov.commands;

import ru.javarush.cryptoanaliser.parfenov.entity.Result;

public class Decrypt implements Action{
    public static void printIt() {
        System.out.println("Decrypt is here!");
    }
    public Result execute(String[] args) {
        throw new UnsupportedOperationException();
    }
}
