package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testDB2Files extends TestCase {
    public  void testDB2(){

        parseTest.parsefiles(EDbVendor.dbvdb2, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS + "db2");
        parseTest.parsefiles(EDbVendor.dbvdb2, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA + "db2");
        parseTest.parsefiles(EDbVendor.dbvdb2, gspCommon.BASE_SQL_DIR_PRIVATE_ALLVERSIONS + "db2");
        parseTest.parsefiles(EDbVendor.dbvdb2, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA + "db2");
    }
}
