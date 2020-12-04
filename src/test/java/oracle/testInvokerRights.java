package oracle;
/*
 * Date: 13-2-8
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateFunction;
import junit.framework.TestCase;

public class testInvokerRights extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE OR REPLACE FUNCTION RULE13014TESTFUNC1( A1 IN NUMBER )\n" +
                "RETURN NUMBER AUTHID CURRENT_USER\n" +
                "AS\n" +
                "BEGIN\n" +
                "NULL;\n" +
                "END;\n" +
                "/";
        assertTrue(sqlparser.parse() == 0);

        TPlsqlCreateFunction function =   (TPlsqlCreateFunction)sqlparser.sqlstatements.get(0);
        assertTrue(function.getInvokerRightsClause().getDefiner().toString().equalsIgnoreCase("CURRENT_USER"));

    }
}
