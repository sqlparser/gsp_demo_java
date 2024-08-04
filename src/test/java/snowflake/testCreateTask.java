package snowflake;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCallStatement;

import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.snowflake.TCreateTaskStmt;
import junit.framework.TestCase;

public class testCreateTask  extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE OR REPLACE TASK EDM_REFINED_DEV.DW_APPL.Archive_HCM_TOTALEE_R_TASK\n" +
                "WAREHOUSE = 'DEV_ENGINEER_WH'\n" +
                "SCHEDULE = '1 MINUTE'\n" +
                "WHEN\n" +
                "SYSTEM$STREAM_HAS_DATA('EDM_REFINED_DEV.DW_APPL.HCM_TOTALEE_SNOWFLAKE_STREAM_R')\n" +
                "AS\n" +
                "call EDM_REFINED_DEV.DW_APPL.sp_ARCHIVE_HCM_TOTALEE();";

        assertTrue(sqlparser.parse() == 0);

        TCreateTaskStmt createtaskStmt = (TCreateTaskStmt)sqlparser.sqlstatements.get(0);

        assertTrue(createtaskStmt.getTaskName().toString().equalsIgnoreCase("EDM_REFINED_DEV.DW_APPL.Archive_HCM_TOTALEE_R_TASK"));
        assertTrue(createtaskStmt.getSqlStatement().sqlstatementtype == ESqlStatementType.sstcall);
        TCallStatement callStatement = (TCallStatement) createtaskStmt.getSqlStatement();
        assertTrue(callStatement.getRoutineName().toString().equalsIgnoreCase("EDM_REFINED_DEV.DW_APPL.sp_ARCHIVE_HCM_TOTALEE"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE TASK t1\n" +
                "  SCHEDULE = 'USING CRON 0 9-17 * * SUN America/Los_Angeles'\n" +
                "  USER_TASK_MANAGED_INITIAL_WAREHOUSE_SIZE = 'XSMALL'\n" +
                "AS\n" +
                "SELECT CURRENT_TIMESTAMP;";
        assertTrue(sqlparser.parse() == 0);

        TCreateTaskStmt createtaskStmt = (TCreateTaskStmt)sqlparser.sqlstatements.get(0);

        assertTrue(createtaskStmt.getTaskName().toString().equalsIgnoreCase("t1"));
        assertTrue(createtaskStmt.getSqlStatement().sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement) createtaskStmt.getSqlStatement();
        assertTrue(selectSqlStatement.getResultColumnList().size() == 1);
    }

    public void test3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE TASK t1\n" +
                "  SCHEDULE = '60 MINUTE'\n" +
                "  TIMESTAMP_INPUT_FORMAT = 'YYYY-MM-DD HH24'\n" +
                "  USER_TASK_MANAGED_INITIAL_WAREHOUSE_SIZE = 'XSMALL'\n" +
                "AS\n" +
                "INSERT INTO mytable(ts) VALUES(CURRENT_TIMESTAMP);";

        assertTrue(sqlparser.parse() == 0);

        TCreateTaskStmt createtaskStmt = (TCreateTaskStmt)sqlparser.sqlstatements.get(0);

        assertTrue(createtaskStmt.getTaskName().toString().equalsIgnoreCase("t1"));
        assertTrue(createtaskStmt.getSqlStatement().sqlstatementtype == ESqlStatementType.sstinsert);
        TInsertSqlStatement insertSqlStatement = (TInsertSqlStatement) createtaskStmt.getSqlStatement();
        assertTrue(insertSqlStatement.getTargetTable().toString().equalsIgnoreCase("mytable"));
    }

    public void test4(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE OR REPLACE TASK DB.SCH.TASK_2\n" +
                "WAREHOUSE=WH1\n" +
                "AFTER TASK_1\n" +
                "AS SELECT 1;";

        assertTrue(sqlparser.parse() == 0);

        TCreateTaskStmt createtaskStmt = (TCreateTaskStmt)sqlparser.sqlstatements.get(0);

        assertTrue(createtaskStmt.getTaskName().toString().equalsIgnoreCase("DB.SCH.TASK_2"));
        assertTrue(createtaskStmt.getTaskOptionList().get(1).getAfter().getObjectName(0).getObjectToken().toString().equalsIgnoreCase("TASK_1"));

    }

    public void test5(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CREATE OR REPLACE TASK DB.SCH.TASK_2\n" +
                "WAREHOUSE=WH1\n" +
                "AFTER DB1.SCH1.TASK_1\n" +
                "AS SELECT 1;";

        assertTrue(sqlparser.parse() == 0);

        TCreateTaskStmt createtaskStmt = (TCreateTaskStmt)sqlparser.sqlstatements.get(0);

        assertTrue(createtaskStmt.getTaskName().toString().equalsIgnoreCase("DB.SCH.TASK_2"));
        assertTrue(createtaskStmt.getTaskOptionList().get(1).getAfter().getObjectName(0).getDatabaseToken().toString().equalsIgnoreCase("DB1"));
        assertTrue(createtaskStmt.getTaskOptionList().get(1).getAfter().getObjectName(0).getSchemaToken().toString().equalsIgnoreCase("SCH1"));
        assertTrue(createtaskStmt.getTaskOptionList().get(1).getAfter().getObjectName(0).getObjectToken().toString().equalsIgnoreCase("TASK_1"));

    }

}
