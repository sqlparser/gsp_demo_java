package mssql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EFunctionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

import junit.framework.TestCase;


public class testContainsFunction extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "Select contains(column,'value'), count(id) from Products;";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TFunctionCall functionCall = select.getResultColumnList().getResultColumn(0).getExpr().getFunctionCall();
        assertTrue(functionCall.getFunctionType() == EFunctionType.contains_t);
        switch (functionCall.getFunctionType()){
            case contains_t:
               assertTrue(functionCall.getExpr1().toString().equalsIgnoreCase("column"));
               assertTrue(functionCall.getExpr2().toString().equalsIgnoreCase("'value'"));
               break;
        }

    }
}
