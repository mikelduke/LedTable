/**
 * 
 */
package net.mdp3.java.rpi.ledtable.effects.playlist;

/**
 * @author Mikel
 *
 */
public class PlaylistTimer extends Thread {
	private long delay = 100;
	private long setTime = 100;
	private long currentTime = 0;
	
	private volatile boolean run = false;
	private volatile boolean isTiming = false;
	private Playlist pl = null;
	
	public PlaylistTimer(int seconds, Playlist pl) {
		this.pl = pl;
		setTime(seconds);
	}
	
	@Override
	public final void run() {
		run = true;
		
		while(run) {
			delay();
			checkTimer();
		}
	}
	
	/**
	 * Resets the current time, sets the new time, and stops the current timer
	 * 
	 * @param newTime
	 */
	public void setTime(int newTime) {
		stopTimer();
		this.setTime = newTime * 1000;
		this.currentTime = 0;
	}
	
	private void checkTimer() {
		if (isTiming()) {
			timerTick();
			if (this.currentTime >= this.setTime && run) {
				stopTimer();
				pl.next();
			}
		}
	}
	
	public boolean isRunning() {
		return run;
	}
	
	public long getDelay() {
		return delay;
	}
	
	public void setDelay(long delay) {
		this.delay = delay;
	}
	
	public void shutdownTimer() {
		stopTimer();
		this.run = false;
	}
	
	private void timerTick() {
		//TODO change to use real time instead of just delay time
		this.currentTime += this.delay;
	}
	
	private void delay() {
		try {
			Thread.sleep(this.delay);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public synchronized void startTimer() {
		this.isTiming = true;
	}
	
	public synchronized void stopTimer() {
		this.isTiming = false;
	}
	
	public synchronized boolean isTiming() {
		return this.isTiming;
	}
	
	public void resetTimer() {
		this.currentTime = 0;
	}
}
