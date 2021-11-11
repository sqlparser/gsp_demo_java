package testSQLFiles;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testGreenplumFiles extends TestCase {
    public  void testGreenplum(){
        parsefiles(EDbVendor.dbvgreenplum,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"greenplum");
        parsefiles(EDbVendor.dbvgreenplum, gspCommon.BASE_SQL_DIR_PRIVATE_ALLVERSIONS +"greenplum");
        parsefiles(EDbVendor.dbvgreenplum, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"greenplum");
        parsefiles(EDbVendor.dbvgreenplum, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA +"greenplum");
    }
}
