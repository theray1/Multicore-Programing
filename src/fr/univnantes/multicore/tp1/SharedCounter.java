package fr.univnantes.multicore.tp1;

import fr.univnantes.multicore.Argument;

public class SharedCounter {

	static int value = 0;
	
	// TODO : invert comments on next two lines
	// synchronized static void increment() {
	static synchronized void increment() {
		++value;
	}

	// TODO : invert comments on next two lines
	// synchronized static void decrement() {
	static void decrement() {
		synchronized(SharedCounter.class) {
			--value;
		}		
	}
	
	static void doSomething(int iterations) {
		for(int j = 0; j < iterations; ++j) {
			increment();
			decrement();
		}
	}
	

	public static void main( String args[] ) throws InterruptedException {

		// Get the number of iterations in the loops executed by the threads
		int iterations = Argument.get(args, "Number of loop iterations?");

		// Create two threads (see below if you do not know lambda expressions)
		Thread t1 = new Thread(() -> doSomething(iterations));
		Thread t2 = new Thread(() -> doSomething(iterations));

		// Start all threads
		t1.start();
		t2.start();

		// Wait until all threads have terminated
		t1.join();
		t2.join();

		// TODO: Predict the expected output
		// Explain the observation
		System.out.println("Value: " + value);

	}

}

/*
 * A brief note on Lambda expressions:
 * 
 * The instruction:
 * 
 * new Thread(() -> {...});
 * 
 * is interpreted as 
 * 
 * Runnable runnable = () -> {...};
 * new Thread(runnable);
 * 
 * where the first line creates the anonymous class:
 * 
 * Runnable runnable = new Runnable () {
 *   public void run() { 
 *     ...
 *   }
 * };
 * 
 * that is itself equivalent to:
 * 
 * Runnable runnable = new MyRunnable();
 * 
 * class MyRunnable implements Runnable {
 *   public void run() { 
 *     ...
 *   }
 * }
 * 
 * @see https://www.jmdoudoux.fr/java/dej/chap-lambdas.htm for more explanations on lambda expressions
 */