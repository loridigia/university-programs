import java.util.*;
public class Solver extends AbstractThread {

    /**
     * Map used to track cells to be removed.
     */
    public Map<Integer, List<Cell>> map;

    /**
     * Used to identifies changes during execution.
     */
    public boolean changed;

    /**
     * Solvers ready to be launched if changes true.
     */
    public Solver m1,m2,m3;

    public Solver(Cell[][] matrix, int rowL, int rowH, int colL, int colH) {
        super(matrix, rowL, rowH, colL, colH);
        this.map = new HashMap<>();
    }

    /**
     * Given a matrix with candidates, computes one or more solutions.
     */
    @Override
    public void compute() {
        switch(type) {
            case BLOCK:
                updateBlocks(); break;
            case ROW:
                forkBlocks(); updateRows(); break;
            case ENTIRE:
                do {
                    forkRows();
                    updateColumns();
                } while (changed);
                boolean[] bools = MatrixUtils.checkLegality(matrix);
                boolean isFull = bools[0];
                boolean isLegal = bools[1];
                if (isFull && isLegal) this.instances++;
                else if (!isFull) choice();
                break;
        }
    }

    /**
     * Make a choice if matrix has no other valid moves.
     */
    private void choice() {
        Cell cell = MatrixUtils.findChoice(this.matrix);
        Cell[][] matrix;
        Set<Solver> rms = new HashSet<>();
        if (cell != null) {
            for (Integer i : cell.value) {
                matrix = MatrixUtils.clone(this.matrix);
                matrix[cell.row][cell.col].fix(i);
                rms.add(new Solver(matrix,0,9,0,9));
            }
            start(rms);
        }
    }

    /**
     * Checks if threads launched got changes.
     * @param m1 first thread.
     * @param m2 second thread.
     * @param m3 third thread.
     * @return true if changes are found.
     */
    private boolean isChanged(Solver m1, Solver m2, Solver m3) {
        return m1.changed || m2.changed || m3.changed;
    }

    @Override
    protected void forkRows() {
        m1 = new Solver(matrix, 0, 3, 0, 9);
        m2 = new Solver(matrix, 3, 6, 0, 9);
        m3 = new Solver(matrix, 6, 9, 0, 9);
        start(new HashSet<>(Arrays.asList(m1,m2,m3)));
        changed = isChanged(m1,m2,m3);
    }

    @Override
    protected void forkBlocks() {
        m1 = new Solver(matrix,  rowL, rowH, 0, 3);
        m2 = new Solver(matrix,  rowL, rowH, 3, 6);
        m3 = new Solver(matrix,  rowL, rowH, 6, 9);
        start(new HashSet<>(Arrays.asList(m1,m2,m3)));
        changed = isChanged(m1,m2,m3) || changed;
    }

    /**
     * Updates matrix if map contains new fixed cells.
     */
    private void fixEntry() {
        List<Cell> list;
        for(Map.Entry<Integer, List<Cell>> entry : map.entrySet()) {
            list = entry.getValue();
            if (list.size() == 1) {
                list.get(0).fix(entry.getKey());
                changed = true;
            }
        }
        map = new HashMap<>();
    }

    /**
     * Removes for every block unnecessary candidates
     */
    private void updateBlocks() {
        changed = removeFixedFromBlocks();
        for (int i = rowL; i < rowH; i++) {
            for (int j = colL; j < colH; j++) {
                updateMap(i,j);
            }
        }
        fixEntry();
    }

    /**
     * Removes for every row unnecessary candidates
     */
    private void updateRows() {
        changed = removeFixedFromRows();
        for (int i = rowL; i < rowH; i++) {
            for (int j = colL; j < colH; j++) {
                updateMap(i,j);
            }
            fixEntry();
        }
    }

    /**
     * Removes for every column unnecessary candidates
     */
    private void updateColumns() {
        changed = removeFixedFromColumns();
        for (int i = colL; i < colH; i++) {
            for (int j = rowL; j < rowH; j++) {
                updateMap(j,i);
            }
            fixEntry();
        }
    }

    /**
     * Updates fixed cells map.
     * @param indexes used to define matrix access.
     */
    private void updateMap(int... indexes) {
        Cell cell;
        List<Cell> values;
        cell = matrix[indexes[0]][indexes[1]];
        if (!cell.isFixed()) {
            for (int number : cell.value) {
                values = map.get(number);
                if (values == null) map.put(number, new ArrayList<>(Collections.singletonList(cell)));
                else values.add(cell);
            }
        }
    }
}
