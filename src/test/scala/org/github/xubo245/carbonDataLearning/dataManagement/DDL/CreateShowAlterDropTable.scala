package org.github.xubo245.carbonDataLearning.dataManagement.DDL

import java.io.File

import org.apache.carbondata.core.constants.CarbonCommonConstants
import org.apache.carbondata.core.util.CarbonProperties
import org.apache.spark.sql.SparkSession

/**
  * Created by root on 8/11/17.
  */
object CreateShowAlterDropTable {
  def main(args: Array[String]): Unit = {
    val rootPath = new File(this.getClass.getResource("/").getPath
      + "../../").getCanonicalPath
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
    println(storeLocation)
    println(metastoredb)
    import carbon._
    val path = s"$rootPath/src/main/resources/sample.csv"

    sql("DROP TABLE IF EXISTS test_table")
    sql("CREATE TABLE IF NOT EXISTS test_table(id string,name string,city string,age Int) STORED BY 'carbondata' ")
    carbon.sql(s"""LOAD DATA INPATH '$path' INTO TABLE test_table""")
    carbon.sql("SELECT * FROM test_table").show()
    //    sc.stop()

    sql("CREATE TABLE IF NOT EXISTS productSalesTable ( productNumber Int," +
      "ProductName String,storeCity String,storeProvince String,productCategory String," +
      " productBatch String, saleQuantity Int,revenue Int) STORED BY 'carbondata'" +
      " TBLPROPERTIES ('DICTIONARY_EXCLUDE'='storeCity', 'DICTIONARY_INCLUDE'='productNumber'," +
      " 'NO_INVERTED_INDEX'='productBatch','SORT_COLUMNS'='productName,storeCity')")
    sql("select * from productSalesTable  ").show()
    sql("SHOW SEGMENTS FOR TABLE productSalesTable").show()

    sql("show tables ").show()

    sql("DROP TABLE IF EXISTS carbondata")
    sql(" ALTER TABLE productSalesTable RENAME TO carbondata")
    sql("show tables ").show()

    sql("ALTER TABLE carbondata ADD COLUMNS (a1 INT, b1 STRING)").show()
    sql("select * from carbondata  ").show()


    sql("ALTER TABLE carbondata ADD COLUMNS (c1 INT, d1 STRING) TBLPROPERTIES('DICTIONARY_INCLUDE'='c1')")
    sql("select * from carbondata  ").show()

    sql("ALTER TABLE carbondata ADD COLUMNS (e1 INT, f1 STRING) TBLPROPERTIES('DEFAULT.VALUE.e1'='10')")
    sql("select * from carbondata  ").show()

    sql("ALTER TABLE carbondata DROP COLUMNS (b1)")
    sql("select * from carbondata  ").show()
    sql("ALTER TABLE carbondata DROP COLUMNS (c1,d1)")
    sql("select * from carbondata  ").show()


    sql("ALTER TABLE carbondata CHANGE a1 a1 BIGINT")
    sql("select * from carbondata  ").show()

    //    sql("ALTER TABLE carbondata CHANGE e1 e1 Decimal(10,2)")
    sql("ALTER TABLE carbondata ADD COLUMNS (g1 Decimal(10,2), h1 Decimal(10,2))").show()
    sql("ALTER TABLE carbondata CHANGE g1 g1 DECIMAL(18,2)")
    sql("select * from carbondata  ").show()

    sql("DROP TABLE IF EXISTS carbondata")
    sql("show tables ").show()

    carbon.stop()

  }

}
