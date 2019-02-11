import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MatrixUtils {

    /**
     * Static length of sudoku.
     */
    public static int N = 9;

    /**
     * Filling factor percentage.
     */
    public static double percentage;

    /**
     * Object used to convert solutions space to readable string.
     */
    static DecimalFormat df = new DecimalFormat("#.##");

    /**
     * Formats double given in input.
     * @param d double to be processed
     * @return string representation of d.
     */
    public static String format(double d) {
        return df.format(d);
    }

    /**
     * Reads file given in input.
     * @param path path of the file to be read.
     * @return matrix made of cells.
     * @throws IOException if file not found.
     */
    public static Cell[][] readFile(String path) throws IOException {
        percentage = 0;
        int c; int x = 0; int y = 0;
        Cell[][] matrix = new Cell[N][N];
        Cell cell;
        BufferedReader br = new BufferedReader(new FileReader(path));
        while ((c = br.read()) != -1) {
            if (c == 10) continue;
            int ch = Character.getNumericValue(c);
            if (ch == -1) cell = new Cell(x,y);
            else {
                cell = new Cell(x,y, Collections.singleton(ch));
                percentage++;
            }
            matrix[x][y] = cell;
            if (++y == 9) {
                x++;
                y = 0;
            }
        }
        percentage *= 0.81;
        return matrix;
    }

    /**
     * Calculates number of solutions space.
     * @param matrix to be processed.
     */
    public static void getSolutionsSpace(Cell[][] matrix) {
        double sol = 1;
        for (Cell[] row : matrix) {
            for (Cell cell : row) {
                if (!cell.isFixed())
                    sol *= cell.value.size();
            }
        }
        System.out.println("Solutions space: " + sol);
    }

    /**
     * Converts running time in minutes.
     * @param millis value to be converted.
     * @return time converted.
     */
    public static String getTime(long millis) {
        return String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(millis),
                TimeUnit.MILLISECONDS.toSeconds(millis) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
        );
    }

    /**
     *
     * @param matrix instance.
     * @param rowL lowest value for row in instance.
     * @param rowH highest value for row in instance.
     * @param colL lowest value for col in instance.
     * @param colH highest value for col in instance.
     * @return fixed values.
     */
    public static Set<Integer> getFixed(Cell[][] matrix, int rowL, int rowH, int colL, int colH) {
        Set<Integer> list = new HashSet<>();
        Cell c;
        for (int i = rowL; i < rowH; i++)
            for (int j = colL; j < colH; j++) {
                c = matrix[i][j];
                if (c.isFixed()) list.add(c.getSingleton());
            }
        return list;
    }

    /**
     * Takes first not fixed cell.
     * @param matrix instance.
     * @return cell not fixed.
     */
    public static Cell findChoice(Cell[][] matrix) {
        Cell cell;
        for (int i = 0; i < MatrixUtils.N; i++) {
            for (int j = 0; j < MatrixUtils.N; j++) {
                cell = matrix[i][j];
                if (!cell.isFixed()) return cell;
            }
        }
        return null;
    }

    /**
     * Clones a matrix.
     * @param matrix to be cloned.
     * @return new matrix cloned.
     */
    public static Cell[][] clone(Cell[][] matrix) {
         Cell[][] matrix2 = new Cell[MatrixUtils.N][MatrixUtils.N];
         for (int i = 0; i < MatrixUtils.N; i++) {
             for (int j = 0; j < MatrixUtils.N; j++) {
                 Cell cell = matrix[i][j];
                 Cell cell2 = new Cell(cell.row, cell.col, cell.value);
                 matrix2[i][j] = cell2;
             }
         }
         return matrix2;
    }

    /**
     * Returns block of a cell.
     * @param row row cell.
     * @param col column cell.
     * @return block reference.
     */
    public static int getBlock(int row, int col){ return row/3*3 + col/3; }

    /**
     * Checks if matrix is legal
     * @param matrix instance.
     * @return matrix legal / full.
     */
    public static boolean[] checkLegality(Cell[][] matrix) {
        boolean isFull = true;
        boolean isLegal = true;
        boolean[] bools;
        int singleton;
        HashMap<Integer, boolean[]> blocks = new HashMap<>();

        for (Cell[] row : matrix){
            for (Cell cell : row) {
                if (cell.isFixed()) {
                    singleton = cell.getSingleton();
                    if (blocks.containsKey(cell.block)) {
                        bools = blocks.get(cell.block);
                        if (bools[singleton]) {
                            isLegal = false;
                            if (!isFull) return new boolean[] {isFull, isLegal};
                        }
                        else bools[singleton] = true;
                    }
                    else {
                        bools = new boolean[10];
                        bools[singleton] = true;
                        blocks.put(cell.block, bools);
                    }
                }
                else {
                    isFull = false;
                    if (!isLegal) return new boolean[] {isFull, isLegal};
                }
            }
        }

        for (Cell[] row : matrix) {
            bools = new boolean[10];
            for (Cell cell : row){
                if (notValid(cell, bools)) {
                    isLegal = false;
                    return new boolean[] {isFull, isLegal};
                }
            }
        }
        Cell cell;
        for (int j = 0; j < 9; j++){
            bools = new boolean[10];
            for (int i = 0; i < 9; i++) {
                cell = matrix[i][j];
                if (notValid(cell, bools)) {
                    isLegal = false;
                    return new boolean[]{isFull, isLegal};
                }
            }
        }
        return new boolean[] {isFull, isLegal};
    }

    /**
     * Check if portion of matrix is valid.
     * @param cell to be processed.
     * @param bools tracks cell already found.
     * @return false if cell is found.
     */
    private static boolean notValid(Cell cell, boolean[] bools) {
        if (cell.isFixed()){
            int singleton = cell.getSingleton();
            if (bools[singleton]) {
                return true;
            }
            else bools[singleton] = true;
        }
        return false;
    }
}
