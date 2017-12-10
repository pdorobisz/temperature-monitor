#!/bin/bash

# Configure API KEY and URL
GRAFANA_API_KEY=eyJrIjoiU2ZDSkNpYVYzbVpBU3dtekFIYmhBckV0SktTUXdHM3AiLCJuIjoiYWRtaW5rZXkiLCJpZCI6MX0=
GRAFANA_URL=localhost:3000

echo "Creating Influx DB data source..."
curl -H "Authorization: Bearer $GRAFANA_API_KEY"  \
 -H "Content-Type: application/json" $GRAFANA_URL/api/datasources -d \
'
{
  "name":"temperature",
  "type":"influxdb",
  "url":"http://localhost:8086",
  "access":"proxy",
  "user":"",
  "password":"",
  "database":"temperature",
  "basicAuth":false
}'
echo ""
echo "done"

echo "Creating dashboard..."
curl -X POST  -H "Authorization: Bearer $GRAFANA_API_KEY" -H "Content-Type: application/json" $GRAFANA_URL/api/dashboards/db -d @dashboard.json
echo ""
echo "done"
