package step5;

/**
 * This class implements the alley from step 1.
 * @author Martin og Mathias
 */
public class AlleySemaphore extends Alley {

	// both semaphores is a way of entering the same critical region. which
	// results in a kind of wait- or method-state.
	/**
	 * semaphore used for standard entering of the critical region in method
	 * state
	 */
	private Semaphore method_semaphore = new Semaphore(1);
	/** semaphore used to enter the critical region in wait-state */
	private Semaphore wait = new Semaphore(0);

	/**
	 * Initializes the Alley with predefined positions 
	 */
	public AlleySemaphore() {
		super();
	}

	/**
	 * Enters the alley as the car with id equals n
	 * @param n
	 * @throws InterruptedException
	 */
	public void enter(int n) throws InterruptedException {
		// the car will repeatedly check if it can enter the alley, and if not,
		// it will wait until it will get notified.
		while (true) {
			// enters critical region when the critical region is released to
			// method_semaphore.
			method_semaphore.P();

			// checks if the car can enter the alley. if this is the case the
			// loop will be broken.
			if (count < 1) {
				dir = getDir(n);
				break; // enter the alley
			} else if (rightDir(n)) {
				break; // enter the alley
			}

			// increment the waiting counter so others know that there's a car
			// waiting, then leave the critical region and try to enter on the
			// "wait" semaphore.
			waiting++;
			method_semaphore.V();
			wait.P();
			waiting--;

			// decrement the waiting counter, and check whether or not there are
			// more cars, who is waiting. If this is the case the next will be
			// notified by leaving the critical region with "wait.V()". else the
			// region will be left with the method semaphore, so everyone else
			// can continue.
			if (waiting > 0) {
				wait.V();
			} else {
				method_semaphore.V();
			}
		}

		/* The car has now entered the alley, and the counter is incremented */
		count++;
		method_semaphore.V();
		return;
	}

	/**
	 * Leaves the alley as the car with id n. and will then notify the next car
	 * who waits, that it can enter.
	 * @param n n is the id of the car
	 * @throws InterruptedException, if the semaphore is interupted.
	 */
	public void leave(int n) throws InterruptedException {
		// display.println("m leave");
		method_semaphore.P(); // Enter critical region
		count--;
		if (count < 1 && waiting > 0) {
			wait.V();
		} else {
			method_semaphore.V(); // leave critical region
		}
	}


}
