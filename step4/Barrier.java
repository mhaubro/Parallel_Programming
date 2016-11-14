package step4;

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

	/** Indicates whether the @see #threshold has been changed */
	protected boolean threshold_change = false;

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
	 *
	 * Strategy of the method: Enter the critical region and indicate that
	 * you're waiting. Start wait by wait.V(); If you are the n'th car waiting,
	 * indicate that you are not waiting, and start a cascade of notifications
	 * to the waiting cars doing wait.V(); When the cars are released, if a car
	 * sees that it is the last on released, it will stop the cascade. If there
	 * are more cars waiting, a notification is sent to the next. All this is
	 * done in critical regions.
	 * 
	 * Please note that only threshold cars will be sent off, since the whole
	 * function is a critical region (method.P()) Due to this, when threshold
	 * cars hit the barrier, they will be sent off, and no more cars will be
	 * sent off with them.
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

	public abstract void setThreshold(int k) throws InterruptedException;

}
