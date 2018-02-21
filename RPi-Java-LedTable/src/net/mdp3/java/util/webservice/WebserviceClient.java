/**
 * 
 */
package net.mdp3.java.util.webservice;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

/**
 * @author Mikel
 *
 */
public class WebserviceClient {
	private final static Logger LOG = Logger.getLogger(WebserviceClient.class.getName());
	private final static String NAME = "Util-WebserviceClient";
	
	/**
	 * Opens an HTTP Connection to the specified URL and returns the result
	 * 
	 * @param urlStr
	 * @return
	 * @throws IOException
	 */
	public static String webserviceCall(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		return webserviceCall(url);
	}
	
	/**
	 * Opens an HTTP Connection to the specified URL and returns the result
	 * 
	 * @param url
	 * @return response from web server
	 * @throws IOException
	 */
	public static String webserviceCall(URL url) throws IOException {
		LOG.entering(WebserviceClient.NAME, "webserviceCall");
		
		URLConnection urlc = url.openConnection();
		urlc.connect();
		
		BufferedReader in = new BufferedReader(new InputStreamReader(
				urlc.getInputStream()));
		
		String inputLine;
		String resp = "";
		while ((inputLine = in.readLine()) != null) { 
			resp += inputLine + '\n';
		}
		LOG.finer("Response: " + resp);
		in.close();
		
		LOG.exiting(WebserviceClient.NAME, "webserviceCall", "ret=" + resp);
		return resp;
	}
	
	public static String webservicePost(String urlStr, String data, boolean waitForResult) throws IOException {
		URL url = new URL(urlStr);
		return webservicePost(url, data, waitForResult);
	}
	
	public static String webservicePost(URL url, String data, boolean waitForResult) throws IOException {
		LOG.entering(WebserviceClient.NAME, "webservicePost", new Object[] 
				{"url: " + url + "data: " + data, "waitForResult: " + waitForResult});
		
		String ret = "";
		
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		
		con.setDoOutput(true);
		DataOutputStream out = new DataOutputStream(con.getOutputStream());
		out.writeBytes(data);
		out.flush();
		out.close();
		
		int responseCode = con.getResponseCode();
		LOG.fine("Response Code: " + responseCode);
		
		if (waitForResult) {
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			
			while ((inputLine = in.readLine()) != null) {
				ret += "\n" + inputLine;
			}
			in.close();
		} else {
			ret += responseCode;
		}
		
		LOG.fine(ret);
		
		LOG.exiting(WebserviceClient.NAME, "webservicePost", "Response Code: " + responseCode + "ret=" + ret);
		return ret;
	}
}
