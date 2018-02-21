/**
 * 
 */
package net.mdp3.java.util.console;

import java.util.List;

/**
 * Console Command Batch Process
 * 
 * This class spawns a new thread to run the commands passed in to the batch 
 * List and runs them with the specified delay in between each. This process 
 * can be set to loop for continuous looping. Commands are not run against the 
 * system shell, but are run against the ConsoleReader object which uses 
 * Reflection to run methods on the object passed in.
 * 
 * This class allows for an object to be set up with methods, and allows for a 
 * text file list of methods to be run against it automatically. This can be 
 * used to automate a configurable setup for a program and other things. 
 * 
 * @author Mikel
 *
 */
public class ConsoleBatch extends Thread {
	private ConsoleReader consoleHandler;
	private List<ConsoleCommand> batch;
	private boolean run = false;
	private int commandIndex = 0;
	private boolean looping = false;
	
	public ConsoleBatch(ConsoleReader cr, List<ConsoleCommand> v) {
		this.consoleHandler = cr;
		this.batch = v;
	}
	
	public void run() {
		while(run) {
			if (this.commandIndex >= batch.size() && this.looping) this.commandIndex = 0;
			else if (this.commandIndex >= batch.size()) {
				run = false;
				return;
			}
			
			String command = batch.get(this.commandIndex).getCommand();
			System.out.println("Batch Command: " + command);
			try {
				this.consoleHandler.parseInput(command);
			} catch (Exception e1) {
				e1.printStackTrace();
				System.out.println("Error in command batch with command " + command + "\n" + e1);
			}
			
			try {
				Thread.sleep(batch.get(this.commandIndex).getDelay());
			} catch (InterruptedException e) {
				System.out.println("Error sleeping Command Batch " + e);
				e.printStackTrace();
			}
			this.commandIndex++;
		}
	}
	
	public void startBatch() {
		if (batch.size() > 0 && !run) {
			run = true;
			this.start();
		}
	}
	
	public void stopBatch() {
		run = false;
	}
	
	public void setLooping(boolean b) {
		this.looping = b;
	}
	
	public boolean isRunning() {
		return run;
	}
}

class ConsoleCommand {
	private String command = "";
	private long waitTime = 100;
	
	public ConsoleCommand(String c, long l) {
		this.command = c;
		this.waitTime = l;
	}
	
	public String getCommand() {
		return this.command;
	}
	
	public long getDelay() {
		return this.waitTime;
	}
}