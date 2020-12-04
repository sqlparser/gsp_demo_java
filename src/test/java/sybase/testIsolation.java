package sybase;
/*
 * Date: 13-8-29
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EIsolationLevel;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TIsolationClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testIsolation extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsybase);
        sqlparser.sqltext = "Select abc from tablea\n" +
                "at isolation 0";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TIsolationClause isolationClause = select.getIsolationClause();
        assertTrue(isolationClause.getIsolationLevel() == EIsolationLevel.readUncommitted);
    }
}
