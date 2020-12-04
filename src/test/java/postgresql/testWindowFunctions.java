package postgresql;
/*
 * Date: 11-5-24
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TOrderBy;
import gudusoft.gsqlparser.nodes.TPartitionClause;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TWindowDef;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testWindowFunctions extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "SELECT depname, empno, salary, avg(salary) OVER (PARTITION BY depname) FROM empsalary";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column3 = select.getResultColumnList().getResultColumn(3);
        TExpression expression3 = column3.getExpr();
        TFunctionCall functionCall3 = expression3.getFunctionCall();
        assertTrue(functionCall3.getFunctionName().toString().equalsIgnoreCase("avg"));
        TWindowDef windowDef3 = functionCall3.getWindowDef();
        TPartitionClause partitionClause3 = windowDef3.getPartitionClause();
        assertTrue(partitionClause3.getExpressionList().getExpression(0).toString().equalsIgnoreCase("depname"));

       // System.out.println(partitionClause.getExpressionList().getExpression(0).toString());
       // System.out.println(functionCall3.getFunctionName().toString());

    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "SELECT empno, salary, rank() OVER (PARTITION BY depname1 ORDER BY salary DESC) FROM empsalary";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column2 = select.getResultColumnList().getResultColumn(2);
        TExpression expression2 = column2.getExpr();
        TFunctionCall functionCall2 = expression2.getFunctionCall();
        assertTrue(functionCall2.getFunctionName().toString().equalsIgnoreCase("rank"));
        TWindowDef windowDef2 = functionCall2.getWindowDef();
        TPartitionClause partitionClause2 = windowDef2.getPartitionClause();
        assertTrue(partitionClause2.getExpressionList().getExpression(0).toString().equalsIgnoreCase("depname1"));

        TOrderBy orderBy2 = windowDef2.getOrderBy( );
        assertTrue(orderBy2.getItems().getOrderByItem(0).getSortKey().toString().equalsIgnoreCase("salary"));
    }

    public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "SELECT salary, sum(salary) OVER () FROM empsalary";
        assertTrue(sqlparser.parse() == 0);

        sqlparser.sqltext = "SELECT empno,salary, sum(salary) OVER (ORDER BY salary1) FROM empsalary";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column2 = select.getResultColumnList().getResultColumn(2);
        TExpression expression2 = column2.getExpr();
        TFunctionCall functionCall2 = expression2.getFunctionCall();
        assertTrue(functionCall2.getFunctionName().toString().equalsIgnoreCase("sum"));
        TWindowDef windowDef2 = functionCall2.getWindowDef();

        TOrderBy orderBy2 = windowDef2.getOrderBy( );
        assertTrue(orderBy2.getItems().getOrderByItem(0).getSortKey().toString().equalsIgnoreCase("salary1"));
    }

    public void test4(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "SELECT sum(salary) OVER w, avg(salary) OVER w\n" +
                "  FROM empsalary\n" +
                "  WINDOW w AS (PARTITION BY depname ORDER BY salary DESC)";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expression0 = column0.getExpr();
        TFunctionCall functionCall0 = expression0.getFunctionCall();
        assertTrue(functionCall0.getFunctionName().toString().equalsIgnoreCase("sum"));
        TWindowDef windowDef2 = functionCall0.getWindowDef();
        assertTrue(windowDef2.getName().toString().equalsIgnoreCase("w"));
        //System.out.println(windowDef2.getName().toString());

    }

}
