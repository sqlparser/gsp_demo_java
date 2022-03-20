package snowflake;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TAlterSchemaStmt;
import junit.framework.TestCase;

public class testAlterSchema extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "ALTER SCHEMA IF EXISTS something3 SWAP WITH something;";
        assertTrue(sqlparser.parse() == 0);

        TAlterSchemaStmt alterSchemaStmt = (TAlterSchemaStmt)sqlparser.sqlstatements.get(0);
        assertTrue(alterSchemaStmt.getAlterType() == TAlterSchemaStmt.AlterType.swapWith);
        assertTrue(alterSchemaStmt.getSchemaName().toString().equalsIgnoreCase("something3"));
        assertTrue(alterSchemaStmt.getNewSchemaName().toString().equalsIgnoreCase("something"));
    }
}

