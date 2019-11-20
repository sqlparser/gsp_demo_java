package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testInformixFiles extends TestCase {
    public  void testInformix(){
        parsefiles(EDbVendor.dbvinformix,test.gspCommon.BASE_SQL_DIR+"informix");
    }
}
