package step5;

import java.util.ArrayList;

/**
 * Alley is an abstract class, used to simplify the implementation and shifting
 * between the semaphore and monitor implementations.
 * 
 * @author Marting og Mathias
 *
 */
public abstract class Alley {

	protected boolean dir = false; // true if up, false if down.

	// holds the layout of the alley, in the form of positions
	private ArrayList<Pos> alley_positions = new ArrayList<Pos>();

	/**
	 * Initializes the Allay with predefined positions 
	 */
	public Alley() {
		// define alley positions
		for (int i = 1; i <= 9; i++) {
			alley_positions.add(new Pos(i, 0));
		}
		alley_positions.add(new Pos(1, 1));
		alley_positions.add(new Pos(1, 2));

	}

	/**
	 * Enters the alley as the car with id equals n
	 * @param n
	 * @throws InterruptedException
	 */
	public abstract void enter(int n) throws InterruptedException;

	/**
	 * Leaves the alley as the car with id equals n
	 * @param n
	 * @throws InterruptedException
	 */
	public abstract void leave(int n) throws InterruptedException;

	/**
	 * Determines whether or not a step from current to next will course the car to enter the alley.
	 * @param current current position
	 * @param next next position
	 * @return true if entering, otherwise false.
	 */
	public boolean isEntering(Pos current, Pos next) {
		return isInAlley(next) && !isInAlley(current);
	}

	/**
	 * Determines whether or not a step from current to next will course the car to leave the alley.
	 * @param current current position
	 * @param next next position
	 * @return true if leaving, otherwise false.
	 */
	public boolean isLeaving(Pos current, Pos next) {
		return isInAlley(current) && !isInAlley(next);
	}

	/**
	 * Checks if a position is in the alley.
	 * @param position position to be checked.
	 * @return true if the position is in the alley, otherwise false.
	 */
	public boolean isInAlley(Pos position) {
		return alley_positions.contains(position);
	}

	/**
	 * Checks if the direction of car n, is equal to the current position.
	 * @param n id of the car
	 * @return true if the directions is equal, otherwise false
	 */
	protected boolean rightDir(int n) {
		return dir == getDir(n);
	}

	/**
	 * Finds the direction of a car from it's id n.
	 * @param n n is the id.
	 * @return true if up, false if down.
	 */
	protected boolean getDir(int n) {
		return (n >= 5);
	}

}
