/******************************************************************************
 * AdaFruitEx.h
 *
 *  Created on: Jul 15, 2013
 *      Author: Mikel
 *
 * Header file created to use the Adafruit WS2801 library and examples in 
 * eclipse
 *
 * http://www.mikelduke.com
 *
 * https://github.com/adafruit/Adafruit-WS2801-Library
 * 
 *****************************************************************************/

#ifndef ADAFRUITEX_H_
#define ADAFRUITEX_H_

uint32_t Color(byte r, byte g, byte b);
uint32_t Wheel(byte WheelPos);

void rainbow(uint8_t wait, Adafruit_WS2801 strip);
void rainbowCycle(uint8_t wait, Adafruit_WS2801 strip);
void colorWipe(uint32_t c, uint8_t wait, Adafruit_WS2801 strip);

void drawX(uint8_t w, uint8_t h, uint8_t wait, Adafruit_WS2801 strip);
void bounce(uint8_t w, uint8_t h, uint8_t wait, Adafruit_WS2801 strip);


#endif /* ADAFRUITEX_H_ */
