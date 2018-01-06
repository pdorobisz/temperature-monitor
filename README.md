# Temperature and humidity monitor for Raspberry Pi

Uses DHT22 sensor connected to Raspberry Pi's GPIO ports to read temperature and humidity values.
Measurements can be stored in InfluxDB and Grafana can be used to display collected data.

## Building application

Use following command to build Debian package with temperature monitor application:

`sbt debian:packageBin`

## Development

Temperature monitor is a Play application and can be run locally with following command:

`sbt run`

There's `docker-compose.yml` file provided to run local instances of Grafana and InfluxDB for testing.
Use following command to run them:

`docker-compose up`
 
You can also completely disable InfluxDB integration by setting `app.influxDb.enable` property to `false`.
Grafana integration can be disabled by setting `app.grafana.enable` property to `false`.

To mock sensor provide script simulating sensor reading via `app.sensor.command` property. Such command should print
single line to stdout:

`<temperature> <humidity>`
 
Alternatively use settings similar to this:

```
app {
  sensor {
    #pin = 22 # disabled!
    command = "echo 22.3 67.5"
    ...
  }
...
}
```

## Installation and configuration

Following instructions were tested on Raspberry Pi Zero.

### Temperature and humidity sensor

Connect DHT sensor to data pin (by default application is using GPIO22) and install python library for sensor:
https://github.com/adafruit/Adafruit_Python_DHT

Following figure shows Pi's pin numbers: https://webofthings.org/wp-content/uploads/2016/10/pi-gpio.png

### InfluxDB

Skip this section if you don't want to store readings in database. 

**Installation**

ssh to Raspberry Pi and:

```
wget https://dl.influxdata.com/influxdb/releases/influxdb_1.3.5_armhf.deb
sudo dpkg -i influxdb_1.3.5_armhf.deb
sudo systemctl start influxd
```

**Configuration**

By default authentication is disabled and anyone can access InfluxDB from anywhere. Following changes to configuration
enable authentication and restrict database access to localhost only. They're optional but highly recommended.
Add following settings to `/etc/influxdb/influxdb.conf` and restart `influxd` service when done:

```
[http]
  # Determines whether HTTP endpoint is enabled.
  # enabled = true

  # The bind address used by the HTTP service.
  bind-address = "127.0.0.1:8086"

  # Determines whether user authentication is enabled over HTTP/HTTPS.
  auth-enabled = true
```

Open InfluxDB client (`influx`) and create new admin user (skip this step if you chose not to enable authentication):

`CREATE USER admin WITH PASSWORD 'admin' WITH ALL PRIVILEGES`

Exit client and reopen it as admin:

`influx -username admin -password admin`

Create new database:

`CREATE DATABASE temperature`

If authentication is enabled create new users:

```
CREATE USER grafana WITH PASSWORD 'grafana'
CREATE USER monitor WITH PASSWORD 'monitor'

GRANT READ ON "temperature" TO "grafana"
GRANT WRITE ON "temperature" TO "monitor"
```

### Grafana

Skip if you're not installing InfluxDB or you're not planning to use dashboards to view readings.

**Installation**

Based on: https://www.circuits.dk/install-grafana-influxdb-raspberry/

ssh to Raspberry Pi and:

`curl https://bintray.com/user/downloadSubjectPublicKey?username=bintray | sudo apt-key add -`

add `deb https://dl.bintray.com/fg2it/deb-rpi-1b jessie main` to `/etc/apt/sources.list`

```
sudo apt-get update
sudo apt-get install grafana
sudo systemctl start  grafana-server
```

**Configuration**

Login to Grafana (http://raspberrypi:3000) as `admin` (default password: "admin"), open side menu, click organisation
name and select `API Keys` option, create new api key and add it to `scripts/configureGrafana.sh` script.
You may also need to update InfluxDB credentials used by Grafana.

Execute script to create new InfluxDB datasource and dashboard:

```
cd scripts
./configureGrafana.sh
```

Next edit Grafana's config file (`/etc/grafana/grafana.ini`) and enable anonymous access:

```
#################################### Anonymous Auth ##########################
[auth.anonymous]
# enable anonymous access
enabled = true

# specify organization name that should be used for unauthenticated users
org_name = Main Org.

# specify role for unauthenticated users
org_role = Viewer
```

Restart Grafana server when done:

```
sudo systemctl stop  grafana-server
sudo systemctl start  grafana-server
```

### Temperature monitor

**Installation**

Copy Debian package to Raspberry Pi:

`scp target/temperature-monitor_1.0_all.deb pi@raspberrypi:temperature-monitor_1.0_all.deb`

then ssh to Raspberry Pi and install it:

`sudo dpkg -i temperature-monitor_1.0_all.deb`

To uninstall previous version:

`sudo apt-get remove --purge temperature-monitor`

Log files are located in `/var/log/temperature-monitor/`

**Configuration**

Configuration files are located in `/etc/temperature-monitor`. To change configuration create `override.conf` file and
override in it configuration properties from `application.conf` and `production.conf`. Don't modify original files as
you will lose your config when uninstalling/upgrading application.

If you decided not to store readings you should disable InfluxDB integration or you will see error messages in logs:

```
app.influxDb.enable = false
```

Otherwise make sure that provided InfluxDB credentials are correct (if authentication is disabled you don't have to
change anything as InfluxDB will ignore all provided credentials):

```
app.influxDb.username = <USERNAME>
app.influxDb.password = <PASSWORD>
```

Disable embedded graph if you're not using Grafana:

```
app.grafana.enable = false
```

When using different GPIO pin for sensor than default (22) update it in the config:

```
app.sensor.pin = <PIN NUMBER>
```

### Other

Set correct timezone on Raspberry Pi:

`dpkg-reconfigure tzdata`

Enable time synchronization:

```
sudo timedatectl set-ntp 1
sudo timedatectl set-local-rtc 0
```

## Usage

Open http://raspberrypi:9000/

View dashboard: http://raspberrypi:3000/dashboard/db/temperature-and-humidity
