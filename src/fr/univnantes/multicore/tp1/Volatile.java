package fr.univnantes.multicore.tp1;

import fr.univnantes.multicore.InterruptedExceptions;

/**
 * @author Matthieu Perrin, inspired from https://github.com/thibaultdelor/InvalidCodeBlog
 */
public class Volatile {

	// TODO: comment/uncomment the two following lines:
	//static volatile int shared = 0;
	static int shared = 0;

	
	static void writer() throws InterruptedException {
		// Every 1/2 second, write the next integer value to shared
		for(int i = 1; i <= 5; ++i) {
			System.out.println("Write shared to " + i);
			shared = i;
			Thread.sleep(500);
		}
	}

	
	static void reader() {
		// Print each i as soon as it sees it written to shared
		for(int i = 1; i <= 5; ++i) {
			while (shared < i); // wait until value >= i
			System.out.println("Read shared: " + i);
		}
	}

	
	public static void main(String[] args) throws InterruptedException {
		new Thread(Volatile::reader).start();
		Thread.sleep(250);
		new Thread(InterruptedExceptions.ignore(Volatile::writer)).start();
	}
	
	
	
	
//	
//	/**
//	 * Invokes Thread.sleep and ignores the exception
//	 * @param time the time to sleep
//	 */
//	private static void sleep(int time) {
//		try { 
//			Thread.sleep(time); 
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}
}
