package net.mdp3.java.util.settings;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

public class CmdLineProperties {
	
	private CmdLineProperties() { }
	
	public static Properties toProperties(String[] args) throws IOException {
		Properties p = new Properties();
		
		StringBuilder sb = new StringBuilder();
		for (String arg : args) {
			sb.append(arg).append("\n");
		}
		
		String cmdLineProps = sb.toString();
		
		p.load(new StringReader(cmdLineProps));
		
		return p;
	}
}
