import java.util.Arrays;
import java.util.HashSet;

public class Mapper extends AbstractThread {

    /**
     * Initialize sudoku mapper.
     */
    public Mapper(Cell[][] matrix, int rowL, int rowH, int colL, int colH) {
        super(matrix, rowL, rowH, colL, colH);
    }

    /**
     * Given a sudoku instance, calculates potential candidates.
     */
    @Override
    public void compute() {
        switch(type) {
            case BLOCK:
                removeFixedFromBlocks(); break;
            case ROW:
                forkBlocks(); removeFixedFromRows(); break;
            case ENTIRE:
                forkRows();
                removeFixedFromColumns();
                MatrixUtils.getSolutionsSpace(matrix);
                Solver rm = new Solver(matrix, rowL, rowH, colL, colH);
                Main.fjp.invoke(rm);
                this.instances = rm.instances;
                break;
        }
    }

    @Override
    protected void forkRows() {
        start(new HashSet<>(Arrays.asList(
            new Mapper(matrix,  0, 3, colL, colH),
            new Mapper(matrix,  3, 6, colL, colH),
            new Mapper(matrix,  6, 9, colL, colH)
        )));
    }

    @Override
    protected void forkBlocks() {
        start(new HashSet<>(Arrays.asList(
            new Mapper(matrix,  rowL, rowH, 0, 3),
            new Mapper(matrix,  rowL, rowH, 3, 6),
            new Mapper(matrix,  rowL, rowH, 6, 9)
        )));
    }
}