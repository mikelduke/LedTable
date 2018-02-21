/**
 * 
 */
package net.mdp3.java.util.test;

import java.io.FileNotFoundException;
import java.io.IOException;

import net.mdp3.java.util.file.SimpleFileIO;

/**
 * Test script for the SimpleFileIO class.
 * 
 * Writes a string to a file, loads the file, then appends a string to file 
 * and reloads the contents.
 * 
 * @author Mikel
 *
 */
public class SimpleFileIOTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String text = "Test String!!@RWSF";
		try {
			SimpleFileIO.writeStringToFile("test.txt", text);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			System.out.println(SimpleFileIO.loadFileToString("test.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		try {
			SimpleFileIO.appendStringToFile("test.txt", "Appending!");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			System.out.println(SimpleFileIO.loadFileToString("test.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}

}
