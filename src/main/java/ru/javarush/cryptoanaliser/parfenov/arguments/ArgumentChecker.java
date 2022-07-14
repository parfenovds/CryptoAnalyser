package ru.javarush.cryptoanaliser.parfenov.arguments;

import ru.javarush.cryptoanaliser.parfenov.commands.Action;
import ru.javarush.cryptoanaliser.parfenov.exception.ApplicationException;

public class ArgumentChecker {
    public int getAmountOfArguments(String[] args) {
        int amountOfArguments = args.length;
        if(amountOfArguments == 0) {
            throw new ApplicationException("You did not add any arguments! Please, try again.");
        }
        return amountOfArguments;
    }
    public boolean hasExactAmountOfArguments(Action action, int amountOfArguments) {
        int commandArgument = 1;
        if(action.getArgumentAmount() != (amountOfArguments - commandArgument)) {
            throw new ApplicationException("You should have put " +
                    (action.getArgumentAmount() + commandArgument) +
                    " argument(s) but you've put " +
                    amountOfArguments + " instead. Please, try again.");
        }
        return true;
    }
}
