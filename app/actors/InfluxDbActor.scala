package actors

import javax.inject.Inject

import akka.actor.Actor
import com.paulgoldbaum.influxdbclient.Parameter.Precision
import com.paulgoldbaum.influxdbclient.{InfluxDB, Point}
import models.SensorReading
import play.api.{Configuration, Logger}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class InfluxDbActor @Inject()(config: Configuration)(implicit ec: ExecutionContext) extends Actor {

  private val influxDbHost = config.get[String]("app.influxDb.host")
  private val influxDbPort = config.get[Int]("app.influxDb.port")
  private val influxDbDatabase = config.get[String]("app.influxDb.database")

  override def preStart: Unit = if (config.get[Boolean]("app.influxDb.enable"))
    context.system.eventStream.subscribe(self, classOf[SensorReading])

  override def receive: Receive = {
    case SensorReading(timestamp, temperature, humidity) =>
      writeToInfluxDb(timestamp, temperature, humidity)
  }

  private def writeToInfluxDb(timestamp: Long, temperature: Float, humidity: Float) = {
    val influxDb = InfluxDB.connect(influxDbHost, influxDbPort)
    val database = influxDb.selectDatabase(influxDbDatabase)
    val point = Point("temperature-humidity")
      .addTag("sensor", "sensor1")
      .addField("tmp", temperature)
      .addField("hum", humidity)
    val f = database.write(point, precision = Precision.MILLISECONDS)
    f.onComplete {
      case Failure(e) =>
        Logger.error("write to Influx DB failed", e)
        influxDb.close()
      case Success(true) =>
        Logger.info(s"$point successfully wrote to Influx DB")
        influxDb.close()
      case Success(false) =>
        influxDb.close()
    }
    f
  }
}
