package mandat;

public class Grid {
	
	private Semaphore[][] sema = new Semaphore[Layout.ROWS][Layout.COLS];
	
	public Grid(){
		for (int y = 0; y < Layout.ROWS; y++){
			for (int x = 0; x < Layout.COLS; x++){
				sema[y][x] = new Semaphore(1);
			}
		}
	}
	
	public void enter(Pos pos) throws InterruptedException{
		sema[pos.row][pos.col].P();
	}

	public void leave(Pos pos){
		sema[pos.row][pos.col].V();
	}
}
