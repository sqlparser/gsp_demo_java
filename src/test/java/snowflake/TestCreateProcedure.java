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
}
