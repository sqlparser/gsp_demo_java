package testSQLFiles;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testDatabricksFiles  extends TestCase {
    public  void testDatabricksSql(){
        parsefiles(EDbVendor.dbvdatabricks, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"databricks");
        parsefiles(EDbVendor.dbvdatabricks, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA +"databricks");
    }
}
