package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testOpenedgeFiles extends TestCase {
    public  void testOpenedge(){
        parsefiles(EDbVendor.dbvopenedge,test.gspCommon.BASE_SQL_DIR+"openedge");
        parsefiles(EDbVendor.dbvopenedge,test.gspCommon.BASE_SQL_DIR+"java/openedge");
    }

}