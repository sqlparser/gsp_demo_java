package scriptWriter;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testOracle extends TestCase
{
    public void testCreateTableBeforeSelect( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvoracle );
        sqlparser.sqltext = "create table tmp_db_hadr_dbrs (synchronization_state number);\n" +
                "SELECT dbrs.synchronization_state FROM tmp_db_hadr_dbrs dbrs";

        sqlparser.parse( );

       // System.out.println(sqlparser.sqlstatements.get(1).toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvoracle, sqlparser.sqlstatements.get(1).toString(), sqlparser.sqlstatements.get(1).toScript()));
    }
}
