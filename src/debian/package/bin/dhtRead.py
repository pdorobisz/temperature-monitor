#!/usr/bin/python

import Adafruit_DHT as dht

humidity, temperature =  dht.read_retry(dht.DHT22, 22)
print('{0:0.1f} {1:0.1f}'.format(temperature, humidity))
