package ru.javarush.cryptoanalyser.parfenov.commands;

import ru.javarush.cryptoanalyser.parfenov.arguments.ArgumentTypes;
import ru.javarush.cryptoanalyser.parfenov.entity.Result;
import ru.javarush.cryptoanalyser.parfenov.entity.ResultCode;
import ru.javarush.cryptoanalyser.parfenov.statCollecting.StatCollector;

import java.nio.file.Path;
import java.util.Map;

public class StatisticDecrypt extends AbstractCrypt implements Action {
    public static void printIt() {
        System.out.println("StatisticDecrypt is here!");
    }
    private final ArgumentTypes[] argumentTypes = { ArgumentTypes.INPUT_FILE, ArgumentTypes.OUTPUT_FILE, ArgumentTypes.DICT_FILE };
    public Result execute(Map<ArgumentTypes, Object> arguments) {
        Path inputPath = (Path) arguments.get(ArgumentTypes.INPUT_FILE);
        Path outputPath = (Path) arguments.get(ArgumentTypes.OUTPUT_FILE);
        Path dictPath = (Path) arguments.get(ArgumentTypes.DICT_FILE);
        StatCollector inputStatCollector = new StatCollector();
        StatCollector dicStatCollector = new StatCollector();
        inputStatCollector.getStatistics(inputPath);
        dicStatCollector.getStatistics(dictPath);
        getResult(inputPath, outputPath, inputStatCollector, dicStatCollector);

        return new Result(ResultCode.OK, "Done");
    }

    @Override
    public ArgumentTypes[] getArgumentTypes() {
        return argumentTypes;
    }
    @Override
    public int getArgumentAmount() {
        return argumentTypes.length;
    }
}
