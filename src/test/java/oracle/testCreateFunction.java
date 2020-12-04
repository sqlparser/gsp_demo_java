package oracle;
/*
 * Date: 13-3-27
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.oracle.TPlsqlCreateFunction;
import junit.framework.TestCase;

public class testCreateFunction extends TestCase {

    public void testAggregate(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE FUNCTION SecondMax (input NUMBER) RETURN NUMBER\n" +
                "    PARALLEL_ENABLE AGGREGATE USING SecondMaxImpl;";
        assertTrue(sqlparser.parse() == 0);

        TPlsqlCreateFunction f = (TPlsqlCreateFunction)sqlparser.sqlstatements.get(0);
        assertTrue(f.getFunctionName().toString().equalsIgnoreCase("SecondMax"));
        assertTrue(f.getImplementionType().toString().equalsIgnoreCase("SecondMaxImpl"));

    }

    public void testImplementionType(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "create or replace function \"USER1\".\"FUNC1\" ( \"I1\" in \"T1\".\"C1\"%TYPE )\n" +
                "return INTEGER\n" +
                "aggregate using \"T1\";";
        assertTrue(sqlparser.parse() == 0);

        TPlsqlCreateFunction f = (TPlsqlCreateFunction)sqlparser.sqlstatements.get(0);
        assertTrue(f.getFunctionName().toString().equalsIgnoreCase("\"USER1\".\"FUNC1\""));
        assertTrue(f.getImplementionType().toString().equalsIgnoreCase("\"T1\""));

    }
}
