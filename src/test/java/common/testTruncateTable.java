package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.stmt.TTruncateStatement;
import junit.framework.TestCase;

public class testTruncateTable extends TestCase {

    public void testOracleTableSchema(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "truncate table SCH.TBL";
        assertTrue(sqlparser.parse() == 0);

        TTruncateStatement ts = (TTruncateStatement)sqlparser.sqlstatements.get(0);
        TObjectName tb = ts.tables.getTable(0).getTableName();
        assertTrue(tb.toString().equalsIgnoreCase("SCH.TBL"));
        assertTrue(tb.getSchemaString().equalsIgnoreCase("SCH"));
        assertTrue(tb.getObjectString().equalsIgnoreCase("TBL"));

    }


}
