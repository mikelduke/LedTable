LedTable
========

Arduino
*******************************************************************************

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


Java
*******************************************************************************

The Java app for the Raspberry pi to control the Arduino requires the Pi4J library.

http://pi4j.com/

In addition, for connectivity the RPi should be running Apache with php and MySQL. 
The php site stores user selections into a MySQL database which are then read by the 
java program.

The Raspberry Pi connects to the Arduino via the USB port. This is the simplest 
connection type available. The app requires Java which is included in newer 
Raspbian releases and must be run with sudo.

The file LedTable_Settings.java should be modified to include the user/password/ip 
settings required for the MySQL server used to interface with the php webpage.

A run.sh shell script is included for your convenience. This file should be modified 
to point to the correct paths and needs to be run with sudo.


php
*******************************************************************************

The php app is a simple webpage interface to pick from several selections. The 
user can pick one of the hardcoded Arduino demo modes, or select another for the 
Java app to load some image files or folder.

Folder selection has not been implemented, the folder paths are hardcoded.

The MySQL server settings need to modified in the index.php file, along with the 
folder paths for use with images.

MySQL database setup scripts are available in the sql/ folder. These may need to be 
modified to support your specific setup, especially the grants in CreateDatabase.sql

