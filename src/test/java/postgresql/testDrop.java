package postgresql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDropMaterializedViewStmt;
import junit.framework.TestCase;

public class testDrop extends TestCase {

    public void testDropMaterializedView(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "DROP MATERIALIZED VIEW IF EXISTS my_materialized_view;";
        assertTrue(sqlparser.parse() == 0);

        TDropMaterializedViewStmt dropMaterializedViewStmt = (TDropMaterializedViewStmt)sqlparser.sqlstatements.get(0);
        assertTrue(dropMaterializedViewStmt.getViewName().toString().equalsIgnoreCase("my_materialized_view"));
    }
}

