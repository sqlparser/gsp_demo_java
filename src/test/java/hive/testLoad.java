package hive;
/*
 * Date: 13-8-15
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TPartitionExtensionClause;
import gudusoft.gsqlparser.stmt.hive.THiveLoad;
import junit.framework.TestCase;

public class testLoad extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "LOAD DATA LOCAL INPATH  '/tmp/simple.json' INTO TABLE json_table;";
        assertTrue(sqlparser.parse() == 0);

        THiveLoad load = (THiveLoad)sqlparser.sqlstatements.get(0);
        assertTrue(load.isIslocal());
        assertTrue(!load.isIsoverwrite());
        assertTrue(load.getPath().toString().equalsIgnoreCase("'/tmp/simple.json'"));
        assertTrue(load.getTable().toString().equalsIgnoreCase("json_table"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "LOAD DATA LOCAL INPATH '/tmp/pv_2008-06-08_us.txt' INTO TABLE page_view PARTITION(date='2008-06-08', country='US');";
        assertTrue(sqlparser.parse() == 0);

        THiveLoad load = (THiveLoad)sqlparser.sqlstatements.get(0);
        assertTrue(load.isIslocal());
        assertTrue(!load.isIsoverwrite());
        assertTrue(load.getPath().toString().equalsIgnoreCase("'/tmp/pv_2008-06-08_us.txt'"));
        assertTrue(load.getTable().getTableName().toString().equalsIgnoreCase("page_view"));
        TPartitionExtensionClause p = load.getTable().getPartitionExtensionClause();
        assertTrue(p.getKeyValues().size() == 2);
        assertTrue(p.getKeyValues().getExpression(0).getLeftOperand().toString().equalsIgnoreCase("date"));
        assertTrue(p.getKeyValues().getExpression(0).getRightOperand().toString().equalsIgnoreCase("'2008-06-08'"));
        assertTrue(p.getKeyValues().getExpression(1).getLeftOperand().toString().equalsIgnoreCase("country"));
        assertTrue(p.getKeyValues().getExpression(1).getRightOperand().toString().equalsIgnoreCase("'US'"));
    }

    public void test3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "LOAD DATA LOCAL INPATH /tmp/pv_2008-06-08_us.txt INTO TABLE page_view PARTITION(date='2008-06-08', country='US')";
        assertTrue(sqlparser.parse() == 0);

        THiveLoad load = (THiveLoad)sqlparser.sqlstatements.get(0);
        assertTrue(load.isIslocal());
        assertTrue(!load.isIsoverwrite());
        assertTrue(load.getPath().toString().trim().equalsIgnoreCase("/tmp/pv_2008-06-08_us.txt"));
        assertTrue(load.getTable().getTableName().toString().equalsIgnoreCase("page_view"));
       // System.out.println(load.getPath().toString());
    }
}
