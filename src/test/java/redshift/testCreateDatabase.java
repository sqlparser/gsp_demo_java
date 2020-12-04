package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateDatabaseSqlStatement;
import junit.framework.TestCase;


public class testCreateDatabase extends TestCase {

    public void testTable() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "create database tickit_test\n" +
                "with owner dwuser;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatedatabase);
        TCreateDatabaseSqlStatement database = (TCreateDatabaseSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(database.getDatabaseName().toString().equalsIgnoreCase("tickit_test"));
        assertTrue(database.getDbOwner().toString().equalsIgnoreCase("dwuser"));
    }
}