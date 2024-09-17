package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testOracleFiles extends TestCase {

    public  void testOracle(){
        parseTest.parsefiles(EDbVendor.dbvoracle, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"oracle");
        parseTest.parsefiles(EDbVendor.dbvoracle, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"oracle");
        parseTest.parsefiles(EDbVendor.dbvoracle, gspCommon.BASE_SQL_DIR_PRIVATE_ALLVERSIONS +"oracle");
        parseTest.parsefiles(EDbVendor.dbvoracle, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA +"oracle");
    }
}
