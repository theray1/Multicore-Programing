package fr.univnantes.multicore.examples.locks;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * A starvation-free lock by Lamport, that only uses read/write registers
 * Problem: we need to know the number of participants before the execution
 * @author Matthieu Perrin
 */
public class BackeryLock {
	private final int nbThreads;
	private final AtomicIntegerArray hello;
	private final AtomicIntegerArray clock;

	public BackeryLock(int n) {
		nbThreads = n;
		hello = new AtomicIntegerArray(n);
		clock = new AtomicIntegerArray(n);
	}
		
	public void lock(int threadId) {
		hello.set(threadId, 1);               // Say hello
		int myClock=0;
		for(int i = 0; i<nbThreads; i++) {	  // Take a ticket greater that all other tickets
			int otherClock = clock.get(i);
			if(otherClock > myClock) myClock = otherClock;
		}
		clock.set(threadId, myClock+1);
	    while(blocked(threadId, myClock));    // Wait for your turn
	}
	
	private boolean blocked(int threadId, int myClock) {
		for(int i = 0; i<nbThreads; i++) {
			// Should thread_i go before us?
			if (hello.get(i) == 1) {
				// Lexicographic order on (clock, threadId)
				int otherClock = clock.get(i);
				if (otherClock < myClock) return true;
				if (otherClock == myClock && i < threadId) return true;
			}
		}
		return false;
	}

	public void unlock(int threadId) {
		// Say goodbye
		hello.set(threadId, 0);
	}	
}
