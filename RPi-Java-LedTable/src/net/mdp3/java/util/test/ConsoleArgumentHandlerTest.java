/**
 * 
 */
package net.mdp3.java.util.test;

import java.util.HashMap;
import java.util.List;

import net.mdp3.java.util.console.ConsoleArgumentHandler;

/**
 * @author Mikel
 *
 */
public class ConsoleArgumentHandlerTest {

	/**
	 * 
	 */
	public ConsoleArgumentHandlerTest() {
		
	}
	
	public void method1() {
		System.out.println("run method 1");
	}
	
	public void method2(String value) {
		System.out.println("run method 2: " + value);
	}
	
	public void setTest1() {
		System.out.println("setTest1 test");
	}
	
	public void setTest2(String t) {
		System.out.println("setTest2 test " + t);
	}
	
	public void setATest3() {
		System.out.println("setATest3");
	}
	
	public void setTest4(Object o) {
		System.out.println("setTest4");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("ConsoleArgumentHandler Test Start");
		ConsoleArgumentHandlerTest caht = new ConsoleArgumentHandlerTest();
		ConsoleArgumentHandler cah = new ConsoleArgumentHandler(caht, args);
		
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("m", "method1");
		map.put("m2", "method2");
		cah.setShortCutMap(map);
		
		try {
			cah.addShortcut("m1", "method1");
			cah.addShortcut("m22", "method2");
			cah.addShortcut("m3", "method3"); //Should throw an exception
		} catch (NoSuchMethodException e1) {
			e1.printStackTrace();
			System.out.println("Error: Method not found or wrong param types: " + e1);
		}
		
		try {
			List<String> failedCmds = cah.runArgs();
			
			for (String cmd : failedCmds)
				System.out.println("Failed: " + cmd);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//Test autoshortcut
		cah.clearShortcuts();
		cah.autoCreateShortCuts();
		System.out.println(cah.getShortcutMap());
		//should have {test1=setTest1, atest3=setATest3, t=setTest1, a=setATest3, test2=setTest2}
		//methods that start with set, have 0 or 1 string param, and 1 letter shortcuts for the first new letter found
		
		System.out.println("ConsoleArgumentHandler Test End");
	}

}
