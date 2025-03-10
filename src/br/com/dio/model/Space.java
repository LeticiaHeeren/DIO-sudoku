package br.com.dio.model;

import java.util.HashSet;
import java.util.Set;

public class Space {

    private Integer actual;
    private final int expected;
    private final boolean fixed;
    private Set<Integer> possibleNumbers;

    public Space(final int expected, final boolean fixed) {
        this.expected = expected;
        this.fixed = fixed;
        if (fixed) {
            actual = expected;
        } else {
            this.possibleNumbers = new HashSet<>();
            for (int i = 1; i <= 9; i++) {
                possibleNumbers.add(i);
            }
        }
    }

    public Integer getActual() {
        return actual;
    }

    public void setActual(final Integer actual) {
        if (fixed) return;
        this.actual = actual;
    }

    public void clearSpace() {
        setActual(null);
    }

    public int getExpected() {
        return expected;
    }

    public boolean isFixed() {
        return fixed;
    }

    public Set<Integer> getPossibleNumbers() {
        return possibleNumbers;
    }

    public void removePossibleNumber(int number) {
        possibleNumbers.remove(number);
    }

    public void addPossibleNumber(int number) {
        possibleNumbers.add(number);
    }
}