package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDropViewSqlStatement;
import junit.framework.TestCase;


public class testDropView extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "drop view eventview cascade;";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstdropview);
        TDropViewSqlStatement dropView = (TDropViewSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(dropView.getViewNameList().size() == 1);
        assertTrue(dropView.getViewNameList().getObjectName(0).toString().equalsIgnoreCase("eventview"));
    }
}