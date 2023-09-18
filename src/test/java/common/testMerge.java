package common;
/*
 * Date: 11-7-28
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TMergeSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testMerge extends TestCase {

    public void testDB2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdb2);
        sqlparser.sqltext = "MERGE INTO archive ar\n" +
                "USING (SELECT activity,description FROM activities)ac\n" +
                "ON (ar.activity =ac.activity)\n" +
                "WHEN MATCHED THEN\n" +
                "UPDATE SET\n" +
                "description =ac.description\n" +
                "WHEN NOT MATCHED THEN\n" +
                "INSERT\n" +
                "(activity,description)\n" +
                "VALUES (ac.activity,ac.description);";


        assertTrue(sqlparser.parse() == 0);

        TMergeSqlStatement mergeSqlStatement = (TMergeSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(mergeSqlStatement.getTargetTable().toString().equalsIgnoreCase("archive"));
        assertTrue(mergeSqlStatement.getTargetTable().getAliasClause().toString().equalsIgnoreCase("ar"));
        TTable usingTable = mergeSqlStatement.getUsingTable();
        assertTrue(usingTable.getTableType() == ETableSource.subquery);
        assertTrue(usingTable.getAliasClause().toString().equalsIgnoreCase("ac"));

        assertTrue(mergeSqlStatement.getCondition().toString().equalsIgnoreCase("(ar.activity =ac.activity)"));

        assertTrue(mergeSqlStatement.getWhenClauses().size() == 2);
        TMergeWhenClause whenClause0 = mergeSqlStatement.getWhenClauses().getElement(0);
        TMergeWhenClause whenClause1 = mergeSqlStatement.getWhenClauses().getElement(1);
        assertTrue(whenClause0.getType() == TMergeWhenClause.matched);
        assertTrue(whenClause1.getType() == TMergeWhenClause.not_matched);

        TMergeUpdateClause mergeUpdateClause = whenClause0.getUpdateClause();
        assertTrue(mergeUpdateClause.getUpdateColumnList().getResultColumn(0).toString().equalsIgnoreCase("description =ac.description"));

        TMergeInsertClause mergeInsertClause = whenClause1.getInsertClause();

        assertTrue(mergeInsertClause.getColumnList().getObjectName(0).toString().equalsIgnoreCase("activity"));
        assertTrue(mergeInsertClause.getColumnList().getObjectName(1).toString().equalsIgnoreCase("description"));

        assertTrue(mergeInsertClause.getValuelist().getResultColumn(0).toString().equalsIgnoreCase("ac.activity"));
        assertTrue(mergeInsertClause.getValuelist().getResultColumn(1).toString().equalsIgnoreCase("ac.description"));

    }

    public void testSQLServer(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "MERGE INTO Sales.SalesReason AS Target\n" +
                "USING (VALUES ('Recommendation','Other'), ('Review', 'Marketing'), ('Internet', 'Promotion'))\n" +
                "       AS Source (NewName, NewReasonType)\n" +
                "ON Target.Name = Source.NewName\n" +
                "WHEN MATCHED THEN\n" +
                "\tUPDATE SET ReasonType = Source.NewReasonType\n" +
                "WHEN NOT MATCHED BY TARGET THEN\n" +
                "\tINSERT (Name, ReasonType) VALUES (NewName, NewReasonType)\n" +
                "OUTPUT $action INTO @SummaryOfChanges;";

        assertTrue(sqlparser.parse() == 0);

        TMergeSqlStatement mergeSqlStatement = (TMergeSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(mergeSqlStatement.getTargetTable().toString().equalsIgnoreCase("Sales.SalesReason"));
        assertTrue(mergeSqlStatement.getTargetTable().getAliasClause().toString().equalsIgnoreCase("Target"));
        TTable usingTable = mergeSqlStatement.getUsingTable();
        assertTrue(usingTable.getTableType() == ETableSource.rowList);
        assertTrue(usingTable.getValueClause().getRows().size() == 3);
        assertTrue(usingTable.getValueClause().getRows().get(0).getResultColumn(0).toString().equalsIgnoreCase("'Recommendation'"));
        assertTrue(usingTable.getValueClause().getRows().get(0).getResultColumn(1).toString().equalsIgnoreCase("'Other'"));
        assertTrue(usingTable.getValueClause().getRows().get(1).getResultColumn(0).toString().equalsIgnoreCase("'Review'"));
        assertTrue(usingTable.getValueClause().getRows().get(1).getResultColumn(1).toString().equalsIgnoreCase("'Marketing'"));
        assertTrue(usingTable.getValueClause().getRows().get(2).getResultColumn(0).toString().equalsIgnoreCase("'Internet'"));
        assertTrue(usingTable.getValueClause().getRows().get(2).getResultColumn(1).toString().equalsIgnoreCase("'Promotion'"));

        assertTrue(usingTable.getAliasClause().toString().equalsIgnoreCase("Source (NewName, NewReasonType)"));
        assertTrue(usingTable.getAliasClause().getAliasName().toString().equalsIgnoreCase("Source"));
        assertTrue(usingTable.getAliasClause().getColumns().getObjectName(0).toString().equalsIgnoreCase("NewName"));
        assertTrue(usingTable.getAliasClause().getColumns().getObjectName(1).toString().equalsIgnoreCase("NewReasonType"));

        assertTrue(mergeSqlStatement.getCondition().toString().equalsIgnoreCase("Target.Name = Source.NewName"));

        assertTrue(mergeSqlStatement.getWhenClauses().size() == 2);
        TMergeWhenClause whenClause0 = mergeSqlStatement.getWhenClauses().getElement(0);
        TMergeWhenClause whenClause1 = mergeSqlStatement.getWhenClauses().getElement(1);
        assertTrue(whenClause0.getType() == TMergeWhenClause.matched);
        assertTrue(whenClause1.getType() == TMergeWhenClause.not_matched_by_target);

        TMergeUpdateClause mergeUpdateClause = whenClause0.getUpdateClause();
        assertTrue(mergeUpdateClause.getUpdateColumnList().getResultColumn(0).toString().equalsIgnoreCase("ReasonType = Source.NewReasonType"));

        TMergeInsertClause mergeInsertClause = whenClause1.getInsertClause();

        assertTrue(mergeInsertClause.getColumnList().getObjectName(0).toString().equalsIgnoreCase("Name"));
        assertTrue(mergeInsertClause.getColumnList().getObjectName(1).toString().equalsIgnoreCase("ReasonType"));

        assertTrue(mergeInsertClause.getValuelist().getResultColumn(0).toString().equalsIgnoreCase("NewName"));
        assertTrue(mergeInsertClause.getValuelist().getResultColumn(1).toString().equalsIgnoreCase("NewReasonType"));

    }

    public void testOracle(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "MERGE INTO test1 a\n" +
                "USING all_objects b\n" +
                "ON (a.object_id = b.object_id)\n" +
                "WHEN MATCHED THEN\n" +
                "UPDATE SET a.status = b.status\n" +
                "WHEN NOT MATCHED THEN\n" +
                "INSERT (object_id, status)\n" +
                "VALUES (b.object_id, b.status);";
        assertTrue(sqlparser.parse() == 0);

        TMergeSqlStatement mergeSqlStatement = (TMergeSqlStatement)sqlparser.sqlstatements.get(0);
        //System.out.println(mergeSqlStatement.tables.getTable(0).toString());
        assertTrue(mergeSqlStatement.getTargetTable().toString().equalsIgnoreCase("test1"));
        assertTrue(mergeSqlStatement.getTargetTable().getAliasClause().toString().equalsIgnoreCase("a"));
        assertTrue(mergeSqlStatement.getUsingTable().toString().equalsIgnoreCase("all_objects"));
        assertTrue(mergeSqlStatement.getUsingTable().getAliasClause().toString().equalsIgnoreCase("b"));
        assertTrue(mergeSqlStatement.getCondition().toString().equalsIgnoreCase("a.object_id = b.object_id"));

        assertTrue(mergeSqlStatement.getWhenClauses().size() == 2);
        TMergeWhenClause whenClause0 = mergeSqlStatement.getWhenClauses().getElement(0);
        TMergeWhenClause whenClause1 = mergeSqlStatement.getWhenClauses().getElement(1);
        assertTrue(whenClause0.getType() == TMergeWhenClause.matched);
        assertTrue(whenClause1.getType() == TMergeWhenClause.not_matched);

        TMergeUpdateClause mergeUpdateClause = whenClause0.getUpdateClause();
        assertTrue(mergeUpdateClause.getUpdateColumnList().getResultColumn(0).toString().equalsIgnoreCase("a.status = b.status"));

        TMergeInsertClause mergeInsertClause = whenClause1.getInsertClause();

        assertTrue(mergeInsertClause.getColumnList().getObjectName(0).toString().equalsIgnoreCase("object_id"));
        assertTrue(mergeInsertClause.getColumnList().getObjectName(1).toString().equalsIgnoreCase("status"));

        assertTrue(mergeInsertClause.getValuelist().getResultColumn(0).toString().equalsIgnoreCase("b.object_id"));
        assertTrue(mergeInsertClause.getValuelist().getResultColumn(1).toString().equalsIgnoreCase("b.status"));

    }

    public void testOracle2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "MERGE INTO bonuses D\n" +
                "   USING (SELECT employee_id, salary, department_id FROM employees\n" +
                "   WHERE department_id = 80) S\n" +
                "   ON (D.employee_id = S.employee_id)\n" +
                "   WHEN MATCHED THEN UPDATE SET D.bonus = D.bonus + S.salary*.01\n" +
                "     DELETE WHERE (S.salary > 8000)\n" +
                "   WHEN NOT MATCHED THEN INSERT (D.employee_id, D.bonus)\n" +
                "     VALUES (S.employee_id, S.salary*0.1)\n" +
                "     WHERE (S.salary <= 8000);";
        assertTrue(sqlparser.parse() == 0);

        TMergeSqlStatement mergeSqlStatement = (TMergeSqlStatement)sqlparser.sqlstatements.get(0);

        assertTrue(mergeSqlStatement.getTargetTable().toString().equalsIgnoreCase("bonuses"));
        assertTrue(mergeSqlStatement.getTargetTable().getAliasClause().toString().equalsIgnoreCase("D"));

        assertTrue(mergeSqlStatement.getWhenClauses().size() == 2);
        TMergeWhenClause whenClause0 = mergeSqlStatement.getWhenClauses().getElement(0);
        TMergeWhenClause whenClause1 = mergeSqlStatement.getWhenClauses().getElement(1);
        assertTrue(whenClause0.getType() == TMergeWhenClause.matched);
        assertTrue(whenClause1.getType() == TMergeWhenClause.not_matched);

        TMergeUpdateClause mergeUpdateClause = whenClause0.getUpdateClause();
        assertTrue(mergeUpdateClause.getUpdateColumnList().getResultColumn(0).toString().equalsIgnoreCase("D.bonus = D.bonus + S.salary*.01"));
        assertTrue(mergeUpdateClause.getDeleteWhereClause().toString().equalsIgnoreCase("(S.salary > 8000)"));
//
//        TMergeInsertClause mergeInsertClause = whenClause1.getInsertClause();
//
//        assertTrue(mergeInsertClause.getColumnList().getObjectName(0).toString().equalsIgnoreCase("object_id"));
//        assertTrue(mergeInsertClause.getColumnList().getObjectName(1).toString().equalsIgnoreCase("status"));
//
//        assertTrue(mergeInsertClause.getValuelist().getResultColumn(0).toString().equalsIgnoreCase("b.object_id"));
//        assertTrue(mergeInsertClause.getValuelist().getResultColumn(1).toString().equalsIgnoreCase("b.status"));

    }

    public void testMergeVisitor(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "MERGE INTO t1 USING t2 ON a1=c2 AND b1=b2 WHEN NOT MATCHED THEN INSERT (c2, b2, a2);";
        assertTrue(sqlparser.parse() == 0);

        TMergeSqlStatement mergeSqlStatement = (TMergeSqlStatement)sqlparser.sqlstatements.get(0);
        mergeSqlStatement.acceptChildren(new mergeVisitor());

        sqlparser.sqltext = "MERGE INTO t1 USING t2 ON a1=b2 AND c1=10 AND b1<b2 WHEN MATCHED THEN UPDATE SET b1=b2";
        assertTrue(sqlparser.parse() == 0);

        mergeSqlStatement = (TMergeSqlStatement)sqlparser.sqlstatements.get(0);
        mergeSqlStatement.acceptChildren(new mergeVisitor());

        sqlparser.sqltext = "MERGE INTO t1 USING t2 ON a1=c2 WHEN MATCHED THEN DELETE";
        assertTrue(sqlparser.parse() == 0);

        mergeSqlStatement = (TMergeSqlStatement)sqlparser.sqlstatements.get(0);
        mergeSqlStatement.acceptChildren(new mergeVisitor());
    }

}

class mergeVisitor extends TParseTreeVisitor {

    public void preVisit(TMergeInsertClause node){
       // System.out.println("Found:"+node.getClass().getName());
    }

    public void preVisit(TMergeUpdateClause node){
       // System.out.println("Found:"+node.getClass().getName());
    }
    public void preVisit(TMergeDeleteClause node){
        //System.out.println("Found:"+node.getClass().getName());
    }

}