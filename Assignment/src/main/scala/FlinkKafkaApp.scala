import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.flink.api.common.eventtime.WatermarkStrategy
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.connector.kafka.sink.{KafkaRecordSerializationSchema, KafkaSink}
import org.apache.flink.connector.kafka.source.KafkaSource
import org.apache.flink.streaming.api.scala._


object FlinkKafkaApp extends App {

  private val env = StreamExecutionEnvironment.getExecutionEnvironment


  // Kafka consumer
  private val kafkaSource = KafkaSource.builder[String]()
    .setBootstrapServers("localhost:9092")
    .setTopics("Consumer_details")
    .setValueOnlyDeserializer(new SimpleStringSchema())
    .build()

  // Kafka producer for even topic
  private val evenKafkaSink = KafkaSink.builder[String]()
    .setBootstrapServers("localhost:9092")
    .setRecordSerializer(KafkaRecordSerializationSchema.builder[String]().setTopic("EVEN_TOPIC")
      .setValueSerializationSchema(new SimpleStringSchema())
      .build())
    .build()
  // Kafka producer for odd topic
  private val oddKafkaSink = KafkaSink.builder[String]()
    .setBootstrapServers("localhost:9092")
    .setRecordSerializer(KafkaRecordSerializationSchema.builder[String]().setTopic("ODD_TOPIC")
      .setValueSerializationSchema(new SimpleStringSchema())
      .build())
    .build()

  val inputStream: DataStream[String] = env.fromSource(kafkaSource, WatermarkStrategy.noWatermarks(), "KafkaSource")


  val helper = new Helper {}

  private val streams = helper.ageSegregator(inputStream)
  val mapper = new ObjectMapper()
  // Push messages to respective kafka topic
  try {
    streams._1.map(person => mapper.writeValueAsString(person)).sinkTo(evenKafkaSink)
    streams._2.map(person => mapper.writeValueAsString(person)).sinkTo(oddKafkaSink)
  } catch {
    case e: Exception => println("Failed to push messages to kafka::", e)
  }

  // Actual parameters to be passed from a config reader.
  val dbConfig = Map("database" -> "Customer_db",
    "Driver" -> "org.postgresql.Driver",
    "username" -> "username",
    "Password" -> "password"
  )

  val sinkDb = helper.dbSink(dbConfig)
  try {
    streams._1.addSink(sinkDb)
    streams._1.addSink(sinkDb)
  } catch {
    case e: Exception => println("Failed to sink messages to database::", e)
  }

  env.execute("Flink Kafka Stream Processing")

}