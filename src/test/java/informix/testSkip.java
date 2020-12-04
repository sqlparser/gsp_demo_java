package informix;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;

import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testSkip extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvinformix);
        sqlparser.sqltext = "SELECT SKIP 2 FIRST 8 col1 FROM tab1 WHERE col1 > 50";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement stmt = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getLimitClause().getOffset().toString().equalsIgnoreCase("2"));
        assertTrue(stmt.getLimitClause().getSelectFetchFirstValue().toString().equalsIgnoreCase("8"));

    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvinformix);
        sqlparser.sqltext = "SELECT SKIP 28 col1 FROM tab1 WHERE col1 > 50";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement stmt = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getLimitClause().getOffset().toString().equalsIgnoreCase("28"));

    }
}
