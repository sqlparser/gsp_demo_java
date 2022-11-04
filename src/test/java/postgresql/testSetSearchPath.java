package postgresql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESetStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSetStmt;
import junit.framework.TestCase;

public class testSetSearchPath extends TestCase {

    public void testCreateSchema() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "SET search_path = sqlflow, pg_catalog;";
        assertTrue(sqlparser.parse() == 0);

        TSetStmt setStmt = (TSetStmt)sqlparser.sqlstatements.get(0);
        assertTrue(setStmt.getSetStatementType() == ESetStatementType.variable);
        assertTrue(setStmt.getVariableName().toString().equalsIgnoreCase("search_path"));
        assertTrue(setStmt.getVariableValueList().getExpression(0).toString().equalsIgnoreCase("sqlflow"));
        assertTrue(setStmt.getVariableValueList().getExpression(1).toString().equalsIgnoreCase("pg_catalog"));
    }
}
