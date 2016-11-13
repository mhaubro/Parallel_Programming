package mandat;

public class AlleySemaphore extends Alley {

	public AlleySemaphore(CarDisplayI display) {
		super(display);
	}

	private final int NO_CAR = -1;

	private int count = 0; // how many cars in the alley.
	private int waiting = 0;
	private int repair_target = NO_CAR;

	private Semaphore method_semaphore = new Semaphore(1);
	private Semaphore is_empty = new Semaphore(0);

	/**
	 * 
	 * @param n,
	 *            is the identity of the car, who is entering. n is used to
	 *            determine the direction of the car.
	 * @throws InterruptedException,
	 *             since the method is running on semaphores, who throws the
	 *             exception
	 */
	public void enter(int n) throws InterruptedException {
		/*
		 * the car will repeatedly check if it can enter the alley, and if not,
		 * it will wait till it can
		 */
		while (true) {
			method_semaphore.P();

			// checks if the car can enter the alley, by breaking the
			// waiting-loop
			if (count < 1) {
				dir = getDir(n);
				break; // enter the alley
			} else if (rightDir(n)) {
				break; // enter the alley
			}

			/*
			 * increment the waiting counter so others know that there's a car
			 * waiting, then leave the critical region and try to enter on the
			 * "is_empty" semaphore
			 */
			waiting++;
			method_semaphore.V();
			is_empty.P();
			waiting--;

			// if the car is set to be repaired, it will exit the method with
			// false to signal that entering was not a succes.
			if (repair_target == n) {
				notifyNext();
				return;
			}

			/*
			 * decrement the waiting counter, and check whether or not there are
			 * more cars, who is waiting. If this is the case the next will be
			 * notified by leaving the critical region with is_empty. else the
			 * region will be left with the method semaphore, so everyone else
			 * can continue.
			 */

			notifyNext();
		}

		/* The car has now entered the alley, and the counter is incremented */
		count++;
		method_semaphore.V();
		return;
	}

	public void leave(int n) throws InterruptedException {
		// display.println("m leave");
		method_semaphore.P(); // Enter critical region
		count--;
		if (count < 1 && waiting > 0) {
			is_empty.V();
		} else {
			method_semaphore.V(); // leave critical region
		}
	}

	public void remove(int n, Pos position) throws InterruptedException {
		method_semaphore.P();
		if (isInAlley(position)) {
			count--;
			if (count < 1 && waiting > 0) {
				is_empty.V();
			} else {
				method_semaphore.V(); // leave critical region
			}
		} else {
			if (waiting > 0) {
				repair_target = n;
				is_empty.V();
			} else {
				method_semaphore.V();
			}
		}

	}

	private void notifyNext() {
		if (waiting > 0) {
			is_empty.V();
		} else {
			repair_target = NO_CAR;
			method_semaphore.V();
		}
	}

}
