package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TExplainPlan;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;


public class testExplain extends TestCase {

    public void testSelect() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "explain\n" +
                "select eventid, eventname, event.venueid, venuename\n" +
                "from event, venue\n" +
                "where event.venueid = venue.venueid;";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstExplain);
        TExplainPlan explainPlan = (TExplainPlan) sqlparser.sqlstatements.get(0);
        assertTrue(explainPlan.getStatement().sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement select = (TSelectSqlStatement)explainPlan.getStatement();
        assertTrue(select.joins.getJoin(0).getTable().toString().equalsIgnoreCase("event"));
        assertTrue(select.joins.getJoin(1).getTable().toString().equalsIgnoreCase("venue"));
    }

    public void testCreateTable() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "explain create table venue_nonulls as\n" +
                "select * from venue\n" +
                "where venueseats is not null;";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstExplain);
        TExplainPlan explainPlan = (TExplainPlan) sqlparser.sqlstatements.get(0);
        assertTrue(explainPlan.getStatement().sqlstatementtype == ESqlStatementType.sstcreatetable);
        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement)explainPlan.getStatement();
        assertTrue(createTable.getTableName().toString().equalsIgnoreCase("venue_nonulls"));
        TSelectSqlStatement select = createTable.getSubQuery();
        assertTrue(select.getWhereClause().getCondition().toString().equalsIgnoreCase("venueseats is not null"));
    }

}