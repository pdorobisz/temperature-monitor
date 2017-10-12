#!/bin/bash

if [[ "$#" -ne 1 ]]; then
    echo "usage:"
    echo $0 "<SENSOR_PIN>"
    exit 1
fi

export PYTHON_EGG_CACHE=/tmp/temperature-monitor/python-eggs
python `dirname $0`/dhtRead.py $1
