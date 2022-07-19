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

import java.util.HashMap;
import java.util.Map;

@Command(name = "cypher", subcommands = {CommandLine.HelpCommand.class },
        description = "Caesar cypher command")
public class Runner implements Runnable {
    private final Map<ArgumentTypes, Object> arguments = new HashMap<>();
    @Spec CommandSpec spec;
    @Command(name = "encrypt", description = "Encrypt from file to file using key")
    void encrypt(
            @Parameters(paramLabel = "<source file>", description = "source file with text to encrypt") String src,
            @Parameters(paramLabel = "<dest file>", description = "dest file which should have encrypted text") String dest,
            @Parameters(paramLabel = "<key>", description = "key for encryption") int key,
            @Option(names = {"-a", "--alphabet"}, paramLabel = "ALPHABET", description = "your custom alphabet") String alphabet,
            @Option(names = {"-t", "--tinyAlphabet"}, paramLabel = "TINY_ALPHABET", description = "(has priority over the --alphabet flag) if you want to use tiny alphabet (good for stat analysis)") boolean tinyAlphabet,
            @Option(names = {"-l", "--toLowerCase"}, description = "change every letter to lowercase before encrypting") boolean toLowerCase) {
        if(tinyAlphabet) {
            alphabet = Alphabet.TINY_ALPHABET;
        } else if(alphabet == null) {
            alphabet = Alphabet.FULL_ALPHABET;
        }
        Action action = ArgumentExtractor.extractCommand("encrypt");
        arguments.put(ArgumentTypes.COMMAND, action);
        arguments.put(ArgumentTypes.INPUT_FILE, ArgumentExtractor.extractFileName(src));
        arguments.put(ArgumentTypes.OUTPUT_FILE, ArgumentExtractor.extractFileName(dest));
        arguments.put(ArgumentTypes.KEY, key);
        arguments.put(ArgumentTypes.ALPHABET, alphabet);
        arguments.put(ArgumentTypes.TO_LOWER_CASE, toLowerCase);
        MainController mainController = new MainController();
        Application application = new Application(mainController);
        Result result = application.run(arguments);
        System.out.println(result);
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
        Action action = ArgumentExtractor.extractCommand("decrypt");
        arguments.put(ArgumentTypes.COMMAND, action);
        arguments.put(ArgumentTypes.INPUT_FILE, ArgumentExtractor.extractFileName(src));
        arguments.put(ArgumentTypes.OUTPUT_FILE, ArgumentExtractor.extractFileName(dest));
        arguments.put(ArgumentTypes.KEY, key);
        arguments.put(ArgumentTypes.ALPHABET, alphabet);
        arguments.put(ArgumentTypes.TO_LOWER_CASE, false);
        MainController mainController = new MainController();
        Application application = new Application(mainController);
        Result result = application.run(arguments);
        System.out.println(result);
    }

    @Command(name = "brute", description = "Decrypt from file to file using brute force") // |3|
    void bruteForce(
            @Parameters(paramLabel = "<source file>", description = "source file with encrypted text") String src,
            @Parameters(paramLabel = "<dest file>", description = "dest file which should have decrypted text") String dest,
            @Option(names = {"-a", "--alphabet"}, paramLabel = "ALPHABET", description = "your custom alphabet") String alphabet) {
        if(alphabet == null) {
            alphabet = Alphabet.FULL_ALPHABET;
        }
        Action action = ArgumentExtractor.extractCommand("brute");
        arguments.put(ArgumentTypes.COMMAND, action);
        arguments.put(ArgumentTypes.INPUT_FILE, ArgumentExtractor.extractFileName(src));
        arguments.put(ArgumentTypes.OUTPUT_FILE, ArgumentExtractor.extractFileName(dest));
        arguments.put(ArgumentTypes.ALPHABET, alphabet);
        MainController mainController = new MainController();
        Application application = new Application(mainController);
        Result result = application.run(arguments);
        System.out.println(result);
    }

    @Command(name = "statistics", description = "Decrypt from file to file using statistical analysis") // |3|
    void statisticalDecrypt(
            @Parameters(paramLabel = "<source file>", description = "source file with encrypted text") String src,
            @Parameters(paramLabel = "<dest file>", description = "dest file which should have decrypted text") String dest,
            @Parameters(paramLabel = "<dict file>", description = "file with unencrypted representative text") String dict) {
        Action action = ArgumentExtractor.extractCommand("statistics");
        arguments.put(ArgumentTypes.COMMAND, action);
        arguments.put(ArgumentTypes.INPUT_FILE, ArgumentExtractor.extractFileName(src));
        arguments.put(ArgumentTypes.OUTPUT_FILE, ArgumentExtractor.extractFileName(dest));
        arguments.put(ArgumentTypes.DICT_FILE, ArgumentExtractor.extractFileName(dict));
        MainController mainController = new MainController();
        Application application = new Application(mainController);
        Result result = application.run(arguments);
        System.out.println(result);
    }

    @Override
    public void run() {
        throw new ParameterException(spec.commandLine(), "Specify a subcommand");
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Runner()).execute(args);
        System.exit(exitCode);
    }
}
