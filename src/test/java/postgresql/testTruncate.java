package postgresql;
/*
 * Date: 13-12-4
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TTruncateStatement;
import junit.framework.TestCase;

public class testTruncate extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "TRUNCATE bigtable, fattable;";
        assertTrue(sqlparser.parse() == 0);

        TTruncateStatement stmt = (TTruncateStatement)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.joins.size() == 2);
        assertTrue(stmt.joins.getJoin(0).getTable().toString().equalsIgnoreCase("bigtable"));
        assertTrue(stmt.joins.getJoin(1).getTable().toString().equalsIgnoreCase("fattable"));
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "TRUNCATE bigtable, fattable RESTART IDENTITY;";
        assertTrue(sqlparser.parse() == 0);

        TTruncateStatement stmt = (TTruncateStatement)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.joins.size() == 2);
        assertTrue(stmt.joins.getJoin(0).getTable().toString().equalsIgnoreCase("bigtable"));
        assertTrue(stmt.joins.getJoin(1).getTable().toString().equalsIgnoreCase("fattable"));
    }

}
