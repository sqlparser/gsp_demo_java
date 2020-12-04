package mysql;
/*
 * Date: 13-11-14
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testLeftRightShift extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "SELECT 12 << 3,12 >> 3 FROM whatever";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expression = selectSqlStatement.getResultColumnList().getResultColumn(0).getExpr();
        assertTrue(expression.getExpressionType() == EExpressionType.left_shift_t);
        assertTrue(expression.getLeftOperand().toString().equalsIgnoreCase("12"));
        assertTrue(expression.getRightOperand().toString().equalsIgnoreCase("3"));

        expression = selectSqlStatement.getResultColumnList().getResultColumn(1).getExpr();
        assertTrue(expression.getExpressionType() == EExpressionType.right_shift_t);
        assertTrue(expression.getLeftOperand().toString().equalsIgnoreCase("12"));
        assertTrue(expression.getRightOperand().toString().equalsIgnoreCase("3"));
    }

}
