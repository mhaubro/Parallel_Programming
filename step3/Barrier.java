package step3;

/**
 * Barrier is an abstract class, used to simplify the implementation and shifting
 * between the semaphore and monitor implementations.
 * 
 * @author Martin og Mathias
 *
 */

public abstract class Barrier {
	/** The amount of cars the barrier should open for */
	protected int threshold = 9;

	/** Indicates whether the barrier is turned on */
	protected boolean isOn;

	protected int waiting = 0;

	/**
	 * Constructor for the barrier-object.
	 * 
	 * @param isOn
	 *            Indicated whether the barrier is turned on.
	 */
	public Barrier(boolean isOn) {
		this.isOn = isOn;
	}

	
	
	
	/**
	 * Method used to wait for the barrier synchronization, to make sure
	 * that @see #threshold and only threshold drives.
	 * 
	 * @throws InterruptedException
	 *             if interrupted while doing a semaphore.P()-operation.
	 */
	public abstract void sync() throws InterruptedException;

	/** Turns on the barrier. @throws InterruptedException */
	public abstract void on() throws InterruptedException;

	/** Turns off the barrier. @throws InterruptedException */
	public abstract void off() throws InterruptedException;

	/**
	 * Checks if car n is in front of the barrier @param current Position of the
	 * car, @param n Number of the car
	 * 
	 * @return Whether the car is located right in front of the barrier
	 */
	public boolean atBarrier(Pos current, int N) {

		// checks if the x-coordinate is within the barrier.
		if (current.col < 3)
			return false;

		if (N < 5) { // cars going up
			return (current.row == 6);
		} else { // cars going down
			return (current.row == 5);

		}
	}
}
