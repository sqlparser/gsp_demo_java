package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testVerticaFiles extends TestCase {
    public  void testVertica(){
        String db = "vertica";
        parseTest.parsefiles(EDbVendor.dbvvertica, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS + db);
        parseTest.parsefiles(EDbVendor.dbvvertica, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA + db);
        parseTest.parsefiles(EDbVendor.dbvvertica, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA + db);
    }
}