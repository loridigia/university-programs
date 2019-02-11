import java.util.*;
import java.util.concurrent.RecursiveAction;

public abstract class AbstractThread extends RecursiveAction {
    /**
     * Enum class to define which type of instance the thread is in.
     */
    enum Type { BLOCK, ROW, ENTIRE }

    /**
     * Lowest value for row in the instance.
     */
    protected int rowL;

    /**
     * Highest value for row in the instance.
     */
    protected int rowH;

    /**
     * Lowest value for column in the instance.
     */
    protected int colL;

    /**
     * Highest value for column in the instance.
     */
    protected int colH;

    /**
     * Type of instance during recursive actions.
     */
    protected Type type;

    /**
     * Matrix representing the Sudoku instance.
     */
    protected Cell[][] matrix;

    /**
     * Number of instances produced by children.
     */
    protected int instances;

    /**
     * Constructor.
     * @param matrix
     * @param rowL lowest index row.
     * @param rowH highest index row.
     * @param colL lowest index column.
     * @param colH highest index column
     */

    public AbstractThread(Cell[][] matrix, int rowL, int rowH, int colL, int colH) {
        this.matrix = matrix;
        this.rowL = rowL;
        this.rowH = rowH;
        this.colL = colL;
        this.colH = colH;
        this.type = Type.valueOf(getType().toString());
    }

    /**
     * Computes the recursive action.
     */
    @Override public abstract void compute();

    /**
     * Fork instance by rows.
     */
    protected abstract void forkRows();

    /**
     * Fork instance by columns.
     */
    protected abstract void forkBlocks();

    /**
     * Forks three instances of AbstractThread using threads halving.
     * @param threads set of threads ready to be ran.
     */
    protected void start(Set<? extends AbstractThread> threads) {
        AbstractThread last = threads.stream().findAny().orElse(null);
        if(last != null) {
            threads.remove(last);
            for (AbstractThread t : threads) t.fork();
            last.compute();
            this.instances += last.instances;
            for (AbstractThread t : threads) {
                t.join();
                this.instances += t.instances;
            }
        }
    }

    /**
     * Calculates the type of the current instance.
     * @return type of current instance.
     */
    protected Type getType() {
        if (rowH - rowL == 3 && colH - colL == 3) return Type.BLOCK;
        else if (rowH - rowL == 3 && colH == 9)   return Type.ROW;
        else return Type.ENTIRE;
    }

    /**
     * Removes fixed values from blocks.
     * @return true if any cell changed.
     */
    protected boolean removeFixedFromBlocks() {
        Cell cell;
        boolean repeat = true;
        boolean changed = false;
        Set<Integer> set;
        while(repeat) {
            repeat = false;
            set = MatrixUtils.getFixed(matrix, rowL, rowH, colL, colH);
            for (int i = rowL; i < rowH; i++) {
                for (int j = colL; j < colH; j++) {
                    cell = matrix[i][j];
                    if (!cell.isFixed()) {
                        changed = cell.reduce(set) || changed;
                        repeat = cell.isFixed() || repeat;
                    }
                }
            }
        }
        return changed;
    }

    /**
     * Removes fixed values from rows.
     * @return true if any cell changed.
     */
    protected boolean removeFixedFromRows() {
        Cell cell;
        boolean repeat = true;
        boolean changed = false;
        Set<Integer> set;
        while(repeat) {
            repeat = false;
            for (int i = rowL; i < rowH; i++) {
                set = MatrixUtils.getFixed(matrix, i, i+1, colL, colH);
                for (int j = colL; j < colH; j++) {
                    cell = matrix[i][j];
                    if (!cell.isFixed()) {
                        changed = cell.reduce(set) || changed;
                        repeat = cell.isFixed() || repeat;
                    }
                }
            }
        }
        return changed;
    }

    /**
     * Removes fixed values from columns
     * @return true if any cell changed.
     */
    protected boolean removeFixedFromColumns() {
        Cell cell;
        boolean repeat = true;
        boolean changed = false;
        Set<Integer> set;
        while(repeat) {
            repeat = false;
            for (int i = colL; i < colH; i++) {
                set = MatrixUtils.getFixed(matrix, rowL, rowH, i, i+1);
                for (int j = rowL; j < rowH; j++) {
                    cell = matrix[j][i];
                    if (!cell.isFixed()) {
                        changed = cell.reduce(set) || changed;
                        repeat = cell.isFixed() || repeat;
                    }
                }
            }
        }
        return changed;
    }
}
