package ru.javarush.cryptoanalyser.parfenov.constants;

public class Alphabet {
    private static final String BIG_EN = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String SMALL_EN = "abcdefghijklmnopqrstuvwxyz";
    private static final String BIG_RU = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    private static final String SMALL_RU = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
    private static final String PUNCT_MIN = ".,!?\n ";
    private static final String PUNCT_ADDITIONAL = "«»:();\"'—-*…";
    private static final String DIGITS = "0123456789";
    public static final String FULL_ALPHABET = BIG_EN + SMALL_EN + BIG_RU + SMALL_RU +
            PUNCT_MIN + PUNCT_ADDITIONAL + DIGITS;
    public static final String TINY_ALPHABET = SMALL_RU + PUNCT_MIN;
}