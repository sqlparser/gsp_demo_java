package netezza;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDropSchemaSqlStatement;
import junit.framework.TestCase;

public class testDropSchema extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "drop schema temp_schema cascade;";
        assertTrue(sqlparser.parse() == 0);
        TDropSchemaSqlStatement dropSchemaSqlStatement = (TDropSchemaSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(dropSchemaSqlStatement.getSchemaName().toString().equalsIgnoreCase("temp_schema"));
        if (dropSchemaSqlStatement.getDropBehavior() != null){
            assertTrue(dropSchemaSqlStatement.getDropBehavior().toString().equalsIgnoreCase("cascade"));
        }

    }

}
