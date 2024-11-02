package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testHanaFiles extends TestCase {
    public  void testHana(){
        parseTest.parsefiles(EDbVendor.dbvhana, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS + "hana");
        parseTest.parsefiles(EDbVendor.dbvhana, gspCommon.BASE_SQL_DIR_PRIVATE +"java/hana/");
        parseTest.parsefiles(EDbVendor.dbvhana, gspCommon.BASE_SQL_DIR_PUBLIC +"java/hana/");
    }

}
