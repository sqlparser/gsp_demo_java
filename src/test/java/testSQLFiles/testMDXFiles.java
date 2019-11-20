package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testMDXFiles extends TestCase {

    public  void testMdx(){
        parsefiles(EDbVendor.dbvmdx,test.gspCommon.BASE_SQL_DIR+"mdx");
        parsefiles(EDbVendor.dbvmdx,test.gspCommon.BASE_SQL_DIR+"java/mdx/passed");
    }

}

