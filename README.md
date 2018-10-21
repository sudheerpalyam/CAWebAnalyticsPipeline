### Technical assessment

Currently focusing on performing Page View analytics on Streaming Market Stock data, but this framework is generally extendable for any Data Engineering, Machine Learning Engineering tasks.

@author: Sudheer Palyam


### Scalability considerations:
    KAFKA:
        1. Partitions with in Topics drives the parallelism of consumers. So increase the number of partitions so that a Spark Consumer Group can process these messages in parallel.
        2. Based on throughput requirements one can pick a rough number of partitions.
            Lets call the throughput from producer to a single partition is P
            Throughput from a single partition to a consumer is C
            Target throughput is T
            Required partitions = Max (T/P, T/C)
     SPARK:
        1. Run in Yarn-Client or Yarn-Cluster mode with kafka consumer group set. Set the Number of Kafka Partitions according to number of Spark Executor tasks feasible on the cluster.
        2. Appropriate Spark Streaming Watermark configuration.
     AWS:
        1. Porting this pipeline to AWS Kinesis Firehose & Analytics will take care of scaling shards automatically and these are also managed serverless services.




### Architectural Patterns:
    Given problem can be implemented in the following architectures, Considering Cost optimization, Reliability, Operational Efficiency, Performance & Security (CROPS):





####   Unit Tests
Stock Aggregations Unit Tests results page
https://rawgit.com/sudheerpalyam/CAWebAnalyticsPipeline/master/target/test-reports/index.html

![Unit Test Results](static/ScalaTestResults.png?raw=true "Unit Test Results")

### Quick steps to setup kafka and run locally:
  Download from https://kafka.apache.org/downloads
  ```
  start zookeeper:
  $<kafka-dir>/bin/zookeeper-server-start.sh config/zookeeper.properties

  start kafka broker(s):
  $<kafka-dir>/bin/kafka-server-start.sh config/server.properties

  create kafka topics:
  $<kafka-dir>/bin/kafka-topics.sh --create --topic "page_views" --replication-factor 1 --partitions 4 --zookeeper localhost:2181
  $<kafka-dir>/bin/kafka-topics.sh --create --topic "aggregate_page_views" --replication-factor 1 --partitions 4 --zookeeper localhost:2181

  List Topics:
  $<kafka-dir>/bin/kafka-topics.sh  --list --zookeeper localhost:2181
  Delete Topic
  $<kafka-dir>/bin/kafka-topics.sh  --delete --topic page_views --zookeeper localhost:2181


  $<kafka-dir>/bin/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic page_views
  $<kafka-dir>/bin/bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic page_views --from-beginning


  describe:
  $<kafka-dir>/bin/kafka-topics.sh --zookeeper localhost:2181 --describe --topic page_views
  $<kafka-dir>/bin/kafka-topics.sh --zookeeper localhost:2181 --describe --topic aggregate_page_views


  Spark Submit Script:
  src/main/resources/submitSpark.sh

  nohup spark-submit \
   --deploy-mode client --master yarn \
   --executor-cores 4 --executor-memory 6g --conf spark.yarn.executor.memoryOverhead=1G --conf spark.yarn.driver.memoryOverhead=1G  \
   --jars $(echo /home/sudheerpalyam/jars/*.jar | tr ' ' ',') \
   --class au.com.ca.assessment.spark.streaming.KafkaStructuredStreaming \
   /home/sudheerpalyam/jars/CAWebAnalyticsPipeline_2.11-0.1.jar \
   --isGlue false \
   --mode  yarn >> ../logs/stock-spark-$runTime.log &

```

### Next Steps:
  1) Integrate a visualization layer based on Kibana & InfluxDB to continuously stream raw vs moving averages
  2) Run Kafka & Spark in Yarn/Mesos/DCOS Clustered Mode
  3) Implement the same pipeline using AWS native Serverless components replacing:
        Kafka -> AWS Kinesis Streams
        Spark -> AWS Kinesis Analytics (As there is no serverless equivalent of Spark yet in AWS)
        Spark Console/Kafka Writer -> AWS Kinesis FireHose
        Kibana -> AWS QuickSight
        Scala Kafka Producer -> Kinesis Data Generator
  4) Dockerize all the workflow components and run it in Container managers like Kubernetes or AWS Elastic Kubernetes Service
  5) Enhance Unit Tests and perform Code Coverage and eventually DevOps
  6) SonarQube/Fortify code vulnerability assessment
  7) Associate a Machine Learning use case which can be facilitated by moving averages.
  8) Integrate with actual public Stock streams APIs like and perform RealTime rolling averages :
    https://globalrealtime.xignite.com/v3/xGlobalRealTime.json/ListExchanges?
    https://kite.trade/docs/connect/v1/



  ### References:
  http://springbrain.blogspot.com/2017/12/spark-scala-perform-data-aggregation-on.html (My blog)
  https://github.com/soniclavier/bigdata-notebook/blob/master/spark_23
  https://github.com/pablo-tech/SparkService--Statistician
  https://aws.amazon.com/big-data/datalakes-and-analytics/
  https://docs.aws.amazon.com/streams/latest/dev/learning-kinesis-module-one.html
  https://vishnuviswanath.com/spark_structured_streaming.html -- Good blog on Structured Streaming
  https://databricks.com/blog/2017/05/08/event-time-aggregation-watermarking-apache-sparks-structured-streaming.html - Structured Streaming Window aggregations
  https://github.com/snowplow/spark-streaming-example-project/blob/master/src/main/scala/com.snowplowanalytics.spark/streaming/StreamingCounts.scala - Spark Streaming write to DynamoDB
  https://github.com/snowplow/spark-streaming-example-project/blob/master/src/main/scala/com.snowplowanalytics.spark/streaming/kinesis/KinesisUtils.scala - Kinesis Utils
  https://github.com/snowplow/spark-streaming-example-project/blob/master/src/main/scala/com.snowplowanalytics.spark/streaming/storage/DynamoUtils.scala - Dynamo Utils
  https://www.slideshare.net/SparkSummit/time-series-analytics-with-spark-spark-summit-east-talk-by-simon-Ouellette

