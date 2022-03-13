package postgresql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateSchemaSqlStatement;
import gudusoft.gsqlparser.stmt.postgresql.TSetSearchPathStmt;
import junit.framework.TestCase;

public class testSetSearchPath extends TestCase {

    public void testCreateSchema() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "SET search_path = sqlflow, pg_catalog;";
        assertTrue(sqlparser.parse() == 0);

        TSetSearchPathStmt searchPathStmt = (TSetSearchPathStmt) sqlparser.sqlstatements.get(0);
        assertTrue(searchPathStmt.getSearchPathList().getObjectName(0).toString().equalsIgnoreCase("sqlflow"));
        assertTrue(searchPathStmt.getSearchPathList().getObjectName(1).toString().equalsIgnoreCase("pg_catalog"));
    }
}
