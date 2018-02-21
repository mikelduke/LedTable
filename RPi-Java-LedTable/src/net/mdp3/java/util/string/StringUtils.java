/**
 * 
 */
package net.mdp3.java.util.string;

/**
 * @author Mikel
 *
 */
public class StringUtils {
	public static int tryParseInt(String str) throws Exception {
		int ret = 0;
		
		try {
			ret = Integer.parseInt(str);
		} catch (NumberFormatException nfe) {
			throw new Exception("Invalid String " + str);
		}

		return ret;
	}
	
	public static <E extends Enum<E>> String[] enumToString(Class<E> enumData) {
		E[] enumAr = enumData.getEnumConstants();
		String namesAr[] = new String[enumAr.length];
		
		for (int i = 0; i < enumAr.length; i++) {
			namesAr[i] = enumAr[i].name();
		}
		
		return namesAr;
	}
}
