package teradata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.EFunctionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testDateFunction extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT '2020-12-12'(NAMED dt_run), EMPLOYEE_ID\n" +
                "FROM foodmart.TRIMMED_EMPLOYEE\n" +
                "GROUP BY DATE, EMPLOYEE_ID;";

        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expression = select.getGroupByClause().getItems().getGroupByItem(0).getExpr();
        //System.out.println(select.getGroupByClause().getItems().size());
        assertTrue(expression.toString().equalsIgnoreCase("DATE"));
        assertTrue(expression.getExpressionType() == EExpressionType.function_t);
        TFunctionCall functionCall = expression.getFunctionCall();
        assertTrue(functionCall.getFunctionName().toString().equalsIgnoreCase("DATE"));
        assertTrue(functionCall.getFunctionType() == EFunctionType.date_t);
    }
}
