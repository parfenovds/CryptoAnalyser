package ru.javarush.cryptoanalyser.parfenov.constants;

public class Alphabet {
    public static final String BIG_ENGLISH = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String SMALL_ENGLISH = "abcdefghijklmnopqrstuvwxyz";
    public static final String BIG_RUSSIAN = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ";
    public static final String SMALL_RUSSIAN = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";
    public static final String PUNCT_BEFORE_SPACE = ".,»:!?);";
    public static final String PUNCT_AFTER_SPACE = "«(";
    public static final String PUNCT_AMBI_SPACE = "\"";
    public static final String PUNCT_NOT_CHECKABLE = "' —-*\n…";
    public static final String DIGITS = "0123456789";
    public static final String TINY_ALPHA = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя.,!?\n ";
    //public static final List<Character> ALPHA = new ArrayList<>(SMALL_ALPHABET);




    public static final String FULL_ALPHABET = BIG_ENGLISH + SMALL_ENGLISH + BIG_RUSSIAN + SMALL_RUSSIAN +
            PUNCT_BEFORE_SPACE + PUNCT_AFTER_SPACE + PUNCT_AMBI_SPACE + PUNCT_NOT_CHECKABLE + DIGITS;
}