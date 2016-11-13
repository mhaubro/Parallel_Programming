package mandat;

public class Grid {

	final static int FREE = -54310;

	private Location[][] sema = new Location[Layout.ROWS][Layout.COLS];

	public Grid() {
		for (int y = 0; y < Layout.ROWS; y++) {
			for (int x = 0; x < Layout.COLS; x++) {
				sema[y][x] = new Location();
			}
		}
	}

	public void enter(Pos pos, int n) throws InterruptedException {
		sema[pos.row][pos.col].enter(n);
	}

	public void free(Pos pos) throws InterruptedException {
		sema[pos.row][pos.col].free();
	}

	public void remove(int n) throws InterruptedException {
		for (Location[] la : sema) {
			for (Location l : la) {
				l.remove(n);
			}
		}
	}

	private class Location {

		private int user = FREE;

		private int repair = FREE;

		private Semaphore method = new Semaphore(1);
		private Semaphore wait = new Semaphore(0);

		private int waiting = 0;

		// TODO fix the removing of a car when it exits.
		public void enter(int n) throws InterruptedException {
			while (true) {
				method.P();
				if (repair == n) {
					// mark that the car has been removed from the grid.
					repair = FREE;
					break;
				} else if (user == FREE) {
					user = n;
					break;
				}

				waiting++;
				method.V();
				wait.P();
				waiting--;

				// notify all who waits, before returning the critical region to
				// method
				notifyNext();
			}
			method.V();
		}

		public void free() throws InterruptedException {
			method.P();
			user = FREE;
			notifyNext();
		}

		/**
		 * takes control of the critical region, mark the car n for repair and
		 * Initializes a check for all waiting cars to see if they should be
		 * removed
		 * 
		 * @param n the car to be removed
		 * @throws InterruptedException
		 */
		public void remove(int n) throws InterruptedException {
			method.P();
			// if the user is n, then free the location, else mark the repair
			// and initialize a check for all waiting cars.
			if (user == n) {
				user = FREE;
			} else {
				repair = n;
			}
			notifyNext();
		}

		/**
		 * exits the critical region either to wait, if there are more waiting,
		 * or else to method.
		 */
		private void notifyNext() {
			if (waiting > 0) {
				wait.V();
			} else {
				// if the repair mark has not been found yet, it means that
				// the car is not waiting in the location.
				repair = FREE;
				method.V();
			}
		}
	}
}
