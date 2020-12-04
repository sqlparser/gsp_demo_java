package mysql;
/*
 * Date: 13-3-26
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDescribeStmt;
import junit.framework.TestCase;

public class testDescribe extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "describe table1";
        assertTrue(sqlparser.parse() == 0);

        TDescribeStmt stmt = (TDescribeStmt)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getTableName().toString().equalsIgnoreCase("table1"));
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "describe menagerie.pet";
        assertTrue(sqlparser.parse() == 0);

        TDescribeStmt stmt = (TDescribeStmt)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getTableName().toString().equalsIgnoreCase("menagerie.pet"));
        assertTrue(stmt.getTableName().getSchemaString().equalsIgnoreCase("menagerie"));
        assertTrue(stmt.getTableName().getTableString().equalsIgnoreCase("pet"));
    }
}
