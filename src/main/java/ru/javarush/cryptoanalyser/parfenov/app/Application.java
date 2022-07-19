package ru.javarush.cryptoanalyser.parfenov.app;

import ru.javarush.cryptoanalyser.parfenov.arguments.ArgumentTypes;
import ru.javarush.cryptoanalyser.parfenov.entity.Result;
import ru.javarush.cryptoanalyser.parfenov.controller.MainController;

import java.util.Map;

public class Application {
    private final MainController mainController;
    public Application(MainController mainController) {
        this.mainController = mainController;
    }
    public Result run(Map<ArgumentTypes, Object> arguments) {
        return mainController.execute(arguments);
    }
}
