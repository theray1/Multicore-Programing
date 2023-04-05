package fr.univnantes.multicore.tp1;

import fr.univnantes.multicore.InterruptedExceptions;

public class ThreadVizualizer {

	static void t1Code() throws InterruptedException {
		Thread t2 = new Thread(InterruptedExceptions.ignore(() -> factorial(20)), "factorial");
		t2.start();
		t2.join();
	}

	static int factorial(int n) throws InterruptedException {
		Thread.sleep(10);
		if(n==0) return 1;
		else return n*factorial(n-1);
	}
	
	public static void main(String args[]) throws InterruptedException {
		
		new Thread(InterruptedExceptions.ignore(() -> t1Code()), "Thread t1").start();

		Thread.sleep(20);
		
		var stackTraces = Thread.getAllStackTraces();
		for(Thread t : stackTraces.keySet()) {
			System.out.println(t);
			for(var element : stackTraces.get(t)) {
				System.out.print('\t');
				System.out.println(element);
			}
		}
	}	

}

