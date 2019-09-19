package test;


import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.nodes.*;
import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class testExpression extends TestCase {

    public void testFlattenExpr(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "SELECT * from t where a>1 and b=1 or c=2";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = select.getWhereClause().getCondition();
        assertTrue(((TExpression)expr.getFlattedAndOrExprs().get(1)).getAndOrTokenBeforeExpr().toString().equalsIgnoreCase("and"));
        assertTrue(((TExpression)expr.getFlattedAndOrExprs().get(2)).getAndOrTokenBeforeExpr().toString().equalsIgnoreCase("or")); ;

    }

    public void testCollate(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "SELECT latincol COLLATE greek_ci_as";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column = select.getResultColumnList().getResultColumn(0);
        TExpression expr2 = column.getExpr();
        assertTrue(expr2.getRightOperand().toString().equalsIgnoreCase("greek_ci_as"));
    }

    public void testFunctionName(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT COUNT(*)";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column = select.getResultColumnList().getResultColumn(0);
        TExpression expr2 = column.getExpr();
        TFunctionCall func = expr2.getFunctionCall();
    }

    public void testNotEqual(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT COUNT(*)\n" +
                "FROM orders\n" +
                "WHERE A NOT = B;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TWhereClause where = select.getWhereClause();
        TExpression expr = where.getCondition();
        //System.out.println(expr.getComparisonOperator().toString());
        assertTrue(expr.getComparisonOperator().toString().equalsIgnoreCase("NOT ="));

    }

    public void testIntervalExpr(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT (end_time - start_time) DAY(4,1) TO SECOND (2)\n" +
                "FROM BillDateTime;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column = select.getResultColumnList().getResultColumn(0);
        TExpression expr = column.getExpr();
        //System.out.println(expr.getExpressionType());
        assertTrue(expr.getExpressionType() == EExpressionType.interval_t);

        sqlparser.sqltext = "SELECT (end_time - start_time) DAY\n" +
                "FROM BillDateTime;";
        assertTrue(sqlparser.parse() == 0);
        select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        column = select.getResultColumnList().getResultColumn(0);
        expr = column.getExpr();
        //System.out.println(expr.getExpressionType());
        assertTrue(expr.getExpressionType() == EExpressionType.interval_t );

        sqlparser.sqltext = "SELECT (end_time - start_time) DAY1\n" +
                "FROM BillDateTime;";
        assertTrue(sqlparser.parse() == 0);
        select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        column = select.getResultColumnList().getResultColumn(0);
        expr = column.getExpr();
        //System.out.println(expr.getExpressionType());
        assertTrue(expr.getExpressionType() == EExpressionType.parenthesis_t);
    }

    public void testInList(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT COUNT(*)\n" +
                "FROM orders\n" +
                "WHERE A in (1,2,3+4);";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TWhereClause where = select.getWhereClause();
        TExpression expr = where.getCondition();
        assertTrue(expr.getExpressionType() == EExpressionType.in_t);
        assertTrue(expr.getRightOperand().getExpressionType() == EExpressionType.list_t);
        TExpressionList expressionList = expr.getRightOperand().getExprList();
        assertTrue(expressionList.getExpression(0).toString().equalsIgnoreCase("1"));
        assertTrue(expressionList.getExpression(1).toString().equalsIgnoreCase("2"));
        assertTrue(expressionList.getExpression(2).toString().equalsIgnoreCase("3+4"));

    }
}
