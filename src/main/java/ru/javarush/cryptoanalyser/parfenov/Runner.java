package ru.javarush.cryptoanalyser.parfenov;

import ru.javarush.cryptoanalyser.parfenov.app.Application;
import ru.javarush.cryptoanalyser.parfenov.controller.MainController;
import ru.javarush.cryptoanalyser.parfenov.entity.Result;

public class Runner {
    public static void main(String[] args) {
        long m = System.currentTimeMillis();
//        System.out.println();
//        MainController mainController = new MainController();
//        Application application = new Application(mainController);
//        Result result = application.run(args);
//        System.out.println(result);
        System.out.println(System.currentTimeMillis() - m);
    }
}
