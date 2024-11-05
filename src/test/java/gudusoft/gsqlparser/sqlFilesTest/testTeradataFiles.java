package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testTeradataFiles extends TestCase {
    public  void testTeradata(){
        String db = "teradata";
        parseTest.parsefiles(EDbVendor.dbvteradata, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS + db+"/verified");
        parseTest.parsefiles(EDbVendor.dbvteradata, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA + db);
        parseTest.parsefiles(EDbVendor.dbvteradata, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA + db);
        parseTest.parsefiles(EDbVendor.dbvteradata, gspCommon.BASE_SQL_DIR_PRIVATE_ALLVERSIONS + db);
    }

}
