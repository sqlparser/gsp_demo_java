package gudusoft.gsqlparser.oracleTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.nodes.oracle.TListaggOverflow;
import junit.framework.TestCase;

public class testListAggOverflow extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "SELECT department_id \"Dept.\",\n" +
                "       LISTAGG(last_name, '; ' ON OVERFLOW TRUNCATE '...')\n" +
                "               WITHIN GROUP (ORDER BY hire_date) \"Employees\"\n" +
                "  FROM employees";
        assertTrue(sqlparser.parse() == 0);

        functionVisitor fv = new functionVisitor();
        sqlparser.sqlstatements.get(0).acceptChildren(fv);

    }

    class functionVisitor extends TParseTreeVisitor {

        public void preVisit(TFunctionCall functionCall) {
            if (functionCall.getFunctionName().toString().equalsIgnoreCase("LISTAGG")) {
                TListaggOverflow listaggOverflow = functionCall.getListaggOverflow();
                assertTrue(listaggOverflow.getOn_overflow_type() == TListaggOverflow.ON_OVERFLOW_TRUNCATE);
                assertTrue(listaggOverflow.getTruncation_indicator().toString().equalsIgnoreCase("'...'"));
                assertTrue(!listaggOverflow.isWithCount());
                assertTrue(!listaggOverflow.isWithoutCount());

            }
        }
    }
}
