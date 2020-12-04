package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testHiveFiles extends TestCase {
    public  void testHive(){
        parsefiles(EDbVendor.dbvhive,common.gspCommon.BASE_SQL_DIR+"hive");
        parsefiles(EDbVendor.dbvhive,common.gspCommon.BASE_SQL_DIR+"java/hive/");
    }
}

