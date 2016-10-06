package mandat;

public class Alley {
	
	private Semaphore alleySemaphore = new Semaphore(1);
	
	public void enter(int i) throws InterruptedException{
		alleySemaphore.P();
	}
	
	public void leave(int i){
		alleySemaphore.V();
	}

}
