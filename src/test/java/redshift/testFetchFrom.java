package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.redshift.TFetchFromStmt;
import junit.framework.TestCase;


public class testFetchFrom extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "fetch forward 5 from lollapalooza;";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstFetchFrom);
        TFetchFromStmt fetchFromStmt = (TFetchFromStmt) sqlparser.sqlstatements.get(0);
        assertTrue(fetchFromStmt.getCursorName().toString().equalsIgnoreCase("lollapalooza"));
    }
}