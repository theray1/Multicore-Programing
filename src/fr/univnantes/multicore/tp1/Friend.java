package fr.univnantes.multicore.tp1;

import fr.univnantes.multicore.InterruptedExceptions;

public class Friend {

	private final String nom;

	public Friend(String nom) {
		this.nom = nom;
	}

	public synchronized void salute(Friend friend) throws InterruptedException {
		System.out.println(this.nom + " dit : \"je salue mon ami " + friend.nom + "\"");
		Thread.sleep(1000);
		friend.answer(this);
	}

	public synchronized void answer(Friend friend) {
		System.out.println(
				this.nom + 
				" dit : \"mon ami " + 
				friend.nom + 
				" m'a salué et je le salue en retour\"");
	}


	/*
	 * TODO: Use the debugger to display the following outputs.
	 * 
	 * Output 1:
	 * Alphonse dit : "je salue mon ami Gaston"
	 * Gaston dit : "mon ami Alphonse m'a salué et je le salue en retour"
	 * Gaston dit : "je salue mon ami Alphonse"
	 * Alphonse dit : "mon ami Gaston m'a salué et je le salue en retour"
	 *
	 * Output 2:
	 * Alphonse dit : "je salue mon ami Gaston"
	 * Gaston dit : "je salue mon ami Alphonse"
	 */
	public static void main(String[] args) {

		final Friend alphonse = new Friend("Alphonse");
		final Friend gaston = new Friend("Gaston");

		new Thread(InterruptedExceptions.ignore(() -> 
			alphonse.salute(gaston)
		)).start();
		
		new Thread(InterruptedExceptions.ignore(() -> 
			gaston.salute(alphonse)
		)).start();

	}
}


