package views

import java.text.SimpleDateFormat
import java.util.Date

import models.SensorReading

case class SensorReadingView(date: String, temperature: String, humidity: String)

object SensorReadingView {

  def apply(r: SensorReading): SensorReadingView = SensorReadingView(
    date = new SimpleDateFormat("dd.MM.YYY HH:mm:ss").format(new Date(r.timestamp)),
    temperature = r.temperature.toString,
    humidity = r.humidity.toString
  )
}

