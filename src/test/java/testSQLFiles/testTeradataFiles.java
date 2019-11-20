package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testTeradataFiles extends TestCase {
    public  void testTeradata(){
        parsefiles(EDbVendor.dbvteradata,test.gspCommon.BASE_SQL_DIR+"teradata/verified");
        parsefiles(EDbVendor.dbvteradata,test.gspCommon.BASE_SQL_DIR+"java/teradata");
    }

}
