package scriptWriter;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testSnowflake extends TestCase
{
    public void testCreateTableBeforeSelect( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "select a.PayerCD, a.ClaimID, a.MemberID, a.PreviousDate\n" +
                "from Claims as a\n" +
                "order by a.PreviousDate NULLS FIRST";

        sqlparser.parse( );

        // System.out.println(sqlparser.sqlstatements.get(1).toScript());
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvsnowflake, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
    }
}
