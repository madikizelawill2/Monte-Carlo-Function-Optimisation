package MonteCarloMini;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ForkJoinPool;

class MonteCarloMinimizationParallel {
	static final boolean DEBUG = false;

	static long startTime = 0;
	static long endTime = 0;
	public static ConcurrentLinkedDeque<CommonThreads> commonThreads;

	// timers - note milliseconds
	private static void tick() {
		startTime = System.currentTimeMillis();
	}

	private static void tock() {
		endTime = System.currentTimeMillis();
	}

	public static void main(String[] args) {
		commonThreads = new ConcurrentLinkedDeque<CommonThreads>();// thread safe list to store CommonThreads objects

		try {
			int rows, columns; // grid size
			double xmin, xmax, ymin, ymax; // x and y terrain limits
			TerrainArea terrain; // object to store the heights and grid points visited by searches
			double searches_density; // Density - number of Monte Carlo searches per grid position - usually less
										// than 1!

			int num_searches; // Number of searches
			SearchParallel[] searches; // Array of searches
			Random rand = new Random(); // the random number generator

			if (args.length != 7) {
				System.out.println("Incorrect number of command line arguments provided.");
				System.exit(0);
			}
			/* Read argument commonThreads */
			rows = Integer.parseInt(args[0]);
			columns = Integer.parseInt(args[1]);
			xmin = Double.parseDouble(args[2]);
			xmax = Double.parseDouble(args[3]);
			ymin = Double.parseDouble(args[4]);
			ymax = Double.parseDouble(args[5]);
			searches_density = Double.parseDouble(args[6]);

			if (DEBUG) {
				/* Print arguments */
				System.out.printf("Arguments, Rows: %d, Columns: %d\n", rows, columns);
				System.out.printf("Arguments, x_range: ( %f, %f ), y_range( %f, %f )\n", xmin, xmax, ymin, ymax);
				System.out.printf("Arguments, searches_density: %f\n", searches_density);
				System.out.printf("\n");
			}

			// Initialize
			terrain = new TerrainArea(rows, columns, xmin, xmax, ymin, ymax);
			num_searches = (int) (rows * columns * searches_density);
			searches = new SearchParallel[num_searches];
			for (int i = 0; i < num_searches; i++) {
				searches[i] = new SearchParallel(i + 1, rand.nextInt(rows), rand.nextInt(columns), terrain);

			}

			if (DEBUG) {
				/* Print initial commonThreads */
				System.out.printf("Number searches: %d\n", num_searches);
				// terrain.print_heights();
			}

			// start timer
			tick();

			// all searches
			int min = Integer.MAX_VALUE;
			// int local_min = Integer.MAX_VALUE;
			int finder = -1;

			ForkJoinPool pool = new ForkJoinPool();// creating the fork/join pool
			SearchParallel para = new SearchParallel(searches, 0, num_searches, commonThreads, num_searches);
			pool.invoke(para);// invoking the pool

			for (CommonThreads z : commonThreads) {// looping through the concurrentlinkedeque
				if (z.min < min) {// finding the global minimum
					min = z.min;
					finder = z.index;
				}
			}

			// end timer
			tock();

			if (DEBUG) {
				/* print final state */
				terrain.print_heights();
				terrain.print_visited();
			}
			FileWriter fw = new FileWriter("timesParallel.txt", true);
			BufferedWriter bw = new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			pw.printf("Rows: %d, Columns: %d\n", rows, columns);
			pw.printf("Search density: %f (%d searches)\n", searches_density, num_searches);
			pw.printf(" %d ms\n", endTime - startTime);
			pw.println(" ");
			pw.close();


			System.out.printf("Run parameters\n");
			System.out.printf("\t Rows: %d, Columns: %d\n", rows, columns);
			System.out.printf("\t x: [%f, %f], y: [%f, %f]\n", xmin, xmax, ymin, ymax);
			System.out.printf("\t Search density: %f (%d searches)\n", searches_density, num_searches);

			/* Total computation time */
			System.out.printf("Time: %d ms\n", endTime - startTime);
			int tmp = terrain.getGrid_points_visited();
			System.out.printf("Grid points visited: %d  (%2.0f%s)\n", tmp, (tmp / (rows * columns * 1.0)) * 100.0, "%");
			tmp = terrain.getGrid_points_evaluated();
			System.out.printf("Grid points evaluated: %d  (%2.0f%s)\n", tmp, (tmp / (rows * columns * 1.0)) * 100.0,
					"%");

			/* Results */
			System.out.printf("Global minimum: %d at x=%.1f y=%.1f\n\n", min,
					terrain.getXcoord(searches[finder].getPos_row()), terrain.getYcoord(searches[finder].getPos_col()));

		} catch (Exception e) {
			e.printStackTrace();
			;
		}
	}

}