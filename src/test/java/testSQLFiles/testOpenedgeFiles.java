package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testOpenedgeFiles extends TestCase {
    public  void testOpenedge(){
        parsefiles(EDbVendor.dbvopenedge,common.gspCommon.BASE_SQL_DIR+"openedge");
        parsefiles(EDbVendor.dbvopenedge,common.gspCommon.BASE_SQL_DIR+"java/openedge");
    }

}