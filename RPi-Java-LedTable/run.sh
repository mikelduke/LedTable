if [[ $1 = 'y' ]];
then
	echo "updating"
	svn up
	echo "compiling"
	cd /home/mikel/mdp3/rpi/java/LedTable/src/net/mdp3/java/rpi/ledtable
	rm *.class
	javac -classpath .:classes:/opt/pi4j/lib/'*' *.java
fi

if [[ $1 = 'c' ]];
then
	echo "compiling without update"
	cd /home/mikel/mdp3/rpi/java/LedTable/src/net/mdp3/java/rpi/ledtable
	rm *.class
	javac -classpath .:classes:/opt/pi4j/lib/'*' *.java
fi

echo "run app"
cd /home/mikel/mdp3/rpi/java/LedTable/src
sudo java -classpath .:classes:/opt/pi4j/lib/'*':/usr/share/java/'*'  net/mdp3/java/rpi/ledtable/LedTable
