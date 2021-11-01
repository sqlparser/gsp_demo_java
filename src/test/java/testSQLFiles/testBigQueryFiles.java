package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testBigQueryFiles  extends TestCase {

    public  void testBigquery(){
        parsefiles(EDbVendor.dbvbigquery,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"bigquery");
        parsefiles(EDbVendor.dbvbigquery,common.gspCommon.BASE_SQL_DIR_PRIVATE +"java/bigquery/");
    }

}

