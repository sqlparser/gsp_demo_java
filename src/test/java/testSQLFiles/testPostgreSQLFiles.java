package testSQLFiles;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testPostgreSQLFiles extends TestCase {

    public  void testPostgresql(){
        parseTest.parsefiles(EDbVendor.dbvpostgresql,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"postgresql/verified");
        parseTest.parsefiles(EDbVendor.dbvpostgresql,common.gspCommon.BASE_SQL_DIR_PRIVATE +"java/postgresql");
    }

}
