package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.redshift.TRedshiftLock;
import junit.framework.TestCase;


public class testLock extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "lock event, sales;";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftLock);
        TRedshiftLock lock = (TRedshiftLock) sqlparser.sqlstatements.get(0);
        assertTrue(lock.getTableList().size() == 2);
        assertTrue(lock.getTableList().getObjectName(0).toString().equalsIgnoreCase("event"));

    }
}