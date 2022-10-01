package teradata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TAlterProcedureStmt;
import junit.framework.TestCase;


public class testAlterProcedure extends TestCase {

    public void testPrepare(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "ALTER PROCEDURE spa_tz COMPILE AT TIME ZONE LOCAL;";
        assertTrue(sqlparser.parse() == 0);

        TAlterProcedureStmt cp = (TAlterProcedureStmt)sqlparser.sqlstatements.get(0);
        assertTrue(cp.getProcedureName().toString().equalsIgnoreCase("spa_tz"));

    }
}
