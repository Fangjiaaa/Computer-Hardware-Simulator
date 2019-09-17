# Computer-Hardware-Simulator
Created a simulator in Java to demonstrate the communication between CPU and memory to process the data in text files.

	Description:
Computer Hardware Simulator (Java, Operating System)						     Mar.2018 – May.2018
•	Created a simulator in Java to demonstrate the communication between CPU and memory to process the data in text files.
•	Implemented a reader class in Java to read the data. The CPU then uses Java runtime exec method to request the data from memory, parse the data, and send it back to the memory. The memory then writes the result to the output file with the writer class.

	Files: 
1.	CPU.java: 
The source file to simulate the CPU, processing instructions and communicating with Memory.

2.	Memory.java:
The source file to simulate Memory, reading the sample file to the memory array and communicating with CPU.

3.	sample5.txt:
The sample file I created. Output: Ni Hao ya :)

4.	Summary.doc:
Summary paper of the project.

5.	README.doc:
Lists of files, descriptions of each file, and how to compile the project.


	How to compile and run: 
1.	Compile:
javac Memory.java
javac CPU,java

2.	Run:
Java CPU sample5.txt 30

You can change the sample file name and timer 
