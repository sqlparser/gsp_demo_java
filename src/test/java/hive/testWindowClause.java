package hive;
/*
 * Date: 13-8-12
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testWindowClause extends TestCase {

    public void testWindowing(){
          TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
          sqlparser.sqltext ="SELECT a, COUNT(b) OVER (PARTITION BY c)\n" +
                  "FROM T;";
          assertTrue(sqlparser.parse() == 0);

          TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
          assertTrue(select.getResultColumnList().size() == 2);

        TExpression expression = select.getResultColumnList().getResultColumn(1).getExpr();
        assertTrue(expression.getExpressionType() == EExpressionType.function_t);

        assertTrue(select.getResultColumnList().getResultColumn(1).toString().equalsIgnoreCase("COUNT(b) OVER (PARTITION BY c)"));
        TFunctionCall functionCall = expression.getFunctionCall();
        assertTrue(functionCall.getFunctionName().toString().equalsIgnoreCase("COUNT"));

        TWindowDef windowDef = functionCall.getWindowDef();
        assertTrue(windowDef.getPartitionClause().getExpressionList().size() == 1);
        assertTrue(windowDef.getPartitionClause().getExpressionList().getExpression(0).toString().equalsIgnoreCase("c"));

        //System.out.println(windowSpecification.toString());

        assertTrue(select.joins.size() == 1);
        TJoin join = select.joins.getJoin(0);
        assertTrue(join.getKind() == TBaseType.join_source_fake);

        TTable table = join.getTable();
        assertTrue(table.getTableType() == ETableSource.objectname);

    }

    public void testWindowing2(){
          TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
          sqlparser.sqltext ="SELECT a, SUM(b) OVER (PARTITION BY c ORDER BY d ROWS BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW)\n" +
                  "FROM T;";
          assertTrue(sqlparser.parse() == 0);

          TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
          assertTrue(select.getResultColumnList().size() == 2);

        TExpression expression = select.getResultColumnList().getResultColumn(1).getExpr();
        assertTrue(expression.getExpressionType() == EExpressionType.function_t);

        TFunctionCall functionCall = expression.getFunctionCall();
        assertTrue(functionCall.getFunctionName().toString().equalsIgnoreCase("SUM"));

        TWindowDef windowDef = functionCall.getWindowDef();
        assertTrue(windowDef.getPartitionClause().getExpressionList().size() == 1);
        assertTrue(windowDef.getPartitionClause().getExpressionList().getExpression(0).toString().equalsIgnoreCase("c"));

        TOrderByItem orderByItem = windowDef.getOrderBy().getItems().getOrderByItem(0);
        assertTrue(orderByItem.toString().equalsIgnoreCase("d"));

        TWindowFrame windowFrame = windowDef.getWindowFrame();
        assertTrue(windowFrame.getLimitRowType() == ELimitRowType.Rows);
        TWindowFrameBoundary start  =  windowFrame.getStartBoundary();
        TWindowFrameBoundary end = windowFrame.getEndBoundary();
        assertTrue(start.getBoundaryType() == EBoundaryType.ebtUnboundedPreceding);
        assertTrue(end.getBoundaryType() == EBoundaryType.ebtCurrentRow);
        //System.out.println(windowSpecification.toString());

        assertTrue(select.joins.size() == 1);
        TJoin join = select.joins.getJoin(0);
        assertTrue(join.getKind() == TBaseType.join_source_fake);

        TTable table = join.getTable();
        assertTrue(table.getTableType() == ETableSource.objectname);

    }

    public void testWindowing3(){
          TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
          sqlparser.sqltext ="SELECT a, AVG(b) OVER (PARTITION BY c ORDER BY d ROWS BETWEEN 3 PRECEDING AND 3 FOLLOWING)\n" +
                  "FROM T;";
          assertTrue(sqlparser.parse() == 0);

          TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
          assertTrue(select.getResultColumnList().size() == 2);

        TExpression expression = select.getResultColumnList().getResultColumn(1).getExpr();
        assertTrue(expression.getExpressionType() == EExpressionType.function_t);

        TFunctionCall functionCall = expression.getFunctionCall();
        assertTrue(functionCall.getFunctionName().toString().equalsIgnoreCase("AVG"));

        TWindowDef windowSpecification = functionCall.getWindowDef();
        assertTrue(windowSpecification.getPartitionClause().getExpressionList().size() == 1);
        assertTrue(windowSpecification.getPartitionClause().getExpressionList().getExpression(0).toString().equalsIgnoreCase("c"));

        TOrderByItem orderByItem = windowSpecification.getOrderBy().getItems().getOrderByItem(0);
        assertTrue(orderByItem.toString().equalsIgnoreCase("d"));

        TWindowFrame windowFrame = windowSpecification.getWindowFrame();
        assertTrue(windowFrame.getLimitRowType() == ELimitRowType.Rows);
        TWindowFrameBoundary start  =  windowFrame.getStartBoundary();
        TWindowFrameBoundary end = windowFrame.getEndBoundary();
        assertTrue(start.getBoundaryType() == EBoundaryType.ebtPreceding);
        assertTrue(start.getBoundaryNumber().toString().equalsIgnoreCase("3"));
        assertTrue(end.getBoundaryType() == EBoundaryType.ebtFollowing);
        assertTrue(end.getBoundaryNumber().toString().equalsIgnoreCase("3"));
        //System.out.println(windowSpecification.toString());

        assertTrue(select.joins.size() == 1);
        TJoin join = select.joins.getJoin(0);
        assertTrue(join.getKind() == TBaseType.join_source_fake);

        TTable table = join.getTable();
        assertTrue(table.getTableType() == ETableSource.objectname);

    }
    public void testWindowing4(){
          TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
          sqlparser.sqltext ="SELECT a, SUM(b) OVER w\n" +
                  "FROM T\n" +
                  "WINDOW w AS (PARTITION BY c ORDER BY d ROWS UNBOUNDED PRECEDING)";
          assertTrue(sqlparser.parse() == 0);

          TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
          assertTrue(select.getResultColumnList().size() == 2);

        TExpression expression = select.getResultColumnList().getResultColumn(1).getExpr();
        assertTrue(expression.getExpressionType() == EExpressionType.function_t);

        TFunctionCall functionCall = expression.getFunctionCall();
        assertTrue(functionCall.getFunctionName().toString().equalsIgnoreCase("SUM"));

        TWindowDef windowSpecification = functionCall.getWindowDef();
        assertTrue(windowSpecification.getName().toString().equalsIgnoreCase("w"));
        //System.out.println(windowSpecification.toString());

        TWindowClause windowClause = select.getWindowClause();
        assertTrue(windowClause.getWindowDefs().size() == 1);
        TWindowDef windowDefinition = windowClause.getWindowDefs().getElement(0);
       // assertTrue(windowDefinition.getName().toString().equalsIgnoreCase("w"));
       // TWindowDef windowSpecification2 = windowDefinition.getWindowSpecification();
        assertTrue(windowDefinition.getPartitionClause().getExpressionList().getExpression(0).toString().equalsIgnoreCase("c"));
        assertTrue(windowDefinition.getWindowFrame().getLimitRowType() == ELimitRowType.Rows);
        assertTrue(windowDefinition.getWindowFrame().getStartBoundary().getBoundaryType() == EBoundaryType.ebtUnboundedPreceding);

        assertTrue(select.joins.size() == 1);
        TJoin join = select.joins.getJoin(0);
        assertTrue(join.getKind() == TBaseType.join_source_fake);

        TTable table = join.getTable();
        assertTrue(table.getTableType() == ETableSource.objectname);

    }

}
