package presto;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TExplainPlan;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testExplain extends TestCase {

    public void testSelect() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpresto);
        sqlparser.sqltext = "EXPLAIN ANALYZE SELECT count(*), clerk FROM orders WHERE orderdate > date '1995-01-01' GROUP BY clerk;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstExplain);
        TExplainPlan explainPlan = (TExplainPlan)sqlparser.sqlstatements.get(0);
        assertTrue(explainPlan.getStatement().sqlstatementtype  == ESqlStatementType.sstselect);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)explainPlan.getStatement();
        assertTrue(selectSqlStatement.getTables().getTable(0).toString().equalsIgnoreCase("orders"));
    }

    public void testSelect2() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpresto);
        sqlparser.sqltext = "EXPLAIN ANALYZE VERBOSE SELECT count(clerk) OVER() FROM orders WHERE orderdate > date '1995-01-01';";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstExplain);
        TExplainPlan explainPlan = (TExplainPlan)sqlparser.sqlstatements.get(0);
        assertTrue(explainPlan.getStatement().sqlstatementtype  == ESqlStatementType.sstselect);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)explainPlan.getStatement();
        assertTrue(selectSqlStatement.getTables().getTable(0).toString().equalsIgnoreCase("orders"));
    }

}
