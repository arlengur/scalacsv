import org.scalatest._
import scala.io.Source
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import ru.arlen._
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class ReadCsvTest extends AnyWordSpec with Matchers {
  "Read from string with spases" should {
    "correctly work" in {
      val src = io.Source.fromString(
        "  2   ,  2016-01-01 00:00:00,   2016-01-01 00:00:00, 3 ,1.76,-73.960624694824219,40.781330108642578,1,N,-73.977264404296875,40.758514404296875,2,8,0,0.5,0,0,0.3,8.8"
      )
      val taxiTable = new TaxiTableImpl()

      val iter = new TaxiTableImpl().from_source(src)

      iter.toList(0) shouldBe Trip(
        1.76,
        taxiTable.toLocatDateTime("2016-01-01 00:00:00"),
        taxiTable.toLocatDateTime("2016-01-01 00:00:00"),
        3
      )

    }
  }

  "Read from 2 lines string" should {
    "correctly work" in {
      val src = io.Source.fromString(
        "2,2016-01-01 00:00:00,2016-01-01 00:00:00,3,1.76,-73.960624694824219,40.781330108642578,1,N,-73.977264404296875,40.758514404296875,2,8,0,0.5,0,0,0.3,8.8\n2,2016-01-01 00:00:00,2016-01-01 00:00:00,3,1.76,-73.960624694824219,40.781330108642578,1,N,-73.977264404296875,40.758514404296875,2,8,0,0.5,0,0,0.3,8.8"
      )
      val taxiTable = new TaxiTableImpl()

      val iter = new TaxiTableImpl().from_source(src)

      iter.toList(0) shouldBe Trip(
        1.76,
        taxiTable.toLocatDateTime("2016-01-01 00:00:00"),
        taxiTable.toLocatDateTime("2016-01-01 00:00:00"),
        3
      )
      iter.toList(1) shouldBe Trip(
        1.76,
        taxiTable.toLocatDateTime("2016-01-01 00:00:00"),
        taxiTable.toLocatDateTime("2016-01-01 00:00:00"),
        3
      )

    }
  }

  "Avg from 2 lines string" should {
    "correctly work" in {
      val src = io.Source.fromString(
        "  2   ,  2016-01-01 00:00:00,   2016-01-01 00:00:00, 3 ,1.76,-73.960624694824219,40.781330108642578,1,N,-73.977264404296875,40.758514404296875,2,8,0,0.5,0,0,0.3,8.8\n2   ,  2016-01-01 00:00:00,   2016-01-01 00:00:00, 3 ,1.76,-73.960624694824219,40.781330108642578,1,N,-73.977264404296875,40.758514404296875,2,8,0,0.5,0,0,0.3,8.8"
      )
      val taxiTable = new TaxiTableImpl()

      val iter = new TaxiTableImpl().from_source(src)
      val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
      val start = LocalDateTime.parse("2016-01-01 00:00:00", formatter)
      val end = LocalDateTime.parse("2016-01-01 00:00:00", formatter)
      val aver = taxiTable.getAverageDistances(iter, start, end)

      aver should not be empty
      aver should contain(3 -> 1.76)

    }
  }

  "Calc average" should {
    "correctly work" in {
      val Path = "/Users/agalin/Downloads/archive/yellow_tripdata_2015-01.csv"
      val taxiTable = new TaxiTableImpl()

      val t0 = System.nanoTime()
      val iter = taxiTable.from_csv(Path)
      println("Elapsed from csv time: " + (System.nanoTime() - t0) / 1e9d + "s")

      val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
      val start = LocalDateTime.parse("2015-01-09 20:33:38", formatter)
      val end = LocalDateTime.parse("2015-01-10 20:43:41", formatter)

      val t1 = System.nanoTime()
      val aver1 = taxiTable.getAverageDistances(iter, start, end)
      println(
        "Elapsed from AverageDistances time: " + (System
          .nanoTime() - t1) / 1e9d + "s"
      )
      println(aver1)

      val t2 = System.nanoTime()
      val aver2 = taxiTable.getAverageDistancesVector(iter, start, end)
      println(
        "Elapsed from getAverageDistancesVector time: " + (System
          .nanoTime() - t2) / 1e9d + "s"
      )
      println(aver2)
    }
  }
}
