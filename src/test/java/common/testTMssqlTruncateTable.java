package common;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;


import gudusoft.gsqlparser.stmt.TTruncateStatement;
import junit.framework.TestCase;

public class testTMssqlTruncateTable extends TestCase {

    public void testTableName(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "TRUNCATE TABLE T1";
        assertTrue(sqlparser.parse() == 0);
        TTruncateStatement stmt = (TTruncateStatement)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getTableName().toString().equalsIgnoreCase("T1"));

    }

}
