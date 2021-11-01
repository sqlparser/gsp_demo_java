package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testMDXFiles extends TestCase {

    public  void testMdx(){
        parsefiles(EDbVendor.dbvmdx,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"mdx");
        parsefiles(EDbVendor.dbvmdx,common.gspCommon.BASE_SQL_DIR_PRIVATE +"java/mdx/passed");
    }

}

