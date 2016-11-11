package mandat;

public class Barrier {
	
	private CarDisplayI display;
	
	private int threshold = 9;
	private boolean threshold_change = false;
	
	private boolean isOn;
	
	private int waiting = 0;
	private Semaphore wait = new Semaphore(0);
	
	private Semaphore method = new Semaphore(1);
	
	public Barrier(boolean isOn, CarDisplayI display){
		this.isOn = isOn;
		this.display = display;
	}
	
	public void sync() throws InterruptedException{
		method.P();
		if (isOn){
			if (++waiting < threshold){
				method.V();
				wait.P();//enter the critical region. and notify all who is waiting
				waiting--;
				if (waiting > 0 || threshold_change){
					wait.V();
					return;
				}else{
					//Thread.sleep(100);
					display.println("Barrier: All notified.");
					method.V();
					return;
				}
			}else{
				waiting--;
				wait.V();
				return;
			}
		}
		method.V();
	}
	
	public void on() throws InterruptedException{
		method.P();
		isOn = true;
		method.V();
	}
	
	public void off() throws InterruptedException{
		method.P();
		isOn = false;
		wait.V();
	}
	
	public boolean atBarrier(Pos current, int N){
		
		// checks if the x-coordinate is within the barrier.
		if (current.col < 3)
			return false;
		
		if (N < 5){ // cars going up
			if (current.row == 6)
				return true;
		}else{ // cars going down
			if (current.row == 5)
				return true;
		}
		return false;
	}
	
	public void setThreshold(int k) throws InterruptedException {
		threshold_change = true;
		wait.P();
		threshold_change = false;
		threshold = k;
		if (waiting > 0){
			wait.V();
		}else{
			method.V();
		}
	}

}
