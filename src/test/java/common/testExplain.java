package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;

import gudusoft.gsqlparser.nodes.TMultiTarget;
import gudusoft.gsqlparser.nodes.TMultiTargetList;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.nodes.TValueClause;
import gudusoft.gsqlparser.stmt.TExplainPlan;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;


public class testExplain extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvvertica);
        sqlparser.sqltext = "explain select a from t";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstExplain);
        TExplainPlan explainPlan = (TExplainPlan) sqlparser.sqlstatements.get(0);
        assertTrue(explainPlan.getStatement().sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement select = (TSelectSqlStatement)explainPlan.getStatement();
        assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("t"));
    }

    public void testMySQL(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "EXPLAIN EXTENDED SELECT 1 FROM t1 WHERE a = 18446744073709551615 AND TRIM(a) = b";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstExplain);
        TExplainPlan explainPlan = (TExplainPlan) sqlparser.sqlstatements.get(0);
        assertTrue(explainPlan.getStatement().sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement select = (TSelectSqlStatement)explainPlan.getStatement();
        assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("t1"));
    }

    public void testHana(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhana);
        sqlparser.sqltext = "EXPLAIN PLAN SET STATEMENT_NAME = 'TPC-H Q10' FOR\n" +
                "   SELECT TOP 20\n" +
                "      c_custkey,\n" +
                "      c_name,\n" +
                "      SUM(l_extendedprice * (1 - l_discount)) AS revenue,\n" +
                "      c_acctbal,\n" +
                "      n_name,\n" +
                "      c_address,\n" +
                "      c_phone,\n" +
                "      c_comment\n" +
                "      FROM\n" +
                "         customer,\n" +
                "         orders,\n" +
                "         lineitem,\n" +
                "         nation\n" +
                "      WHERE\n" +
                "         c_custkey = o_custkey\n" +
                "      AND l_orderkey = o_orderkey\n" +
                "      AND o_orderdate >= '1993-10-01'\n" +
                "      AND o_orderdate < ADD_MONTHS('1993-10-01',3)\n" +
                "      AND l_returnflag = 'R'\n" +
                "      AND c_nationkey = n_nationkey\n" +
                "      GROUP BY\n" +
                "         c_custkey,\n" +
                "         c_name,\n" +
                "         c_acctbal,\n" +
                "         c_phone,\n" +
                "         n_name,\n" +
                "         c_address,\n" +
                "         c_comment\n" +
                "      ORDER BY\n" +
                "         revenue DESC;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstExplain);
        TExplainPlan explainPlan = (TExplainPlan) sqlparser.sqlstatements.get(0);
        assertTrue(explainPlan.getStatement().sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement select = (TSelectSqlStatement)explainPlan.getStatement();
        assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("customer"));
    }

    public void testSparksql(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsparksql);
        sqlparser.sqltext = "EXPLAIN EXTENDED select k, sum(v) from values (1, 2), (1, 3), t(k, v) group by k;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstExplain);
        TExplainPlan explainPlan = (TExplainPlan) sqlparser.sqlstatements.get(0);
        assertTrue(explainPlan.getStatement().sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement select = (TSelectSqlStatement)explainPlan.getStatement();
        TValueClause valueClause = select.getTables().getTable(0).getValueClause();
        assertTrue(valueClause.getRows().size() == 3);
        TResultColumnList row = valueClause.getRows().get(0);
        assertTrue(row.size() == 2);
        assertTrue(row.getResultColumn(0).toString().equalsIgnoreCase("1"));
       // assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("t"));
    }
}