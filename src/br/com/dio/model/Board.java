package br.com.dio.model;

import static br.com.dio.model.GameStatusEnum.*;
import java.util.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Board {

    private final List<List<Space>> spaces;

    public Board(final List<List<Space>> spaces) {
        this.spaces = spaces;
        updatePossibleNumbers();
    }

    public List<List<Space>> getSpaces() {
        return spaces;
    }

    public GameStatusEnum getStatus() {
        if (spaces.stream().flatMap(Collection::stream).noneMatch(s -> !s.isFixed() && nonNull(s.getActual()))) {
            return NON_STARTED;
        }
        return spaces.stream().flatMap(Collection::stream).anyMatch(s -> isNull(s.getActual())) ? INCOMPLETE : COMPLETE;
    }

    public boolean hasErrors() {
        if (getStatus() == NON_STARTED) {
            return false;
        }
        return spaces.stream().flatMap(Collection::stream)
                .anyMatch(s -> nonNull(s.getActual()) && !s.getActual().equals(s.getExpected()));
    }

    public boolean changeValue(final int col, final int row, final int value) {
        var space = spaces.get(col).get(row);
        if (space.isFixed()) {
            return false;
        }
        if (isValidMove(col, row, value)) {
            space.setActual(value);
            updatePossibleNumbers();
            return true;
        }
        return false;
    }

    public boolean clearValue(final int col, final int row) {
        var space = spaces.get(col).get(row);
        if (space.isFixed()) {
            return false;
        }
        space.clearSpace();
        updatePossibleNumbers();
        return true;
    }

    public void reset() {
        spaces.forEach(c -> c.forEach(Space::clearSpace));
        updatePossibleNumbers();
    }

    public boolean gameIsFinished() {
        return !hasErrors() && getStatus().equals(COMPLETE);
    }

    public boolean isValidMove(int col, int row, int value) {
        // Verificar linha e coluna
        for (int i = 0; i < 9; i++) {
            Integer rowValue = spaces.get(col).get(i).getActual();
            Integer colValue = spaces.get(i).get(row).getActual();
            if ((rowValue != null && rowValue == value) || (colValue != null && colValue == value)) {
                return false;
            }
        }
        // Verificar subgrade 3x3
        int startRow = (row / 3) * 3;
        int startCol = (col / 3) * 3;
        for (int i = startCol; i < startCol + 3; i++) {
            for (int j = startRow; j < startRow + 3; j++) {
                Integer subGridValue = spaces.get(i).get(j).getActual();
                if (subGridValue != null && subGridValue == value) {
                    return false;
                }
            }
        }
        return true;
    }

    public void updatePossibleNumbers() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                Space space = spaces.get(i).get(j);
                if (space.getActual() == null) {
                    space.getPossibleNumbers().clear();
                    for (int num = 1; num <= 9; num++) {
                        if (isValidMove(i, j, num)) {
                            space.addPossibleNumber(num);
                        }
                    }
                }
            }
        }
    }

    public String getHint(int col, int row) {
        Space space = spaces.get(col).get(row);
        if (space.getActual() != null) {
            return "A célula já está preenchida.";
        }

        if (space.getPossibleNumbers().isEmpty()) {
            return "Nenhum número possível para esta célula. Verifique se há erros no tabuleiro.";
        }

        if (space.getPossibleNumbers().size() == 1) {
            int hint = space.getPossibleNumbers().iterator().next();
            return String.format("Dica: O número %d pode ser colocado na posição [%d,%d].", hint, col, row);
        }

        // Prioriza células com menos possibilidades
        List<Integer> possibleNumbers = new ArrayList<>(space.getPossibleNumbers());
        Collections.shuffle(possibleNumbers); // Aleatoriza para variar as dicas

        for (int num : possibleNumbers) {
            if (isValidMove(col, row, num)) {
                return String.format("Dica: O número %d é uma possibilidade para a posição [%d,%d].", num, col, row);
            }
        }

        return "Nenhuma dica disponível para esta célula no momento.";
    }
}