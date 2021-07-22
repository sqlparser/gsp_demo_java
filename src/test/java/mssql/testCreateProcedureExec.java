package mssql;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.compiler.TSymbolTableManager;
import gudusoft.gsqlparser.compiler.TVariable;
import gudusoft.gsqlparser.nodes.TExecParameter;
import gudusoft.gsqlparser.nodes.mssql.TExecuteAsClause;
import gudusoft.gsqlparser.nodes.mssql.TProcedureOption;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.stmt.TBlockSqlStatement;
import gudusoft.gsqlparser.stmt.TCommonBlock;
import gudusoft.gsqlparser.stmt.mssql.*;
import junit.framework.TestCase;

public class testCreateProcedureExec extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE PROC usp_query (\n" +
                "    @table NVARCHAR(128)\n" +
                ")\n" +
                "AS\n" +
                "BEGIN\n" +
                "\n" +
                "    DECLARE @sql NVARCHAR(MAX);\n" +
                "    -- construct SQL\n" +
                "    SET @sql = N'SELECT * FROM ' + @table;\n" +
                "    -- execute the SQL\n" +
                "    EXEC sp_executesql @sql;\n" +
                "    \n" +
                "END;";
        assertTrue(sqlparser.parse() == 0);

        TMssqlCreateProcedure createProcedure = (TMssqlCreateProcedure)sqlparser.sqlstatements.get(0);

        assertTrue(createProcedure.getBodyStatements().size() == 1);
        assertTrue(createProcedure.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstmssqlblock);
        TMssqlBlock block =  (TMssqlBlock)createProcedure.getBodyStatements().get(0);
        assertTrue(block.getBodyStatements().size() == 3);
//        System.out.println(block.getBodyStatements().get(0).sqlstatementtype);
//        System.out.println(block.getBodyStatements().get(1).sqlstatementtype);
//        System.out.println(block.getBodyStatements().get(2).sqlstatementtype);
        TMssqlDeclare declare = (TMssqlDeclare) block.getBodyStatements().get(0);
        assertTrue(declare.getVariables().getDeclareVariable(0).getVariableName().getDbObjectType() == EDbObjectType.variable);

        TMssqlSet set = (TMssqlSet) block.getBodyStatements().get(1);
        assertTrue(set.getSetType() == TBaseType.mstLocalVar);
        assertTrue(set.getVarName().getDbObjectType() == EDbObjectType.variable);

        TMssqlExecute execute = (TMssqlExecute)block.getBodyStatements().get(2);
        assertTrue(execute.getModuleName().toString().equalsIgnoreCase("sp_executesql"));
        assertTrue(execute.getExecType() == TBaseType.metExecSp);
        TExecParameter execParameter = execute.getParameters().getExecParameter(0);
        assertTrue(execParameter.getParameterValue().getObjectOperand().getDbObjectType() == EDbObjectType.variable);
    }

    public void testIProcExchange(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);

        sqlparser.sqltext = "EXEC usp_query 'production.brands';\n" +
                "\n" +
                "GO\n" +
                "\n" +
                "CREATE PROC usp_query (\n" +
                "    @table NVARCHAR(128)\n" +
                ")\n" +
                "AS\n" +
                "BEGIN\n" +
                "\n" +
                "    DECLARE @sql NVARCHAR(MAX);\n" +
                "    -- construct SQL\n" +
                "    SET @sql = N'SELECT * FROM ' + @table;\n" +
                "    -- execute the SQL\n" +
                "    EXEC sp_executesql @sql;\n" +
                "    \n" +
                "END;\n";
        //System.out.println(sqlparser.sqltext);
        assertTrue(sqlparser.parse() == 0);

        TMssqlCreateProcedure createProcedure = (TMssqlCreateProcedure)sqlparser.sqlstatements.get(2);
        TMssqlBlock block =  (TMssqlBlock)createProcedure.getBodyStatements().get(0);
        TMssqlExecute execute = (TMssqlExecute)block.getBodyStatements().get(2);
        assertTrue(execute.getModuleName().toString().equalsIgnoreCase("sp_executesql"));
        assertTrue(execute.getExecType() == TBaseType.metExecSp);
        assertTrue(execute.getSqlText().equalsIgnoreCase("SELECT * FROM production.brands"));
    }

    public void testIProcExchange2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);

        sqlparser.sqltext = "EXEC usp_query 'production.brands';\n" +
                "\n" +
                "GO\n" +
                "\n" +
                "CREATE PROC usp_query (\n" +
                "    @table NVARCHAR(128)\n" +
                ")\n" +
                "AS\n" +
                "BEGIN\n" +
                "\n" +
                "    DECLARE @sql NVARCHAR(MAX);\n" +
                "    -- construct SQL\n" +
                "    SET @sql = N'SELECT * FROM ' + @table;\n" +
                "    -- execute the SQL\n" +
                "    EXEC sp_executesql N'SELECT * FROM production.products';\n" +
                "    \n" +
                "END;\n";
        //System.out.println(sqlparser.sqltext);
        assertTrue(sqlparser.parse() == 0);

        TMssqlCreateProcedure createProcedure = (TMssqlCreateProcedure)sqlparser.sqlstatements.get(2);
        TMssqlBlock block =  (TMssqlBlock)createProcedure.getBodyStatements().get(0);
        TMssqlExecute execute = (TMssqlExecute)block.getBodyStatements().get(2);
        assertTrue(execute.getModuleName().toString().equalsIgnoreCase("sp_executesql"));
        assertTrue(execute.getExecType() == TBaseType.metExecSp);
        assertTrue(execute.getSqlText().equalsIgnoreCase("SELECT * FROM production.products"));

    }


}

//class TProcedureSQLEnv extends TSQLEnv {
//
//    public TProcedureSQLEnv(EDbVendor dbVendor){
//        super(dbVendor);
//    }
//
//    public void initSQLEnv() {
//    }
//
//    public String getProcedureParameterValue(String procedureName, int paramPos){
//        return "N'production.products'";
//    }
//    public void getVariableValue(String variableName, String variableValue){
//        System.out.println("Variable name:"+variableName+"\nvalue:\n"+variableValue);
//    }
//}


