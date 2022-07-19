package ru.javarush.cryptoanalyser.parfenov;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Spec;
import ru.javarush.cryptoanalyser.parfenov.app.Application;
import ru.javarush.cryptoanalyser.parfenov.arguments.ArgumentExtractor;
import ru.javarush.cryptoanalyser.parfenov.arguments.ArgumentTypes;
import ru.javarush.cryptoanalyser.parfenov.commands.Action;
import ru.javarush.cryptoanalyser.parfenov.constants.Alphabet;
import ru.javarush.cryptoanalyser.parfenov.controller.MainController;
import ru.javarush.cryptoanalyser.parfenov.entity.Result;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.io.File;
import java.util.Map;

@Command(name = "cypher", subcommands = {CommandLine.HelpCommand.class },
        description = "Caesar cypher command")
public class PicocliRunner implements Runnable {
    private final Map<ArgumentTypes, Object> arguments = new HashMap<>();
    @Spec CommandSpec spec;
    @Command(name = "encrypt", description = "Encrypt from file to file using key")
    void encrypt(
            @Parameters(paramLabel = "<source file>", description = "source file with text to encrypt") String src,
            @Parameters(paramLabel = "<dest file>", description = "dest file which should have encrypted text") String dest,
            @Parameters(paramLabel = "<key>", description = "key for encryption") int key,
            @Option(names = {"-a", "--alphabet"}, paramLabel = "ALPHABET", description = "your custom alphabet") String alphabet,
            @Option(names = {"-t", "--tinyAlphabet"}, paramLabel = "TINY_ALPHABET", description = "(has priority over the --alphabet flag) if you want to use tiny alphabet (good for stat analysis)") boolean tinyAlphabet,
            @Option(names = {"-l", "--toLowerCase"}, description = "your custom alphabet") boolean toLowerCase) {
        if(tinyAlphabet) {
            alphabet = Alphabet.TINY_ALPHA;
        } else if(alphabet == null) {
            alphabet = Alphabet.FULL_ALPHABET;
        }
        ArgumentExtractor argumentExtractor = new ArgumentExtractor();
        Action action = argumentExtractor.extractCommand("encrypt");
        arguments.put(ArgumentTypes.COMMAND, action);
        arguments.put(ArgumentTypes.INPUT_FILE, argumentExtractor.extractFileName(src));
        arguments.put(ArgumentTypes.OUTPUT_FILE, argumentExtractor.extractFileName(dest));
        arguments.put(ArgumentTypes.KEY, key);
        arguments.put(ArgumentTypes.ALPHABET, alphabet);
        arguments.put(ArgumentTypes.TO_LOWER_CASE, toLowerCase);
        MainController mainController = new MainController();
        Application application = new Application(mainController);
        Result result = application.run(arguments);
        // TODO
    }

    @Command(name = "decrypt", description = "Decrypt from file to file using key") // |3|
    void decrypt(
            @Parameters(paramLabel = "<source file>", description = "source file with encrypted text") String src,
            @Parameters(paramLabel = "<dest file>", description = "dest file which should have decrypted text") String dest,
            @Parameters(paramLabel = "<key>", description = "key for encryption") int key,
            @Option(names = {"-a", "--alphabet"}, paramLabel = "ALPHABET", description = "your custom alphabet") String alphabet) {
        if(alphabet == null) {
            alphabet = Alphabet.FULL_ALPHABET;
        }
        ArgumentExtractor argumentExtractor = new ArgumentExtractor();
        Action action = argumentExtractor.extractCommand("decrypt");
        arguments.put(ArgumentTypes.COMMAND, action);
        arguments.put(ArgumentTypes.INPUT_FILE, argumentExtractor.extractFileName(src));
        arguments.put(ArgumentTypes.OUTPUT_FILE, argumentExtractor.extractFileName(dest));
        arguments.put(ArgumentTypes.KEY, key);
        arguments.put(ArgumentTypes.ALPHABET, alphabet);
        arguments.put(ArgumentTypes.TO_LOWER_CASE, false);
        MainController mainController = new MainController();
        Application application = new Application(mainController);
        Result result = application.run(arguments);
        // TODO
    }

    @Command(name = "brute", description = "Decrypt from file to file using brute force") // |3|
    void bruteForce(
            @Parameters(paramLabel = "<source file>", description = "source file with encrypted text") String src,
            @Parameters(paramLabel = "<dest file>", description = "dest file which should have decrypted text") String dest,
            @Option(names = {"-a", "--alphabet"}, paramLabel = "ALPHABET", description = "your custom alphabet") String alphabet) {
        if(alphabet == null) {
            alphabet = Alphabet.FULL_ALPHABET;
        }
        ArgumentExtractor argumentExtractor = new ArgumentExtractor();
        Action action = argumentExtractor.extractCommand("brute");
        arguments.put(ArgumentTypes.COMMAND, action);
        arguments.put(ArgumentTypes.INPUT_FILE, argumentExtractor.extractFileName(src));
        arguments.put(ArgumentTypes.OUTPUT_FILE, argumentExtractor.extractFileName(dest));
        arguments.put(ArgumentTypes.ALPHABET, alphabet);
        MainController mainController = new MainController();
        Application application = new Application(mainController);
        Result result = application.run(arguments);
        // TODO
    }

    @Command(name = "statistics", description = "Decrypt from file to file using statistical analysis") // |3|
    void statisticalDecrypt(
            @Parameters(paramLabel = "<source file>", description = "source file with encrypted text") String src,
            @Parameters(paramLabel = "<dest file>", description = "dest file which should have decrypted text") String dest,
            @Parameters(paramLabel = "<dict file>", description = "file with unencrypted representative text") String dict) {
        ArgumentExtractor argumentExtractor = new ArgumentExtractor();
        Action action = argumentExtractor.extractCommand("statistics");
        arguments.put(ArgumentTypes.COMMAND, action);
        arguments.put(ArgumentTypes.INPUT_FILE, argumentExtractor.extractFileName(src));
        arguments.put(ArgumentTypes.OUTPUT_FILE, argumentExtractor.extractFileName(dest));
        arguments.put(ArgumentTypes.DICT_FILE, argumentExtractor.extractFileName(dict));
        MainController mainController = new MainController();
        Application application = new Application(mainController);
        Result result = application.run(arguments);
    }




    @Override
    public void run() {
        throw new ParameterException(spec.commandLine(), "Specify a subcommand");
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new PicocliRunner()).execute(args);
        System.exit(exitCode);
    }
}
