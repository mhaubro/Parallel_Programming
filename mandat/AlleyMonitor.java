package mandat;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AlleyMonitor {

	private final static int MAX_IN_DIR = 4;

	private boolean dir = false; // true if up, false if down.
	private int count = 0;
	private int since_last_dir = 0; // to guarentee the cars wont wait for too
									// long.

	// holds the layout of the alley, in the form of positions
	private ArrayList<Pos> alley_positions = new ArrayList<Pos>();

	public AlleyMonitor() {
		// define alley positions
		for (int i = 1; i <= 10; i++) {
			alley_positions.add(new Pos(i, 0));
		}
		alley_positions.add(new Pos(1, 1));
		alley_positions.add(new Pos(1, 2));
	}

	public synchronized void enter(int n) throws InterruptedException {
		while (true) {
			if (rightDir(n)) {
				if (since_last_dir < MAX_IN_DIR) {
					break;
				}
			} else if (count == 0) {
				dir = getDir(n);
				since_last_dir = 0;
				break;
			}
			this.wait();
		}
		count++;
		since_last_dir++;
	}

	public synchronized void leave(int n) {
		count--;
		if (count < 1) {
			this.notifyAll();
		}
	}

	public boolean isEntering(Pos current, Pos next) {
		return alley_positions.contains(next) && !alley_positions.contains(current);
	}

	public boolean isLeaving(Pos current, Pos next) {
		return alley_positions.contains(current) && !alley_positions.contains(next);
	}

	private boolean rightDir(int n) {
		return dir == getDir(n);
	}

	private boolean getDir(int n) {
		return (n >= 5);
	}

}
