void setup() {
  Serial.begin(9600);

  pinMode(2, INPUT);
  pinMode(3, INPUT);
  pinMode(4, INPUT);
  pinMode(5, INPUT);

  pinMode(6, INPUT);
  pinMode(7, INPUT);
}
byte lastMessage;
byte lastA;
byte lastB;
void loop() {
  byte message = 0;

  for (int i = 0; i < 4; i++)
    message |= digitalRead(i + 2) << i;

  byte scrollA = digitalRead(7);
  byte scrollB = digitalRead(6);

  message |= ((lastA & lastB & scrollA & !scrollB) | (!lastA & !lastB & scrollA & scrollB))<<4;

  message |= ((lastA & lastB & !scrollA & !scrollB) | (!lastA & !lastB & scrollA & !scrollB))<<5;

  Serial.write(message);
  //if(message != 0)
    //delay(100);
  lastA = scrollA;
  lastB = scrollB;
  delay(1);
}
