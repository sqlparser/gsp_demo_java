package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testGreenplumFiles extends TestCase {
    public  void testGreenplum(){
        parseTest.parsefiles(EDbVendor.dbvgreenplum, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS + "greenplum");
        parseTest.parsefiles(EDbVendor.dbvgreenplum, gspCommon.BASE_SQL_DIR_PRIVATE_ALLVERSIONS + "greenplum");
        parseTest.parsefiles(EDbVendor.dbvgreenplum, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA + "greenplum");
        parseTest.parsefiles(EDbVendor.dbvgreenplum, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA + "greenplum");
    }
}
