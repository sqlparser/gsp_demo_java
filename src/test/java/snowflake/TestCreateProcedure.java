package snowflake;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateProcedureStmt;
import junit.framework.TestCase;

public class TestCreateProcedure  extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE or replace PROCEDURE proc3()\n" +
                "  RETURNS VARCHAR\n" +
                "  LANGUAGE javascript\n" +
                "  AS\n" +
                "  $$\n" +
                "  var rs = snowflake.execute( { sqlText:\n" +
                "      `INSERT INTO table1 (\"column 1\")\n" +
                "           SELECT 'value 1' AS \"column 1\" ;`\n" +
                "       } );\n" +
                "  return 'Done.';\n" +
                "  $$;\n" +
                "  ;";
        //System.out.println(sqlparser.sqltext);

        assertTrue(sqlparser.parse() == 0);
        TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(0);
        assertTrue(sqlStatement.sqlstatementtype == ESqlStatementType.sstcreateprocedure);
        TCreateProcedureStmt createProcedure = (TCreateProcedureStmt)sqlStatement;
        assertTrue(createProcedure.getProcedureName().toString().equalsIgnoreCase("proc3"));
        assertTrue(createProcedure.getRoutineLanguage().equalsIgnoreCase("javascript"));
        assertTrue(createProcedure.getRoutineBody().equalsIgnoreCase("$$\n" +
                "  var rs = snowflake.execute( { sqlText:\n" +
                "      `INSERT INTO table1 (\"column 1\")\n" +
                "           SELECT 'value 1' AS \"column 1\" ;`\n" +
                "       } );\n" +
                "  return 'Done.';\n" +
                "  $$"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create or replace procedure get_row_count(table_name VARCHAR)\n" +
                "  returns float not null\n" +
                "  language javascript\n" +
                "  as\n" +
                "  $$\n" +
                "  var row_count = 0;\n" +
                "  // Dynamically compose the SQL statement to execute.\n" +
                "  var sql_command = \"select count(*) from \" + TABLE_NAME;\n" +
                "  // Run the statement.\n" +
                "  var stmt = snowflake.createStatement(\n" +
                "         {\n" +
                "         sqlText: sql_command\n" +
                "         }\n" +
                "      );\n" +
                "  var res = stmt.execute();\n" +
                "  // Get back the row count. Specifically, ...\n" +
                "  // ... get the first (and in this case only) row from the result set ...\n" +
                "  res.next();\n" +
                "  // ... and then get the returned value, which in this case is the number of\n" +
                "  // rows in the table.\n" +
                "  row_count = res.getColumnValue(1);\n" +
                "  return row_count;\n" +
                "  $$\n" +
                "  ;";
        assertTrue(sqlparser.parse() == 0);
        TCustomSqlStatement sqlStatement = sqlparser.sqlstatements.get(0);
        assertTrue(sqlStatement.sqlstatementtype == ESqlStatementType.sstcreateprocedure);
        TCreateProcedureStmt createProcedure = (TCreateProcedureStmt)sqlStatement;
        assertTrue(createProcedure.getProcedureName().toString().equalsIgnoreCase("get_row_count"));
        assertTrue(createProcedure.getParameterDeclarations().getParameterDeclarationItem(0).getParameterName().toString().equalsIgnoreCase("table_name"));
        assertTrue(createProcedure.getRoutineLanguage().equalsIgnoreCase("javascript"));
        assertTrue(createProcedure.getRoutineBody().equalsIgnoreCase("$$\n" +
                "  var row_count = 0;\n" +
                "  // Dynamically compose the SQL statement to execute.\n" +
                "  var sql_command = \"select count(*) from \" + TABLE_NAME;\n" +
                "  // Run the statement.\n" +
                "  var stmt = snowflake.createStatement(\n" +
                "         {\n" +
                "         sqlText: sql_command\n" +
                "         }\n" +
                "      );\n" +
                "  var res = stmt.execute();\n" +
                "  // Get back the row count. Specifically, ...\n" +
                "  // ... get the first (and in this case only) row from the result set ...\n" +
                "  res.next();\n" +
                "  // ... and then get the returned value, which in this case is the number of\n" +
                "  // rows in the table.\n" +
                "  row_count = res.getColumnValue(1);\n" +
                "  return row_count;\n" +
                "  $$"));
    }


}
