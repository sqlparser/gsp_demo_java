package testSQLFiles;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testVerticaFiles extends TestCase {
    public  void testVertica(){
        parsefiles(EDbVendor.dbvvertica,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"vertica");
        parsefiles(EDbVendor.dbvvertica, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA +"vertica");
        parsefiles(EDbVendor.dbvvertica, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"vertica");
    }
}