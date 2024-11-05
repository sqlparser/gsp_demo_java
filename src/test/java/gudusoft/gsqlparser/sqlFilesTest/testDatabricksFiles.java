package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testDatabricksFiles  extends TestCase {
    public  void testDatabricksSql(){
        parseTest.parsefiles(EDbVendor.dbvdatabricks, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA + "databricks");
        parseTest.parsefiles(EDbVendor.dbvdatabricks, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA + "databricks");
    }
}
