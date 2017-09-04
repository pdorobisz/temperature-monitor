# Temperature and humidity monitor for Raspberry Pi

Uses DHT22 sensor connected to Raspberry Pi's GPIO and Influx DB to store measurements. Grafana can be used to display 
collected data.

## Installation

Following instructions tested on Raspberry Pi Zero.

### Influx DB

```
wget https://dl.influxdata.com/influxdb/releases/influxdb_1.3.5_armhf.deb
sudo dpkg -i influxdb_1.3.5_armhf.deb
sudo systemctl start influxd
```

### Grafana

https://www.circuits.dk/install-grafana-influxdb-raspberry/

`curl https://bintray.com/user/downloadSubjectPublicKey?username=bintray | sudo apt-key add -`

add `deb https://dl.bintray.com/fg2it/deb-rpi-1b jessie main` to `/etc/apt/sources.list`

```
sudo apt-get update
sudo apt-get install grafana
sudo systemctl start  grafana-server
```

## Useful commands

### InfluxDB

After logging to Influx's command line client:

`show databases`

`use <database>`

`show series`

Using HTTP api:

`curl -G 'http://localhost:8086/query?pretty=true' --data-urlencode "db=temperature" --data-urlencode "q=SELECT * FROM \"temperature-humidity\""`

`curl -i -X POST http://localhost:8086/query --data-urlencode "q=DROP DATABASE temperature"`

`curl -i -X POST http://localhost:8086/query --data-urlencode "q=CREATE DATABASE temperature"`
