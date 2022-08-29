package mssql;


import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.nodes.mssql.TInlineResultSetDefinition;
import gudusoft.gsqlparser.nodes.mssql.TResultSetDefinition;
import gudusoft.gsqlparser.nodes.mssql.TResultSetsExecuteOption;
import gudusoft.gsqlparser.stmt.mssql.TMssqlExecute;
import junit.framework.TestCase;

public class testExecuteOption extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "EXEC Integration.GetMovementUpdates parameter1,parameter2 with recompile";
        int result = sqlparser.parse();
        assertTrue(result==0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstmssqlexec);
        TMssqlExecute execute = (TMssqlExecute)sqlparser.sqlstatements.get(0);
        assertTrue(execute.getExecuteOption().getExecuteOptionKind() == EExecuteOptionKind.eokRecompile);
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "EXEC Integration.GetMovementUpdates parameter1,parameter2 \n" +
                "WITH RESULT SETS\n" +
                "(\n" +
                "    (\n" +
                "        [Date Key] date,\n" +
                "        [WWI Stock Item Transaction ID] int,\n" +
                "        [WWI Invoice ID] int,\n" +
                "        [WWI Purchase Order ID] int,\n" +
                "        [Quantity] int,\n" +
                "        [WWI Stock Item ID] int,\n" +
                "        [WWI Customer ID] int,\n" +
                "        [WWI Supplier ID] int,\n" +
                "        [WWI Transaction Type ID] int,\n" +
                "        [Last Modified When] datetime2(7)\n" +
                "    )\n" +
                ");";
        int result = sqlparser.parse();
        assertTrue(result==0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstmssqlexec);
        TMssqlExecute execute = (TMssqlExecute)sqlparser.sqlstatements.get(0);
        assertTrue(execute.getExecuteOption().getExecuteOptionKind() == EExecuteOptionKind.eokResultSets);
        TResultSetsExecuteOption resultSetsExecuteOption = (TResultSetsExecuteOption) execute.getExecuteOption();
        assertTrue(resultSetsExecuteOption.getResultSetsOptionKind() == EResultSetsOptionKind.rsoResultSetsDefined);
        TResultSetDefinition resultSetDefinition = resultSetsExecuteOption.getDefinitions().get(0);
        assertTrue(resultSetDefinition.getResultSetType() == EResultSetType.rstInline);
        TInlineResultSetDefinition inlineResultSetDefinition = (TInlineResultSetDefinition)resultSetDefinition;
        TColumnDefinition columnDefinition = inlineResultSetDefinition.getColumnDefinitionList().getColumn(0);
        assertTrue(columnDefinition.getColumnName().toString().equalsIgnoreCase("[Date Key]"));

    }


    public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "EXEC Integration.GetMovementUpdates parameter1,parameter2 \n" +
                "WITH RESULT SETS UNDEFINED\n";

        int result = sqlparser.parse();
        assertTrue(result==0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstmssqlexec);
        TMssqlExecute execute = (TMssqlExecute)sqlparser.sqlstatements.get(0);
        assertTrue(execute.getExecuteOption().getExecuteOptionKind() == EExecuteOptionKind.eokResultSets);
        TResultSetsExecuteOption resultSetsExecuteOption = (TResultSetsExecuteOption) execute.getExecuteOption();
        assertTrue(resultSetsExecuteOption.getResultSetsOptionKind() == EResultSetsOptionKind.rsoUndefined);

    }

    public void test4(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "SP_EXECUTESQL N'SET FMTONLY ON SELECT * FROM [dbo].[insertbulk] '";

        int result = sqlparser.parse();
        assertTrue(result==0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstmssqlexec);
        TMssqlExecute execute = (TMssqlExecute)sqlparser.sqlstatements.get(0);
        assertTrue(execute.getExecuteType() == EExecType.module_with_params);
        assertTrue(execute.getModuleName().toString().equalsIgnoreCase("SP_EXECUTESQL"));
        assertTrue(execute.getParameters().getExecParameter(0).toString().equalsIgnoreCase("N'SET FMTONLY ON SELECT * FROM [dbo].[insertbulk] '"));
        assertTrue(execute.getParameters().getExecParameter(0).getParameterValue().toString().equalsIgnoreCase("N'SET FMTONLY ON SELECT * FROM [dbo].[insertbulk] '"));
    }

}
