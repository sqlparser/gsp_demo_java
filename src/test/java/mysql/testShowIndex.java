package mysql;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.mysql.TShowIndexStmt;
import junit.framework.TestCase;

public class testShowIndex extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "SHOW INDEXES FROM pet WHERE key_name = \"test_index\";";
        assertTrue(sqlparser.parse() == 0);

        TShowIndexStmt showIndexStmt = (TShowIndexStmt)sqlparser.sqlstatements.get(0);
        assertTrue(showIndexStmt.getTableName().toString().equalsIgnoreCase("pet"));
        assertTrue(showIndexStmt.getWhereCondition().toString().equalsIgnoreCase("key_name = \"test_index\""));
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "SHOW INDEX FROM mytable FROM mydb";
        assertTrue(sqlparser.parse() == 0);

        TShowIndexStmt showIndexStmt = (TShowIndexStmt)sqlparser.sqlstatements.get(0);
        assertTrue(showIndexStmt.getTableName().toString().equalsIgnoreCase("mytable"));
        assertTrue(showIndexStmt.getDbName().toString().equalsIgnoreCase("mydb"));

    }

}
