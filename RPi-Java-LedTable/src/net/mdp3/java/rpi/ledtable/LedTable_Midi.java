/**
 * 
 */
package net.mdp3.java.rpi.ledtable;

import java.util.LinkedList;
import java.util.Random;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Transmitter;

/**
 * @author Mikel
 *
 */
public class LedTable_Midi extends Thread implements Receiver {

	private LinkedList<MidiDevice> openDevices = new LinkedList<MidiDevice>();
	private LinkedList<Transmitter> transmitters = new LinkedList<Transmitter>();
	
	private int mode = 1;
	private final int NOTE = 144; //0x90
	
	private boolean run = false;
	private long tableUpdateDelay = LedTable_Settings.midiUpdateDelay;
	
	Random rand = new Random();
	
	public LedTable_Midi() {
	}
	
	public boolean isRunning() {
		return run;
	}
	
	public void run() {
		while(run) {
			updateTable();
			
			if (run) {
				try {
					Thread.sleep(tableUpdateDelay);
				} catch (InterruptedException e) {
					/*if (!e.toString().equalsIgnoreCase("sleep interrupted")) {
						System.out.println("Error sleeping Midi Thread: " + e);
						e.printStackTrace();
					}*/
				}
			} else { //extra check to close inputs cleanly
				close();
			}
		}
	}
	
	public void setMode(int m) {
		this.mode = m;
	}
	
	public void startMidi() {
		if (!run) {
			if (loadInputDevices() || LedTable_Settings.debug) {
				run = true;
				this.start();
			}
		}
	}
	
	public void stopMidi() {
		if (LedTable_Settings.debug) System.out.println("Stopping Midi Thread!");
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
			LedTable_Util.tableAr[0][scaleNote * 3] 	= rand.nextInt(255); //data2 * 2; //R
			LedTable_Util.tableAr[0][scaleNote * 3 + 1] = rand.nextInt(255); //data2 * 2; //G
			LedTable_Util.tableAr[0][scaleNote * 3 + 2] = rand.nextInt(255); //data2 * 2; //B
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
			LedTable_Util.moveTableDown();
			LedTable_Util.fade(30);
		}
		LedTable_Util.sendTableAr();
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
