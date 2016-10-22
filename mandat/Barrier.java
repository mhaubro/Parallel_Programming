package mandat;

public class Barrier {
	
	private int threshold = 8;
	
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
				method.P();
				waiting--;
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
	}
	
	public void atBarrier(Pos current, Pos next){
		// TODO implement check, to se if the car is at the barrier.
	}

}
