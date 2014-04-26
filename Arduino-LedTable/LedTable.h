/******************************************************************************
 * LedTable.h
 *
 * Created on: Jul 14, 2013
 *     Author: Mikel
 *
 * http://www.mikelduke.com
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
 *****************************************************************************/

// Only modify this file to include
// - function definitions (prototypes)
// - include files
// - extern variable definitions
// In the appropriate section

#ifndef LedTable_H_
#define LedTable_H_
#include "Arduino.h"
//add your includes for the project LedTable here


//end of add your includes here
#ifdef __cplusplus
extern "C" {
#endif
void loop();
void setup();
#ifdef __cplusplus
} // extern "C"
#endif

//constructors for functions that run a demo of adafruit examples
void runDemos(uint8_t wait);
void runGridDemos(uint8_t wait);
void runStrandDemos(uint8_t wait);

void tablePulse(int inc);
void tablePulse2(int inc);
void randomColorPulse(int inc);
void setTableColor(byte r, byte g, byte b);

void handleRead(char c);
void dumpSerial();

void sendStatus();
void readLedTableColor();
void readLedArray();

//Do not add code below this line
#endif /* LedTable_H_ */
