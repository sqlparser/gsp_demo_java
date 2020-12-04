package common;
/*
 * Date: 12-5-23
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testSubquery extends TestCase {

    public void test0(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "SELECT * FROM tab WHERE NOT EXISTS (SELECT 'x' FROM tab2)";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr =  selectSqlStatement.getWhereClause().getCondition();
        assertTrue(expr.getExpressionType() == EExpressionType.logical_not_t);
        TExpression expr1 = expr.getRightOperand();
        assertTrue(expr1.getExpressionType() == EExpressionType.exists_t);
        assertTrue(expr1.getSubQuery().toString().equalsIgnoreCase("(SELECT 'x' FROM tab2)"));
    }

}
