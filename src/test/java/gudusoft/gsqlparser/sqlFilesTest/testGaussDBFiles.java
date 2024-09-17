package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testGaussDBFiles extends TestCase {

    public  void testGaussDB(){
        parseTest.parsefiles(EDbVendor.dbvgaussdb, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA + "gaussdb");
        parseTest.parsefiles(EDbVendor.dbvgaussdb, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA + "gaussdb");
    }

}
