package common;
/*
 * Date: 2010-8-31
 * Time: 15:49:17
 */

import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.stmt.mssql.TMssqlRaiserror;

public class testMssqlRaiserror extends TestCase {
    private TGSqlParser parser = null;

    protected void setUp() throws Exception {
        super.setUp();
        parser = new TGSqlParser(EDbVendor.dbvmssql);
    }

    protected void tearDown() throws Exception {
        parser = null;
        super.tearDown();
    }

    public void test1(){
        parser.sqltext = "RAISERROR (N'This is message %s %d.', -- Message text.\n" +
                "           10, -- Severity,\n" +
                "           1, -- State,\n" +
                "           N'number', -- First argument.\n" +
                "           5); -- Second argument." ;
        assertTrue(parser.parse() == 0);

        TMssqlRaiserror stmt = (TMssqlRaiserror)parser.sqlstatements.get(0);
        assertTrue(stmt.getMessageText().toString().equalsIgnoreCase("N'This is message %s %d.'"));
        assertTrue(stmt.getSeverity().toString().equalsIgnoreCase("10"));
        assertTrue(stmt.getState().toString().equalsIgnoreCase("1"));
        assertTrue(stmt.getArgs().size() == 2);
        assertTrue(stmt.getArgs().getExpression(0).toString().equalsIgnoreCase("N'number'") );
        assertTrue(stmt.getArgs().getExpression(1).toString().equalsIgnoreCase("5") );
    }
}
