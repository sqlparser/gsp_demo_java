package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.commonTest.gspCommon;
import junit.framework.TestCase;

public class testMDXFiles extends TestCase {

    public  void testMdx(){
        parseTest.parsefiles(EDbVendor.dbvmdx, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS + "mdx");
        parseTest.parsefiles(EDbVendor.dbvmdx, gspCommon.BASE_SQL_DIR_PRIVATE +"java/mdx/passed");
    }

}

