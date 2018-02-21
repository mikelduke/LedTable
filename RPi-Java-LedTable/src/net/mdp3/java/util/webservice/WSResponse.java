package net.mdp3.java.util.webservice;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WSResponse {
	private int responseCode = 200;
	private byte[] responseBody;
	private Map<String, List<String>> headersMap = null;
	
	private static final String DEFAULT_CHARSET = "UTF-8";
	
	public WSResponse() {
		this(200);
	}
	
	public WSResponse(int responseCode) {
		this(responseCode, (byte[]) null);
	}
	
	public WSResponse(int responseCode, String responseBody) throws UnsupportedEncodingException {
		this(responseCode, responseBody, DEFAULT_CHARSET);
	}
	
	public WSResponse(int responseCode, String responseBody, String charset) throws UnsupportedEncodingException {
		this(responseCode, responseBody.getBytes(charset));
	}
	
	public WSResponse(int responseCode, byte[] responseBody) {
		this(responseCode, responseBody, new HashMap<String, List<String>>());
	}
	
	public WSResponse(int responseCode, byte[] responseBody, Map<String, List<String>> headersMap) {
		setResponseCode(responseCode);
		setResponseBody(responseBody);
		setHeadersMap(headersMap);
	}
	
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public byte[] getResponseBody() {
		return responseBody;
	}
	public void setResponseBody(byte[] responseBody) {
		this.responseBody = responseBody;
	}
	public void setResponseBody(String responseBody) throws UnsupportedEncodingException {
		this.responseBody = responseBody.getBytes(DEFAULT_CHARSET);
	}
	public Map<String, List<String>> getHeadersMap() {
		return headersMap;
	}
	public void setHeadersMap(Map<String, List<String>> headersMap) {
		this.headersMap = headersMap;
	}
}
