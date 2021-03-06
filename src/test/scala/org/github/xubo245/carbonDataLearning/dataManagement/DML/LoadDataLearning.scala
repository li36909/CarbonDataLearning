package org.github.xubo245.carbonDataLearning.dataManagement.DML

import java.io.File

import org.apache.carbondata.core.constants.CarbonCommonConstants
import org.apache.carbondata.core.util.CarbonProperties
import org.apache.spark.sql.SparkSession

/**
  * Created by root on 8/11/17.
  */
object LoadDataLearning {
  def main(args: Array[String]): Unit = {
    val rootPath = new File(this.getClass.getResource("/").getPath
      + "../../../..").getCanonicalPath
    val storeLocation = s"$rootPath/examples/spark2/target/store"
    val warehouse = s"$rootPath/examples/spark2/target/warehouse"
    val metastoredb = s"$rootPath/examples/spark2/target"

    CarbonProperties.getInstance()
      .addProperty(CarbonCommonConstants.CARBON_TIMESTAMP_FORMAT, "yyyy/MM/dd HH:mm:ss")
      .addProperty(CarbonCommonConstants.CARBON_DATE_FORMAT, "yyyy/MM/dd")
    //      .addProperty(CarbonCommonConstants.ENABLE_UNSAFE_COLUMN_PAGE_LOADING, "true")

    import org.apache.spark.sql.CarbonSession._
    val carbon = SparkSession
      .builder()
      .master("local")
      .appName("CarbonSessionExample")
      .config("spark.sql.warehouse.dir", warehouse)
      .config("spark.driver.host", "localhost")
      .getOrCreateCarbonSession(storeLocation, metastoredb)
    carbon.sparkContext.setLogLevel("ERROR")


//    val path = s"$rootPath/src/main/resources/data.csv"
//    val dicPath = s"$rootPath/src/main/resources/data.dictionary"
    carbon.sql("DROP TABLE IF EXISTS carbontable")

//    carbon.sql(
//      s"""
//         | CREATE TABLE carbontable(
//         | shortField SHORT,
//         | intField INT,
//         | bigintField LONG,
//         | doubleField DOUBLE,
//         | stringField STRING,
//         | timestampField TIMESTAMP,
//         | decimalField DECIMAL(18,2),
//         | dateField DATE,
//         | charField CHAR(5),
//         | floatField FLOAT,
//         | complexData ARRAY<STRING>
//         | )
//         | STORED BY 'carbondata'
//         | TBLPROPERTIES('DICTIONARY_INCLUDE'='dateField, charField')
//       """.stripMargin)
    carbon.sql("CREATE TABLE carbontable (empno int, empname String, designation String, doj Timestamp, workgroupcategory int, workgroupcategoryname String, deptno int, deptname String, projectcode int, projectjoindate Timestamp, projectenddate Timestamp,attendance int,utilization int,salary int) STORED BY 'org.apache.carbondata.format' tblproperties('sort_columns'='empname', 'dictionary_include'='empname')")


    carbon.sql(s"""
         |LOAD DATA LOCAL INPATH '/david/xubo/CarbonDataLearning/src/main/resources/data2.csv'
         |INTO table carbontable options('DELIMITER'=',',
         |'QUOTECHAR'='\"','COMMENTCHAR'='#',
         |'FILEHEADER'='empno,empname,designation,doj,workgroupcategory,workgroupcategoryname,deptno,deptname,projectcode, projectjoindate,projectenddate,attendance,utilization,salary',
         | 'MULTILINE'='true','ESCAPECHAR'='\\','COMPLEX_DELIMITER_LEVEL_1'='$$',
         | 'COMPLEX_DELIMITER_LEVEL_2'=':','ALL_DICTIONARY_PATH'='/david/xubo/CarbonDataLearning/src/main/resources/data.dictionary','SINGLE_PASS'='TRUE')""".stripMargin)
    carbon.sql("SELECT * FROM carbontable").show()


    //    sc.stop()
    carbon.stop()

  }

}
