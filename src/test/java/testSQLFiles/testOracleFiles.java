package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testOracleFiles extends TestCase {

    public  void testOracle(){
        parseTest.parsefiles(EDbVendor.dbvoracle,test.gspCommon.BASE_SQL_DIR+"oracle");
        parseTest.parsefiles(EDbVendor.dbvoracle,test.gspCommon.BASE_SQL_DIR+"java/oracle/");
    }
}
