/******************************************************************************
 * LedTable.cpp
 *
 * Created on: Jul 14, 2013
 *     Author: Mikel
 *
 * http://www.mikelduke.com
 *
 * Main program for use in the LedTable coffee table project based on an 8x12 array 
 * of WS2801 RGB leds on a string. 
 *
 * This program runs on an Arduino Uno using 50pc WS2801 based RGB LEDS similar to 
 * these http://www.adafruit.com/products/322
 *
 * This program requires the use of the Adafruit WS2801 Arduino Library available
 * here https://github.com/adafruit/Adafruit-WS2801-Library
 *
 * The code compiled in Eclipse when set up for Arduino following these instructions 
 * http://www.baeyens.it/eclipse/ and maybe using the Arduino IDE if renamed to .ino
 * but this is untested and changes are probably needed.
 *
 * Demo 1: https://www.youtube.com/watch?v=T2C9mu11J88
 * Demo 2: https://www.youtube.com/watch?v=4fyQAG1ji9Y
 * Demo 3: https://www.youtube.com/watch?v=qthHU0W8SG4
 *
 *****************************************************************************/

#include "SPI.h"
#include "Adafruit_WS2801.h"
#include "LedTable.h"
#include "AdafruitEx.h"

#define TIMEOUT 2000

uint8_t dataPin  = 2;    // Yellow wire on Adafruit Pixels
uint8_t clockPin = 3;    // Green wire on Adafruit Pixels

int arrayX = 12;
int arrayY = 8;
int numLeds = arrayX * arrayY;

enum States { STOPPED, DEMO, PULSE1, PULSE2, SERIAL_READ, SET_COLOR, PULSE_RANDOM, WAITING};
int state = DEMO;
String statusStr = "init";

Adafruit_WS2801 strip = Adafruit_WS2801((uint16_t)arrayX, (uint16_t)arrayY, dataPin, clockPin);

void setup() {
	randomSeed(analogRead(0));
	Serial.begin(57600);
	Serial.println("Led Table");

	strip.begin();
	strip.show();
}

void loop() {
	if (Serial.available() > 0) {
		char c;
		c = Serial.read();
		//Serial.print("Received: ");
		//Serial.println(c);
		handleRead(c);
	}

	if (state == STOPPED) {
		statusStr = "Stopped";
		setTableColor(0, 0, 0);
	}
	else if (state == DEMO) runDemos(0);
	else if (state == PULSE1) tablePulse(1);
	else if (state == PULSE2) tablePulse2(5);
	else if (state == SERIAL_READ) readLedArray();
	else if (state == PULSE_RANDOM) randomColorPulse(1);
	else if (state == WAITING) delay(5);
	//strip.show();
}

void handleRead(char c) {
	if (c == 'X') {
		sendStatus();
	}
	else if (c == '0') {
		state = STOPPED;
		dumpSerial();
	}
	else if (c == '1') {
		state = DEMO;
		dumpSerial();
	}
	else if (c == '2') {
		state = PULSE1;
		dumpSerial();
	}
	else if (c == '3') {
		state = PULSE2;
		readLedTableColor();
		dumpSerial();
	}
	else if (c == '4') {
		state = SERIAL_READ;
		//readLedArray();
		//dumpSerial();
	}
	else if (c == '5') {
		state = SET_COLOR;
		dumpSerial();
	}
	else if (c == '6') {
		state = PULSE_RANDOM;
		dumpSerial();
	}
	//else dumpSerial();
}

void runDemos(uint8_t wait) {
	statusStr = "Running AdaFruit Demos";
	runGridDemos(wait);
	runStrandDemos(wait);
}

void runGridDemos(uint8_t wait) {
	//Serial.println("Running AdaFruit Grid Demos");
	drawX(12, 8, 100, strip);
	bounce(12, 8, 50, strip);
}

void runStrandDemos(uint8_t wait) {
	//Serial.println("Running Adafruit Strip Demos");
	colorWipe(Color(255, 0, 0), wait, strip);
	colorWipe(Color(0, 255, 0), wait, strip);
	colorWipe(Color(0, 0, 255), wait, strip);
	rainbow(wait, strip);
	rainbowCycle(wait, strip);
}

void tablePulse(int inc) {
	statusStr = "Running Table Pulses";
	for (byte i = 0; i < 255; i += inc) {
		setTableColor(i, 0, 0);

		//Added to quickly break the loop when serial data comes in
		if (Serial.available() > 0) return;
	}
	for (byte i = 0; i < 255; i += inc) {
		setTableColor(0, i, 0);

		//Added to quickly break the loop when serial data comes in
		if (Serial.available() > 0) return;
	}
	for (byte i = 0; i < 255; i += inc) {
		setTableColor(0, 0, i);

		//Added to quickly break the loop when serial data comes in
		if (Serial.available() > 0) return;
	}
	//Added to quickly break the loop when serial data comes in
	if (Serial.available() > 0) return;
}

void setTableColor(byte r, byte g, byte b) {
	for (int x = 0; x < arrayX; x++) {
		for (int y = 0; y < arrayY; y++) {
			strip.setPixelColor(x, y, r, g, b);
		}
	}
	strip.show();
}

void dumpSerial() {
	while (Serial.available() > 0) {
		byte b;
		b = Serial.read();
	}
}

void sendStatus() {
	Serial.print("Status: ");
	Serial.println(statusStr);
}

void readLedTableColor() {
	int r = 0;
	int g = 0;
	int b = 0;

	r = Serial.parseInt();
	g = Serial.parseInt();
	b = Serial.parseInt();

	setTableColor(r, g, b);
}

//Serial buffer is only 64b!
void readLedArray() {
	int ledsRead = 0;
	int delayTime = 0;
	while (ledsRead < numLeds) {
		while (Serial.available() < 3) {
			delay(5);
			delayTime += 5;
			if (delayTime > TIMEOUT) {
				state = STOPPED;
				return;
			}
		}
		char r = Serial.read();
		char g = Serial.read();
		char b = Serial.read();
		strip.setPixelColor(ledsRead, r, g, b);
		ledsRead++;
	}
	strip.show();
	state = WAITING;
}

void tablePulse2(int inc) {
	//statusStr = "Running Table Pulses";
	for (int i = 20; i <= 255; i += inc) {
		setTableColor((byte)i, 0, 0);
		//if (Serial.available() > 0) return;
	}
	for (int i = 255; i >= 20; i -= inc) {
		setTableColor((byte)i, 0, 0);
		//if (Serial.available() > 0) return;
	}
	for (int i = 20; i <= 255; i += inc) {
		setTableColor(0, (byte)i, 0);
		//if (Serial.available() > 0) return;
	}
	for (int i = 255; i >= 20; i -= inc) {
		setTableColor(0, (byte)i, 0);
		//if (Serial.available() > 0) return;
	}
	for (int i = 20; i <= 255; i += inc) {
		setTableColor(0, 0, (byte)i);
		//if (Serial.available() > 0) return;
	}
	for (int i = 255; i >= 20; i -= inc) {
		setTableColor(0, 0, (byte)i);
		//if (Serial.available() > 0) return;
	}
	//Added to quickly break the loop when serial data comes in
	if (Serial.available() > 0) return;
}

void randomColorPulse(int inc) {
	byte oldR = strip.pixels[0];
	byte oldG = strip.pixels[1];
	byte oldB = strip.pixels[2];

	byte newR = random(255);
	byte newG = random(255);
	byte newB = random(255);

	while ((newR > oldR + inc/2) || (newR < oldR - inc/2)) {
		if (oldR < newR) {
			oldR += inc;
		}
		else if (oldR > newR) {
			oldR -= inc;
		}
		setTableColor(oldR, oldG, oldB);
		if (Serial.available() > 0) return;
	}
	while ((newG > oldG + inc/2) || (newG < oldG - inc/2)) {
		if (oldG < newG) {
			oldG += inc;
		}
		else if (oldG > newG) {
			oldG -= inc;
		}
		setTableColor(oldR, oldG, oldB);
		if (Serial.available() > 0) return;
	}
	while ((newB > oldB + inc/2) || (newB < oldB - inc/2)) {
		if (oldB < newB) {
			oldB += inc;
		}
		else if (oldB > newB) {
			oldB -= inc;
		}
		setTableColor(oldR, oldG, oldB);
		if (Serial.available() > 0) return;
	}
}
