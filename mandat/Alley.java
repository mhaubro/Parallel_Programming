package mandat;

import java.util.ArrayList;

public class Alley {
	
	private boolean dirDown = false;
	private int count = 0;
	private Semaphore control = new Semaphore(1);
	private ArrayList<Pos> alley_positions = new ArrayList<Pos>();
	
	public Alley(){
		//define alley positions
		for (int i = 1; i <= 10; i++){
			alley_positions.add(new Pos(i,0));
		}
		alley_positions.add(new Pos(1,1));
		alley_positions.add(new Pos(1,2));
	}
	
	private Semaphore alleySemaphore = new Semaphore(1);
	
	public void enter(int n) throws InterruptedException{
		control.P();
		if (goingDown(n)){
			
		}else{
			
		}
		control.V();
		alleySemaphore.P();
	}
	
	public void leave(int n) throws InterruptedException{
		alleySemaphore.V();
	}
	
	private void goIn() throws InterruptedException{
		control.P();
		count++;
		control.V();
	}
	
	private void goOut() throws InterruptedException{
		control.P();
		count--;
		control.V();
	}
	
	public boolean isEntering(Pos current, Pos next){
		return alley_positions.contains(next) && !alley_positions.contains(current);
	}
	
	public boolean isLeaving(Pos current, Pos next){
		return alley_positions.contains(current) && !alley_positions.contains(next);
	}
	
	private boolean goingDown(int n){
		return n < 5;
	}

}
