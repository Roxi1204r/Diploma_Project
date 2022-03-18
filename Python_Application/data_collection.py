# Importing modules
import spidev # To communicate with SPI devices
from numpy import interp # To scale values
from time import sleep # To add delay
import RPi.GPIO as GPIO # To use GPIO pins
import Adafruit_DHT # For DHT11
import pyrebase
import gpiozero
import math
import json
import sys

config = {
    "apiKey": "",
    "authDomain": "projectID.firebaseapp.com",
    "databaseURL": "https://projectID-defaultrtdb.europe-west1.firebasedatabase.app/",
    "projectId": "projectID",
    "storageBucket": "projectID.appspot.com" 
}

firebase = pyrebase.initialize_app(config)

# Initialize firebase data
db = firebase.database()
print("Send data to Firebase using Raspberry Pi")
print("----------------------------------------")

# Start SPI connection
spi = spidev.SpiDev() # Created an object
spi.open(0,0)
flamePin = 17 # D0 connected to D17
relay = 5

def setup():
    GPIO.setmode(GPIO.BCM)
    GPIO.setwarnings(False)
    GPIO.setup(flamePin, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
    GPIO.setup(relay, GPIO.OUT)

# Read MCP3008 data
def analogInput(channel):
    spi.max_speed_hz = 1350000
    adc = spi.xfer2([1,(8+channel)<<4,0])
    data = ((adc[1]&3) << 8) + adc[2]
    return data

while True:
    setup()
    if GPIO.input(flamePin) == True:
        fire="No fire detected!"
    else:
        fire="Flame detected!!!"
    humidity, temperature = Adafruit_DHT.read_retry(Adafruit_DHT.DHT11, 4)
    moisture = analogInput(0) # Reading from CH0
    moisture = interp(moisture, [0, 1023], [100, 0])
    light = analogInput(2) # Reading from CH2
    light = interp(light, [0, 1023], [0, 100])
    if moisture < 36:
        irrigation = "Irrigation system - ON!"
        GPIO.output(5, GPIO.LOW)
        sleep(2)
        GPIO.output(5, GPIO.HIGH)
    else:
        irrigation = "Irrigation system - OFF!"
    if humidity is not None and temperature is not None and moisture is not None and light is not None:
        print("Temperature={0:0.1f}°C Humidity={1:0.1f}% Soil Moisture={2:0.1f} Light Level={3:0.1f}".format(temperature, humidity, moisture, light))
    print(fire)
    print(irrigation)
 
    data = {
        "temperature": str(temperature),
        "humidity": str(humidity),
        "moisture": str(round(moisture,2)),
        "light": str(round(light,2)),
        "fire": fire,
        "irrigation": irrigation
    }
    db.child("sensors").child("1").set(data)
    db.child("storage").push(data)
    sleep(2)
    GPIO.cleanup()