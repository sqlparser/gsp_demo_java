package snowflake;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.snowflake.TAlterTaskStmt;
import junit.framework.TestCase;

public class testAlterTask extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "ALTER TASK B RESUME;";
        assertTrue(sqlparser.parse() == 0);

        TAlterTaskStmt alterTask = (TAlterTaskStmt)sqlparser.sqlstatements.get(0);
        assertTrue(alterTask.getTaskName().toString().equalsIgnoreCase("B"));

    }
}
