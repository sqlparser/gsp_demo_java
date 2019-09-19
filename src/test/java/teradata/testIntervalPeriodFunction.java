package test.teradata;
/*
 * Date: 11-10-13
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testIntervalPeriodFunction extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT\n" +
                "    person_id,\n" +
                "    INTERVAL(enrolled_period) DAY(4) AS DayCt       -- Note that DAY does not work (overflow)\n" +
                "FROM person_coaching_period\n" +
                "WHERE END(enrolled_period) IS NOT UNTIL_CHANGED     -- Only those that are ended\n" +
                "ORDER BY person_id, coaching_program, enrolled_period;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn resultColumn = select.getResultColumnList().getResultColumn(1);
        assertTrue(resultColumn.getExpr().toString().equalsIgnoreCase("INTERVAL(enrolled_period) DAY(4)"));
        assertTrue(resultColumn.getExpr().getExpressionType() == EExpressionType.function_t);

        TFunctionCall functionCall = resultColumn.getExpr().getFunctionCall();
        assertTrue(functionCall.getArgs().getExpression(0).toString().equalsIgnoreCase("enrolled_period"));
    }

}
