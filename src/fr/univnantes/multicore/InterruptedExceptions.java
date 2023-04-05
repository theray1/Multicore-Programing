package fr.univnantes.multicore;

/**
 * A tool used to simplify exception handling in snippet examples. 
 * This class should probably not be used in production!
 * @author Matthieu Perrin
 */
public class InterruptedExceptions {

	public static interface RunnableWithInterruptedException {
		void run() throws InterruptedException;
	}

	public static Runnable ignore(RunnableWithInterruptedException runnable) {
		return new Runnable() {
			@Override
			public void run() {
				try {
					runnable.run();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
	}

}
