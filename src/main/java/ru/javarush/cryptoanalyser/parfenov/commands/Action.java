package ru.javarush.cryptoanalyser.parfenov.commands;

import ru.javarush.cryptoanalyser.parfenov.arguments.ArgumentTypes;
import ru.javarush.cryptoanalyser.parfenov.entity.Result;

import java.util.Map;

public interface Action {
    ArgumentTypes[] argumentTypes = new ArgumentTypes[0];
    Result execute(Map<ArgumentTypes, Object> arguments);
    ArgumentTypes[] getArgumentTypes();
    int getArgumentAmount();
}
