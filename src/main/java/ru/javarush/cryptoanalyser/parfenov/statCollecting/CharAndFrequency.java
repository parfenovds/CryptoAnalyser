package ru.javarush.cryptoanalyser.parfenov.statCollecting;

import java.util.Objects;

public class CharAndFrequency implements Comparable<CharAndFrequency> {
    private Character character;

    private int frequency;

    public CharAndFrequency(Character character) {
        this.character = character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    public char getCharacter() {
        return character;
    }

    public int getFrequency() {
        return frequency;
    }

    public void addFrequency() {
        frequency++;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CharAndFrequency that)) return false;
        return character == that.character;
    }

    @Override
    public int hashCode() {
        return Objects.hash(character);
    }

    @Override
    public int compareTo(CharAndFrequency anotherChar) {//the reason why it's impossible to find Caf in TreeMap by char only
        int result = anotherChar.frequency - this.frequency;
        if(result == 0) {
            result = this.character.compareTo(anotherChar.character);
        }
        return result;
    }

    @Override
    public String toString() {
        return character.toString();
    }
}
