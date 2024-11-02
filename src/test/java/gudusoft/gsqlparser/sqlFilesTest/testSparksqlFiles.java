package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testSparksqlFiles extends TestCase {
    public  void testSparksql(){
        parseTest.parsefiles(EDbVendor.dbvsparksql, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS + "sparksql");
        parseTest.parsefiles(EDbVendor.dbvsparksql, gspCommon.BASE_SQL_DIR_PRIVATE_ALLVERSIONS + "sparksql");
        parseTest.parsefiles(EDbVendor.dbvsparksql, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA + "sparksql");
    }
}
