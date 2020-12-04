package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDropDatabaseStmt;
import junit.framework.TestCase;


public class testDropDatabase extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "drop database tickit_test;";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstdropdatabase);
        TDropDatabaseStmt dropDatabase = (TDropDatabaseStmt) sqlparser.sqlstatements.get(0);
        assertTrue(dropDatabase.getDatabaseName().toString().equalsIgnoreCase("tickit_test"));


    }
}