LedTable
========

http://www.mikelduke.com

Code for my LedTable, an 8x12 rgb led string based table using WS2801 chips and 
controlled by and Arduino using a RPi for internet connectivity and higher level 
functionality.

The Arduino code requires the Adafruit WS2801 Library and contains modifications 
to the included example code to combine the examples for use as a header file.

https://github.com/adafruit/Adafruit-WS2801-Library

This program runs on an Arduino Uno using 50pc WS2801 based RGB LEDS similar to 
these http://www.adafruit.com/products/322

The code compiles in Eclipse when set up for Arduino following these instructions 
http://www.baeyens.it/eclipse/ and might compile using the Arduino IDE if renamed 
to .ino but this is untested and changes are probably needed.

Demo 1: https://www.youtube.com/watch?v=T2C9mu11J88
Demo 2: https://www.youtube.com/watch?v=4fyQAG1ji9Y
Demo 3: https://www.youtube.com/watch?v=qthHU0W8SG4


========
The Java app for the Raspberry pi to control the Arduino requires the Pi4J library.

http://pi4j.com/

The Raspberry Pi connects to the Arduino via the USB port. This is the simplest 
connection type available. The app requires Java which is included in newer 
Raspbian releases and must be run with sudo.
