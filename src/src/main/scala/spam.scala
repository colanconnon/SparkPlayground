/* pi.scala */
import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.Pipeline
import org.apache.spark.ml.classification.DecisionTreeClassificationModel
import org.apache.spark.ml.classification.DecisionTreeClassifier
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator
import org.apache.spark.ml.feature.{IndexToString, StringIndexer, VectorIndexer, Word2Vec, Tokenizer}

object spam {
  def main(args: Array[String]) {
    val spark = SparkSession.builder.appName("Simple Application").getOrCreate()
    val sc = spark.sparkContext
    sc.setLogLevel("WARN")
    val data = spark.read.format("csv").option("header", "true").load("./spam.csv").na.drop(Array("v1", "v2"))
    
    val labelIndexer = new StringIndexer()
      .setInputCol("v1")
      .setOutputCol("indexedLabel")
      .fit(data)

    val tokenizer = new Tokenizer().setInputCol("v2").setOutputCol("words")

    val featureIndexer = new Word2Vec()
      .setInputCol("words")
      .setOutputCol("indexedFeatures")
      .setVectorSize(3)
      .setMinCount(0)
  
    val Array(trainingData, testData) = data.randomSplit(Array(0.8, 0.2))

    val dt = new DecisionTreeClassifier()
      .setLabelCol("indexedLabel")
      .setFeaturesCol("indexedFeatures")

    val labelConverter = new IndexToString()
      .setInputCol("prediction")
      .setOutputCol("predictedLabel")
      .setLabels(labelIndexer.labels)

    val pipeline = new Pipeline()
    .setStages(Array(tokenizer, labelIndexer, featureIndexer, dt, labelConverter))

    val model = pipeline.fit(trainingData)

    val predictions = model.transform(testData)

    predictions.select("predictedLabel", "v1", "v2").show(10)

    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("indexedLabel")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")
    val accuracy = evaluator.evaluate(predictions)
    println(s"Test Error = ${(1.0 - accuracy)}")
    
    spark.stop()
  }
}