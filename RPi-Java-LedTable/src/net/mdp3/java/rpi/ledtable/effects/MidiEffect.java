/**
 * 
 */
package net.mdp3.java.rpi.ledtable.effects;

import java.util.LinkedList;
import java.util.Random;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

import net.mdp3.java.rpi.ledtable.LedTable_Selection;
import net.mdp3.java.rpi.ledtable.LedTable_Settings;
import net.mdp3.java.rpi.ledtable.LedTable_Util;
import net.mdp3.java.rpi.ledtable.table.Table;

/**
 * @author Mikel
 *
 */
public class MidiEffect extends Effect implements Receiver {

	private LinkedList<MidiDevice> openDevices = new LinkedList<MidiDevice>();
	private LinkedList<Transmitter> transmitters = new LinkedList<Transmitter>();
	
	private int mode = 1;
	private final int NOTE = 144; //0x90
	
	Random rand = new Random();
	private boolean devicesLoaded = false;
	
	public MidiEffect(Table t, LedTable_Selection s) {
		super(t, s);
	}
	
	@Override
	public EffectInfo setEffectInfo() {
		EffectInfoParameter[] paramInfo = {
				//TODO add parameter info
		};
		EffectInfo ei = new EffectInfo("MidiEffect", 
				"Listens to midi info and does stuff", paramInfo);
		
		return ei;
	}
	
	public void effectsLoop() {
		if (!devicesLoaded) {
			loadInputDevices();
		} 
		updateTable();
		
		if (run) {
			delay();
		} else { //extra check to close inputs cleanly
			close();
		}
	}
	
	public void setMode(int m) {
		this.mode = m;
	}
	
	public void stopEffect() {
		LOG.finer("Stopping Midi Thread!");
		if (run) {
			run = false;
			close();
		}
	}
	
	private boolean loadInputDevices() {
		MidiDevice device = null;
		MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
		boolean deviceLoaded = false;
		
		for (int i = 0; i < infos.length; i++) {
			System.out.println(i + ": " + infos[i].getName());
			try {
				device = MidiSystem.getMidiDevice(infos[i]);
				System.out.println("  " + i + " class: " + device.getClass().getName());
				System.out.println("  " + i + " receivers: " + device.getMaxReceivers());
				System.out.println("  " + i + " transmitters: " + device.getMaxTransmitters());
				
				if (device.getClass().getName().equalsIgnoreCase("com.sun.media.sound.MidiInDevice")) {
					System.out.println("MIDI In Found!");
					if (!device.isOpen() && device.getMaxTransmitters() != 0) {
						device.open();
						openDevices.addLast(device);
						
						Transmitter t = device.getTransmitter();
						transmitters.addLast(t);
						t.setReceiver(this);
						
						deviceLoaded = true;
					}
				}
		    	device = null;
			}
			catch (MidiUnavailableException e) {
				System.out.println("Error loading device " + i + " " + e);
		    }
		}
		
		this.devicesLoaded = deviceLoaded;
		return deviceLoaded;
	}
	
	@Override
	public void close() {
		for (int i = 0; i < transmitters.size(); i++)
			transmitters.get(i).close();
		for (int i = 0; i < openDevices.size(); i++)
			openDevices.get(i).close();
		run = false;
	}

	@Override
	public void send(MidiMessage arg0, long arg1) {
		ShortMessage message = new LedTable_ShortMessage(arg0.getMessage());
		
		if (LedTable_Settings.debug)
			System.out.println("MIDIMessage [Channel: " + message.getChannel() 
					+ ", Command: " + message.getCommand() + ", Data1: " + message.getData1() 
					+ ", Data2: " + message.getData2() + ']');
		
		if (mode == 1) handleMidi(message.getCommand(), message.getData1(), message.getData2());
	}
	
	/**
	 * handleMidi
	 * 
	 * Midi Handler to switch modes
	 * 
	 * @param cmd	Midi Command
	 * @param data1	Midi Data 1 (Typically Note)
	 * @param data2 Midi Data 2 (Typically Velocity)
	 */
	private void handleMidi(int cmd, int data1, int data2) {
		if (mode == 1) handleMidiMode1(cmd, data1, data2);
	}
	
	/**
	 * handleMidiMode1
	 * 
	 * Mode 1 for Midi Input, Controls how the table initially reacts to Midi Input
	 * 
	 * @param cmd
	 * @param data1
	 * @param data2
	 */
	private void handleMidiMode1(int cmd, int data1, int data2) {
		if (cmd == NOTE) {
			int scaleNote = data1 % 12;
			LedTable_Util.getTableAr()[0][scaleNote * 3] 	= (byte) rand.nextInt(255); //data2 * 2; //R
			LedTable_Util.getTableAr()[0][scaleNote * 3 + 1] = (byte) rand.nextInt(255); //data2 * 2; //G
			LedTable_Util.getTableAr()[0][scaleNote * 3 + 2] = (byte) rand.nextInt(255); //data2 * 2; //B
		}
	}
	
	/**
	 * updateTable
	 * 
	 * Function called from the Run method when thread is running, this
	 * method calls different table utility update methods based on the mode.
	 * This allows the table leds to change when there is no midi input.
	 */
	private void updateTable() {
		if (mode == 1) {
			//TODO implement this
		}
	}
	
	/**
	 * LedTable_ShortMessage
	 * @author Mikel
	 * 
	 * Simple class extended from javax.sound.midi.ShortMessage to be able
	 * to use the protected new ShortMessage(byte[]) method.
	 */
	private class LedTable_ShortMessage extends ShortMessage {
		public LedTable_ShortMessage(byte[] bAr) {
			super(bAr);
		}
	}
}
