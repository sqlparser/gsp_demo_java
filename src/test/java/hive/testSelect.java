package hive;
/*
 * Date: 13-8-9
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.nodes.TSortBy;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testSelect extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext ="SELECT page_views.*\n" +
                "FROM page_views\n" +
                "WHERE page_views.date >= '2008-03-01' AND page_views.date <= '2008-03-31'";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(select.getResultColumnList().size() == 1);
        TResultColumn resultColumn = select.getResultColumnList().getResultColumn(0);
        TExpression expr = resultColumn.getExpr();
        assertTrue(expr.getExpressionType() == EExpressionType.simple_object_name_t);
        TObjectName objectName  = expr.getObjectOperand();
        assertTrue(objectName.toString().equalsIgnoreCase("page_views.*"));

        assertTrue(select.joins.size() == 1);
        TJoin join = select.joins.getJoin(0);
        assertTrue(join.getKind() == TBaseType.join_source_fake);
        assertTrue(join.getTable().toString().equalsIgnoreCase("page_views"));

        TWhereClause whereClause = select.getWhereClause();
        TExpression condition = whereClause.getCondition();
        assertTrue(condition.getExpressionType() == EExpressionType.logical_and_t);
        assertTrue(condition.toString().equalsIgnoreCase("page_views.date >= '2008-03-01' AND page_views.date <= '2008-03-31'"));
        TExpression left = condition.getLeftOperand();
        assertTrue(left.getExpressionType() == EExpressionType.simple_comparison_t);
        assertTrue(left.getComparisonOperator().toString().equalsIgnoreCase(">="));
        assertTrue(left.getComparisonOperator().tokencode == TBaseType.great_equal);
        TExpression right = condition.getRightOperand();
        //System.out.println(left.getComparisonOperator().tokencode);
    }


    public void testGroupBy(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext ="SELECT col1 FROM t1 GROUP BY col1 HAVING SUM(col2) > 10;";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(select.getResultColumnList().size() == 1);
        TGroupBy groupBy = select.getGroupByClause();
        assertTrue(groupBy.getItems().size() == 1);
        TGroupByItem groupByItem = groupBy.getItems().getGroupByItem(0);
        assertTrue(groupByItem.getExpr().toString().equalsIgnoreCase("col1"));
        TExpression havingCondition = groupBy.getHavingClause();
        assertTrue(havingCondition.getExpressionType() == EExpressionType.simple_comparison_t);
        assertTrue(havingCondition.toString().equalsIgnoreCase("SUM(col2) > 10"));
        TExpression left = havingCondition.getLeftOperand();
        TExpression right = havingCondition.getRightOperand();
        assertTrue(left.getExpressionType() == EExpressionType.function_t);
        TFunctionCall functionCall = left.getFunctionCall();
        assertTrue(functionCall.getFunctionName().toString().equalsIgnoreCase("SUM"));
        assertTrue(functionCall.getArgs().getExpression(0).toString().equalsIgnoreCase("col2"));
        assertTrue(right.getExpressionType() == EExpressionType.simple_constant_t);
    }

    public void testSubqueryInFromClause(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext ="SELECT col1 FROM (SELECT col11, SUM(col2) AS col2sum FROM t1 GROUP BY col1) t2 WHERE t2.col2sum > 10";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(select.getResultColumnList().size() == 1);

        assertTrue(select.joins.size() == 1);
        TJoin join = select.joins.getJoin(0);
        assertTrue(join.getKind() == TBaseType.join_source_fake);

        TTable table = join.getTable();
        assertTrue(table.getTableType() == ETableSource.subquery);

        TSelectSqlStatement subquery  = table.getSubquery();
        assertTrue(subquery.getResultColumnList().getResultColumn(0).toString().equalsIgnoreCase("col11"));

    }

  public void testLimitClause(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext ="SELECT * FROM t1 LIMIT 5;";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(select.getResultColumnList().size() == 1);

       TLimitClause limitClause = select.getLimitClause();
      //System.out.println(limitClause.getOffset().toString());
       assertTrue(limitClause.getOffset().toString().equalsIgnoreCase("5"));

    }

  public void testSortBy(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext ="SELECT * FROM sales SORT BY amount DESC LIMIT 5;;";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(select.getResultColumnList().size() == 1);

        TSortBy sortBy  = select.getSortBy();
      assertTrue(sortBy.getItems().getOrderByItem(0).getSortKey().toString().equalsIgnoreCase("amount"));
      assertTrue(sortBy.getItems().getOrderByItem(0).getSortType() == TBaseType.srtDesc);

    }


}
