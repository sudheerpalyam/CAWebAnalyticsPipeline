package au.com.ca.assessment.spark.streaming

import java.sql.Timestamp

import org.apache.spark.SparkConf
import org.apache.spark.sql.streaming.Trigger
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}

/**
  * @author sudheerpalyam
  * @version 0.1
  *
  * Explore Spark streaming aggregations.
  */
object FileStreamingAggregations {

  //convert aggregates into typed data
  case class PageView(anonymousUserId: String, url: String, time: Option[Int],  browser: String,  os: String,  screenResolution: String,  eventTimeReadable: String)
  case class AggretatedPageView(anonymousUserId: String, url: String, maxTime: Option[Double], avgViews: Option[Double])


  def main(args: Array[String]): Unit = {

    //create a spark session, and run it on local mode
    val spark = SparkSession.builder()
      .appName("StreaminAggregations")
      .master("local[*]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    import spark.implicits._

    // Page View data schema
    val schema = StructType(
      StructField("anonymousUserId", StringType) ::
        StructField("url", StringType) ::
        StructField("time", IntegerType) ::
        StructField("browser", StringType) ::
        StructField("os", StringType) ::
        StructField("screenResolution", StringType) :: Nil)

    //read the files from source
    val pageViews: DataFrame = spark
      .readStream
      .schema(schema).option("header", "true")
      .csv("/Users/sudheer/workspace/CAWebAnalyticsPipeline/data/pageviews")

//    //do aggregates
//    val aggregates = pageViews
//      .groupBy("stockName", "tradeType")
//      .agg(
//        "price" → "max",
//        "quantity" → "avg")
//      .withColumnRenamed("max(price)", "maxPrice")
//      .withColumnRenamed("avg(quantity)", "avgQuantity")

//    aggregates.printSchema()
//    aggregates.explain()
//
//    val typedAggregates = aggregates.as[AggretatedPageView]
//    val filtered  = typedAggregates
//      .filter(_.maxPrice.exists(_ > 70))
//      .where("avgQuantity > 10")
//      .repartition(10)


    val query = pageViews
      .writeStream
      .queryName("page_views")
      .partitionBy("anonymousUserId")
      .outputMode("append")
      .format("console")
      .start()


    // Publish raw page views into a Kafka topic, so that it can be written to Data lake, perform further processing, render UI etc.
    val kafkaSinkQuery = pageViews
      .selectExpr("CAST(anonymousUserId AS STRING) AS key", "to_json(struct(*)) AS value")
      .writeStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "localhost:9092")
      .option("topic", "page_views")
      .option("checkpointLocation", "/tmp/spark/continuousCheckpoint")
      .queryName("kafka spark - raw page views")
      .outputMode("update")
      .trigger(Trigger.Continuous("10 seconds")) //how often to checkpoint the offsets,
      .outputMode("append") //ouput only new records
      .start()

    query.awaitTermination()
  }
}