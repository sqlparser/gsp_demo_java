package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testPartitonedBy extends TestCase {

    public void testTPartitionClause() {
        TGSqlParser tgSqlParser = new TGSqlParser(EDbVendor.dbvhive);

        tgSqlParser.sqltext = "select day_id,mac_id,mac_color,day_num," +
                "sum(day_num)over(partition by day_id,mac_id order by day_id) sum_num from test_temp_mac_id";

        assertTrue(tgSqlParser.parse() == 0);

        TSelectSqlStatement sqlstatements = (TSelectSqlStatement) tgSqlParser.getSqlstatements().get(0);
        TResultColumnList resultColumnList = sqlstatements.getResultColumnList();
        TExpression expr = resultColumnList.getResultColumn(4).getExpr();
        assertTrue(expr.toString().equalsIgnoreCase("sum(day_num)over(partition by day_id,mac_id order by day_id)"));

        TFunctionCall functionCall = expr.getFunctionCall();
        TWindowDef windowDef = functionCall.getWindowDef();
        assertTrue(windowDef.toString().equalsIgnoreCase("(partition by day_id,mac_id order by day_id)"));

        TPartitionClause partitionClause = windowDef.getPartitionClause();
        assertTrue(partitionClause.toString().equalsIgnoreCase("partition by day_id,mac_id"));
    }


    public void testTPartitionExprs() {
        TGSqlParser tgSqlParser = new TGSqlParser(EDbVendor.dbvhive);

        tgSqlParser.sqltext = "select day_id,mac_id,mac_color,day_num," +
                "sum(day_num)over(partition by day_id,mac_id order by day_id) sum_num from test_temp_mac_id";

        assertTrue(tgSqlParser.parse() == 0);

        TSelectSqlStatement sqlstatements = (TSelectSqlStatement) tgSqlParser.getSqlstatements().get(0);

        TResultColumnList resultColumnList = sqlstatements.getResultColumnList();
        TExpression expr = resultColumnList.getResultColumn(4).getExpr();
        assertTrue(expr.toString().equalsIgnoreCase("sum(day_num)over(partition by day_id,mac_id order by day_id)"));

        TFunctionCall functionCall = expr.getFunctionCall();
        TWindowDef windowDef = functionCall.getWindowDef();
        assertTrue(windowDef.toString().equalsIgnoreCase("(partition by day_id,mac_id order by day_id)"));

        TPartitionClause partitionClause = windowDef.getPartitionClause();
        TExpressionList expressionList = partitionClause.getExpressionList();
        assertTrue(expressionList.toString().equalsIgnoreCase("day_id,mac_id"));
        assertTrue(expressionList.getExpression(0).toString().equalsIgnoreCase("day_id"));
        assertTrue(expressionList.getExpression(1).toString().equalsIgnoreCase("mac_id"));

        TOrderBy orderBy = windowDef.getOrderBy();
        assertTrue(orderBy.toString().equalsIgnoreCase("order by day_id"));
    }
}
