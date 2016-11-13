package mandat;

public class AlleyMonitor extends Alley {

	public AlleyMonitor() {
		super();
	}

	// to improve the fairness of the alley. but still not completely fair
	private int since_last_dir = 0;
	private final static int MAX_IN_DIR = 4;

	private int count = 0;

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
