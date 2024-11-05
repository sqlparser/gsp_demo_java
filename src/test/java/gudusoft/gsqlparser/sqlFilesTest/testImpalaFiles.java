package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testImpalaFiles extends TestCase {
    public  void testImpala(){
        parseTest.parsefiles(EDbVendor.dbvimpala, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS + "impala");
        parseTest.parsefiles(EDbVendor.dbvimpala, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA + "impala");
        parseTest.parsefiles(EDbVendor.dbvimpala, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA + "impala");
    }
}
