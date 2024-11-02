package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testHiveFiles extends TestCase {
    public  void testHive(){
        parseTest.parsefiles(EDbVendor.dbvhive, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS + "hive");
        parseTest.parsefiles(EDbVendor.dbvhive, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA + "hive");
        parseTest.parsefiles(EDbVendor.dbvhive, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA + "hive");
        parseTest.parsefiles(EDbVendor.dbvhive, gspCommon.BASE_SQL_DIR_PRIVATE_ALLVERSIONS + "hive");

    }
}

