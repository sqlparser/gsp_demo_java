package common;
/*
 * Date: 2011-1-13
 * Time: 18:14:43
 */

import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TDropIndexItem;
import gudusoft.gsqlparser.stmt.TDropIndexSqlStatement;

public class testTDropIndexSqlStatement extends TestCase {

    public void testmssql1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "DROP INDEX IX_ProductVendor_BusinessEntityID \n" +
                "    ON Purchasing.ProductVendor;";
        assertTrue(sqlparser.parse() == 0);

        TDropIndexSqlStatement drop = (TDropIndexSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(drop.getDropIndexItemList().size() == 1);

        TDropIndexItem  item = drop.getDropIndexItemList().getDropIndexItem(0);
        assertTrue(item.getIndexName().toString().equalsIgnoreCase("IX_ProductVendor_BusinessEntityID"));
        assertTrue(item.getObjectName().toString().equalsIgnoreCase("Purchasing.ProductVendor"));
    }
    
    public void testmysql1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "DROP INDEX `PRIMARY` ON t;";
        assertTrue(sqlparser.parse() == 0);

        TDropIndexSqlStatement drop = (TDropIndexSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(drop.getIndexName().toString().equalsIgnoreCase("`PRIMARY`"));
        assertTrue(drop.getTableName().toString().equalsIgnoreCase("t"));
       // assertTrue(item.getIndexName().toString().equalsIgnoreCase("IX_ProductVendor_BusinessEntityID"));
       // assertTrue(item.getObjectName().toString().equalsIgnoreCase("Purchasing.ProductVendor"));
    }
}
