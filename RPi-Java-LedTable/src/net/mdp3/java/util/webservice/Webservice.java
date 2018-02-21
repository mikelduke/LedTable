/**
 * 
 */
package net.mdp3.java.util.webservice;

import java.io.IOException;
import java.io.OutputStream;
import java.net.BindException;
import java.util.Map;
import java.util.logging.Logger;

//These imports require a sun/oracle jvm
//http://stackoverflow.com/questions/9579970/can-not-use-the-com-sun-net-httpserver-httpserver
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

/**
 * Webservice
 * 
 * Simple Java Webservice using the Sun HTTP Server implementation. The 
 * necessary packages may not be included in all JVMs. 
 * 
 * This class creates a webserver at the specified port and context, which 
 * listens for connections, builds the access URL parameters into a HashMap
 * and finally calls the wsAccess callback specified by the WebserviceListener
 * interface. The String returned by wsAccess is returned back to the browser.
 * 
 * @author Mikel
 * @see http://stackoverflow.com/questions/9579970/can-not-use-the-com-sun-net-httpserver-httpserver
 * @see http://www.oracle.com/technetwork/java/faq-sun-packages-142232.html
 */
@SuppressWarnings("restriction")
public class Webservice extends WebserviceBase {
	private final static String CLAZZ = Webservice.class.getName();
	private final static Logger LOG = Logger.getLogger(CLAZZ);
	
	/**
	 * Starts the SUN class webservice with service name on specified port
	 * 
	 * @param port Port to use, must not be in use
	 * @param name Webservice Name, this will be in the URL 
	 * @param listener Class implementing WebserviceListener for callbacks
	 * @throws BindException
	 * @throws IOException
	 */
	public Webservice(int port, String name, WebserviceListener listener) throws BindException, IOException {
		super(port, name, new GetHandler(listener));
	}

	/**
	 * called by http://ip/name/~
	 * parses url to get user info and file to send
	 * 
	 * @author Mikel
	 *
	 */
	static class GetHandler implements HttpHandler {
		//TODO Add sending files, can set file serve folder on init and serve files from it based on URL
		HttpExchange t;
		WebserviceListener wsl;
		
		public GetHandler(WebserviceListener listener) {
			this.wsl = listener;
		}
		
		/**
		 * 
		 */
		public void handle(HttpExchange t) throws IOException {
			LOG.entering(CLAZZ, "handle", "t: " + t);
			
			this.t = t;
			LOG.finer("Server: Get handler Called");

			Map<String, String> map = WebserviceUtil.getParamsMap(t);

			//Headers h = t.getResponseHeaders();
			String response = wsl.wsAccess(map);
			LOG.finer(response);
			
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
			
			LOG.exiting(CLAZZ, "handle");
		}
	}
}
