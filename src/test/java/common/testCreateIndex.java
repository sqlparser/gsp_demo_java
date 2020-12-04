package common;
/*
 * Date: 12-1-19
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EIndexType;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TOrderByItemList;
import gudusoft.gsqlparser.stmt.TCreateIndexSqlStatement;
import junit.framework.TestCase;

public class testCreateIndex extends TestCase {

    public void testOracle(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE unique INDEX ord_customer_ix_demo ON orders (customer_id, sales_rep_id asc)";
        assertTrue(sqlparser.parse() == 0);

        TCreateIndexSqlStatement indexSqlStatement = (TCreateIndexSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(indexSqlStatement.getIndexType() == EIndexType.itUnique);
        assertTrue(indexSqlStatement.getIndexName().toString().equalsIgnoreCase("ord_customer_ix_demo"));
        assertTrue(indexSqlStatement.getTableName().toString().equalsIgnoreCase("orders"));
        TOrderByItemList list = indexSqlStatement.getColumnNameList();
        assertTrue(list.getOrderByItem(0).getSortKey().toString().equalsIgnoreCase("customer_id"));
        assertTrue(list.getOrderByItem(0).getSortType() == TBaseType.srtNone); //srtAsc,srtNone,srtDesc
        assertTrue(list.getOrderByItem(1).getSortKey().toString().equalsIgnoreCase("sales_rep_id"));
        assertTrue(list.getOrderByItem(1).getSortType() == TBaseType.srtAsc); //srtNone,srtDesc

    }

    public void testMySQL(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "CREATE fulltext INDEX id_index USING BTREE ON lookup (id);";
        assertTrue(sqlparser.parse() == 0);

        TCreateIndexSqlStatement indexSqlStatement = (TCreateIndexSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(indexSqlStatement.getIndexType() == EIndexType.itFulltext);
        assertTrue(indexSqlStatement.getIndexName().toString().equalsIgnoreCase("id_index"));
        assertTrue(indexSqlStatement.getTableName().toString().equalsIgnoreCase("lookup"));
        TOrderByItemList list = indexSqlStatement.getColumnNameList();
        assertTrue(list.getOrderByItem(0).getSortKey().toString().equalsIgnoreCase("id"));
        assertTrue(list.getOrderByItem(0).getSortType() == TBaseType.srtNone); //srtAsc,srtNone,srtDesc

    }

    public void testMSSQL(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE UNIQUE CLUSTERED INDEX Idx1 ON t1(c);";
        assertTrue(sqlparser.parse() == 0);

        TCreateIndexSqlStatement indexSqlStatement = (TCreateIndexSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(indexSqlStatement.getIndexType() == EIndexType.itUnique);
        assertTrue(indexSqlStatement.getIndexName().toString().equalsIgnoreCase("Idx1"));
        assertTrue(indexSqlStatement.getTableName().toString().equalsIgnoreCase("t1"));
        TOrderByItemList list = indexSqlStatement.getColumnNameList();
        assertTrue(list.getOrderByItem(0).getSortKey().toString().equalsIgnoreCase("c"));
        assertTrue(list.getOrderByItem(0).getSortType() == TBaseType.srtNone); //srtAsc,srtNone,srtDesc

    }

    public void testTableRef(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "CREATE INDEX index_name ON hari (ke);";
        assertTrue(sqlparser.parse() == 0);

        TCreateIndexSqlStatement indexSqlStatement = (TCreateIndexSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(indexSqlStatement.getIndexName().toString().equalsIgnoreCase("index_name"));
        assertTrue(indexSqlStatement.tables.getTable(0).getTableName().toString().equalsIgnoreCase("hari"));
    }

}
