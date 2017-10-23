package models

import java.text.SimpleDateFormat
import java.util.Date

case class ReadingView(timestamp: String, temperature: String, humidity: String)

object ReadingView {

  def apply(r: SensorReading): ReadingView = ReadingView(
    timestamp = new SimpleDateFormat("dd.MM.YYY HH:mm:ss").format(new Date(r.timestamp)),
    temperature = r.temperature.toString,
    humidity = r.humidity.toString
  )
}

