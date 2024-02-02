package redshift;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testExpression extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "select ('SMITH' || 'JONES')";
        assertTrue(sqlparser.parse() == 0);
        TResultColumn resultColumn = ((TSelectSqlStatement)sqlparser.sqlstatements.get(0)).getResultColumnList().getResultColumn(0);
        TExpression expression = resultColumn.getExpr();
        assertTrue(expression.getExpressionType() == EExpressionType.parenthesis_t);
        expression = expression.getLeftOperand();
        assertTrue(expression.getExpressionType() == EExpressionType.concatenate_t);
    }

    public void test2() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "select 2.0 ^ 3.0";
        assertTrue(sqlparser.parse() == 0);
        TResultColumn resultColumn = ((TSelectSqlStatement)sqlparser.sqlstatements.get(0)).getResultColumnList().getResultColumn(0);
        TExpression expression = resultColumn.getExpr();
        assertTrue(expression.getExpressionType() == EExpressionType.exponentiate_t);
    }

    public void test3() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "select |/ 25.0;";
        assertTrue(sqlparser.parse() == 0);
        TResultColumn resultColumn = ((TSelectSqlStatement)sqlparser.sqlstatements.get(0)).getResultColumnList().getResultColumn(0);
        TExpression expression = resultColumn.getExpr();
        assertTrue(expression.getExpressionType() == EExpressionType.unary_squareroot_t);
        assertTrue(expression.getRightOperand().toString().equalsIgnoreCase("25.0"));
    }

    public void test4() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "select ||/ 25.0;";
        assertTrue(sqlparser.parse() == 0);
        TResultColumn resultColumn = ((TSelectSqlStatement)sqlparser.sqlstatements.get(0)).getResultColumnList().getResultColumn(0);
        TExpression expression = resultColumn.getExpr();
        assertTrue(expression.getExpressionType() == EExpressionType.unary_cuberoot_t);
        assertTrue(expression.getRightOperand().toString().equalsIgnoreCase("25.0"));
    }

    public void test5() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "select @ -5.0;";
        assertTrue(sqlparser.parse() == 0);
        TResultColumn resultColumn = ((TSelectSqlStatement)sqlparser.sqlstatements.get(0)).getResultColumnList().getResultColumn(0);
        TExpression expression = resultColumn.getExpr();
        assertTrue(expression.getExpressionType() == EExpressionType.unary_absolutevalue_t);
        expression = expression.getRightOperand();
        assertTrue(expression.getExpressionType() == EExpressionType.unary_minus_t);
        //assertTrue(expression.getRightOperand().toString().equalsIgnoreCase("25.0"));
    }

    public void test6() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "select 1 << 4;";
        assertTrue(sqlparser.parse() == 0);
        TResultColumn resultColumn = ((TSelectSqlStatement)sqlparser.sqlstatements.get(0)).getResultColumnList().getResultColumn(0);
        TExpression expression = resultColumn.getExpr();
       // System.out.println(expression.getExpressionType());
        assertTrue(expression.getExpressionType() == EExpressionType.left_shift_t);
    }

    public void test7() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "select 1 >> 4;";
        assertTrue(sqlparser.parse() == 0);
        TResultColumn resultColumn = ((TSelectSqlStatement)sqlparser.sqlstatements.get(0)).getResultColumnList().getResultColumn(0);
        TExpression expression = resultColumn.getExpr();
        // System.out.println(expression.getExpressionType());
        assertTrue(expression.getExpressionType() == EExpressionType.right_shift_t);
    }

    public void test8() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "select 1 & 4;";
        assertTrue(sqlparser.parse() == 0);
        TResultColumn resultColumn = ((TSelectSqlStatement)sqlparser.sqlstatements.get(0)).getResultColumnList().getResultColumn(0);
        TExpression expression = resultColumn.getExpr();
        // System.out.println(expression.getExpressionType());
        assertTrue(expression.getExpressionType() == EExpressionType.bitwise_and_t);
    }

    public void test9() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "select 1 | 4;";
        assertTrue(sqlparser.parse() == 0);
        TResultColumn resultColumn = ((TSelectSqlStatement)sqlparser.sqlstatements.get(0)).getResultColumnList().getResultColumn(0);
        TExpression expression = resultColumn.getExpr();
        // System.out.println(expression.getExpressionType());
        assertTrue(expression.getExpressionType() == EExpressionType.bitwise_or_t);
    }

    public void testBitwiseXor() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "select 1 # 4;";
        assertTrue(sqlparser.parse() == 0);
        TResultColumn resultColumn = ((TSelectSqlStatement)sqlparser.sqlstatements.get(0)).getResultColumnList().getResultColumn(0);
        TExpression expression = resultColumn.getExpr();
        // System.out.println(expression.getExpressionType());
        assertTrue(expression.getExpressionType() == EExpressionType.bitwise_xor_t);
    }

    public void testBitwiseNot() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "select ~ 4;";
        assertTrue(sqlparser.parse() == 0);
        TResultColumn resultColumn = ((TSelectSqlStatement)sqlparser.sqlstatements.get(0)).getResultColumnList().getResultColumn(0);
        TExpression expression = resultColumn.getExpr();
        //  System.out.println(expression.getExpressionType());
        assertTrue(expression.getExpressionType() == EExpressionType.unary_bitwise_not_t);
    }

    public void testExprList() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "select * from venue\n" +
                "where (venuecity, venuestate) in (('Miami', 'FL'), ('Tampa', 'FL'))\n" +
                "order by venueid";
        assertTrue(sqlparser.parse() == 0);
        TExpression expression =((TSelectSqlStatement)sqlparser.sqlstatements.get(0)).getWhereClause().getCondition();
        //  System.out.println(expression.getExpressionType());
        assertTrue(expression.getExpressionType() == EExpressionType.in_t);
        TExpression left = expression.getLeftOperand();
        TExpression right = expression.getRightOperand();
        assertTrue(left.getExpressionType() == EExpressionType.list_t);
        assertTrue(right.getExpressionType() == EExpressionType.list_t);
        assertTrue(right.getExprList().size() == 2);
        TExpression right1 = right.getExprList().getExpression(0);
        assertTrue(right1.getExpressionType() == EExpressionType.list_t);
        assertTrue(right1.getExprList().size() == 2);
        assertTrue(right1.getExprList().getExpression(0).toString().endsWith("'Miami'"));
    }

    public void testPosixOperator() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "select distinct city from users\n" +
                "where city ~ '.*E.*|.*H.*' order by city;";
        //System.out.println(sqlparser.sqltext);
        assertTrue(sqlparser.parse() == 0);
        TExpression expression =((TSelectSqlStatement)sqlparser.sqlstatements.get(0)).getWhereClause().getCondition();
        //  System.out.println(expression.getExpressionType());
        assertTrue(expression.getExpressionType() == EExpressionType.pattern_matching_t);
        TExpression left = expression.getLeftOperand();
        TExpression right = expression.getRightOperand();
        //System.out.println(left.toString());
        //System.out.println(right.toString());
    }

    public void testPosixNotOperator() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "select distinct city from users\n" +
                "where city !~ '.*E.*|.*H.*' order by city;";
        assertTrue(sqlparser.parse() == 0);
        TExpression expression =((TSelectSqlStatement)sqlparser.sqlstatements.get(0)).getWhereClause().getCondition();
        assertTrue(expression.getNotToken().toString().endsWith("!~"));
        assertTrue(expression.getExpressionType() == EExpressionType.pattern_matching_t);
        TExpression left = expression.getLeftOperand();
        TExpression right = expression.getRightOperand();
      //  System.out.println(left.toString());
      //  System.out.println(right.toString());
    }

}