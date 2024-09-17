package gudusoft.gsqlparser.hanaTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TMultiTarget;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TUpsertStmt;
import junit.framework.TestCase;

public class testUpsert extends TestCase {
    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhana);
        sqlparser.sqltext = "UPSERT T VALUES (1, 1);";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstupsert);
        TUpsertStmt upsertStmt = (TUpsertStmt) sqlparser.sqlstatements.get(0);
        assertTrue(upsertStmt.getTargetTable().toString().equalsIgnoreCase("T"));
        TMultiTarget multiTarget = upsertStmt.getValues().getMultiTarget(0);
        TResultColumnList columnList = multiTarget.getColumnList();
        assertTrue(columnList.getResultColumn(0).toString().equalsIgnoreCase("1"));
        assertTrue(columnList.getResultColumn(1).toString().equalsIgnoreCase("1"));
    }

    public void test2() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhana);
        sqlparser.sqltext = "UPSERT T VALUES (1, 9) WHERE KEY = 1;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstupsert);
        TUpsertStmt upsertStmt = (TUpsertStmt) sqlparser.sqlstatements.get(0);
        assertTrue(upsertStmt.getTargetTable().toString().equalsIgnoreCase("T"));
        TMultiTarget multiTarget = upsertStmt.getValues().getMultiTarget(0);
        TResultColumnList columnList = multiTarget.getColumnList();
        assertTrue(columnList.getResultColumn(0).toString().equalsIgnoreCase("1"));
        assertTrue(columnList.getResultColumn(1).toString().equalsIgnoreCase("9"));
        assertTrue(upsertStmt.getWhereClause().getCondition().toString().equalsIgnoreCase("KEY = 1"));
    }

    public void test3() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhana);
        sqlparser.sqltext = "UPSERT T VALUES (1, 8) WITH PRIMARY KEY;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstupsert);
        TUpsertStmt upsertStmt = (TUpsertStmt) sqlparser.sqlstatements.get(0);
        assertTrue(upsertStmt.getTargetTable().toString().equalsIgnoreCase("T"));
        TMultiTarget multiTarget = upsertStmt.getValues().getMultiTarget(0);
        TResultColumnList columnList = multiTarget.getColumnList();
        assertTrue(columnList.getResultColumn(0).toString().equalsIgnoreCase("1"));
        assertTrue(columnList.getResultColumn(1).toString().equalsIgnoreCase("8"));
    }

    public void test4() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhana);
        sqlparser.sqltext = "UPSERT T SELECT KEY + 2, VAL FROM T;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstupsert);
        TUpsertStmt upsertStmt = (TUpsertStmt) sqlparser.sqlstatements.get(0);
        assertTrue(upsertStmt.getTargetTable().toString().equalsIgnoreCase("T"));
        TSelectSqlStatement select = upsertStmt.getSubQuery();
        assertTrue(select.getTables().getTable(0).toString().equalsIgnoreCase("T"));
    }

    public void test5() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhana);
        sqlparser.sqltext = "UPSERT T1 VALUES ( 1, ARRAY ( 21, 22, 23, 24 ) ) WHERE ID = 1;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstupsert);
        TUpsertStmt upsertStmt = (TUpsertStmt) sqlparser.sqlstatements.get(0);
        assertTrue(upsertStmt.getTargetTable().toString().equalsIgnoreCase("T1"));
    }

    public void test6() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhana);
        sqlparser.sqltext = "UPSERT T1 VALUES ( 1, ARRAY ( SELECT C1 FROM T0 ) ) WHERE ID = 1;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstupsert);
        TUpsertStmt upsertStmt = (TUpsertStmt) sqlparser.sqlstatements.get(0);
        assertTrue(upsertStmt.getTargetTable().toString().equalsIgnoreCase("T1"));
    }

}
