import processing.serial.*;

Serial myPort;  // The serial port
PrintWriter output;
boolean start = false;
String inBuffer;
void setup() {
  // List all the available serial ports
  printArray(Serial.list());
  // Open the port you are using at the rate you want:
  myPort = new Serial(this, Serial.list()[0], 9600);
  myPort.clear();
  output =  createWriter ("test6.csv");
  myPort.bufferUntil('\n');
  
  noLoop();
}

void draw() {
  
   /*while (myPort.available() > 0) {
      String inBuffer = myPort.readString();
      if (start) { //<>//
        if (inBuffer != null) {
          output.print(inBuffer);
        }
      } else {
         if (inBuffer.equals("start!")) {
          start = true;
        }
      }
  }*/ //<>//
}

void serialEvent(Serial p) { 
  if (myPort.available() > 0) {
    inBuffer = myPort.readStringUntil('\n');
    
    println(inBuffer);
    if (inBuffer.equals("finished!\r\n")) finish();
    if (start && inBuffer != null) {
      output.println(inBuffer.trim());
    }
    else if (inBuffer.equals("start!\r\n")) {
      start = true;
    }
  }
} 

void finish() {
  output.flush();
  output.close();
  exit();
}