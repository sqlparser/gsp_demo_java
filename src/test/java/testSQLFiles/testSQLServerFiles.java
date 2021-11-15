package testSQLFiles;

import common.gspCommon;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

import static testSQLFiles.parseTest.parsefiles;

public class testSQLServerFiles extends TestCase {
    public  void testSQLServer(){
        parsefiles(EDbVendor.dbvmssql, gspCommon.BASE_SQL_DIR_PRIVATE_ALLVERSIONS +"mssql");
        parsefiles(EDbVendor.dbvmssql,common.gspCommon.BASE_SQL_DIR_PUBLIC_ALLVERSIONS +"mssql");
        parsefiles(EDbVendor.dbvmssql, gspCommon.BASE_SQL_DIR_PUBLIC_JAVA +"mssql");
        parsefiles(EDbVendor.dbvmssql, gspCommon.BASE_SQL_DIR_PRIVATE_JAVA +"mssql");
    }
}
