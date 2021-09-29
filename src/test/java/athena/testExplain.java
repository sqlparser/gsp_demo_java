package athena;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TExplainPlan;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testExplain extends TestCase {

    public void testSelect() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvathena);
        sqlparser.sqltext = "EXPLAIN (FORMAT GRAPHVIZ)\n" +
                "SELECT \n" +
                "      c.c_custkey,\n" +
                "      o.o_orderkey,\n" +
                "      o.o_orderstatus\n" +
                "   FROM tpch100.customer c \n" +
                "   JOIN tpch100.orders o \n" +
                "       ON c.c_custkey = o.o_custkey \n" +
                "   WHERE c.c_custkey = 5566684;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstExplain);
        TExplainPlan explainPlan = (TExplainPlan) sqlparser.sqlstatements.get(0);
        assertTrue(explainPlan.getStatement().sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement) explainPlan.getStatement();
        assertTrue(selectSqlStatement.getTables().getTable(0).getTableName().toString().equalsIgnoreCase("tpch100.customer"));
    }
}
