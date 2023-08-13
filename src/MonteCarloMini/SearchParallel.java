package MonteCarloMini;

// import java.security.PublicKey;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.RecursiveAction;

/* M. Kuttel 2023
 * SearchParalleler class that lands somewhere random on the surfaces and 
 * then moves downhill, stopping at the local minimum.
 */

public class SearchParallel extends RecursiveAction {
	private int id; // SearchParallel identifier
	private int pos_row, pos_col; // Position in the grid
	private int steps; // number of steps to end of SearchParallel
	private boolean stopped; // Did the SearchParallel hit a previous trail?
	private int hi, lo;
	private static SearchParallel[] arr;
	int tmp;
	public ConcurrentLinkedDeque<CommonThreads> Vals;
	public int finder;
	int num_searches;

	private TerrainArea terrain;

	public SearchParallel(int id, int pos_row, int pos_col, TerrainArea terrain) {
		this.id = id;
		this.pos_row = pos_row; // randomly allocated
		this.pos_col = pos_col; // randomly allocated
		this.terrain = terrain;
		this.stopped = false;
	}

	/**
	 * This is the second constructor for SearchParallel and configures the
	 * SearchParallel array to be split amongst several threads
	 * 
	 * @param arr                 the array of SearchParallel objects
	 * @param lo                  the lowest index of the array
	 * @param hi                  the highest index in the array
	 * @param commonThreads              the ConcurrentLinkedDeque which will be changed
	 *                            during the compute function
	 * @param num_SearchParallels the total number of searches
	 */
	public SearchParallel(SearchParallel[] arr, int lo, int hi, ConcurrentLinkedDeque<CommonThreads> commonThreads,
			int num_SearchParallels) {
		this.arr = arr;
		this.lo = lo;
		this.hi = hi;
		this.Vals = commonThreads;
		this.finder = -1;
		this.num_searches = num_SearchParallels;
	}

	public int find_valleys() {
		int height = Integer.MAX_VALUE;
		Direction next = Direction.STAY_HERE;
		while (terrain.visited(pos_row, pos_col) == 0) { // stop when hit existing path
			height = terrain.get_height(pos_row, pos_col);
			terrain.mark_visited(pos_row, pos_col, id); // mark current position as visited
			steps++;
			next = terrain.next_step(pos_row, pos_col);
			switch (next) {
				case STAY_HERE:
					return height; // found local valley
				case LEFT:
					pos_row--;
					break;
				case RIGHT:
					pos_row = pos_row + 1;
					break;
				case UP:
					pos_col = pos_col - 1;
					break;
				case DOWN:
					pos_col = pos_col + 1;
					break;
			}
		}
		stopped = true;
		return height;
	}

	public int getID() {
		return id;
	}

	public int getPos_row() {
		return pos_row;
	}

	public int getPos_col() {
		return pos_col;
	}

	public int getSteps() {
		return steps;
	}

	public boolean isStopped() {
		return stopped;
	}

	/**
	 * This function is called when the fork/join pool is invoked and will split the
	 * array amongst several threads.
	 */
	@Override
	protected void compute() {
		if ((hi - lo) <= 0.09 * num_searches) {
			int local_min = Integer.MAX_VALUE;

			for (int i = lo; i < hi; i++) {
				tmp = arr[i].find_valleys();
				if ((!arr[i].isStopped()) && (tmp < local_min)) {
					local_min = tmp;
					finder = i;

				}
			}
			if (finder != -1) {// if the finder is found to be -1, do not add it to the list
				Vals.add(new CommonThreads(local_min, finder));
			}

		} else {// if hi-lo is found to be less than the sequential cutoff...
			int split = (int) ((hi + lo) / 2.0);// finding the index of the middle element in the array.
			SearchParallel left = new SearchParallel(arr, lo, split, Vals, num_searches);// creating a SearchParallel
																							// object that contains the
																							// 'left' half of the array
			SearchParallel right = new SearchParallel(arr, split, hi, Vals, num_searches);// creating a SearchParallel
																							// object that contains the
																							// 'right' half of the array
			left.fork();// 'forking' the left hand side
			right.compute();// computing the rihgt hand side
			left.join();// asking the left hand side to 'wait' for the completion of the right hand
						// threads

		}

	}

}