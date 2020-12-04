package mysql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDropTableSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import junit.framework.TestCase;

public class testInsertIgnore extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "INSERT IGNORE INTO schema1.table1 (col1) VALUES('val1')";
        assertTrue(sqlparser.parse() == 0);
        TInsertSqlStatement insertSqlStatement  = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(insertSqlStatement.getIgnore().toString().equalsIgnoreCase("IGNORE"));
    }

}
