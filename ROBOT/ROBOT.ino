#include <Arduino.h>
#include <BluetoothSerial.h>

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run 'make menuconfig'
#endif

BluetoothSerial SerialBT;

// setting PWM properties
const int freq = 1000;
const int freq2 = 2000;
const int ledAChannel = 0;
const int ledBChannel = 1;
const int resolution = 8;

const int L1 = 18;//red
const int L2 = 19;//green
const int L3 = 22;//blue

const int R1 = 23;//red
const int R2 = 33;//green
const int R3 = 32;//blue

//MESSAGE
String message = "00000000"; // Replace instead for your message
int sizeOfMessage = message.length();

//VARIABLES DE TRANSMISION DE MENSAJES Y MOTORES
String mensaje = "";    
int AI1 = 13; int AI2 = 25;
int BI1 = 4;  int BI2 = 16;
int PWMA = 26;     int PWMB = 27;
String Temp2 = "";     String mensaje2 = "";    
String recibido = "";   String iniciado = "";
int StBy = 17;
//TaskHandle_t Task2;

String m1 = "000";
String m2 = "000";
String direccion = "1";
String colorLed = "R";

void setup() {
  //INICIAMOS EL 2DO NUCLEO
  // xTaskCreatePinnedToCore(loop2, "Task_2", 5000, NULL, 1, &Task2,0);

  Serial.begin(9600);   SerialBT.begin("CAOS-test");

  pinMode(L1, OUTPUT);
  pinMode(L2, OUTPUT);
  pinMode(L3, OUTPUT);
  pinMode(R1, OUTPUT);
  pinMode(R2, OUTPUT);
  pinMode(R3, OUTPUT);
  pinMode(StBy, OUTPUT);
  pinMode(AI2, OUTPUT);
  pinMode(AI1, OUTPUT);
  pinMode(BI2, OUTPUT);
  pinMode(BI1, OUTPUT);
  ledcSetup(ledAChannel, freq, resolution);
  ledcSetup(ledBChannel, freq2, resolution);
  ledcAttachPin(PWMA, ledAChannel);
  ledcAttachPin(PWMB, ledBChannel);
  digitalWrite(StBy, HIGH);
}

void loop() {  

   while (SerialBT.available())
   {
     char command = SerialBT.read();
     recibido += command;
     if(command == '#'){
       m1 = recibido.substring(3,6);
       m2 = recibido.substring(6,9);
       direccion = recibido.substring(1,2);
       colorLed = recibido.substring(2,3);

       if(colorLed == "R"){
        red();
       }else if(colorLed == "G"){
        green();
       }else if(colorLed == "B"){
        blue();
       }
       if(direccion == "1"){
        
          Serial.println("FRONT");
          Serial.println("M1 - " + m1);
          Serial.println("M2 - " + m2);
          digitalWrite(AI2, LOW);
          digitalWrite(AI1, HIGH);
          ledcWrite(ledAChannel, m1.toInt());
          digitalWrite(BI2, LOW);
          digitalWrite(BI1, HIGH);
          ledcWrite(ledBChannel, m2.toInt());
          
       }else{
        
          Serial.println("BACK");
          Serial.println("M1 - " + m1);
          Serial.println("M2 - " + m2);
          digitalWrite(AI2, HIGH);
          digitalWrite(AI1, LOW);
          ledcWrite(ledAChannel, m1.toInt());
          digitalWrite(BI2, HIGH);
          digitalWrite(BI1, LOW);
          ledcWrite(ledBChannel, m2.toInt());
          
       }
       
     }
   }
   delay(50);
   if(recibido != ""){
      recibido = "";
   }
  
}//fin loop

void red(){
  Serial.println("RED");
  digitalWrite(18,HIGH);
  digitalWrite(23,HIGH);

  digitalWrite(19,LOW);
  digitalWrite(33,LOW);

  digitalWrite(22,LOW);
  digitalWrite(32,LOW);
}


void green(){
  Serial.println("GREEN");
  digitalWrite(18,LOW);
  digitalWrite(23,LOW);

  digitalWrite(19,HIGH);
  digitalWrite(33,HIGH);

  digitalWrite(22,LOW);
  digitalWrite(32,LOW);
}

void blue(){
  Serial.println("BLUE");
  digitalWrite(18,LOW);
  digitalWrite(23,LOW);

  digitalWrite(19,LOW);
  digitalWrite(33,LOW);

  digitalWrite(22,HIGH);
  digitalWrite(32,HIGH);
}

//
//void left(){
//  Serial.println("LEFT");
//  digitalWrite(AI2, HIGH);
//  digitalWrite(AI1, LOW);
//  ledcWrite(ledAChannel, 255);
//  digitalWrite(BI2, LOW);
//  digitalWrite(BI1, HIGH);
//  ledcWrite(ledBChannel, 255);
//}
//
//void right(){
//  Serial.println("RIGHT");
//  digitalWrite(AI2, LOW);
//  digitalWrite(AI1, HIGH);
//  ledcWrite(ledAChannel, 255);
//  digitalWrite(BI2, HIGH);
//  digitalWrite(BI1, LOW);
//  ledcWrite(ledBChannel, 255);
//}
//
