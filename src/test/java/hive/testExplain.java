package test.hive;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TGroupBy;
import gudusoft.gsqlparser.nodes.hive.EHiveInsertType;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.hive.THiveExplain;
import gudusoft.gsqlparser.stmt.hive.THiveFromQuery;
import junit.framework.TestCase;


public class testExplain extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "EXPLAIN\n" +
                "FROM src INSERT OVERWRITE TABLE dest_g1 SELECT src.key, sum(substr(src.value,4)) GROUP BY src.key;";
        assertTrue(sqlparser.parse() == 0);

        THiveExplain explain = (THiveExplain)sqlparser.sqlstatements.get(0);
        assertTrue(explain.getStmt().sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement fromQuery = (TSelectSqlStatement)explain.getStmt();
        assertTrue(fromQuery.tables.getTable(0).getTableName().toString().equalsIgnoreCase("src"));
        assertTrue(fromQuery.getHiveBodyList().size() == 1);
        TInsertSqlStatement insert = (TInsertSqlStatement)fromQuery.getHiveBodyList().get(0);
        assertTrue(insert.getHiveInsertType() == EHiveInsertType.overwriteTable);
        assertTrue(insert.getTargetTable().getTableName().toString().equalsIgnoreCase("dest_g1"));
        TSelectSqlStatement select = (TSelectSqlStatement)insert.getSubQuery();
        TGroupBy groupBy = select.getGroupByClause();
        assertTrue(groupBy.getItems().getGroupByItem(0).getExpr().toString().equalsIgnoreCase("src.key"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "EXPLAIN DEPENDENCY\n" +
                "  SELECT key, count(1) FROM srcpart WHERE ds IS NOT NULL GROUP BY key;";
        assertTrue(sqlparser.parse() == 0);

        THiveExplain explain = (THiveExplain)sqlparser.sqlstatements.get(0);
        assertTrue(explain.getStmt().sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement select = (TSelectSqlStatement)explain.getStmt();
        assertTrue(select.getResultColumnList().size() == 2);
        assertTrue(select.tables.getTable(0).getTableName().toString().equalsIgnoreCase("srcpart"));
        assertTrue(select.getWhereClause().getCondition().toString().equalsIgnoreCase("ds IS NOT NULL"));
        TGroupBy groupBy = select.getGroupByClause();
        assertTrue(groupBy.getItems().getGroupByItem(0).getExpr().toString().equalsIgnoreCase("key"));
    }
}
