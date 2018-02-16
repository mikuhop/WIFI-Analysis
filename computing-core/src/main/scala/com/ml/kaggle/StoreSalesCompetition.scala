package com.ml.kaggle

import breeze.linalg.split
import org.apache.log4j.{Level, Logger}
import org.apache.spark
import org.apache.spark.ml.linalg
import org.apache.spark.ml.linalg.Vectors
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import org.apache.spark.sql.types.{DoubleType, IntegerType, StringType}

/**
  * Created by Administrator on 2018/2/14.
  */
object StoreSalesCompetition {


  Logger.getLogger("org.apache.spark").setLevel(Level.WARN)

  def main(args: Array[String]): Unit = {
    val spark:SparkSession = SparkSession.builder().appName("storeSales")
      .master("local[*]")
      .getOrCreate()
import spark.implicits._
    val goal = "Sales"
    val myid = "Id"
    val plot = true
    val path = "F:\\BaiduYunDownload\\Kaggle课程(关注公众号菜鸟要飞，免费领取200G+教程)\\Kaggle实战班(关注公众号菜鸟要飞，免费领取200G+教程)\\七月kaggle(关注公众号菜鸟要飞，免费领取200G+教程)\\代码(关注公众号菜鸟要飞，免费领取200G+教程)\\lecture07_销量预估\\data"

   val (train,test,features,featuresNonNumeric)=loadData(spark,path)
    processData(spark,train,test,features,featuresNonNumeric)
//    train.show(10,false)
//    train.printSchema()
//    test.show(10,false)
    println(features.mkString(","))
    println("Non")
    println(featuresNonNumeric.mkString(","))

  }

  def loadData(spark:SparkSession,path:String): (DataFrame, DataFrame, Array[String], Array[String]) ={
    import spark.implicits._
    val read=spark.read.option("header","true").option("nullValue","NA").option("inferSchema","true")

    val store=read.csv(path+"\\store.csv")
    val train_org=read.csv(path+"\\train.csv").withColumn("StateHoliday",$"StateHoliday".cast(StringType))
    //where build Join after,the Store will display two
     val train =train_org.join(store,Array("Store"),"left")
    val test_org: DataFrame =read.csv(path+"\\test.csv").withColumn("StateHoliday", $"StateHoliday".cast(StringType))
//    val test: DataFrame =test_org.join(store,test_org("Store")===store("Store"),"left")
    val test: DataFrame =test_org.join(store,Array("Store"),"left")
    val features:Array[String]=test.columns
    val featuresNumeric:Seq[String]=test.schema.filter(line=>line.dataType==IntegerType).map(_.name)
    val featuresNonNumeric=features.filterNot(line=>featuresNumeric.contains(line))
//Date,StateHoliday,StoreType,Assortment,PromoInterval
    (train,test,features,featuresNonNumeric)
  }
  case class store2Vector(Store:Int,promos:linalg.Vector)
  def processData(spark:SparkSession,train:DataFrame,test:DataFrame,features:Array[String],featuresNonNumeric:Array[String])={
    val trainCleanSales=train.filter(train("Sales")>0)
    trainCleanSales.show(10,false)
import spark.implicits._
//   train.select($"Date".as[String]).map(date=>{
//      val year=date.split(" ")(0).split("-")(0).toDouble
//      val month=date.split(" ")(0).split("-")(1).toDouble
//      val day=date.split(" ")(0).split("-")(2).toDouble
//
//    })


    //January,February,March,April,May,June,July,August,September,October,November,December
    //Jan,Feb,Mar,Apr,May,Jun,Jul,Aug,Sept,Oct,Nov,Dec
    val months=Array("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sept","Oct","Nov","Dec")
    val promosDS =train.select($"Store".as[Int],$"PromoInterval".as[String])
    promosDS.show(10,false)
   val promosDSs   = promosDS.map(line=>{
     val intervalsStr:String=line._2
     if(intervalsStr!=null) {
       val intervals = intervalsStr.split(",")
       //      val promos=new Array[Int](12)
       val promos = scala.collection.mutable.ArrayBuffer[(Int, Double)]()
       intervals.foreach(month => {
         val index = months.indexOf(month)
         promos += Tuple2(index, 1.0)
       })
       store2Vector(line._1, linalg.Vectors.sparse(12,promos))
     }else{
       store2Vector(line._1,null)
     }
    })
    promosDSs.show(10,false)

  }
  case class StoreCase()

}
