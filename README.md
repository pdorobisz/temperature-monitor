# Temperature and hummidity monitor

Temperature and humidity monitor for Raspberry Pi.

## Useful commands

### InfluxDB

After logging to Influx's command line client:

`show databases`

`use <database>`

`show series`

Using HTTP api:

`curl -G 'http://localhost:8086/query?pretty=true' --data-urlencode "db=temperature" --data-urlencode "q=SELECT * FROM \"temperature-humidity\""`