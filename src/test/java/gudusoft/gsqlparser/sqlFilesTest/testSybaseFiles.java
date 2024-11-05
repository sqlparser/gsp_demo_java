package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testSybaseFiles extends TestCase {
    public  void testSybase(){
        String  db = "sybase";
        parseTest.parsefiles(EDbVendor.dbvsybase, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS + db);
        parseTest.parsefiles(EDbVendor.dbvsybase, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA + db);
        parseTest.parsefiles(EDbVendor.dbvsybase, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA + db);
    }
}
