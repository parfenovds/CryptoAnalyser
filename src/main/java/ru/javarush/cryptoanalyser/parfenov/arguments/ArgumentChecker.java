package ru.javarush.cryptoanalyser.parfenov.arguments;

import ru.javarush.cryptoanalyser.parfenov.commands.Action;
import ru.javarush.cryptoanalyser.parfenov.exception.ApplicationException;

public class ArgumentChecker {
    public int getAmountOfArguments(String[] args) {
        int amountOfArguments = args.length;
        if(amountOfArguments == 0) {
            throw new ApplicationException("You did not add any arguments! Please, try again.");
        }
        return amountOfArguments;
    }
    public void hasExactAmountOfArguments(Action action, int amountOfArguments) {
        int commandArgument = 1;
        if(action.getArgumentAmount() != (amountOfArguments - commandArgument)) {
            throw new ApplicationException("You should have put " +
                    (action.getArgumentAmount() + commandArgument) +
                    " argument(s) but you've put " +
                    amountOfArguments + " instead. Please, try again.");
        }
    }
}
