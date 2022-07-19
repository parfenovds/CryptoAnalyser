package ru.javarush.cryptoanalyser.parfenov.commands;

import ru.javarush.cryptoanalyser.parfenov.arguments.ArgumentTypes;
import ru.javarush.cryptoanalyser.parfenov.entity.Result;

import java.util.Map;

public interface Action {
    Result execute(Map<ArgumentTypes, Object> arguments);
}
