package gudusoft.gsqlparser.sqlFilesTest;

import gudusoft.gsqlparser.commonTest.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testBigQueryFiles  extends TestCase {

    public  void testBigquery(){
        parseTest.parsefiles(EDbVendor.dbvbigquery, gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS + "bigquery");
        parseTest.parsefiles(EDbVendor.dbvbigquery, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA + "bigquery");
        parseTest.parsefiles(EDbVendor.dbvbigquery, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA + "bigquery");
    }

}

