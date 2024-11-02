package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testRedshiftFiles extends TestCase {
    public  void testRedshift(){
        parseTest.parsefiles(EDbVendor.dbvredshift, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS + "redshift");
        parseTest.parsefiles(EDbVendor.dbvredshift, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA + "redshift");
        parseTest.parsefiles(EDbVendor.dbvredshift, gspCommon.BASE_SQL_DIR_PRIVATE_ALLVERSIONS + "redshift");
        parseTest.parsefiles(EDbVendor.dbvredshift, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA + "redshift");
    }
}
