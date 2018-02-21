/**
 * 
 */
package net.mdp3.java.util.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * @author Mikel
 *
 */
public class SimpleFileIO {
	
	public static void writeStringToFile(String fileName, String s) throws IOException {
		writeStringToFile(new File(fileName), s);
	}
	
	public static void writeStringToFile(File file, String s) throws IOException {
		BufferedWriter bw = null;
		
		bw = new BufferedWriter(new FileWriter(file));
		bw.write(s);
		bw.close();
	}
	
	public static String loadFileToString(String fileName) throws FileNotFoundException {
		return loadFileToString(new File(fileName));
	}
	
	public static String loadFileToString(File file) throws FileNotFoundException {
		StringBuilder sb = new StringBuilder();
		Scanner sc = new Scanner(file);
		
		while (sc.hasNext()) {
			sb.append(sc.nextLine()).append("\n");
		}
		sc.close();
		
		return sb.toString();
	}
	
	public static void appendStringToFile(String fileName, String s) throws IOException, FileNotFoundException {
		appendStringToFile(new File(fileName), s);
	}
	
	public static void appendStringToFile(File file, String s) throws IOException, FileNotFoundException {
		String fileContents = loadFileToString(file);
		
		if (!fileContents.endsWith("\n")) fileContents += "\n";
		fileContents += s;
		
		writeStringToFile(file, fileContents);
	}
}
