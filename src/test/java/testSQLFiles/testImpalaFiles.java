package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testImpalaFiles extends TestCase {
    public  void testImpala(){
        parsefiles(EDbVendor.dbvimpala,test.gspCommon.BASE_SQL_DIR+"impala");
        parsefiles(EDbVendor.dbvimpala,test.gspCommon.BASE_SQL_DIR+"java/impala");
    }
}
