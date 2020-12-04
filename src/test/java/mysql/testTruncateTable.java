package mysql;
/*
 * Date: 13-3-25
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TTruncateStatement;
import junit.framework.TestCase;

public class testTruncateTable extends TestCase {

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "truncate table a;";
        assertTrue(sqlparser.parse() == 0);

        TTruncateStatement statement = (TTruncateStatement)sqlparser.sqlstatements.get(0);
        assertTrue(statement.getTableName().toString().equalsIgnoreCase("a"));

        sqlparser.sqltext = "TRUNCATE  tbl_overridedescriptionrule;";
        assertTrue(sqlparser.parse() == 0);

        statement = (TTruncateStatement)sqlparser.sqlstatements.get(0);
        assertTrue(statement.getTableName().toString().equalsIgnoreCase("tbl_overridedescriptionrule"));
        //assertTrue(statement.getTargetTable().toString().equalsIgnoreCase("a"));
    }

}
