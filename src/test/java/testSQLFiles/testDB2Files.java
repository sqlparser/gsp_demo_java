package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testDB2Files extends TestCase {
    public  void testDB2(){

        parsefiles(EDbVendor.dbvdb2,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"db2");
        parsefiles(EDbVendor.dbvdb2,common.gspCommon.BASE_SQL_DIR_PRIVATE +"java/db2/");
    }
}
