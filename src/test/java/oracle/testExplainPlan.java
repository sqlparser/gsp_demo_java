package oracle;
/*
 * Date: 13-2-1
 */


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TMultiTargetList;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import gudusoft.gsqlparser.stmt.TExplainPlan;
import junit.framework.TestCase;

public class testExplainPlan extends TestCase {

    public void testSelect(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "explain plan for select * from dual;";
        assertTrue(sqlparser.parse() == 0);
        TExplainPlan explainPlan = (TExplainPlan)sqlparser.sqlstatements.get(0);
        TSelectSqlStatement select = (TSelectSqlStatement)explainPlan.getStatement();
        assertTrue(select.toString().equalsIgnoreCase("select * from dual"));
        TResultColumn resultColumn = select.getResultColumnList().getResultColumn(0);
        assertTrue(resultColumn.toString().equalsIgnoreCase("*"));

        assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("dual"));
    }

    public void testUpdate(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "EXPLAIN PLAN\n" +
                "SET STATEMENT_ID = 'Raise in Tokyo'\n" +
                "INTO plan_table\n" +
                "FOR UPDATE employees\n" +
                "SET salary = salary * 1.10\n" +
                "WHERE department_id =\n" +
                "(SELECT department_id FROM departments\n" +
                "WHERE location_id = 1200);";
        assertTrue(sqlparser.parse() == 0);
        TExplainPlan explainPlan = (TExplainPlan)sqlparser.sqlstatements.get(0);
        TUpdateSqlStatement update = (TUpdateSqlStatement)explainPlan.getStatement();
        assertTrue(update.getTargetTable().toString().equalsIgnoreCase("employees"));

        TResultColumn resultColumn = update.getResultColumnList().getResultColumn(0);
        assertTrue(resultColumn.toString().equalsIgnoreCase("salary = salary * 1.10"));

        TExpression expression = update.getWhereClause().getCondition();
        assertTrue(expression.getRightOperand().getExpressionType() == EExpressionType.subquery_t);

        TSelectSqlStatement select = expression.getRightOperand().getSubQuery();
        assertTrue(select.getResultColumnList().getResultColumn(0).toString().equalsIgnoreCase("department_id"));


    }

}
