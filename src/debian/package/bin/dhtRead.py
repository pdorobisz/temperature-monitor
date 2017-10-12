#!/usr/bin/python

import sys
import Adafruit_DHT as dht


if(len(sys.argv) == 2):
    pin = sys.argv[1]
else:
    print "usage:\n", sys.argv[0], "<SENSOR_PIN>"
    sys.exit(1)

humidity, temperature =  dht.read_retry(dht.DHT22, pin)

if humidity is not None and temperature is not None:
    print('{0:0.1f} {1:0.1f}'.format(temperature, humidity))
else:
    print("")
