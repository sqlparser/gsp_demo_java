package common;
/*
 * Date: 13-2-5
 */

import gudusoft.gsqlparser.EAggregateType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testAggregateFunction extends TestCase {

    public void test0(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "select AVG(DISTINCT PRSTAFF),avg(a) from b";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TFunctionCall functionCall = selectSqlStatement.getResultColumnList().getResultColumn(0).getExpr().getFunctionCall();
        assertTrue(functionCall.getAggregateType() == EAggregateType.distinct);

        TFunctionCall functionCall1 = selectSqlStatement.getResultColumnList().getResultColumn(1).getExpr().getFunctionCall();
        assertTrue(functionCall1.getAggregateType() == EAggregateType.none);
    }


}
