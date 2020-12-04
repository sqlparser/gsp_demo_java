package common;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDropDatabaseStmt;

import junit.framework.TestCase;

public class testDropDatabase  extends TestCase {

    public void testDropDatabase(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "drop database BaffleTest;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstdropdatabase);
        TDropDatabaseStmt dropDatabaseStmt = (TDropDatabaseStmt) sqlparser.sqlstatements.get(0);
        assertTrue(dropDatabaseStmt.getDatabaseName().toString().equalsIgnoreCase("BaffleTest"));
    }

}
