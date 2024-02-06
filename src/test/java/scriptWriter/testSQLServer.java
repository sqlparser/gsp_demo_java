package scriptWriter;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testSQLServer extends TestCase
{
    public void test1( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvmssql);
        sqlparser.sqltext = "SELECT TOP(?) * FROM sys.dm_exec_connections c";

        sqlparser.parse( );

        // System.out.println(sqlparser.sqlstatements.get(1).toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvsnowflake, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
    }
}
