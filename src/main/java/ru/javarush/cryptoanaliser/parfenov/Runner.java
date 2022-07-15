package ru.javarush.cryptoanaliser.parfenov;

import ru.javarush.cryptoanaliser.parfenov.app.Application;
import ru.javarush.cryptoanaliser.parfenov.commands.BruteForceDecrypt;
import ru.javarush.cryptoanaliser.parfenov.controller.Actions;
import ru.javarush.cryptoanaliser.parfenov.controller.MainController;
import ru.javarush.cryptoanaliser.parfenov.entity.Result;

import java.util.Arrays;

public class Runner {
    public static void main(String[] args) {
        long m = System.currentTimeMillis();
        System.out.println();
        MainController mainController = new MainController();
        Application application = new Application(mainController);
        Result result = application.run(args);
        System.out.println(result);
        System.out.println(System.currentTimeMillis() - m);
    }
}
