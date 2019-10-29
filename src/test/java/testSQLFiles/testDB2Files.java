package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testDB2Files extends TestCase {
    public  void testDB2(){

        parsefiles(EDbVendor.dbvdb2,test.gspCommon.BASE_SQL_DIR+"db2");
        parsefiles(EDbVendor.dbvdb2,test.gspCommon.BASE_SQL_DIR+"java/db2/");
    }
}
