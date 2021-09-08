package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TExecuteSqlStatement;
import junit.framework.TestCase;


public class testExecute extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "execute stmtName (1,2);";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstExecute);
        TExecuteSqlStatement exec = (TExecuteSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(exec.getStatementName().toString().equalsIgnoreCase("stmtName"));
        assertTrue(exec.getParameters().size() == 2);
        assertTrue(exec.getParameters().getExpression(0).toString().equalsIgnoreCase("1"));
    }
}