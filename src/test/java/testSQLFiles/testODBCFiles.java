package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testODBCFiles extends TestCase {
    public  void testODBC(){
        parsefiles(EDbVendor.dbvodbc,common.gspCommon.BASE_SQL_DIR+"odbc");
    }

}
