package mandat;

public class Barrier {
	
	private int threshold = 9;
	
	private boolean isOn;
	
	private int waiting = 0;
	private Semaphore wait = new Semaphore(0);
	
	private Semaphore method = new Semaphore(1);
	
	public Barrier(boolean isOn){
		this.isOn = isOn;
	}
	
	public void sync() throws InterruptedException{
		if (isOn){
			method.P();
			if (++waiting < threshold){
				method.V();
				wait.P();
				if (--waiting > 0){
					wait.V();
					method.P();
				}
			}else{
				wait.V();
				waiting--;
			}
			method.V();
		}
	}
	
	public void on(){
		isOn = true;
	}
	
	public void off(){
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
	
	public void setThreshold(int k) {
		this.threshold = k;
	}

}
