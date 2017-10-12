#!/usr/bin/python

import Adafruit_DHT as dht

humidity, temperature =  dht.read_retry(dht.DHT22, 22)

if humidity is not None and temperature is not None:
    print('{0:0.1f} {1:0.1f}'.format(temperature, humidity))
else:
    print("")
