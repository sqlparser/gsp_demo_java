package snowflake;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.stmt.TCreateFunctionStmt;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.snowflake.TPseudoExprStmt;
import gudusoft.gsqlparser.stmt.snowflake.TSnowflakeCreateFunctionStmt;
import junit.framework.TestCase;


public class testCreateFunction extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE or replace FUNCTION function1() RETURNS \n" +
                "TABLE (SERIAL_NUM nvarchar, STATUS_CD nvarchar) \n" +
                "AS \n" +
                "'select SERIAL_NUM, STATUS_CD from s_asset';";
        assertTrue(sqlparser.parse() == 0);
        TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(0);
        assertTrue(sqlStatement.sqlstatementtype == ESqlStatementType.sstcreatefunction);
        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlStatement;
        assertTrue(createFunction.getFunctionDefinition().toString().equalsIgnoreCase("'select SERIAL_NUM, STATUS_CD from s_asset'"));
        assertTrue(createFunction.getBodyStatements().size() == 1);
        assertTrue(createFunction.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)createFunction.getBodyStatements().get(0);
        assertTrue(selectSqlStatement.getTables().getTable(0).toString().equalsIgnoreCase("s_asset"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create function profit()\n" +
                "  returns numeric(11, 2)\n" +
                "  as\n" +
                "  $$\n" +
                "    select sum((retail_price - wholesale_price) * number_sold) from purchases\n" +
                "  $$\n" +
                "  ;";
        assertTrue(sqlparser.parse() == 0);
        TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(0);
        assertTrue(sqlStatement.sqlstatementtype == ESqlStatementType.sstcreatefunction);
        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlStatement;

        assertTrue(createFunction.getBodyStatements().size() == 1);
        assertTrue(createFunction.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)createFunction.getBodyStatements().get(0);
        assertTrue(selectSqlStatement.getTables().getTable(0).toString().equalsIgnoreCase("purchases"));
    }


    public void testBodyExpr(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create function pi_udf()\n" +
                "  returns float\n" +
                "  as '3.141592654::FLOAT'\n" +
                "  ;";
        assertTrue(sqlparser.parse() == 0);
        TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(0);
        assertTrue(sqlStatement.sqlstatementtype == ESqlStatementType.sstcreatefunction);
        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlStatement;

        assertTrue(createFunction.getBodyStatements().size() == 1);
        assertTrue(createFunction.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstSnowflakePseudoExprStmt);
        TPseudoExprStmt exprStmt = (TPseudoExprStmt)createFunction.getBodyStatements().get(0);
        assertTrue(exprStmt.getExpr().toString().equalsIgnoreCase("3.141592654::FLOAT"));
        assertTrue(exprStmt.getExpr().getExpressionType() == EExpressionType.typecast_t);

    }

    public void testJavascript(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create or replace function js_factorial(d double)\n" +
                "  returns double\n" +
                "  language javascript\n" +
                "  strict\n" +
                "  as '\n" +
                "  if (D <= 0) {\n" +
                "    return 1;\n" +
                "  } else {\n" +
                "    var result = 1;\n" +
                "    for (var i = 2; i <= D; i++) {\n" +
                "      result = result * i;\n" +
                "    }\n" +
                "    return result;\n" +
                "  }\n" +
                "  ';" +
                "  ;";
        assertTrue(sqlparser.parse() == 0);
        TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(0);
        assertTrue(sqlStatement.sqlstatementtype == ESqlStatementType.sstcreatefunction);
        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlStatement;
        assertTrue(createFunction.getFunctionName().toString().equalsIgnoreCase("js_factorial"));
        assertTrue(createFunction.getRoutineLanguage().equalsIgnoreCase("javascript"));
        assertTrue(createFunction.getRoutineBody().equalsIgnoreCase("'\n" +
                "  if (D <= 0) {\n" +
                "    return 1;\n" +
                "  } else {\n" +
                "    var result = 1;\n" +
                "    for (var i = 2; i <= D; i++) {\n" +
                "      result = result * i;\n" +
                "    }\n" +
                "    return result;\n" +
                "  }\n" +
                "  '"));

    }

    public void testUnion(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create function simple_table_function ()\n" +
                "  returns table (x integer, y integer)\n" +
                "  as\n" +
                "  $$\n" +
                "    select 1, 2\n" +
                "    union all\n" +
                "    select 3, 4\n" +
                "  $$\n" +
                "  ;";
        assertTrue(sqlparser.parse() == 0);
        TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(0);
        assertTrue(sqlStatement.sqlstatementtype == ESqlStatementType.sstcreatefunction);
        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlStatement;

        assertTrue(createFunction.getBodyStatements().size() == 1);
        assertTrue(createFunction.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)createFunction.getBodyStatements().get(0);
        assertTrue(selectSqlStatement.getSetOperatorType() == ESetOperatorType.union);
       // assertTrue(selectSqlStatement.getTables().getTable(0).toString().equalsIgnoreCase("purchases"));
    }

    public void testLanguageSQL(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE OR REPLACE\n" +
                "FUNCTION UTIL_DB.PUBLIC.SFWHO() RETURNS TABLE (TS TIMESTAMP_LTZ(9), ACCOUNT VARCHAR(16777216)\n" +
                "\t, USER VARCHAR(16777216), ROLE VARCHAR(16777216), DATABASE VARCHAR(16777216), SCHEMA VARCHAR(16777216), WAREHOUSE VARCHAR(16777216)) LANGUAGE SQL AS 'select\n" +
                "  current_timestamp(),\n" +
                "  current_account(),\n" +
                "  current_user(),\n" +
                "  current_role(),\n" +
                "  current_database(),\n" +
                "  current_schema(),\n" +
                "  current_warehouse()\n" +
                "  ';";
        assertTrue(sqlparser.parse() == 0);
        TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(0);
        assertTrue(sqlStatement.sqlstatementtype == ESqlStatementType.sstcreatefunction);
        TCreateFunctionStmt createFunction = (TCreateFunctionStmt)sqlStatement;

        assertTrue(createFunction.getBodyStatements().size() == 1);
        assertTrue(createFunction.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)createFunction.getBodyStatements().get(0);
        assertTrue(selectSqlStatement.getResultColumnList().getResultColumn(0).toString().equalsIgnoreCase("current_timestamp()"));
    }

}