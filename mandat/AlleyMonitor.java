package mandat;

public class AlleyMonitor extends Alley{

	public AlleyMonitor(CarDisplayI display) {
		super(display);
	}

	private final static int MAX_IN_DIR = 4;

	private int count = 0;
	private int since_last_dir = 0; // to guarentee the cars wont wait for too
									// long.

	public synchronized void enter(int n) throws InterruptedException {
		while (true) {
			if (rightDir(n) && since_last_dir < MAX_IN_DIR) {
				break;
			} else if (count == 0) {
				dir = getDir(n);
				since_last_dir = 0;
				break;
			}
			this.wait();
		}
		count++;
		since_last_dir++;
		return;
	}

	public synchronized void leave(int n) {
		count--;
		if (count < 1) {
			this.notifyAll();
		}
	}

}
