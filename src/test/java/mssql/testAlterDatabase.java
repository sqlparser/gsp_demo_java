package test.mssql;
/*
 * Date: 15-4-22
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.stmt.TAlterDatabaseStmt;
import junit.framework.TestCase;

public class testAlterDatabase extends TestCase {

    public void test1(){

      TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
      sqlparser.sqltext = "ALTER DATABASE CURRENT SET COMPATIBILITY_LEVEL = 110";
      int result = sqlparser.parse();
      assertTrue(result==0);
      assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstalterdatabase);
        TAlterDatabaseStmt alterDatabaseStmt = (TAlterDatabaseStmt)sqlparser.sqlstatements.get(0);
        assertTrue(alterDatabaseStmt.getDatabaseName().toString().equalsIgnoreCase("CURRENT"));
      assertTrue(sqlparser.sqlstatements.get(0).toString().equalsIgnoreCase("ALTER DATABASE CURRENT SET COMPATIBILITY_LEVEL = 110"));
    }


}
