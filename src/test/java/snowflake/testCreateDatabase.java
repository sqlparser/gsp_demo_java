package snowflake;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateDatabaseSqlStatement;
import junit.framework.TestCase;

public class testCreateDatabase  extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create or replace database mydatabase;";
        assertTrue(sqlparser.parse() == 0);

        TCreateDatabaseSqlStatement createDatabaseSqlStatement = (TCreateDatabaseSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createDatabaseSqlStatement.getDatabaseName().toString().equalsIgnoreCase("mydatabase"));
    }

}
