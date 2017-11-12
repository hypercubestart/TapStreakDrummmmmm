int tempo = 112;
const int startButtonPin = 12;
const int ledPin = 13;
const int accelerometerPin = A0;
const int outputA = 2;
const int outputB = 3;
const int rotaryButton = 4;

unsigned long previousMillis = 0;
unsigned long previousMillisButton = 0;
int buttonPrevState = 1;

int aState;
int aLastState;

volatile boolean start = false;

int ledState = LOW;  

unsigned long currentMillis = 0;
unsigned long previousMillisCounter = 0;
long intervalLed;
long intervalCounter;

int startXPos;

void setup() {
  // initialize serial communications at 9600 bps:
  Serial.begin(9600); 
  pinMode(startButtonPin, INPUT);
  digitalWrite(startButtonPin, HIGH); // connect internal pull-up

  pinMode(ledPin, OUTPUT);

  pinMode(accelerometerPin, INPUT);

  //Serial.println("Enter a tempo: ");

  pinMode(outputA, INPUT);
  pinMode(outputB, INPUT);
  pinMode(rotaryButton, INPUT);

  digitalWrite(rotaryButton, HIGH);
  aLastState = digitalRead(outputA);


  intervalLed = 30000.0/tempo;
  intervalCounter = intervalLed/24;

}


void loop() {
  currentMillis = millis();
  /*if (!start) {
    if (tempo == 0) {
      if (Serial.available() > 0) {
        // read the incoming byte:
        String input = Serial.readString();
    
        int inputNumber = input.toInt();
        if (inputNumber < 10 || inputNumber > 300) {
          Serial.println("Please choose a tempo >= 10 and <=300");
        } else {
          tempo = inputNumber;
          Serial.print("Tempo set to: ");
          Serial.println(tempo);
        }
      }
    }
    if (buttonPressed() && tempo != 0) {
      intervalLed = 30000.0/tempo;
      intervalCounter = intervalLed/24;
      start = true;
      Serial.println("Start!");
      startXPos = analogRead(accelerometerPin);
      
    }
  }
  
  else {*/
  if (!start) {
    // put your main code here, to run repeatedly:
    aState = digitalRead(outputA); // Reads the "current" state of the outputA
     // If the previous and the current state of the outputA are different, that means a Pulse has occured
     if (aState != aLastState){     
       // If the outputB state is different to the outputA state, that means the encoder is rotating clockwise
       if (digitalRead(outputB) != aState) { 
         tempo ++;
       } else {
         tempo --;
       }
     } 
     aLastState = aState; // Updates the previous state of the outputA with the current state
    
     if (digitalRead(rotaryButton) != buttonPrevState) {
      if (digitalRead(rotaryButton) == LOW) {
      Serial.print("Tempo currently is set to: ");
      Serial.println(tempo);
      intervalLed = 30000.0/tempo;
      intervalCounter = intervalLed/24;
     }
      buttonPrevState = digitalRead(rotaryButton);
      previousMillisButton = millis();
     } else {
      if (buttonPrevState==0 && millis()-previousMillisButton>500) {
        start = true;
        buttonPrevState = digitalRead(rotaryButton);
        Serial.println("start!");
        Serial.println(tempo);
      }
     }
  } else {
    currentMillis = millis();
    if (currentMillis - previousMillisCounter >= intervalCounter) {
      // save the last time you blinked the LED
      previousMillisCounter = currentMillis;
      Serial.println(analogRead(accelerometerPin));
      /*
      if(analogRead(accelerometerPin)-startXPos>25) {
        Serial.println("Beat registered!");
      }*/
    }
    if (digitalRead(rotaryButton) != buttonPrevState) {
      if (digitalRead(rotaryButton) == LOW) {
        Serial.flush();
        Serial.println("finished!"); 
        Serial.end();
      }
      buttonPrevState = digitalRead(rotaryButton);
    }
  }
    
    updateLed();
}

boolean buttonPressed() {
  int button_pressed = !digitalRead(startButtonPin); // pin low -> pressed
  return button_pressed;
}

void updateLed() {

    if (currentMillis - previousMillis >= intervalLed) {
      // save the last time you blinked the LED
      previousMillis = currentMillis;
  
      // if the LED is off turn it on and vice-versa:
      if (ledState == LOW) {
        ledState = HIGH;
      } else {
        ledState = LOW;
      }
  
      // set the LED with the ledState of the variable:
      digitalWrite(ledPin, ledState);
    }
}

