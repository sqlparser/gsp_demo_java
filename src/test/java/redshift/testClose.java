package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCloseStmt;
import gudusoft.gsqlparser.stmt.redshift.TRedshiftClose;
import junit.framework.TestCase;

public class testClose extends TestCase {
        public void test1() {
            TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
            sqlparser.sqltext = "close movie_cursor;";
            assertTrue(sqlparser.parse() == 0);
            assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sst_closestmt);
            TCloseStmt close = (TCloseStmt) sqlparser.sqlstatements.get(0);
            assertTrue(close.getCursorName().toString().equalsIgnoreCase("movie_cursor"));
        }
}
