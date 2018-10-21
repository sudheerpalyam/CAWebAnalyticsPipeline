package au.com.ca.assessment.spark.streaming

import java.sql.Timestamp

import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.apache.spark.sql.functions._

/**
  * @author sudheerpalyam
  * @version 0.1
  *
  * Spark streaming processor from Kafka source.
  * Ingests messages from Kafka topics, unions messages from different topics, Cast messages to Stock objects, Performs Window based aggregations and write to Console/another Kafka topic.
  *
  */
object KafkaStructuredStreaming {

  //convert aggregates into typed data
  case class StockEvent(stockName: String, tradeType: String, price: Option[Double], quantity: Option[Int], timestamp: Timestamp, eventTimeReadable: String)
  object StockEvent {
    def apply(rawStr: String): StockEvent = {
      val parts = rawStr.split(",")
      StockEvent(parts(0), parts(1), Some(java.lang.Double.parseDouble(parts(2))), Some(Integer.parseInt(parts(3))),  new Timestamp(parts(4).toLong), parts(5))
    }
  }

  def main(args: Array[String]): Unit = {

    //create a spark session, and run it on local mode
    val spark = SparkSession.builder()
      .appName("KafkaSourceStreaming")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    import spark.implicits._

    //read the source
    val df: DataFrame = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
//      .option("group.id", "myStockConsumerGroup")  // Mechanism to run spark tasks as kafka consumer group, to achieve parallel. Controlled by Kafka Topic partitions
      .option("subscribe", "stocks")
      .option("startingOffsets", "latest") // Spark reads only latest Kafka messages, comment this to read from beginning
      //.schema(schema)  : we cannot set a schema for kafka source. Kafka source has a fixed schema of (key, value)
      .load()

    // Map the source messages to Stock object
    val stocks: Dataset[StockEvent] = df
      .selectExpr("CAST(value AS STRING)")
      .map(r ⇒ StockEvent(r.getString(0)))

    val stocksDF : DataFrame = stocks.toDF()

    //windowing
    val aggregates = performSlidingWindowFrame (stocksDF)

    // Get the resultant Dataframe Schema
   aggregates.printSchema()

    // Print the window processing output in Console. Tricky to see when run in Yarn Cluster Mode
    val writeToConsole = aggregates
      .writeStream
      .format("console")
      .option("truncate", "false") //prevent trimming output fields
      .queryName("kafka spark streaming console")
      .outputMode("append") // only supported when we set watermark. output only new
      .start()

    // Publish moving averages into another Kafka topic, so that it can be written to Data lake, render UI etc.
    val writeToKafka = aggregates
      .selectExpr("CAST(stockName AS STRING) AS key", "to_json(struct(*)) AS value")
      .writeStream
      .format("kafka")
      .option("kafka.bootstrap.servers","localhost:9092")
      .option("topic", "stocks_averages")
      //.option("startingOffsets", "earliest") //earliest, latest or offset location. default latest for streaming
      //.option("endingOffsets", "latest") // used only for batch queries
      .option("checkpointLocation", "/tmp/sparkcheckpoint/") //must when not memory or console output
      .queryName("kafka spark streaming kafka")
      //.outputMode("complete") // output everything
      //.outputMode("append")  // only supported when we set watermark. output only new
      .outputMode("append") //ouput only new records
      .start()

    spark.streams.awaitAnyTermination() //running multiple streams at a time
  }

  def performSlidingWindowFrame (df: DataFrame) : DataFrame  = {
        df
      .withWatermark("timestamp", "5 seconds") // Ignore data if they are late by more than 5 seconds
      .groupBy(window(col("timestamp"),"10 seconds","5 seconds"), col("stockName"))  //sliding window of size 10 seconds, that slides every 5 second
      .agg(avg("price").alias("avgPrice"), min("price").alias("minPrice"), max("price").alias("maxPrice"), count("price").alias("count"))
      .select("window.start", "window.end", "stockName", "avgPrice", "minPrice", "maxPrice", "count")
  }
}