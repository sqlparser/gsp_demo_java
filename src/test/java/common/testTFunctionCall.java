package common;
/*
 * Date: 2010-11-16
 * Time: 14:31:41
 */

import gudusoft.gsqlparser.EFunctionType;
import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class testTFunctionCall extends TestCase {

    public void testConvert(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "SELECT x=convert(VARCHAR(1000), 'hello' )";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column = select.getResultColumnList().getResultColumn(0);
        TExpression expr = column.getExpr();
        TExpression expr1 = expr.getRightOperand();
        TFunctionCall function = expr1.getFunctionCall();
        //TExpressionList args = function.getArgs();
        assertTrue(function.getFunctionType() == EFunctionType.convert_t);
        TTypeName typename = function.getTypename();
        assertTrue(typename.toString().equalsIgnoreCase("VARCHAR(1000)"));
        assertTrue(function.getParameter().toString().equalsIgnoreCase("'hello'"));
        //System.out.println(args.size());
        assertTrue(function.isBuiltIn(EDbVendor.dbvmssql));
    }

    public void testExtract(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "SELECT x=extract('1' from 'hello' )";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column = select.getResultColumnList().getResultColumn(0);
        TExpression expr = column.getExpr();
        TExpression expr1 = expr.getRightOperand();
        TFunctionCall function = expr1.getFunctionCall();
        assertTrue(function.getFunctionType() == EFunctionType.extract_t);
        assertTrue(function.getExtract_time_token().toString().equalsIgnoreCase("'1'"));
        assertTrue(function.getExpr1().toString().equalsIgnoreCase("'hello'"));

        assertTrue(function.isBuiltIn(EDbVendor.dbvmssql));
    }

    public void testBuiltIn_mssql(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "SELECT count(*), count1(*) from t";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column = select.getResultColumnList().getResultColumn(0);
        TExpression expr = column.getExpr();
        TFunctionCall function = expr.getFunctionCall();
        assertTrue(function.isBuiltIn(EDbVendor.dbvmssql));

        column = select.getResultColumnList().getResultColumn(1);
        expr = column.getExpr();
        function = expr.getFunctionCall();
        assertTrue(!function.isBuiltIn(EDbVendor.dbvmssql));
    }

    public void testBuiltIn_informix(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvinformix);
        sqlparser.sqltext = "SELECT count(*), count1(*) from t";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column = select.getResultColumnList().getResultColumn(0);
        TExpression expr = column.getExpr();
        TFunctionCall function = expr.getFunctionCall();
        assertTrue(function.isBuiltIn(EDbVendor.dbvinformix));

        column = select.getResultColumnList().getResultColumn(1);
        expr = column.getExpr();
        function = expr.getFunctionCall();
        assertTrue(!function.isBuiltIn(EDbVendor.dbvinformix));
    }

    public void testBuiltIn_greenplum(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgreenplum);
        sqlparser.sqltext = "SELECT count(*), count1(*) from t";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column = select.getResultColumnList().getResultColumn(0);
        TExpression expr = column.getExpr();
        TFunctionCall function = expr.getFunctionCall();
        assertTrue(function.isBuiltIn(EDbVendor.dbvgreenplum));

        column = select.getResultColumnList().getResultColumn(1);
        expr = column.getExpr();
        function = expr.getFunctionCall();
        assertTrue(!function.isBuiltIn(EDbVendor.dbvgreenplum));
    }

    public void testBuiltIn_redshift(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "SELECT count(*), count1(*) from t";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column = select.getResultColumnList().getResultColumn(0);
        TExpression expr = column.getExpr();
        TFunctionCall function = expr.getFunctionCall();
        assertTrue(function.isBuiltIn(EDbVendor.dbvredshift));

        column = select.getResultColumnList().getResultColumn(1);
        expr = column.getExpr();
        function = expr.getFunctionCall();
        assertTrue(!function.isBuiltIn(EDbVendor.dbvredshift));
    }

    public void testBuiltIn_hive(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "SELECT count(*), count1(*) from t";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column = select.getResultColumnList().getResultColumn(0);
        TExpression expr = column.getExpr();
        TFunctionCall function = expr.getFunctionCall();
        assertTrue(function.isBuiltIn(EDbVendor.dbvhive));

        column = select.getResultColumnList().getResultColumn(1);
        expr = column.getExpr();
        function = expr.getFunctionCall();
        assertTrue(!function.isBuiltIn(EDbVendor.dbvhive));
    }

}
