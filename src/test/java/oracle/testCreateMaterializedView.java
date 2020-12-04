package oracle;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.oracle.*;

import gudusoft.gsqlparser.stmt.TCreateMaterializedSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testCreateMaterializedView extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE MATERIALIZED VIEW sales_mv\n" +
                "   BUILD IMMEDIATE\n" +
                "   REFRESH FAST ON COMMIT\n" +
                "   AS SELECT t.calendar_year, p.prod_id, \n" +
                "      SUM(s.amount_sold) AS sum_sales\n" +
                "      FROM times t, products p, sales s\n" +
                "      WHERE t.time_id = s.time_id AND p.prod_id = s.prod_id\n" +
                "      GROUP BY t.calendar_year, p.prod_id;";
        assertTrue(sqlparser.parse() == 0);

        TCreateMaterializedSqlStatement createMaterializedViewStmt = (TCreateMaterializedSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createMaterializedViewStmt.getViewName().toString().equalsIgnoreCase("sales_mv"));
        TSelectSqlStatement subquery = createMaterializedViewStmt.getSubquery();
        assertTrue(subquery.getTables().getTable(0).toString().equalsIgnoreCase("times"));
        TMaterializedViewProps materializedViewProps  = createMaterializedViewStmt.getMaterializedViewProps();
        TOracleBuildClause buildClause = materializedViewProps.getBuildClause();
        assertTrue(buildClause.getBuildType() == EOracleBuildType.obtImmediate);
        TOracleCreateMvRefresh mvRefresh = createMaterializedViewStmt.getMvRefresh();
        assertTrue(mvRefresh.getRefreshOptions().size() == 2);
        TOracleCreateMvRefreshOption refreshOption = mvRefresh.getRefreshOptions().getElement(0);
        assertTrue(refreshOption.getRefreshType() == EMvRefreshType.mrtRefreshFast);
        refreshOption = mvRefresh.getRefreshOptions().getElement(1);
        assertTrue(refreshOption.getRefreshType() == EMvRefreshType.mrtRefreshOnCommit);

    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE MATERIALIZED VIEW rule15004_1(c1, c2) AS SELECT * FROM t1;";
        assertTrue(sqlparser.parse() == 0);

        TCreateMaterializedSqlStatement createMaterializedViewStmt = (TCreateMaterializedSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createMaterializedViewStmt.getViewName().toString().equalsIgnoreCase("rule15004_1"));
        TSelectSqlStatement subquery = createMaterializedViewStmt.getSubquery();
        assertTrue(subquery.getTables().getTable(0).toString().equalsIgnoreCase("t1"));
        assertTrue(createMaterializedViewStmt.getViewAliasClause().getViewAliasItemList().getViewAliasItem(0).toString().equalsIgnoreCase("c1"));
    }
}

