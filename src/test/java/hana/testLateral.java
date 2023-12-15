package hana;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testLateral extends TestCase {
    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhana);
        sqlparser.sqltext = "SELECT TA.a1, TB.b1 FROM TA, LATERAL (SELECT b1, b2 FROM TB WHERE b3 = TA.a3) TB WHERE TA.a2 = TB.b2;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(selectSqlStatement.getSyntaxHints().size() == 0);
    }
}
