package postgresql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TReindexStmt;
import junit.framework.TestCase;

public class testReindex extends TestCase {
    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "REINDEX INDEX my_index";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstReindex);
        TReindexStmt reindexStmt = (TReindexStmt) sqlparser.sqlstatements.get(0);
        assertTrue(reindexStmt.getIndexName().toString().equalsIgnoreCase("my_index"));
    }

    public void test2() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "REINDEX TABLE CONCURRENTLY my_broken_table;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstReindex);
        TReindexStmt reindexStmt = (TReindexStmt) sqlparser.sqlstatements.get(0);
        assertTrue(reindexStmt.getIndexName().toString().equalsIgnoreCase("my_broken_table"));
    }

}
