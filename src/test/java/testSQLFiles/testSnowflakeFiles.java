package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testSnowflakeFiles extends TestCase {
    public  void testSnowflake(){
        parseTest.parsefiles(EDbVendor.dbvsnowflake,test.gspCommon.BASE_SQL_DIR+"snowflake");
    }
}
