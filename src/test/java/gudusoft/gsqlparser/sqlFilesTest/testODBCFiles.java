package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.commonTest.gspCommon;
import junit.framework.TestCase;

public class testODBCFiles extends TestCase {
    public  void testODBC(){
        parseTest.parsefiles(EDbVendor.dbvodbc, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"odbc");
    }

}
