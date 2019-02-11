import java.util.*;

public class SequentialSolver {

    /**
     * Queue containing values fixed and to be cheched.
     */
    private Queue<Cell> queue;

    /**
     * Keeps track of the blocks mapped to his cells.
     */
    private Map<Integer, Set<Cell>> blocks;

    /**
     * Initializes variables.
     */
    public SequentialSolver() {
        this.queue = new ArrayDeque<>();
        this.blocks = new HashMap<>();
    }

    /**
     * Fill the blocks, then it calls backtracking.
     * @param matrix matrix to be processed.
     */
    public int solve(Cell[][] matrix) {
        this.fillBlocks(matrix);
        return _solve(matrix);
    }

    /**
     * Recursive backtracking algorithm to solve sudoku.
     * @param matrix matrix to be processed.
     */
    private int _solve(Cell[][] matrix) {
        int instances = 0;
        trySolve(matrix);

        boolean[] bools = MatrixUtils.checkLegality(matrix);
        boolean isFull = bools[0];
        boolean isLegal = bools[1];
        if (isFull && isLegal) return 1;
        else {
            Cell cell = MatrixUtils.findChoice(matrix);
            if (cell != null) {
                Set<Integer> set = new HashSet<>(cell.value);
                for (int i : set) {
                    Cell[][] clone = MatrixUtils.clone(matrix);
                    Cell cell2 = clone[cell.row][cell.col];
                    cell2.fix(i);
                    SequentialSolver ss = new SequentialSolver();
                    instances += ss.solve(clone);
                }
            }
        }
        return instances;
    }

    /**
     * Tries to solve sudoku, blocks if there aren't any moves available.
     */
    private void trySolve(Cell[][] matrix) {

        removeFixed(matrix);
        for (Map.Entry<Integer, Set<Cell>> entry : blocks.entrySet()) {
            Map<Integer, Set<Cell>> map = new HashMap<>();
            for (Cell cell : entry.getValue()) {
                if (!cell.isFixed()) updateMap(cell, map);
            }
            fixEntry(map);
        }

        removeFixed(matrix);
        for (Cell[] row : matrix) {
            Map<Integer, Set<Cell>> map = new HashMap<>();
            for (Cell cell : row) {
                if (!cell.isFixed()) updateMap(cell, map);
            }
            fixEntry(map);
        }

        removeFixed(matrix);
        for (int i = 0; i < MatrixUtils.N; i++) {
            Map<Integer, Set<Cell>> map = new HashMap<>();
            for (int j = 0; j < MatrixUtils.N; j++) {
                Cell cell = matrix[j][i];
                if (!cell.isFixed()) updateMap(cell, map);
            }
            fixEntry(map);
        }
    }

    /**
     * Removes fixed values from empty cells using queue.
     */
    private void removeFixed(Cell[][] matrix) {
        while (this.queue.size() != 0) {
            Cell c = this.queue.poll();
            if (c == null) return;

            for (Cell cell : blocks.get(c.block)) {
                cleanCell(cell, c);
            }

            for (int i = 0; i < 9; i++) {
                Cell cell = matrix[c.row][i];
                cleanCell(cell, c);
            }

            for (int i = 0; i < 9; i++) {
                Cell cell = matrix[i][c.col];
                cleanCell(cell, c);
            }
        }
    }

    /**
     * Reduces cell input with fixed value of c.
     * @param cell to be reduced.
     * @param c cell fixed to be passed.
     */
    private void cleanCell(Cell cell, Cell c) {
        if (!cell.isFixed()) {
            cell.reduce(c.getSingleton());
            if (cell.isFixed()) {
                this.queue.add(cell);
            }
        }
    }

    /**
     * Saves in a map the value of not fixed cells.
     * @param cell cell to be added.
     * @param map map which contains references to empty cells.
     */
    private void updateMap(Cell cell, Map<Integer, Set<Cell>> map) {
        for (int value : cell.value) {
            Set<Cell> mapSet = map.get(value);
            if (mapSet == null) map.put(value, new HashSet<>(Collections.singleton(cell)));
            else mapSet.add(cell);
        }
    }

    /**
     * If there is a unique cell for a value, this is fixed to that value.
     * Also, updates hasChanged boolean and add the fixed value to queue.
     * @param map map used to check unique cells.
     */
    private void fixEntry(Map<Integer, Set<Cell>> map) {
        for (Map.Entry<Integer, Set<Cell>> entryMap : map.entrySet()) {
            Set<Cell> set = entryMap.getValue();
            if (set.size() == 1) {
                Cell c = set.iterator().next();
                c.fix(entryMap.getKey());
                this.queue.add(c);
            }
        }
    }

    /**
     * Maps all cells to their correspondent block.
     * Add to queue every cell that results fixed.
     * @param matrix to be read.
     */
    private void fillBlocks(Cell[][] matrix) {
        for (Cell[] row : matrix){
            for (Cell cell : row){
                if (cell.isFixed()) {
                    queue.add(cell);
                }
                Set<Cell> set = this.blocks.get(cell.block);
                if (set == null) {
                    this.blocks.put(cell.block, new HashSet<>(Collections.singleton(cell)));
                } else set.add(cell);
            }
        }
    }
}
