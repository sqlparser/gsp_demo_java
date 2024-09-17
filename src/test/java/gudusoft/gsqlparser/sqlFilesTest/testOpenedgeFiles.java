package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.commonTest.gspCommon;
import junit.framework.TestCase;

public class testOpenedgeFiles extends TestCase {
    public  void testOpenedge(){
        parseTest.parsefiles(EDbVendor.dbvopenedge, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"openedge");
        parseTest.parsefiles(EDbVendor.dbvopenedge, gspCommon.BASE_SQL_DIR_PRIVATE +"java/openedge");
    }

}