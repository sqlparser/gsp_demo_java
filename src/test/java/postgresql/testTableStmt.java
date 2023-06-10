package postgresql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.postgresql.TPostgresqlTableStmt;
import junit.framework.TestCase;

public class testTableStmt extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "table ab;";
        assertTrue(sqlparser.parse() == 0);

        TPostgresqlTableStmt stmt = (TPostgresqlTableStmt)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getTableName().equalsIgnoreCase("ab"));
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "table only ab;";
        assertTrue(sqlparser.parse() == 0);

        TPostgresqlTableStmt stmt = (TPostgresqlTableStmt)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getTableName().equalsIgnoreCase("ab"));
    }

}
