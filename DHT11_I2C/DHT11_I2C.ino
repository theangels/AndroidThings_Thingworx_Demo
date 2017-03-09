// Wire Slave Sender
// by Nicholas Zambetti <http://www.zambetti.com>

// Demonstrates use of the Wire library
// Sends data as an I2C/TWI slave device
// Refer to the "Wire Master Reader" example for use with this

// Created 29 March 2006

// This example code is in the public domain.

#include "DHT.h"

#define DHTPIN 7     // what digital pin we're connected to
#define DHTTYPE DHT11

#include <Wire.h>

DHT dht(DHTPIN, DHTTYPE);

int t;
int h;

void setup() {
  Serial.begin(9600);
  dht.begin();
  Wire.begin(8);                // join i2c bus with address #8
  Wire.onRequest(requestEvent); // register event
}

void loop() {
  delay(2000);
  t = dht.readTemperature();
  h = dht.readHumidity();
  if(isnan(t) || isnan(h)){
    Serial.println("Failed to read from DHT sensor!");
    return;
  }
}

// function that executes whenever data is requested by master
// this function is registered as an event, see setup()
void requestEvent() {
  char data[7];
  sprintf(data,"T%dH%dE",t,h);
  Wire.write(data); // respond with message of 5 bytes
  // as expected by master
}
