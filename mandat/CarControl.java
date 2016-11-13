package mandat;
//Prototype implementation of Car Control

//Mandatory assignment
//Course 02158 Concurrent Programming, DTU, Fall 2016

//Hans Henrik Lovengreen    Oct 3, 2016

import java.awt.Color;

class Gate {

	Semaphore g = new Semaphore(0);
	Semaphore e = new Semaphore(1);
	boolean isopen = false;

	public void pass() throws InterruptedException {
		g.P();
		g.V();
	}

	public void open() {
		try {
			e.P();
		} catch (InterruptedException e) {
		}
		if (!isopen) {
			g.V();
			isopen = true;
		}
		e.V();
	}

	public void close() {
		try {
			e.P();
		} catch (InterruptedException e) {
		}
		if (isopen) {
			try {
				g.P();
			} catch (InterruptedException e) {
			}
			isopen = false;
		}
		e.V();
	}

	public void reset() {
		g = new Semaphore(0);
		e = new Semaphore(1);
		isopen = false;
	}

}

class Car extends Thread {

	CarDisplayI cd; // GUI part

	static private Grid grid = new Grid();

	static private Alley alley;

	int basespeed = 100; // Rather: degree of slowness
	int variation = 50; // Percentage of base speed

	int no; // Car number
	Pos startpos; // Startpositon (provided by GUI)
	Pos barpos; // Barrierpositon (provided by GUI)
	Color col; // Car color
	Gate mygate; // Gate at startposition

	int speed; // Current car speed
	Pos curpos; // Current position
	Pos newpos; // New position to go to

	boolean alive = true;

	boolean isMoving = false;

	public Car(int no, CarDisplayI cd, Gate g, Alley a) {

		alley = a;

		this.no = no;
		this.cd = cd;
		mygate = g;
		startpos = cd.getStartPos(no);
		barpos = cd.getBarrierPos(no); // For later use

		col = chooseColor();

		// do not change the special settings for car no. 0
		if (no == 0) {
			basespeed = 0;
			variation = 0;
			setPriority(Thread.MAX_PRIORITY);
		}
	}

	public synchronized void setSpeed(int speed) {
		if (no != 0 && speed >= 0) {
			basespeed = speed;
		} else
			cd.println("Illegal speed settings");
	}

	public synchronized void setVariation(int var) {
		if (no != 0 && 0 <= var && var <= 100) {
			variation = var;
		} else
			cd.println("Illegal variation settings");
	}

	synchronized int chooseSpeed() {
		double factor = (1.0D + (Math.random() - 0.5D) * 2 * variation / 100);
		return (int) Math.round(factor * basespeed);
	}

	private int speed() {
		// Slow down if requested
		final int slowfactor = 3;
		return speed * (cd.isSlow(curpos) ? slowfactor : 1);
	}

	Color chooseColor() {
		return Color.blue; // You can get any color, as longs as it's blue
	}

	Pos nextPos(Pos pos) {
		// Get my track from display
		return cd.nextPos(no, pos);
	}

	boolean atGate(Pos pos) {
		return pos.equals(startpos);
	}

	public void run() {
		try {

			speed = chooseSpeed();
			curpos = startpos;
			cd.mark(curpos, col, no);

			// Runs until it gets interrupted (Taken out for repair).
			// If it is taken out for repair, then the thread will be
			// broken down after the data has been reset.
			// Then it will be restarted by a new thread. Therefore
			// repairing is equiv. to killing a thread and starting a
			// new.
			// Note: Sleeps will always check for interrupts. Semaphores
			// will not, if they can be grabbed. Therefore
			// checkInterrupts();
			// This has been done from the monitor-alley.

			while (true) {

				synchronized (this) {
					while (!alive) {// Waits while the car is being "Repaired".
						this.wait();
					}

					try {

						sleep(speed());// May throw interruptedexception, will
										// check for interrupts

						// Exits the gate, if the gate is open.
						if (atGate(curpos)) {
							mygate.pass();// May check for interrupts, and may
											// throw an interruptedexception
							speed = chooseSpeed();
						}

						// Synchronizes with the barrier. This functionality is
						// not working with car repairs.
						if (CarControl.barrier.atBarrier(curpos, no)) {
							CarControl.barrier.sync();// May check for
														// interrupts, and may
														// throw an
														// interruptedexception
						}

						newpos = nextPos(curpos);

						// Enters the alley, if the alley is to be entered.
						if (alley.isEntering(curpos, newpos)) {
							alley.enter(no);// May check for interrupts, and may
											// throw an interruptedexception
						}

						grid.enter(newpos);// May check for interrupts, and may
											// throw an interruptedexception

						// If the code has reached thus far, the car will "move"
						// on the grid and display
						// Therefore, the car is between grids.
						isMoving = true;

						// Move to new position
						cd.clear(curpos);
						cd.mark(curpos, newpos, col, no);
						sleep(speed());// Checks for interrupts. If the thread
										// survives this, it survives an
										// iteration in the loop, since no
										// command after in the loop can throw
										// an interruptedexception

						// The car will move. See comment above for
						// sleep(speed);
						isMoving = false;

						// Draws the car
						cd.clear(curpos, newpos);
						cd.mark(newpos, col, no);

						// Sets the position
						Pos oldpos = curpos;
						curpos = newpos;

						// Leaves the alley
						if (alley.isLeaving(oldpos, curpos)) {

							alley.leave(no);
						}
						grid.leave(oldpos);

					} catch (InterruptedException e) {
						// There's been an interrupt. It is assumed that when
						// there is an interrupt, the thread Has to fix itself,
						// but become inactive. So the assumption is, that all
						// Interrupts are deactivations of cars. Is an interrupt
						// thrown while a car is
						// inactive, it is NOT
						// catched,
						// Since it must be an
						// error, and not a
						// deactivation.
						if (alive) {
							repair();
						} else {
							throw new InterruptedException();// An error
																// ocurred.
						}
						cd.println("Terminates thread " + no);
					}
				}
			}

		} catch (Exception e) {
			cd.println("Exception in Car no. " + no);
			System.err.println("Exception in Car no. " + no + ":" + e);
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param isMoving2
	 * @throws InterruptedException
	 */
	void repair() throws InterruptedException {
		alive = false;
		grid.leave(curpos);// Leaves the grid

		if (isMoving) {// Is in two grid locations, curpos and
						// newpos
			grid.leave(newpos);
			cd.clear(curpos, newpos);
		} else {
			cd.clear(curpos);
		}

		if (alley.isInAlley(curpos)) {
			// alley.leave() can only throw an Interupted exception if it's an
			// AlleySemaphore
			alley.leave(no);
		}

		// Resets car to start position
		curpos = startpos;
	}

}

public class CarControl implements CarControlI {

	static Barrier barrier;
	CarDisplayI cd; // Reference to GUI
	Car[] car; // Cars
	Gate[] gate; // Gates
	Alley a;

	public CarControl(CarDisplayI cd) {
		this.cd = cd;
		barrier = new Barrier(false, cd);
		car = new Car[9];
		gate = new Gate[9];
		a = new AlleyMonitor();
		// a = new AlleySemaphore();

		for (int no = 0; no < 9; no++) {
			gate[no] = new Gate();
			car[no] = new Car(no, cd, gate[no], a);
			car[no].start();
		}
	}

	public void startCar(int no) {
		gate[no].open();
	}

	public void stopCar(int no) {
		gate[no].close();
	}

	public void barrierOn() {
		boolean done = false;
		while (!done) {
			try {
				barrier.on();
				done = true;
			} catch (InterruptedException e) {
				System.err.println("Barrier set on interrupted. trying again.");
			}
		}
	}

	public void barrierOff() {
		boolean done = false;
		while (!done) {
			try {
				barrier.off();
				done = true;
			} catch (InterruptedException e) {
				System.err.println("Barrier set threshold interrupted. trying again.");
			}
		}
	}

	public void barrierSet(int k) {
		boolean done = false;
		while (!done) {
			try {
				barrier.setThreshold(k);
				done = true;
			} catch (InterruptedException e) {
				System.err.println("Barrier set threshold interrupted. trying again.");
			}
		}
	}

	public void removeCar(int no) {
		if (car[no].alive) {
			car[no].interrupt();// This will only work with AlleyMonitor
			cd.println("Repairing car no " + no);
		} else {
			cd.println("Car no: " + no + " already out for repair");
		}
	}

	public void restoreCar(int no) {
		if (!car[no].alive) {
			synchronized (car[no]) {
				car[no].alive = true;
				cd.mark(car[no].curpos, car[no].col, no);
				car[no].notify();
				cd.println("Car no: " + no + " repaired");
			}
		} else {
			cd.println("Car no " + no + " already running");
		}

	}

	/* Speed settings for testing purposes */

	public void setSpeed(int no, int speed) {
		car[no].setSpeed(speed);
	}

	public void setVariation(int no, int var) {
		car[no].setVariation(var);
	}

}
