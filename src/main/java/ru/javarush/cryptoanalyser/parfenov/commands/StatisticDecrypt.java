package ru.javarush.cryptoanalyser.parfenov.commands;

import ru.javarush.cryptoanalyser.parfenov.arguments.ArgumentTypes;
import ru.javarush.cryptoanalyser.parfenov.entity.Result;
import ru.javarush.cryptoanalyser.parfenov.entity.ResultCode;
import ru.javarush.cryptoanalyser.parfenov.exception.ApplicationException;
import ru.javarush.cryptoanalyser.parfenov.statCollecting.CharAndFrequency;
import ru.javarush.cryptoanalyser.parfenov.statCollecting.StatCollector;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class StatisticDecrypt extends AbstractCrypt implements Action {
    private final ArgumentTypes[] argumentTypes = { ArgumentTypes.INPUT_FILE, ArgumentTypes.OUTPUT_FILE, ArgumentTypes.DICT_FILE };
    public Result execute(Map<ArgumentTypes, Object> arguments) {
        Path inputPath = (Path) arguments.get(ArgumentTypes.INPUT_FILE);
        Path outputPath = (Path) arguments.get(ArgumentTypes.OUTPUT_FILE);
        Path dictPath = (Path) arguments.get(ArgumentTypes.DICT_FILE);
        StatCollector inputStatCollector = new StatCollector();
        StatCollector dicStatCollector = new StatCollector();
        inputStatCollector.getStatistics(inputPath);
        dicStatCollector.getStatistics(dictPath);
        return getResult(inputPath, outputPath, inputStatCollector, dicStatCollector);
    }

    public Result getResult(Path in, Path out, StatCollector inStats, StatCollector dicStats) {
        LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf = inStats.getDraftCharToDicCaf(dicStats.getCharsFreq());
        int bufferSize = 15000;
        boolean checked = false;
        try (BufferedReader reader = new BufferedReader(new FileReader(in.toFile()));
             BufferedWriter writer = new BufferedWriter(new FileWriter(out.toFile()))) {
            int character;
            while ((character = reader.read()) != -1) {
                if ((character = encrypting(character, DraftCharToDicCaf)) != -1) {
                    writer.write(character);
                }
            }
        } catch (IOException e) {
            throw new ApplicationException("It's a problem with your file", e);
        }

        return new Result(ResultCode.OK, "Done");
    }

    public int encrypting(int character, LinkedHashMap<Character, CharAndFrequency> DraftCharToDicCaf) {
        return DraftCharToDicCaf.get((char) character).getCharacter();
    }

    @Override
    public ArgumentTypes[] getArgumentTypes() {
        return argumentTypes;
    }
    @Override
    public int getArgumentAmount() {
        return argumentTypes.length;
    }}
