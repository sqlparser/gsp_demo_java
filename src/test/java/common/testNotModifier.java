package common;
/*
 * Date: 11-12-14
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testNotModifier extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "select * from dual where a not like b";

        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expression = selectSqlStatement.getWhereClause().getCondition();
        //assertTrue(expression.isNotModifier());
        assertTrue(expression.getNotToken() != null);
    }
}
