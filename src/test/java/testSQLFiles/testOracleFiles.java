package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testOracleFiles extends TestCase {

    public  void testOracle(){
        parseTest.parsefiles(EDbVendor.dbvoracle,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"oracle");
        parseTest.parsefiles(EDbVendor.dbvoracle,common.gspCommon.BASE_SQL_DIR_PRIVATE +"java/oracle/");
    }
}
