package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static gudusoft.gsqlparser.sqlFilesTest.parseTest.parsefiles;

public class testSQLServerFiles extends TestCase {
    public  void testSQLServer(){
        String db = "mssql";
        parsefiles(EDbVendor.dbvmssql, gspCommon.BASE_SQL_DIR_PRIVATE_ALLVERSIONS + db);
        parsefiles(EDbVendor.dbvmssql, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS + db);
        parsefiles(EDbVendor.dbvmssql, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA + db);
        parsefiles(EDbVendor.dbvmssql, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA + db);
    }
}
