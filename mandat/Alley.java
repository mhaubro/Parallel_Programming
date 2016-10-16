package mandat;

import java.util.ArrayList;

public class Alley {
	
	private boolean dir = false; // true if up, false if down.
	private int count = 0; // how many cars in the alley.
	private int waiting = 0;
	
	private Semaphore method_semaphore = new Semaphore(1);
	private Semaphore is_empty = new Semaphore(1);
	
	// holds the layout of the alley, in the form of positions
	private ArrayList<Pos> alley_positions = new ArrayList<Pos>();
	
	public Alley(){
		//define alley positions
		for (int i = 1; i <= 10; i++){
			alley_positions.add(new Pos(i,0));
		}
		alley_positions.add(new Pos(1,1));
		alley_positions.add(new Pos(1,2));
	}
	
	public void enter(int n) throws InterruptedException{
		method_semaphore.P(); // Enter critical region
		
		if (count <= 0){
			dir = (n >= 5);
			count++;
		}else if (rightDir(n)){
			count++;
		}else{
			waiting++;
			method_semaphore.V(); // leaving critical region
			is_empty.P(); // wait until the alley is empty
			method_semaphore.P();
			if (--waiting > 0){// if more than one is waiting, throw a coconut at the next.
				is_empty.V();
			}
			method_semaphore.V(); // leave the critical region
			enter(n); // retry the alley entry
			return;
		}
		method_semaphore.V(); // leave critical region
	}
	
	public void leave(int n) throws InterruptedException{
		method_semaphore.P(); // Enter critical region
		count--;
		if (count < 1){
			is_empty.V();
		}
		method_semaphore.V(); // leave critical region
	}
	
	public boolean isEntering(Pos current, Pos next){
		return alley_positions.contains(next) && !alley_positions.contains(current);
	}
	
	public boolean isLeaving(Pos current, Pos next){
		return alley_positions.contains(current) && !alley_positions.contains(next);
	}
	
	private boolean rightDir(int n){
		return dir == (n >= 5);
	}

}
