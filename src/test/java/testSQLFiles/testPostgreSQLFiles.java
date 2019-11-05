package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testPostgreSQLFiles extends TestCase {

    public  void testPostgresql(){
        parseTest.parsefiles(EDbVendor.dbvpostgresql,test.gspCommon.BASE_SQL_DIR+"postgresql/verified");
        parseTest.parsefiles(EDbVendor.dbvpostgresql,test.gspCommon.BASE_SQL_DIR+"java/postgresql");
    }

}
