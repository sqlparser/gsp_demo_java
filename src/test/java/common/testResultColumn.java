package common;
/*
 * Date: 11-4-19
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testResultColumn extends TestCase {

    public void testLinkToTable(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "select a.f1+b.f2*10+5 as a,b.f3 as b from tab1 a,tab2 b where a.id=b.pid";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select  = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn rc0 =select.getResultColumnList().getResultColumn(0);

        //System.out.println(rc0.getAliasClause().toString());
       // System.out.println(rc0.getExpr().toString());
    }

}
