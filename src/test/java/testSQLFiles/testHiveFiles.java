package testSQLFiles;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testHiveFiles extends TestCase {
    public  void testHive(){
        parsefiles(EDbVendor.dbvhive,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"hive");
        parsefiles(EDbVendor.dbvhive, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"hive");
        parsefiles(EDbVendor.dbvhive, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA +"hive");
        parsefiles(EDbVendor.dbvhive, gspCommon.BASE_SQL_DIR_PRIVATE_ALLVERSIONS +"hive");

    }
}

