package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testInformixFiles extends TestCase {
    public  void testInformix(){
        parseTest.parsefiles(EDbVendor.dbvinformix, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS + "informix");
        parseTest.parsefiles(EDbVendor.dbvinformix, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA + "informix");
    }
}
