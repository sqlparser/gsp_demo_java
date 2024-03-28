package testSQLFiles;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testGaussDBFiles extends TestCase {

    public  void testGaussDB(){
        parsefiles(EDbVendor.dbvgaussdb, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"gaussdb");
        parsefiles(EDbVendor.dbvgaussdb, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA +"gaussdb");
    }

}
