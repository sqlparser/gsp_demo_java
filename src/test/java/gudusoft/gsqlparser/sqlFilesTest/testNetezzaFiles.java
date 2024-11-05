package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testNetezzaFiles extends TestCase {


    public  void testNetezza(){
        parseTest.parsefiles(EDbVendor.dbvnetezza, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS + "netezza");
        parseTest.parsefiles(EDbVendor.dbvnetezza, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA + "netezza");
        parseTest.parsefiles(EDbVendor.dbvnetezza, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA + "netezza");
    }


}
