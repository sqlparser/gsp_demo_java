package hive;
/*
 * Date: 13-8-16
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDropIndexSqlStatement;
import junit.framework.TestCase;

public class testDropIndex extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "DROP INDEX table02_index ON table02;";
        assertTrue(sqlparser.parse() == 0);

        TDropIndexSqlStatement dropIndex = (TDropIndexSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(dropIndex.getIndexName().toString().equalsIgnoreCase("table02_index"));
        assertTrue(dropIndex.getTableName().toString().equalsIgnoreCase("table02"));
    }
}
