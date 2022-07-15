package ru.javarush.cryptoanalyser.parfenov.controller;

import ru.javarush.cryptoanalyser.parfenov.commands.*;

public enum Actions {
    ENCRYPT(new Encrypt()),
    DECRYPT(new Decrypt()),
    BRUTE(new BruteForceDecrypt()),
    STATISTICS(new StatisticDecrypt());

    private final Action action;

    Actions(Action action) {
        this.action = action;
    }

    public static Action find(String command) {
        return Actions.valueOf(command.toUpperCase()).action;
    }
}
