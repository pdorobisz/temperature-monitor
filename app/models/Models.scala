package models

case class SensorReading(timestamp: Long, temperature: Float, humidity: Float)

case class GraphConfig(enable: Boolean, graphUrl: String, dashboardUrl: String)
