package mandat;

/**
 * @author Martin og Mathias This class is the implementation of the
 *         alley-synchronization using java monitors. It extends the abstract
 *         class Alley, that holds the basic mutual code for the monitor and
 *         semaphore solutions.
 */

public class AlleyMonitor extends Alley {

	public AlleyMonitor() {
		super();
	}

	/**
	 * since_last_dir and MAX_IN_DIR is made to reduce starvation. While
	 * starvation may still happen, every time MAX_IN_DIR cars have gone through
	 * the alley, everyone waiting to enter will get an opportunity to take
	 * control of the alley. since_last_dir is the amount of cars that has gone
	 * through since the alley was last 'opened' to both directions
	 */
	private int since_last_dir = 0; 
	
	/**@see #since_last_dir*/
	private final static int MAX_IN_DIR = 4;
	
	/** Used to count the amount of cars in the alley. If 0, the alley is free.*/
	private int count = 0;

	/**
	 * Enter is used to enter the alley. This may be interrupted, and it may block, due to
	 * the nature of waiting until the alley is free.
	 * @param n the number of the car entering.
	 */
	public synchronized void enter(int n) throws InterruptedException {
		while (true) {// A guard against spurious monitor wakeups.
			if (rightDir(n) && since_last_dir < MAX_IN_DIR) {
				break;//Enters
			} else if (count == 0) {
				dir = getDir(n);
				since_last_dir = 0;
				break;//Enters
			}
			this.wait();//Waits. Will be notified if a car leaves the alley, and count hits 0.
		}
		//Managing variables, car is entering.
		count++;
		since_last_dir++;
		return;
	}
	
/**
 * Leave is used to tell the alley that the car is leaving the alley.
 * @param n is the number of the leaving car.
 */
	public synchronized void leave(int n) {
		count--;
		if (count < 1) {
			//Everyone waiting will be notified.
			this.notifyAll();
		}
	}

}
