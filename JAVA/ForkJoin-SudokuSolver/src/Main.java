import java.io.IOException;
import java.util.concurrent.ForkJoinPool;

public class Main {

    /**
     * Pool used to run program.
     */
    public static ForkJoinPool fjp = new ForkJoinPool();


    /**
     * Launches the program.
     * @param args file to be processed.
     * @throws IOException if file not found.
     */

    public static void main(String[] args) throws IOException {
        Cell[][] sudoku;
        Mapper mapper;
        sudoku = MatrixUtils.readFile(args[0]);
        mapper = new Mapper(sudoku, 0,9,0,9);
        long start = System.currentTimeMillis();
        Main.fjp.invoke(mapper);
        long finish = System.currentTimeMillis();
        System.out.println("Number of legal instances: " + mapper.instances);
        System.out.println("Starting fillin factor: " + MatrixUtils.format(MatrixUtils.percentage) + "%");
        System.out.println("\nPARALLEL ALGORITHM:");
        System.out.println("Running time on " + args[0] + ": " + MatrixUtils.getTime(finish-start) + "\n");

        System.out.println("SEQUENTIAL ALGORITHM:");
        start = System.currentTimeMillis();
        SequentialSolver solver = new SequentialSolver();
        solver.solve(sudoku);
        finish = System.currentTimeMillis();
        System.out.println("Running time on " + args[0] + ": " + MatrixUtils.getTime(finish-start) + "\n");
    }
}
