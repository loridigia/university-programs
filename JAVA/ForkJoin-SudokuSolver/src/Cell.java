import java.util.*;
public class Cell {

    /**
     * Row reference.
     */
    public int row;

    /**
     * Column reference.
     */
    public int col;

    /**
     * Block reference.
     */
    public int block;

    /**
     * List of possible candidates.
     */
    public Set<Integer> value;

    /**
     * Singleton value of a cell, if present.
     */
    private int singleton;

    /**
     * Check if the cell is fixed.
     */
    private boolean isFixed;

    /**
     * Instantiates a not fixed cell.
     * @param row row reference.
     * @param col column reference.
     */
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.block = MatrixUtils.getBlock(row,col);
        this.value = new HashSet<>(Arrays.asList(1,2,3,4,5,6,7,8,9));
        this.singleton = -1;
        this.isFixed = false;
    }

    /**
     * Instantiates a fixed cell.
     * @param row row reference.
     * @param col column reference.
     */
    public Cell(int row, int col, Set<Integer> value) {
        this.row = row;
        this.col = col;
        this.block = MatrixUtils.getBlock(row,col);
        this.value = new HashSet<>(value);
        checkFixness();
    }

    /**
     * Returns singleton value of a cell.
     * @return singleton value.
     */
    public int getSingleton() { return singleton; }

    /**
     * Returns status cell.
     * @return true if cell is fixed.
     */
    public boolean isFixed() { return this.isFixed; }

    /**
     * Reduces number of candidates of a cell.
     * @param numbers list of candidates to be removed.
     * @return if remove operation has succeeded.
     */
    public boolean reduce(Collection<Integer> numbers) {
        boolean success = this.value.removeAll(numbers);
        if(success) checkFixness();
        return success;
    }

    /**
     * Overload: Reduces number of candidates of a cell.
     * @param number candidate to be removed
     * @return if remove operation has succeeded.
     */
    public boolean reduce(int number) {
        boolean success = this.value.remove(number);
        if(success) checkFixness();
        return success;
    }

    /**
     * Checks if cell is fixed after reduction.
     */
    private void checkFixness() {
        int size = this.value.size();
        if (size == 0) Thread.currentThread().interrupt();
        this.singleton = size == 1 ? this.value.stream().findFirst().get() : -1;
        this.isFixed = this.singleton != -1;
    }

    /**
     * Set a fixed value.
     * @param number fixed number.
     */
    public void fix(Integer number) {
        this.value = Collections.singleton(number);
        this.singleton = this.value.stream().findFirst().get(); //number ?
        this.isFixed = true;
    }

    /**
     * Gets hash value of cell object.
     * @return hashcode.
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(this.row) + Integer.hashCode(this.col);
    }

    /**
     * Check if 2 cells are equal.
     * @param obj to be compared
     * @return true if cells have same col and row indexes.
     */
    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass())
            return false;
        Cell c = (Cell)obj;
        return this.row == c.row && this.col == c.col;
    }

    /**
     * Gets string representation of cell.
     * @return string representation.
     */
    @Override
    public String toString(){
        return this.value.toString();
    }

}
