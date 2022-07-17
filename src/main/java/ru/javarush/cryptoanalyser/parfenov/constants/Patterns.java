package ru.javarush.cryptoanalyser.parfenov.constants;

import java.util.ArrayList;
import java.util.List;

public class Patterns {
    private static final String INCORRECT_LENGTH_BETWEEN_SPACES = "[^ \n\t*-]{25}";
    private static final String SPACE_AFTER_PUNCTUATION = "[.,»:!?);]+[^ \t\n\\d]";
    private static final String FORBIDDEN_LETTERS_AT_START = "[\t\n ]+[ъыьЪЫЬ]+";
    private static final String STUCK_DIGITS_AT_END = "[а-яёА-ЯЁa-zA-Z]+[\\d]+";
    private static final String STUCK_DIGITS_AT_BEGINNING = "[\\d]+[а-яёА-ЯЁa-zA-Z]+";
    private static final String CAPITAL_LETTER_NOT_AT_BEGINNING = "[^ \n\tА-ЯЁA-Z-]+[А-ЯЁA-Z]";
    private static final String IMPOSSIBLE_COMBINATION = "ёя|ёь|ёэ|ъж|эё|ъд|цё|уь|щч|чй|шй|шз|" +
            "ыф|жщ|жш|жц|ыъ|ыэ|ыю|ыь|жй|ыы|жъ|жы|ъш|пй|ъщ|зщ|ъч|ъц|ъу|ъф|ъх|ъъ|ъы|ыо|жя|зй|ъь|ъэ|" +
            "ыа|нй|еь|цй|ьй|ьл|ьр|пъ|еы|еъ|ьа|шъ|ёы|ёъ|ът|щс|оь|къ|оы|щх|щщ|щъ|щц|кй|оъ|цщ|лъ|мй|" +
            "шщ|ць|цъ|щй|йь|ъг|иъ|ъб|ъв|ъи|ъй|ъп|ър|ъс|ъо|ън|ък|ъл|ъм|иы|иь|йу|щэ|йы|йъ|щы|щю|щя|" +
            "ъа|мъ|йй|йж|ьу|гй|эъ|уъ|аь|чъ|хй|тй|чщ|ръ|юъ|фъ|уы|аъ|юь|аы|юы|эь|эы|бй|яь|ьы|ьь|ьъ|" +
            "яъ|яы|хщ|дй|фй";
    public static final String INCORRECT_LONELY_LETTER = " {1}[гдеёзйлмнпртфхцчшщъыьэю] {1}";
    public static final String VALID_LONELY_LETTER = "абвжикосуя";
    public final static List<String> regexBruteList = new ArrayList<>() {{
        add(INCORRECT_LENGTH_BETWEEN_SPACES);
        add(SPACE_AFTER_PUNCTUATION);
        add(FORBIDDEN_LETTERS_AT_START);
        add(STUCK_DIGITS_AT_END);
        add(STUCK_DIGITS_AT_BEGINNING);
        add(CAPITAL_LETTER_NOT_AT_BEGINNING);
        add(IMPOSSIBLE_COMBINATION);
    }};
}
