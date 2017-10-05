#!/bin/bash

export PYTHON_EGG_CACHE=/tmp/temperature-monitor/python-eggs
python `dirname $0`/dhtRead.py
