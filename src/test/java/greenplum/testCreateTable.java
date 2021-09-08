package greenplum;
/*
 * Date: 13-12-26
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TExecuteSqlStatement;
import junit.framework.TestCase;

public class testCreateTable extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgreenplum);
        sqlparser.sqltext = "CREATE TEMP TABLE films_recent WITH (OIDS) ON COMMIT DROP AS \n" +
                "EXECUTE recentfilms('2007-01-01');";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        TExecuteSqlStatement executePreparedStatement = createTableSqlStatement.getExecutePreparedStatement();
        assertTrue(executePreparedStatement.getStatementName().toString().equalsIgnoreCase("recentfilms"));
        assertTrue(executePreparedStatement.getParameters().getExpression(0).toString().equalsIgnoreCase("'2007-01-01'"));

    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgreenplum);
        sqlparser.sqltext = "create table t_key_event_file_student_101 (like t_key_event_file_student)";
        assertTrue(sqlparser.parse() == 0);

        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTableSqlStatement.getTableName().toString().equalsIgnoreCase("t_key_event_file_student_101"));
        assertTrue(createTableSqlStatement.getLikeTableName().toString().equalsIgnoreCase("t_key_event_file_student"));
        //System.out.println(createTableSqlStatement.getLikeTableName().toString());

    }

}
