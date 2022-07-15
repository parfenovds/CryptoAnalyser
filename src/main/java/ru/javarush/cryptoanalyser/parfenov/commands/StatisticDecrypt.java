package ru.javarush.cryptoanalyser.parfenov.commands;

import ru.javarush.cryptoanalyser.parfenov.arguments.ArgumentTypes;
import ru.javarush.cryptoanalyser.parfenov.entity.Result;
import ru.javarush.cryptoanalyser.parfenov.entity.ResultCode;
import ru.javarush.cryptoanalyser.parfenov.statCollecting.StatisticsCollector;

import java.nio.file.Path;
import java.util.Map;

public class StatisticDecrypt implements Action {
    public static void printIt() {
        System.out.println("StatisticDecrypt is here!");
    }
    private final ArgumentTypes[] argumentTypes = { ArgumentTypes.INPUT_FILE };
    public Result execute(Map<ArgumentTypes, Object> arguments) {
        StatisticsCollector statisticsCollector = new StatisticsCollector();
        Path path = (Path) arguments.get(ArgumentTypes.INPUT_FILE);
        statisticsCollector.getStatistics(path);
        return new Result(ResultCode.OK, "Done");
    }
//    private void getStatisticsForText(Path path) {
//        StatisticsCollector statisticsCollector = new StatisticsCollector(path);
//    }
    public ArgumentTypes[] getArgumentTypes() {
        return argumentTypes;
    }
    @Override
    public int getArgumentAmount() {
        return argumentTypes.length;
    }
}
