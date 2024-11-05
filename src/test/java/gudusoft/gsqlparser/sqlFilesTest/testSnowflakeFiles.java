package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testSnowflakeFiles extends TestCase {
    public  void testSnowflake(){
        parseTest.parsefiles(EDbVendor.dbvsnowflake, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS + "snowflake");
        parseTest.parsefiles(EDbVendor.dbvsnowflake, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA + "snowflake");
        parseTest.parsefiles(EDbVendor.dbvsnowflake, gspCommon.BASE_SQL_DIR_PRIVATE_ALLVERSIONS + "snowflake");
        parseTest.parsefiles(EDbVendor.dbvsnowflake, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA + "snowflake");
    }
}
