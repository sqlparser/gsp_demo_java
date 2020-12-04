package oracle;
/*
 * Date: 12-11-1
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testObjectAccess extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "SELECT\n" +
                "   O.OBJECT_ID,\n" +
                "   XMLAGG (XMLELEMENT (K, O.KEY_1 || '|')).EXTRACT ('//text()') AS TEXT_KEY\n" +
                "FROM DAG_OBJECT_FACT O";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select  = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn resultColumn = select.getResultColumnList().getResultColumn(1);
        TExpression expression = resultColumn.getExpr();
        TObjectAccess objectAccess = expression.getObjectAccess();
        TExpression objectExpr = objectAccess.getObjectExpr();
        assertTrue(objectExpr.getExpressionType() == EExpressionType.function_t);
        TFunctionCall functionCall1 = objectExpr.getFunctionCall();
        TExpression arg1 = functionCall1.getArgs().getExpression(0);
        assertTrue(arg1.getExpressionType() ==EExpressionType.function_t );
//        TFunctionCall functionCall2 = arg1.getFunctionCall();
//        assertTrue(functionCall2.getArgs().getExpression(0).toString().equalsIgnoreCase("K"));
//        assertTrue(functionCall2.getArgs().getExpression(1).getExpressionType() == EExpressionType.concatenate_t);
        TFunctionCall functionCall = objectAccess.getMethod();
        assertTrue(functionCall.getFunctionName().toString().equalsIgnoreCase("EXTRACT"));
        assertTrue(functionCall.getArgs().getExpression(0).toString().equalsIgnoreCase("'//text()'"));
    }

}
