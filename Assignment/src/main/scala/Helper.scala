import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.apache.flink.connector.jdbc.JdbcConnectionOptions.JdbcConnectionOptionsBuilder
import org.apache.flink.connector.jdbc.JdbcSink
import org.apache.flink.streaming.api.functions.sink.SinkFunction
import org.apache.flink.streaming.api.scala._
import java.sql.PreparedStatement
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

trait Helper {
  private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

  def calculateAge(dob: String): Int = {
    val birthDate = LocalDate.parse(dob, dateFormatter)
    ChronoUnit.YEARS.between(birthDate, LocalDate.now()).toInt }


  def ageSegregator(inputStream: DataStream[String]): (DataStream[CustomerDetails], DataStream[CustomerDetails]) = {

    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    val processedStream = inputStream.map { message =>
      val person = mapper.readValue(message, classOf[Customer])
      val age = calculateAge(person.dateOfBirth)
      CustomerDetails(person, age) }

    val evenStream = processedStream.filter(_.age % 2 == 0)
    val oddStream = processedStream.filter(_.age % 2 != 0)
    (evenStream, oddStream)

  }

  def dbSink(dbConf: Map[String, String]): SinkFunction[CustomerDetails] = {
    // Define JDBC Sink to write to MySQL
    val jdbcSink = JdbcSink.sink[CustomerDetails](
      "INSERT INTO processed_messages (name, address, date_of_birth, age) VALUES (?, ?, ?, ?)",
      (ps: PreparedStatement, t: CustomerDetails) => {
        ps.setString(1, t.person.name)
        ps.setString(2, t.person.address)
        ps.setDate(3, java.sql.Date.valueOf(LocalDate.parse(t.person.dateOfBirth)))
        ps.setInt(4, t.age)
      },
      new JdbcConnectionOptionsBuilder()
        .withUrl(s"jdbc:postgresql://localhost:5432/${dbConf("database")}")
        .withDriverName(dbConf("Driver"))
        .withUsername(dbConf("username"))
        .withPassword(dbConf("password"))
        .build())
    jdbcSink
  }
}
