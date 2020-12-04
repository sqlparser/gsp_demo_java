package postgresql;
/*
 * Date: 11-6-21
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import junit.framework.TestCase;

public class testUpdateStmt extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "UPDATE weather SET temp_lo = temp_lo+1, temp_hi = temp_lo+15, prcp = DEFAULT\n" +
                "  WHERE city = 'San Francisco' AND date = '2003-07-03';";

        assertTrue(sqlparser.parse() == 0);

        TUpdateSqlStatement updateSqlStatement = (TUpdateSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(updateSqlStatement.getTargetTable().toString().equalsIgnoreCase("weather"));

        TResultColumnList resultColumnList = updateSqlStatement.getResultColumnList();
        assertTrue(resultColumnList.size() == 3);

        TResultColumn resultColumn = resultColumnList.getResultColumn(0);
        TExpression expr = resultColumn.getExpr();
        assertTrue(expr.getExpressionType() == EExpressionType.assignment_t);
        assertTrue(expr.getLeftOperand().toString().equalsIgnoreCase("temp_lo"));
        assertTrue(expr.getRightOperand().toString().equalsIgnoreCase("temp_lo+1"));

    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "UPDATE weather SET (temp_lo, temp_hi, prcp) = (temp_lo+1, temp_lo+15, DEFAULT)\n" +
                "  WHERE city = 'San Francisco' AND date = '2003-07-03';";

        assertTrue(sqlparser.parse() == 0);

        TUpdateSqlStatement updateSqlStatement = (TUpdateSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(updateSqlStatement.getTargetTable().toString().equalsIgnoreCase("weather"));

        TResultColumnList resultColumnList = updateSqlStatement.getResultColumnList();
        assertTrue(resultColumnList.size() == 1);

        TResultColumn resultColumn = resultColumnList.getResultColumn(0);
        TExpression expr = resultColumn.getExpr();
        assertTrue(expr.getExpressionType() == EExpressionType.assignment_t);

        TExpression leftExpr = expr.getLeftOperand();
        assertTrue(leftExpr.getExpressionType() == EExpressionType.list_t);

        assertTrue(leftExpr.getExprList().size() == 3);
        assertTrue(leftExpr.getExprList().getExpression(0).toString().equalsIgnoreCase("temp_lo"));
        assertTrue(leftExpr.getExprList().getExpression(1).toString().equalsIgnoreCase("temp_hi"));
        assertTrue(leftExpr.getExprList().getExpression(2).toString().equalsIgnoreCase("prcp"));

        TExpression rightExpr = expr.getRightOperand();
        assertTrue(rightExpr.getExpressionType() == EExpressionType.list_t);
        assertTrue(rightExpr.getExprList().size() == 3);

        assertTrue(rightExpr.getExprList().getExpression(0).toString().equalsIgnoreCase("temp_lo+1"));
        assertTrue(rightExpr.getExprList().getExpression(1).toString().equalsIgnoreCase("temp_lo+15"));
        assertTrue(rightExpr.getExprList().getExpression(2).toString().equalsIgnoreCase("DEFAULT"));

    }

    public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "UPDATE accounts SET (contact_last_name, contact_first_name) =\n" +
                "    (SELECT last_name, first_name FROM salesmen\n" +
                "     WHERE salesmen.id = accounts.sales_id)";

        assertTrue(sqlparser.parse() == 0);

        TUpdateSqlStatement updateSqlStatement = (TUpdateSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(updateSqlStatement.getTargetTable().toString().equalsIgnoreCase("accounts"));

        TResultColumnList resultColumnList = updateSqlStatement.getResultColumnList();
        assertTrue(resultColumnList.size() == 1);

        TResultColumn resultColumn = resultColumnList.getResultColumn(0);
        TExpression expr = resultColumn.getExpr();
        assertTrue(expr.getExpressionType() == EExpressionType.assignment_t);

        TExpression leftExpr = expr.getLeftOperand();
        assertTrue(leftExpr.getExpressionType() == EExpressionType.list_t);

        assertTrue(leftExpr.getExprList().size() == 2);
        assertTrue(leftExpr.getExprList().getExpression(0).toString().equalsIgnoreCase("contact_last_name"));
        assertTrue(leftExpr.getExprList().getExpression(1).toString().equalsIgnoreCase("contact_first_name"));

        TExpression rightExpr = expr.getRightOperand();
        assertTrue(rightExpr.getExpressionType() == EExpressionType.subquery_t);
        assertTrue(rightExpr.getSubQuery().getResultColumnList().size() == 2);
        assertTrue(rightExpr.getSubQuery().getResultColumnList().getResultColumn(0).getExpr().toString().equalsIgnoreCase("last_name"));
    }

    public void test4(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "UPDATE films SET kind = 'Dramatic' from lib WHERE lib.f = films.f";

        assertTrue(sqlparser.parse() == 0);

        TUpdateSqlStatement updateSqlStatement = (TUpdateSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(updateSqlStatement.getTargetTable().toString().equalsIgnoreCase("films"));

//        assertTrue(updateSqlStatement.getReferenceJoins().size() == 1);
//        assertTrue(updateSqlStatement.getReferenceJoins().getJoin(0).getTable().toString().equalsIgnoreCase("lib"));
        assertTrue(updateSqlStatement.joins.size() == 1);
        assertTrue(updateSqlStatement.joins.getJoin(0).getTable().toString().equalsIgnoreCase("lib"));
    }

}
