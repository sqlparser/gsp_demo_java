package postgresql;
/*
 * Date: 11-5-24
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TOrderBy;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testAggregateExpressions extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "SELECT array_agg(a ORDER BY b DESC) FROM table1 ";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expression0 = column0.getExpr();
        assertTrue(expression0.getExpressionType() == EExpressionType.function_t);
        TFunctionCall functionCall = expression0.getFunctionCall();
        assertTrue(functionCall.getArgs().size() == 1);
        assertTrue(functionCall.getArgs().getExpression(0).toString().equalsIgnoreCase("a"));
        TOrderBy orderBy = functionCall.getSortClause();
        assertTrue(orderBy.getItems().getOrderByItem(0).getSortKey().toString().equalsIgnoreCase("b"));

        sqlparser.sqltext = "SELECT string_agg(a, ',' ORDER BY a) FROM table1 ";
        assertTrue(sqlparser.parse() == 0);
        //System.out.println(orderBy.getItems().getOrderByItem(0).getSortKey().toString());
    }
}
