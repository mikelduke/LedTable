package com.mikelduke.java.util.log;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogUtil {
	private static final String CLAZZ = LogUtil.class.getName();
	private static final Logger LOGGER = Logger.getLogger(CLAZZ);
	
	public static <K, V> void logMap(Level level, String clazz, String msg, Map<K, V> map) {
		StringBuilder sb = new StringBuilder();
		
		if (level == null) {
			level = Level.FINEST;
		}
		
		if (clazz == null || clazz.isEmpty()) {
			clazz = CLAZZ;
		}
		
		if (msg == null || msg.isEmpty()) {
			msg = "Map Values";
		}
		
		if (map == null) {
			sb.append("Map is null");
		}
		
		if (map.isEmpty()) {
			sb.append("Map is empty");
		}
		
		for (K key : map.keySet()) {
			sb.append(key + ": " + map.get(key));
			sb.append("\n");
		}
		
		LOGGER.logp(level, clazz, "logMap", msg + ": " + sb.toString());
	}
	
	public static <K, V> void logMap(Level level, String clazz, Map<K, V> map) {
		logMap(level, clazz, "", map);
	}
	
	public static <K, V> void logMap(Level level, Map<K, V> map) {
		logMap(level, CLAZZ, "", map);
	}
	
	public static <K, V> void logMap(Map<K, V> map) {
		logMap(Level.FINEST, CLAZZ, "Map", map);
	}
}
