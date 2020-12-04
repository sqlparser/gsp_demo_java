package postgresql;
/*
 * Date: 11-5-25
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testRowConstructor extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "SELECT ROW(1,2.5,'this is a test')";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expression0 = column0.getExpr();
        assertTrue(expression0.getExpressionType() == EExpressionType.row_constructor_t);
        assertTrue(expression0.getExprList().getExpression(0).toString().equalsIgnoreCase("1"));
        assertTrue(expression0.getExprList().getExpression(1).toString().equalsIgnoreCase("2.5"));
        assertTrue(expression0.getExprList().getExpression(2).toString().equalsIgnoreCase("'this is a test'"));

        sqlparser.sqltext = "SELECT ROW(t.*, 42) FROM t;";
        assertTrue(sqlparser.parse() == 0);

        sqlparser.sqltext = "SELECT ROW(t.f1, t.f2, 42) FROM t";
        assertTrue(sqlparser.parse() == 0);
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "SELECT ROW(1,2.5,'this is a test') = ROW(1, 3, 'not the same')";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expression0 = column0.getExpr();
        assertTrue(expression0.getExpressionType() == EExpressionType.simple_comparison_t);
        assertTrue(expression0.toString().equalsIgnoreCase("ROW(1,2.5,'this is a test') = ROW(1, 3, 'not the same')"));
        TExpression expression_l = expression0.getLeftOperand();
        assertTrue(expression_l.getExpressionType() == EExpressionType.row_constructor_t);
        assertTrue(expression_l.toString().equalsIgnoreCase("ROW(1,2.5,'this is a test')"));
        TExpression expression_r = expression0.getRightOperand();
        assertTrue(expression_r.getExpressionType() == EExpressionType.row_constructor_t);
        assertTrue(expression_r.toString().equalsIgnoreCase("ROW(1, 3, 'not the same')"));

    }

    public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "SELECT ROW(table.*) IS NULL FROM table";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expression0 = column0.getExpr();
        assertTrue(expression0.getExpressionType() == EExpressionType.null_t);
    }

}
