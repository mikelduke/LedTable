package net.mdp3.java.util.webservice;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;

@SuppressWarnings("restriction")
public class WebserviceUtil {
	private final static String CLAZZ = WebserviceUtil.class.getName();
	private final static Logger LOG = Logger.getLogger(CLAZZ);
	
	private WebserviceUtil() { }

	/**
	 * Splits the requestURI to a hashmap after the ? by splitting on =
	 * 
	 * @param t
	 * @return
	 */
	public static Map<String, String> getParamsMap(HttpExchange t) {
		LOG.fine("Method: " + t.getRequestMethod());
		
		Map<String, String> map = new HashMap<String, String>();
		
		//get request url /get/command info
		String command = t.getRequestURI().toString();
		LOG.fine("Command: " + command);
		
		String params = command.substring(command.indexOf("?") + 1);
		
		if (params.length() > 0) {
			String[] vars  = params.split("&");
			for (int i = 0; i < vars.length; i++) {
				LOG.finer("var " + i + ": " + vars[i]);
				
				String[] parmPair = vars[i].split("=");
				if (parmPair.length == 2) {
					LOG.finer("Key: " + parmPair[0] + " Value: " + parmPair[1]);
					
					map.put(parmPair[0], parmPair[1]);
				}
			}
		}
		
		return map;
	}

	/**
	 * Util method to write the WSResponse object, headers, code, and body to a httpExchange
	 * 
	 * @param httpExchange
	 * @param response
	 * @throws IOException
	 */
	public static void writeResponse(HttpExchange httpExchange, WSResponse response) throws IOException {
		Map<String, List<String>> headers = response.getHeadersMap();
		for (String header: headers.keySet()) {
			for (String value: headers.get(header)) {
				httpExchange.getResponseHeaders().add(header, value);
			}
		}
		
		httpExchange.sendResponseHeaders(response.getResponseCode(), response.getResponseBody().length);
		
		OutputStream os = httpExchange.getResponseBody();
		os.write(response.getResponseBody());
		
		os.close();
	}
}
