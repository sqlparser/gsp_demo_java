package testSQLFiles;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testInformixFiles extends TestCase {
    public  void testInformix(){
        parsefiles(EDbVendor.dbvinformix,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"informix");
        parsefiles(EDbVendor.dbvinformix, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"informix");
    }
}
