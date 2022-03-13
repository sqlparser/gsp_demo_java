package postgresql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.*;
import junit.framework.TestCase;

public class testSchema extends TestCase {

    public void testCreateSchema() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE SCHEMA sqlflow;";
        assertTrue(sqlparser.parse() == 0);

        TCreateSchemaSqlStatement createSchemaSqlStatement = (TCreateSchemaSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(createSchemaSqlStatement.getSchemaName().toString().equalsIgnoreCase("sqlflow"));
    }

    public void testAlterSchema() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "ALTER SCHEMA sqlflow OWNER TO bigking;";
        assertTrue(sqlparser.parse() == 0);

        TAlterSchemaStmt alterSchema = (TAlterSchemaStmt) sqlparser.sqlstatements.get(0);
        assertTrue(alterSchema.getSchemaName().toString().equalsIgnoreCase("sqlflow"));
        assertTrue(alterSchema.getOwnerName().toString().equalsIgnoreCase("bigking"));
    }

}