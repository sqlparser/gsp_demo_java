package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testHiveFiles extends TestCase {
    public  void testHive(){
        parsefiles(EDbVendor.dbvhive,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"hive");
        parsefiles(EDbVendor.dbvhive,common.gspCommon.BASE_SQL_DIR_PRIVATE +"java/hive/");
    }
}

