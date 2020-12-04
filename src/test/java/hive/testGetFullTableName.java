package hive;
/*
 * Date: 14-1-29
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testGetFullTableName extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "INSERT OVERWRITE LOCAL DIRECTORY '/tmp/ttt' SELECT * from (select * from a) c";
          assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insertSqlStatement = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        TSelectSqlStatement select = insertSqlStatement.getSubQuery();
        TTable table  = select.tables.getTable(0);
       // System.out.println(table.getFullName());
    }

}
