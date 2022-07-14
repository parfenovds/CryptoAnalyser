package ru.javarush.cryptoanaliser.parfenov.commands;

import ru.javarush.cryptoanaliser.parfenov.arguments.ArgumentTypes;
import ru.javarush.cryptoanaliser.parfenov.entity.Result;

import java.util.Map;

public interface Action {
    ArgumentTypes[] argumentTypes = new ArgumentTypes[0];
    Result execute(Map<ArgumentTypes, Object> arguments);
    ArgumentTypes[] getArgumentTypes();
    int getArgumentAmount();
}
