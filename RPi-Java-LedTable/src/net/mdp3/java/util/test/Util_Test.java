package net.mdp3.java.util.test;

import net.mdp3.java.util.console.ConsoleReader;

/**
 * util
 * @author Mikel
 *
 * Utils Test class
 */
public class Util_Test {

	private ConsoleReader cr;
	
	/**
	 * Main entry point for Util package when testing.
	 * 
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Util_Test u = new Util_Test();
	}

	public Util_Test() {
		cr = new ConsoleReader("Utils", this);
	}
	
	public void consoleTest() {
		System.out.println("ConsoleTest!");
	}
	
	public void consoleTest2(String s) {
		System.out.println("ConsoleTest2: " + s);
	}
	
	public void quit() {
		cr.quit();
	}
}
