package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TAlterDatabaseStmt;
import junit.framework.TestCase;


public class testAlterDatabase extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "alter database tickit rename to newtickit";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstalterdatabase);
        TAlterDatabaseStmt db = (TAlterDatabaseStmt)sqlparser.sqlstatements.get(0);
        assertTrue(db.getDatabaseName().toString().equals("tickit"));
        assertTrue(db.getNewDatabaseName().toString().endsWith("newtickit"));
    }

    public void test2() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "alter database tickit owner to dwuser;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstalterdatabase);
        TAlterDatabaseStmt db = (TAlterDatabaseStmt)sqlparser.sqlstatements.get(0);
        assertTrue(db.getDatabaseName().toString().equals("tickit"));
        assertTrue(db.getOwnerName().toString().endsWith("dwuser"));
    }
}