package presto;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import junit.framework.TestCase;

public class testCreateTableLike extends TestCase {

    public void testSelect() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpresto);
        sqlparser.sqltext = "CREATE TABLE bigger_orders (\n" +
                "  another_orderkey bigint,\n" +
                "  LIKE orders,\n" +
                "  another_orderdate date\n" +
                ");";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatetable);
        TCreateTableSqlStatement createTableSqlStatement = (TCreateTableSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createTableSqlStatement.getColumnList().size() == 2);
        assertTrue(createTableSqlStatement.getColumnList().getColumn(0).getColumnName().toString().equalsIgnoreCase("another_orderkey"));
        assertTrue(createTableSqlStatement.getColumnList().getColumn(1).getColumnName().toString().equalsIgnoreCase("another_orderdate"));
        assertTrue(createTableSqlStatement.getLikeTableName().toString().equalsIgnoreCase("orders"));
    }

}
