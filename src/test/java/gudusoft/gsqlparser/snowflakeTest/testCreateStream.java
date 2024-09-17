package gudusoft.gsqlparser.snowflakeTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.snowflake.TCreateStreamStmt;
import junit.framework.TestCase;

public class testCreateStream extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create stream mystream on table mytable before (timestamp => to_timestamp(40*365*86400));";
        assertTrue(sqlparser.parse() == 0);

        TCreateStreamStmt createStreamStmt = (TCreateStreamStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createStreamStmt.getStreamName().toString().equalsIgnoreCase("mystream"));
        assertTrue(createStreamStmt.getTableName().toString().equalsIgnoreCase("mytable"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "create stream my_ext_table_stream on external table my_ext_table insert_only = true;";
        assertTrue(sqlparser.parse() == 0);

        TCreateStreamStmt createStreamStmt = (TCreateStreamStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createStreamStmt.getStreamName().toString().equalsIgnoreCase("my_ext_table_stream"));
        assertTrue(createStreamStmt.getTableName().toString().equalsIgnoreCase("my_ext_table"));
    }
}

