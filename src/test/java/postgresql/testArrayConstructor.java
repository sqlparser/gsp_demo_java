package postgresql;
/*
 * Date: 11-5-24
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testArrayConstructor extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "SELECT ARRAY[1,2,3+4] ";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expression0 = column0.getExpr();
        assertTrue(expression0.getExpressionType() == EExpressionType.array_constructor_t);
        assertTrue(expression0.getExprList().getExpression(0).toString().equalsIgnoreCase("1"));
        assertTrue(expression0.getExprList().getExpression(1).toString().equalsIgnoreCase("2"));
        assertTrue(expression0.getExprList().getExpression(2).toString().equalsIgnoreCase("3+4"));
//        System.out.println(expression0.getExprList().getExpression(0).toString());

        sqlparser.sqltext = "SELECT ARRAY[1,2,22.7]::integer[]";
        assertTrue(sqlparser.parse() == 0);
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "SELECT ARRAY[ARRAY[1,2], ARRAY[3,4]]";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expression0 = column0.getExpr();
        assertTrue(expression0.getExpressionType() == EExpressionType.array_constructor_t);
        TExpression expression0_0 = expression0.getExprList().getExpression(0);
        assertTrue(expression0_0.getExpressionType() == EExpressionType.array_constructor_t);
        TExpression expression0_0_0 = expression0_0.getExprList().getExpression(0);
        assertTrue(expression0_0_0.toString().equalsIgnoreCase("1"));

//        assertTrue(expression0.getExprList().getExpression(1).toString().equalsIgnoreCase("2"));
//        assertTrue(expression0.getExprList().getExpression(2).toString().equalsIgnoreCase("3+4"));
//        System.out.println(expression0.getExprList().getExpression(0).toString());
    }

    public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "SELECT ARRAY[[1,2],[3,4]]";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expression0 = column0.getExpr();
        assertTrue(expression0.getExpressionType() == EExpressionType.array_constructor_t);
        TExpression expression0_0 = expression0.getExprList().getExpression(0);
        assertTrue(expression0_0.getExpressionType() ==EExpressionType.array_constructor_t);
        TExpression expression0_0_1 = expression0_0.getExprList().getExpression(1);
        assertTrue(expression0_0_1.toString().equalsIgnoreCase("2"));

        sqlparser.sqltext = "SELECT ARRAY[]::integer[]";
        assertTrue(sqlparser.parse() == 0);

//        assertTrue(expression0.getExprList().getExpression(1).toString().equalsIgnoreCase("2"));
//        assertTrue(expression0.getExprList().getExpression(2).toString().equalsIgnoreCase("3+4"));
//        System.out.println(expression0.getExprList().getExpression(0).toString());
    }

    public void test4(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "SELECT ARRAY(SELECT oid FROM pg_proc WHERE proname LIKE 'bytea%')";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expression0 = column0.getExpr();
        assertTrue(expression0.getExpressionType() == EExpressionType.array_constructor_t);
        TSelectSqlStatement subquery = expression0.getSubQuery();
        TExpression condition  = subquery.getWhereClause().getCondition();
        assertTrue(condition.toString().equalsIgnoreCase("proname LIKE 'bytea%'"));

    }
}
