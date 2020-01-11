package mssql;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.mssql.TExecuteAsClause;
import gudusoft.gsqlparser.nodes.mssql.TProcedureOption;
import gudusoft.gsqlparser.stmt.mssql.TMssqlCreateProcedure;
import gudusoft.gsqlparser.stmt.mssql.TMssqlExecuteAs;
import junit.framework.TestCase;


public class testCreateProcedureOption extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE PROCEDURE dbo.usp_myproc \n" +
                "  WITH EXECUTE AS CALLER\n" +
                "AS \n" +
                "    SELECT SUSER_NAME(), USER_NAME();\n" +
                "    EXECUTE AS USER = 'guest';\n" +
                "    SELECT SUSER_NAME(), USER_NAME();\n" +
                "    REVERT;\n" +
                "    SELECT SUSER_NAME(), USER_NAME();\n" +
                "    DBCC CHECKIDENT (\"HumanResources.Employee\", RESEED, 30);";
        assertTrue(sqlparser.parse() == 0);

        TMssqlCreateProcedure createProcedure = (TMssqlCreateProcedure)sqlparser.sqlstatements.get(0);
        TProcedureOption option = createProcedure.getProcedureOptions().getElement(0);
        assertTrue(option.getOptionType() == EProcedureOptionType.potExecuteAs);
        TExecuteAsClause asClause = option.getExecuteAsClause();
        assertTrue(asClause.getExecuteAsOption() == EExecuteAsOption.eaoCaller);

        //System.out.println(createProcedure.getBodyStatements().size());
        assertTrue(createProcedure.getBodyStatements().get(1).sqlstatementtype == ESqlStatementType.sstmssqlexecuteas);
        TMssqlExecuteAs executeAs = (TMssqlExecuteAs)createProcedure.getBodyStatements().get(1);
        assertTrue(executeAs.getExecuteAsOption() == EExecuteAsOption.eaoUser);
        assertTrue(executeAs.getUserName().equalsIgnoreCase("'guest'"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "create proc dbo.outer\n" +
                "        @var1 nvarchar(255) = ''\n" +
                "        as\n" +
                "        set nocount on\n" +
                "        exec dbo.inner\n" +
                "        @var1";
        assertTrue(sqlparser.parse() == 0);

        TMssqlCreateProcedure createProcedure = (TMssqlCreateProcedure)sqlparser.sqlstatements.get(0);
        assertTrue(createProcedure.getProcedureName().toString().equalsIgnoreCase("dbo.outer"));

        assertTrue(createProcedure.getBodyStatements().size() == 2);
        //System.out.println(createProcedure.getBodyStatements().get(0).sqlstatementtype);
        assertTrue(createProcedure.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstmssqlset);
        assertTrue(createProcedure.getBodyStatements().get(1).sqlstatementtype == ESqlStatementType.sstmssqlexec);
    }


}
