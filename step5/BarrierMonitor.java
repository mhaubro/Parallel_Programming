package step5;

public class BarrierMonitor extends Barrier {
	public BarrierMonitor(boolean isOn) {
		super(isOn);
	}

	boolean signal = false;

	/**
	 * Method used to wait for the barrier synchronization, to make sure
	 * that @see #threshold and only threshold drives.
	 * 
	 * @throws InterruptedException
	 *             if interrupted while doing a semaphore.P()-operation.
	 *
	 *             Strategy of the method: Enter the critical region and
	 *             indicate that you're waiting. Start wait by wait.V(); If you
	 *             are the n'th car waiting, indicate that you are not waiting,
	 *             and start a cascade of notifications to the waiting cars
	 *             doing wait.V(); When the cars are released, if a car sees
	 *             that it is the last on released, it will stop the cascade. If
	 *             there are more cars waiting, a notification is sent to the
	 *             next. All this is done in critical regions.
	 * 
	 *             Please note that only threshold cars will be sent off, since
	 *             the whole function is a critical region (method.P()) Due to
	 *             this, when threshold cars hit the barrier, they will be sent
	 *             off, and no more cars will be sent off with them.
	 */
	public synchronized void sync() throws InterruptedException {
		// method.P();// Enter Rcrit.
		if (isOn) {
			if (++waiting < threshold) {// If there are less cars waiting than
										// the threshold.
				// method.V();
				// wait.P();// Wait until there are threshold cars waiting
				while (signal && isOn) {// If the cars are sent off, new cars
										// should wait here.
					// This ensures that threshold and threshold only cars are
					// sent off
					wait();
				}

				while (!signal && isOn) {// Wait until there are threshold cars
											// // waiting
					wait();
				}

				waiting--;// Decrements waiting, since it has received a
							// go-signal.
				if (waiting < 1) {
					signal = false;
					notifyAll();
				}
				return;

				// if (waiting > 0 || threshold_change) {
				//// wait.V();// Signals another waiting car
				// return;// Starts. By signaling the other car, the baton has
				// // been passed.
				// } else {
				//// method.V();// Opens the critical method-region. No cars
				// // needs signaling.
			} else {
				waiting--;
				// The amount of total waiting cars is >= threshold.
				// wait.V();// Signals other cars
				signal = true;
				notifyAll();// Sends signal to everyone that it's cool to
							// continue

				return;// Goes through the barrier
			}
		}
	}
	// method.V();// Leaves critical region.

	/** Turns on the barrier. @throws InterruptedException */
	public synchronized void on() throws InterruptedException {
		isOn = true;
	}

	/** Turns off the barrier. @throws InterruptedException */
	public synchronized void off() throws InterruptedException {
		isOn = false;
		notifyAll();
	}

	/** This method is not implemented, and reacts by doing nothing */
	public synchronized void setThreshold(int k) throws InterruptedException {
		System.out.println("Using Monitor for Semaphore-implementation exclusive method");
	}

}
