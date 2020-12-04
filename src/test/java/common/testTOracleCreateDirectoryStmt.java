package common;
/*
 * Date: 2010-12-31
 * Time: 14:32:23
 */

import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.stmt.oracle.TOracleCreateDirectoryStmt;

public class testTOracleCreateDirectoryStmt extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE OR REPLACE DIRECTORY \"DATA_PUMP_DIR\" AS 'C:\\oracle\\ora10\\rdbms\\log\\';";
        assertTrue(sqlparser.parse() == 0);

        TOracleCreateDirectoryStmt directory = (TOracleCreateDirectoryStmt)sqlparser.sqlstatements.get(0);
        assertTrue(directory.getDirectoryName().toString().equalsIgnoreCase("\"DATA_PUMP_DIR\""));
        assertTrue(directory.getPath().toString().equalsIgnoreCase("'C:\\oracle\\ora10\\rdbms\\log\\'"));
    }
}
