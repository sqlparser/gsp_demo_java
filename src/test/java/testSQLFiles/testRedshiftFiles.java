package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testRedshiftFiles extends TestCase {
    public  void testRedshift(){
        parsefiles(EDbVendor.dbvredshift,common.gspCommon.BASE_SQL_DIR+"java/redshift");
    }
}
