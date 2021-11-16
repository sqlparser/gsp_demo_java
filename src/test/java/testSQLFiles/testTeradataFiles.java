package testSQLFiles;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testTeradataFiles extends TestCase {
    public  void testTeradata(){
        parsefiles(EDbVendor.dbvteradata,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"teradata/verified");
        parsefiles(EDbVendor.dbvteradata, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"teradata");
        parsefiles(EDbVendor.dbvteradata, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA +"teradata");
        parsefiles(EDbVendor.dbvteradata, gspCommon.BASE_SQL_DIR_PRIVATE_ALLVERSIONS +"teradata");
    }

}
