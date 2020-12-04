package common;
/*
 * Date: 12-8-10
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TCommonBlock;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.nodes.TVarDeclStmt;
import junit.framework.TestCase;

public class testDatatype extends TestCase {

    public void testNvarchar(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "Select 1::nvarchar(20)";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn resultColumn = selectSqlStatement.getResultColumnList().getResultColumn(0);
        assertTrue(resultColumn.getExpr().getExpressionType() == EExpressionType.typecast_t);
        assertTrue(resultColumn.getExpr().getTypeName().getDataType() == EDataType.nvarchar_t);
        assertTrue(resultColumn.getExpr().getTypeName().getLength().toString().equalsIgnoreCase("20"));
    }

    public void testDateAttribute(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "select CAST('20120802' AS DATE FORMAT 'yyyymmdd') from b;";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn resultColumn = selectSqlStatement.getResultColumnList().getResultColumn(0);
        TFunctionCall functionCall = resultColumn.getExpr().getFunctionCall();
        TTypeName datatype = functionCall.getTypename();
        assertTrue(datatype.toString().equalsIgnoreCase("DATE FORMAT 'yyyymmdd'"));
        TDatatypeAttribute attribute = datatype.getDatatypeAttributeList().getElement(0);
        assertTrue(attribute.getValue_literal().toString().equalsIgnoreCase("'yyyymmdd'"));
    }

    public void testrowtype0(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "DECLARE\n" +
                "   SUBTYPE v_empid_subtype IS employees_temp.empid%TYPE;\n" +
                "   SUBTYPE v_emprec_subtype IS employees_temp%ROWTYPE;   \n" +
                "   v_empid    v_empid_subtype;\n" +
                "BEGIN\n" +
                "   v_empid := NULL;  -- this works, null constraint is not inherited\n" +
                "   DBMS_OUTPUT.PUT_LINE('v_emprec.deptname: ' || v_emprec.deptname); \n" +
                "END;";
        assertTrue(sqlparser.parse() == 0);

        TCommonBlock statement = (TCommonBlock)sqlparser.sqlstatements.get(0);
        TVarDeclStmt declStmt0 = (TVarDeclStmt)statement.getDeclareStatements().get(0);
        TTypeName datatype0  =  declStmt0.getDataType();
        assertTrue(datatype0.getDatatypeAttributeList().getElement(0).getAttributeType() == EDataTypeAttribute.plsql_type_t);

        TVarDeclStmt declStmt1 = (TVarDeclStmt)statement.getDeclareStatements().get(1);
        TTypeName datatype1  =  declStmt1.getDataType();
        assertTrue(datatype1.getDatatypeAttributeList().getElement(0).getAttributeType() == EDataTypeAttribute.plsql_rowtype_t);
    }

    public void testVarchar(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE TABLE [dbo].[DimAccount](\n" +
                "    [AccountKey] [int] IDENTITY(1,1) NOT NULL,\n" +
                "    [ParentAccountKey] [int] NULL,\n" +
                "    [AccountCodeAlternateKey] [int] NULL,\n" +
                "    [ParentAccountCodeAlternateKey] [int] NULL,\n" +
                "    [AccountDescription] [nvarchar](50) NULL,\n" +
                "    [AccountType] [nvarchar](50) NULL,\n" +
                "    [Operator] [nvarchar](50) NULL,\n" +
                "    [CustomMembers] [nvarchar](300) NULL,\n" +
                "    [ValueType] [nvarchar](50) NULL,\n" +
                "    [CustomMemberOptions] [nvarchar](200) NULL \n" +
                ") ON [PRIMARY];";

        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TColumnDefinition cd = createTableSqlStatement.getColumnList().getColumn(4);
        assertTrue(cd.getColumnName().toString().equalsIgnoreCase("[AccountDescription]"));
        assertTrue(cd.getDatatype().getDataType() == EDataType.nvarchar_t);
        assertTrue(cd.getDatatype().getLength().toString().equalsIgnoreCase("50"));

        //System.out.print(cd.getDatatype().getDataType());
    }


    public void testVerticalDatatypeExplictCast(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvvertica);
        sqlparser.sqltext = "SELECT(FLOAT '123.5')::INT;";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn resultColumn = selectSqlStatement.getResultColumnList().getResultColumn(0);
        TExpression expr = resultColumn.getExpr();
        assertTrue(expr.getExpressionType() == EExpressionType.typecast_t);
        assertTrue(expr.getTypeName().getDataType() == EDataType.int_t);
        TExpression expr1 = expr.getLeftOperand();
        assertTrue(expr1.getExpressionType() == EExpressionType.parenthesis_t);
        TExpression expr2 = expr1.getLeftOperand();
        assertTrue(expr2.getExpressionType() == EExpressionType.simple_constant_t);
        assertTrue(expr2.getConstantOperand().getValue().equalsIgnoreCase("'123.5'"));
        assertTrue(expr2.getConstantOperand().getCastType().getDataType() == EDataType.float_t);
    }

    public void testVerticalDateExplictCast(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvvertica);
        sqlparser.sqltext = "SELECT DATE 'now'";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn resultColumn = selectSqlStatement.getResultColumnList().getResultColumn(0);
        TExpression expr = resultColumn.getExpr();
        assertTrue(expr.getExpressionType() == EExpressionType.simple_constant_t);
        assertTrue(expr.getConstantOperand().getCastType().getDataType() == EDataType.date_t);
        assertTrue(expr.getConstantOperand().getValue().equalsIgnoreCase("'now'"));


        sqlparser.sqltext = "SELECT DATE('now')";
        assertTrue(sqlparser.parse() == 0);

         selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
         resultColumn = selectSqlStatement.getResultColumnList().getResultColumn(0);
         expr = resultColumn.getExpr();
         assertTrue(expr.getExpressionType() == EExpressionType.function_t);
        TFunctionCall fn = expr.getFunctionCall();
        assertTrue(fn.getFunctionName().toString().equalsIgnoreCase("DATE"));
    }

}
