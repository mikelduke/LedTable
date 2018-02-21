/**
 * 
 */
package net.mdp3.java.rpi.ledtable.effects.playlist;

/**
 * @author Mikel
 *
 */
public class PlaylistEvent {
	private PlaylistEventName eventName = null;
	private Object eventValue = "";
	
	private Object eventSource = null;
	
	public static enum PlaylistEventName {
		PLAYBACK_PLAY,
		PLAYBACK_STOP,
		PLAYBACK_MODE_CHANGED,
		INDEX_CHANGED,
		ITEM_ADDED,
		ITEM_REMOVED,
		PLAYLIST_SAVED,
		PLAYLIST_LOADED,
		PLAYLIST_CLEARED
	}
	
	public PlaylistEvent(PlaylistEventName eventName) {
		this(eventName, null);
	}
	
	public PlaylistEvent(PlaylistEventName eventName, Object eventValue) {
		this(eventName, eventValue, null);
	}
	
	public PlaylistEvent(PlaylistEventName eventName, Object eventValue, Object source) {
		this.setEventName(eventName);
		this.setEventValue(eventValue);
		this.setEventSource(source);
	}

	public PlaylistEventName getEventName() {
		return eventName;
	}

	public void setEventName(PlaylistEventName eventName) {
		this.eventName = eventName;
	}

	public Object getEventValue() {
		return eventValue;
	}

	public void setEventValue(Object eventValue) {
		this.eventValue = eventValue;
	}

	public Object getEventSource() {
		return eventSource;
	}

	public void setEventSource(Object eventSource) {
		this.eventSource = eventSource;
	}
}
