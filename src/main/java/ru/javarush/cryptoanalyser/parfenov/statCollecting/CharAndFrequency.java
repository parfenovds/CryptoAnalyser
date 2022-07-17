package ru.javarush.cryptoanalyser.parfenov.statCollecting;

import java.util.Objects;

public class CharAndFrequency implements Comparable<CharAndFrequency> {
    private Character character;

    private int frequency;
    private double percentage;

    public CharAndFrequency(Character character) {
        this.character = character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    public char getCharacter() {
        return character;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
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
        if (!(o instanceof CharAndFrequency)) return false;
        CharAndFrequency that = (CharAndFrequency) o;
        return character == that.character;
    }

    @Override
    public int hashCode() {
        return Objects.hash(character);
    }

    @Override
    public int compareTo(CharAndFrequency anotherChar) {
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
