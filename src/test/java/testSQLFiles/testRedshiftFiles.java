package testSQLFiles;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testRedshiftFiles extends TestCase {
    public  void testRedshift(){
        parsefiles(EDbVendor.dbvredshift,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"redshift");
        parsefiles(EDbVendor.dbvredshift, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"redshift");
        parsefiles(EDbVendor.dbvredshift, gspCommon.BASE_SQL_DIR_PRIVATE_ALLVERSIONS +"redshift");
        parsefiles(EDbVendor.dbvredshift, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA +"redshift");
    }
}
