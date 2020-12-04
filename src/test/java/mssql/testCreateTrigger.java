package mssql;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.ETriggerActionTime;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TDmlEventClause;
import gudusoft.gsqlparser.nodes.TDmlEventItem;
import gudusoft.gsqlparser.nodes.TSimpleDmlTriggerClause;
import gudusoft.gsqlparser.stmt.TCreateTriggerStmt;
import junit.framework.TestCase;

public class testCreateTrigger extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE TRIGGER reminder\n" +
                "ON titles\n" +
                "FOR INSERT, UPDATE \n" +
                "AS RAISERROR (50009, 16, 10)\n" +
                "GO";
        int result = sqlparser.parse();
        assertTrue(result==0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatetrigger);
        TCreateTriggerStmt createTrigger = (TCreateTriggerStmt)sqlparser.sqlstatements.get(0);

        // dml trigger
        TSimpleDmlTriggerClause dmlTriggerClause = (TSimpleDmlTriggerClause)createTrigger.getTriggeringClause();
        assertTrue(dmlTriggerClause.getActionTime() == ETriggerActionTime.tatFor);
        TDmlEventClause dmlEventClause = (TDmlEventClause)dmlTriggerClause.getEventClause();
        assertTrue(dmlEventClause.getEventItems().size() == 2);
        TDmlEventItem dmlEventItem = (TDmlEventItem)dmlEventClause.getEventItems().get(0);
        assertTrue(dmlEventItem.getDmlType() == ESqlStatementType.sstinsert);
        dmlEventItem = (TDmlEventItem)dmlEventClause.getEventItems().get(1);
        assertTrue(dmlEventItem.getDmlType() == ESqlStatementType.sstupdate);

        //assertTrue(createTrigger.getDmlTypes().toString().equalsIgnoreCase("[tdtInsert, tdtUpdate]"));
        //assertTrue(createTrigger.getTimingPoint().toString().equalsIgnoreCase("ttpFor"));
    }
}
