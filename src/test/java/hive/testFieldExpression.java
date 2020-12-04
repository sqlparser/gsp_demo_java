package hive;
/*
 * Date: 13-8-12
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testFieldExpression extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "SELECT v['code'], COUNT(1) FROM www_access GROUP BY v['code'];";
          assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = select.getResultColumnList().getResultColumn(0).getExpr();
        assertTrue(expr.getExpressionType() == EExpressionType.array_access_expr_t);
        assertTrue(expr.toString().equalsIgnoreCase("v['code']"));
        assertTrue(expr.getLeftOperand().getExpressionType() == EExpressionType.simple_object_name_t);
        assertTrue(expr.getLeftOperand().toString().equalsIgnoreCase("v"));
        assertTrue(expr.getRightOperand().getExpressionType() == EExpressionType.simple_constant_t);
        assertTrue(expr.getRightOperand().toString().equalsIgnoreCase("'code'"));


    }

}
