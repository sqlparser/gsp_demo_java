package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;


public class testCreateView extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "create view myevent as select eventname from event\n" +
                "where eventname = 'LeAnn Rimes';";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreateview);
        TCreateViewSqlStatement createview = (TCreateViewSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(createview.getViewName().toString().equals("myevent"));

        TSelectSqlStatement select = createview.getSubquery();
        assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("event"));
    }
}
