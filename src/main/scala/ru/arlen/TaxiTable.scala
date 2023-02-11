package ru.arlen

import scala.io.Source
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.nio.Buffer
import scala.io.BufferedSource

case class Trip(
    distance: Double,
    pickupDatetime: LocalDateTime,
    dropoffDatetime: LocalDateTime,
    passengerCount: Int
)

trait TaxiTable {
  def from_csv(path: String): IndexedSeq[Trip]
  def getAverageDistances(
      iter: IndexedSeq[Trip],
      start: LocalDateTime,
      end: LocalDateTime
  ): Map[Int, Double]

}

class TaxiTableImpl extends TaxiTable {
  def from_csv(path: String) = {
    val src = Source.fromFile(path)
    val iter = src
      .getLines()
      .drop(1)
      .map(_.split(","))
      .map { case Array(_, start, end, count, distance, _*): Array[String] =>
        Trip(
          distance.toDouble,
          toLocatDateTime(start.trim),
          toLocatDateTime(end.trim),
          count.trim.toInt
        )
      }
      .toIndexedSeq
    iter
  }
  def from_source(src: Source) = {
    val iter = src
      .getLines()
      .map(_.split(","))
      .map { case Array(_, start, end, count, distance, _*): Array[String] =>
        Trip(
          distance.toDouble,
          toLocatDateTime(start.trim),
          toLocatDateTime(end.trim),
          count.trim.toInt
        )
      }
      .toIndexedSeq
    iter
  }
  def getAverageDistances(
      iter: IndexedSeq[Trip],
      start: LocalDateTime,
      end: LocalDateTime
  ): Map[Int, Double] = {
    val aver = iter
      .filter(_.pickupDatetime.compareTo(start) >= 0)
      .filter(_.pickupDatetime.compareTo(end) <= 0)
      .groupBy(_.passengerCount)
      .map(m => m._1 -> m._2.map(_.distance).sum / m._2.length)
    aver
  }

  def getAverageDistancesList(
      iter: Iterator[Trip],
      start: LocalDateTime,
      end: LocalDateTime
  ): Map[Int, Double] = {
    val aver = iter
      .filter(_.pickupDatetime.compareTo(start) >= 0)
      .filter(_.pickupDatetime.compareTo(end) <= 0)
      .toList
      .groupBy(_.passengerCount)
      .map(m => m._1 -> m._2.map(_.distance).sum / m._2.length)
    aver
  }

  def getAverageDistancesVector(
      iter: IndexedSeq[Trip],
      start: LocalDateTime,
      end: LocalDateTime
  ): Map[Int, Double] = {
    val aver = iter
      .filter(_.pickupDatetime.compareTo(start) >= 0)
      .filter(_.pickupDatetime.compareTo(end) <= 0)
      .toVector
      .groupBy(_.passengerCount)
      .map(m => m._1 -> m._2.map(_.distance).sum / m._2.length)
    aver
  }

  def getAverageDistancesFold(
      iter: Iterator[Trip],
      start: LocalDateTime,
      end: LocalDateTime
  ): Map[Int, Double] = {
    val aver = iter
      .filter(_.pickupDatetime.compareTo(start) >= 0)
      .filter(_.pickupDatetime.compareTo(end) <= 0)
      .foldLeft(Map.empty[Int, List[Double]]) { (acc, t) =>
        if (acc.contains(t.passengerCount))
          acc + (t.passengerCount -> (acc.get(t.passengerCount).get ++ List(
            t.distance
          )))
        else acc + (t.passengerCount -> List(t.distance))
      }
      .map(m => m._1 -> m._2.sum / m._2.length)
    aver
  }

  def getAverageDistancesFoldImpr(
      iter: Iterator[Trip],
      start: LocalDateTime,
      end: LocalDateTime
  ): Map[Int, Double] = {
    val aver = iter
      .filter(_.pickupDatetime.compareTo(start) >= 0)
      .filter(_.pickupDatetime.compareTo(end) <= 0)
      .foldLeft(Map.empty[Int, (Int, Double)]) { (acc, t) =>
        if (acc.contains(t.passengerCount))
          acc + (t.passengerCount -> (acc
            .get(t.passengerCount)
            .get
            ._1 + 1 -> (acc.get(t.passengerCount).get._2 + t.distance)))
        else acc + (t.passengerCount -> (1 -> t.distance))
      }
      .map(m => m._1 -> m._2._2 / m._2._2)
    aver
  }

  def getAverageDistancesFoldOnVector(
      iter: Iterator[Trip],
      start: LocalDateTime,
      end: LocalDateTime
  ): Map[Int, Double] = {
    val aver = iter
      .filter(_.pickupDatetime.compareTo(start) >= 0)
      .filter(_.pickupDatetime.compareTo(end) <= 0)
      .foldLeft(Map.empty[Int, Vector[Double]]) { (acc, t) =>
        if (acc.contains(t.passengerCount))
          acc + (t.passengerCount -> (acc
            .get(t.passengerCount)
            .get :+ t.distance))
        else acc + (t.passengerCount -> Vector(t.distance))
      }
      .map(m => m._1 -> m._2.sum / m._2.size)
    aver
  }

  def toLocatDateTime(str: String) = {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    LocalDateTime.parse(str, formatter)
  }
}
