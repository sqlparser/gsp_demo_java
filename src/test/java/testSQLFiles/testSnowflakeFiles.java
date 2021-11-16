package testSQLFiles;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testSnowflakeFiles extends TestCase {
    public  void testSnowflake(){
        parseTest.parsefiles(EDbVendor.dbvsnowflake,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"snowflake");
        parseTest.parsefiles(EDbVendor.dbvsnowflake, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"snowflake");
        parsefiles(EDbVendor.dbvsnowflake, gspCommon.BASE_SQL_DIR_PRIVATE_ALLVERSIONS +"snowflake");
        parsefiles(EDbVendor.dbvsnowflake, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA +"snowflake");
    }
}
