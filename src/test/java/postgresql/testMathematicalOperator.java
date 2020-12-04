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

public class testMathematicalOperator extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "select 2.0 ^ 3.0";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expression0 = column0.getExpr();
        assertTrue(expression0.getExpressionType() == EExpressionType.exponentiate_t);
       // System.out.println(expression0.getExpressionType());
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "select |/ 25.0";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expression0 = column0.getExpr();
        assertTrue(expression0.getExpressionType() == EExpressionType.unary_squareroot_t);

        //System.out.println(expression0.toString());
    }

    public void test3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "select 5 !";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expression0 = column0.getExpr();
        assertTrue(expression0.getExpressionType() == EExpressionType.unary_factorial_t);
        assertTrue(expression0.toString().equalsIgnoreCase("5 !"));
        //System.out.println(expression0.toString());
    }

    public void test4(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);

        sqlparser.sqltext = "select ||/ 27.0";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expression0 = column0.getExpr();
        assertTrue(expression0.getExpressionType() == EExpressionType.unary_cuberoot_t);

        //System.out.println(expression0.toString());
    }

    public void test5(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);

        sqlparser.sqltext = "select !! 5";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expression0 = column0.getExpr();
        assertTrue(expression0.getExpressionType() == EExpressionType.unary_factorialprefix_t);

        //System.out.println(expression0.toString());
    }

    public void test6(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);

        sqlparser.sqltext = "select @ -5.0";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expression0 = column0.getExpr();
        assertTrue(expression0.getExpressionType() == EExpressionType.unary_absolutevalue_t);

        //System.out.println(expression0.toString());
    }

    public void test7(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);

        sqlparser.sqltext = "select 91 & 15";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expression0 = column0.getExpr();
        assertTrue(expression0.getExpressionType() == EExpressionType.bitwise_and_t);

        //System.out.println(expression0.toString());
    }

    public void test8(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);

        sqlparser.sqltext = "select 32 | 3";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expression0 = column0.getExpr();
        assertTrue(expression0.getExpressionType() == EExpressionType.bitwise_or_t);

     //   System.out.println(expression0.toString()+" type:"+expression0.getExpressionType());
    }

    public void test9(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);

        sqlparser.sqltext = "select 17 # 5";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expression0 = column0.getExpr();
        assertTrue(expression0.getExpressionType() == EExpressionType.bitwise_xor_t);

     //   System.out.println(expression0.toString()+" type:"+expression0.getExpressionType());
    }

    public void test10(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);

        sqlparser.sqltext = "select ~1";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expression0 = column0.getExpr();
        assertTrue(expression0.getExpressionType() == EExpressionType.unary_bitwise_not_t);

     //   System.out.println(expression0.toString()+" type:"+expression0.getExpressionType());
    }

    public void test11(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);

        sqlparser.sqltext = "select 1 << 4";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expression0 = column0.getExpr();
        assertTrue(expression0.getExpressionType() == EExpressionType.left_shift_t);

     //   System.out.println(expression0.toString()+" type:"+expression0.getExpressionType());
    }

    public void test12(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);

        sqlparser.sqltext = "select 8 >> 2";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expression0 = column0.getExpr();
        assertTrue(expression0.getExpressionType() == EExpressionType.right_shift_t);

     //   System.out.println(expression0.toString()+" type:"+expression0.getExpressionType());
    }
}
