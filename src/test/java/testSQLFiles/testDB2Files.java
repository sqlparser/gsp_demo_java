package testSQLFiles;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testDB2Files extends TestCase {
    public  void testDB2(){

        parsefiles(EDbVendor.dbvdb2,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"db2");
        parsefiles(EDbVendor.dbvdb2, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"db2");
        parsefiles(EDbVendor.dbvdb2, gspCommon.BASE_SQL_DIR_PRIVATE_ALLVERSIONS +"db2");
        parsefiles(EDbVendor.dbvdb2, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA +"db2");
    }
}
