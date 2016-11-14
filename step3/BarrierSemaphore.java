package step3;

public class BarrierSemaphore {
	/** The amount of cars the barrier should open for */
	private int threshold = 9;

	/** Indicates whether the barrier is turned on */
	private boolean isOn;

	private int waiting = 0;
	/**
	 * Semaphore used to notify cars waiting on the barrier, that they are
	 * allowed to drive Used in a cascade-notification.
	 */
	private Semaphore wait = new Semaphore(0);

	/** Semaphore to indicate access to barrier methods and variables. */
	private Semaphore method = new Semaphore(1);

	/**
	 * Constructor for the barrier-object.
	 * 
	 * @param isOn
	 *            Indicated whether the barrier is turned on.
	 */
	public BarrierSemaphore(boolean isOn) {
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
	public void sync() throws InterruptedException {
		method.P();// Enter Rcrit.
		if (isOn) {
			if (++waiting < threshold) {// If there are less cars waiting than
										// the threshold.
				method.V();
				wait.P();// Wait until there are threshold cars waiting
				waiting--;// Decrements waiting, since it has received a
							// go-signal.

				if (waiting > 0) {
					wait.V();// Signals another waiting car
					return;// Starts. By signaling the other car, the baton has
							// been passed.
				} else {
					method.V();// Opens the critical method-region. No cars
								// needs signaling.
					return;
				}
			} else {
				waiting--;// The amount of total waiting cars is >= threshold.
				wait.V();// Signals other cars
				return;// Goes.
			}
		}
		method.V();// Leaves critical region.
	}

	/** Turns on the barrier. @throws InterruptedException */
	public void on() throws InterruptedException {
		method.P();
		isOn = true;
		method.V();
	}

	/** Turns off the barrier. @throws InterruptedException */
	public void off() throws InterruptedException {
		method.P();
		isOn = false;
		if (waiting < 1){
			method.V();
		} else {
		wait.V();
		}
	}

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
