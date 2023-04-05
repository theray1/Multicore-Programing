package fr.univnantes.multicore.tp2.philosopher;

public class StarvingPhilosopher extends Philosopher {

	public StarvingPhilosopher(DinnerTable table, int philosopherId) {
		super(table, philosopherId);
	}

	@Override
	public void startEat() throws InterruptedException {
		table.takeLeftStick(philosopherId);
		table.takeRightStick(philosopherId);
	}

	@Override
	public void startThink() {
		table.dropLeftStick(philosopherId);
		table.dropRightStick(philosopherId);
	}

}
