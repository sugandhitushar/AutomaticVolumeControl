Name - Automatic Volume Control

Description - Controls the volume of speaker based on amplitude of mic input. Minimum threshold is system's volume at the start of the program and maximum is system's max volume. 

Execution Steps - 
1. Compile all java files from src and keep them besides manifest.txt file.

2. Execute below command to create an executable JAR.
jar -cvfm AutomaticVolumeControl.jar manifest.txt *.class

OR

Download the JAR file and execute it using below command
java -jar AutomaticVolumeControl.jar
