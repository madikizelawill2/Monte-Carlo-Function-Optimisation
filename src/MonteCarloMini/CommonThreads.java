package MonteCarloMini;

/**
 * This class contains a specific local minimum found and its index.
 */
public class CommonThreads {
    public int min;
    public int index;

    /**
     * This is the constructor for the CommonThreads class
     * 
     * @param min   the local minimum found
     * @param index the index of that minimum
     * 
     */
    public CommonThreads(int min, int index) {
        this.min = min;
        this.index = index;
    }
}