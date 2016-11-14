package step3;

/**
 * 
 * @author Martin og Mathias
 * The grid is a datastructure that ensures that cars doesn't bump into each other
 * The grid consists of semaphores in the size of the car-"map". The grid consists solely of
 * an array of semaphores.
 *
 */

public class Grid {
	
	private Semaphore[][] sema = new Semaphore[Layout.ROWS][Layout.COLS];
	
	public Grid(){
		for (int y = 0; y < Layout.ROWS; y++){
			for (int x = 0; x < Layout.COLS; x++){
				sema[y][x] = new Semaphore(1);
			}
		}
	}
	
	
	/**
	 * Enters a position in the grid. Pos is the position of the entering car.
	 * Since theres is a semaphore.P()-operation, this function may block and/or throw an exception
	 */
	public void enter(Pos pos) throws InterruptedException{
		sema[pos.row][pos.col].P();
	}

	/**
	 * Leaves a position in the grid.
	 * Pos is the position that is leaved.
	 */
	public void leave(Pos pos){
		sema[pos.row][pos.col].V();
	}
}
