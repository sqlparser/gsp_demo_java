package test.teradata;
/*
 * Date: 11-10-13
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testUntilChanged extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT * FROM person_coaching_period\n" +
                "WHERE END(enrolled_period) IS UNTIL_CHANGED;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expression = select.getWhereClause().getCondition();
       // System.out.println(expression.getLeftOperand().toString());
        assertTrue(expression.getLeftOperand().toString().equalsIgnoreCase("END(enrolled_period)"));

        TFunctionCall functionCall = expression.getLeftOperand().getFunctionCall();
        assertTrue(functionCall.getArgs().getExpression(0).toString().equalsIgnoreCase("enrolled_period"));

    }

}
