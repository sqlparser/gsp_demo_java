package hive;
/*
 * Date: 13-8-16
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TOrderByItemList;
import gudusoft.gsqlparser.nodes.hive.THiveIndexProperties;
import gudusoft.gsqlparser.nodes.hive.THiveTableProperties;
import gudusoft.gsqlparser.stmt.TCreateIndexSqlStatement;
import junit.framework.TestCase;

public class testCreateIndex extends TestCase {


    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "CREATE INDEX table02_index ON TABLE table02 (column3) AS 'COMPACT' WITH DEFERRED REBUILD;";
        assertTrue(sqlparser.parse() == 0);

        TCreateIndexSqlStatement createIndex = (TCreateIndexSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createIndex.getIndexName().toString().equalsIgnoreCase("table02_index"));
        assertTrue(createIndex.getTableName().toString().equalsIgnoreCase("table02"));
        TOrderByItemList viewColumns = createIndex.getColumnNameList();
        assertTrue(viewColumns.size() == 1);
        assertTrue(viewColumns.getOrderByItem(0).getSortKey().toString().equalsIgnoreCase("column3"));
        assertTrue(createIndex.getAsTypeName().toString().equalsIgnoreCase("'COMPACT'"));
        assertTrue(createIndex.isDeferredRebuildIndex());
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "CREATE INDEX table08_index ON TABLE table08 (column9) AS 'COMPACT' TBLPROPERTIES (\"prop3\"=\"value3\", \"prop4\"=\"value4\");";
        assertTrue(sqlparser.parse() == 0);

        TCreateIndexSqlStatement createIndex = (TCreateIndexSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createIndex.getIndexName().toString().equalsIgnoreCase("table08_index"));
        assertTrue(createIndex.getTableName().toString().equalsIgnoreCase("table08"));
        TOrderByItemList viewColumns = createIndex.getColumnNameList();
        assertTrue(viewColumns.size() == 1);
        assertTrue(viewColumns.getOrderByItem(0).getSortKey().toString().equalsIgnoreCase("column9"));
        assertTrue(createIndex.getAsTypeName().toString().equalsIgnoreCase("'COMPACT'"));
        assertTrue(!createIndex.isDeferredRebuildIndex());

        THiveIndexProperties indexProperties = createIndex.getIndexProperties();
        assertTrue(indexProperties == null);

        THiveTableProperties tableProperties = createIndex.getTableProperties();
        assertTrue(tableProperties.getTableProperties().size() == 2);
    }

}
