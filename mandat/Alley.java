package mandat;

import java.util.ArrayList;

public abstract class Alley {

	protected CarDisplayI display;
	
	protected boolean dir = false; // true if up, false if down.
	
	// holds the layout of the alley, in the form of positions
	private ArrayList<Pos> alley_positions = new ArrayList<Pos>();

	public Alley(CarDisplayI display) {
		this.display = display;
		// define alley positions
		for (int i = 1; i <= 10; i++) {
			alley_positions.add(new Pos(i, 0));
		}
		alley_positions.add(new Pos(1, 1));
		alley_positions.add(new Pos(1, 2));
		
	}

	public abstract void enter(int n) throws InterruptedException;

	public abstract void leave(int n) throws InterruptedException;
	
	public abstract void remove(int n, Pos position) throws InterruptedException;
	
	public boolean isEntering(Pos current, Pos next) {
		return isInAlley(next) && !isInAlley(current);
	}

	public boolean isLeaving(Pos current, Pos next) {
		return isInAlley(current) && !isInAlley(next);
	}
	
	public boolean isInAlley(Pos current){
		return alley_positions.contains(current);
	}
	
	protected boolean rightDir(int n) {
		return dir == getDir(n);
	}

	protected boolean getDir(int n) {
		return (n >= 5);
	}

}
