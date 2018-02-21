/**
 * 
 */
package net.mdp3.java.util.test;

import java.io.File;
import java.net.URL;

import net.mdp3.java.util.console.ConsoleReader;

/**
 * @author Mikel
 *
 */
public class BatchTest {
	
	private ConsoleReader cr;
	
	/**
	 * Main entry point for Util package when testing.
	 * 
	 * @param args
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		BatchTest b = new BatchTest();
	}

	public BatchTest() {
		cr = new ConsoleReader("Batch", this);
		
		URL url = getClass().getResource("BatchTest.txt");
		File file = new File(url.getPath());
		cr.openBatch(file);
		cr.startBatch();
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
