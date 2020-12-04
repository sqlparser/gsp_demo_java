package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.redshift.TRedshiftCancel;
import junit.framework.TestCase;

public class testCancel extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "cancel 802;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftCancel);
        TRedshiftCancel cancel = (TRedshiftCancel) sqlparser.sqlstatements.get(0);
        assertTrue(cancel.getProcessId().equalsIgnoreCase("802"));
    }

    public void test2() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "cancel 802 'Long-running query';";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftCancel);
        TRedshiftCancel cancel = (TRedshiftCancel) sqlparser.sqlstatements.get(0);
        assertTrue(cancel.getProcessId().equalsIgnoreCase("802"));
        assertTrue(cancel.getMessage().equalsIgnoreCase("'Long-running query'"));
    }

}