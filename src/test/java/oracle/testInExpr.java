package oracle;
/*
 * Date: 11-7-10
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testInExpr extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "select * from dual\n" +
                "where AS_OF_DATE IN Last_Day(Add_Months(('1'),-1))";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select  = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expression = select.getWhereClause().getCondition();
        assertTrue(expression.getExpressionType() == EExpressionType.in_t);
        TExpression func_expr = expression.getRightOperand();
        assertTrue(func_expr.getFunctionCall().toString().equalsIgnoreCase("Last_Day(Add_Months(('1'),-1))"));

    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "select * from dual where (dummy,dummy) in (:b3,:b2);";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select  = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expression = select.getWhereClause().getCondition();
        assertTrue(expression.getExpressionType() == EExpressionType.in_t);
        TExpression rexpr = expression.getRightOperand();
       assertTrue(rexpr.getExpressionType() == EExpressionType.list_t);
        TExpression e0 = rexpr.getExprList().getExpression(0);
        assertTrue(e0.getExpressionType() == EExpressionType.simple_object_name_t);

        TExpression e1 = rexpr.getExprList().getExpression(1);
        assertTrue(e1.getExpressionType() == EExpressionType.simple_object_name_t);
        assertTrue(e1.getObjectOperand().toString().equalsIgnoreCase(":b2"));
    }

}
