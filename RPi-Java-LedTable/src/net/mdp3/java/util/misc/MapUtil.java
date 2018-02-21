package net.mdp3.java.util.misc;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MapUtil
{
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue( Map<K, V> map )
	{
		List<Map.Entry<K, V>> list =
				new LinkedList<Map.Entry<K, V>>( map.entrySet() );
		Collections.sort( list, new Comparator<Map.Entry<K, V>>()
		{
			public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
			{
				return (o1.getValue()).compareTo( o2.getValue() );
			}
		} );

		Map<K, V> result = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list)
		{
			result.put( entry.getKey(), entry.getValue() );
		}
		return result;
	}
	
	public static <K, V> String mapToMultilineString(Map<K, V> map) {
		if (map == null) {
			return "Map is null";
		}
		
		if (map.isEmpty()) {
			return "Map is empty";
		}
		
		StringBuilder sb = new StringBuilder();
		
		for (K key : map.keySet()) {
			sb.append(key + ": " + map.get(key));
			sb.append("\n");
		}
		
		return sb.toString();
	}
}