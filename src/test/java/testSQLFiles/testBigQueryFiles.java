package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testBigQueryFiles  extends TestCase {

    public  void testBigquery(){
        parsefiles(EDbVendor.dbvbigquery,test.gspCommon.BASE_SQL_DIR+"bigquery");
        parsefiles(EDbVendor.dbvbigquery,test.gspCommon.BASE_SQL_DIR+"java/bigquery/");
    }

}

