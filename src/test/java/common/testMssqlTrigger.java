package common;
/*
 * Date: 2010-9-1
 * Time: 16:36:33
 */

import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.ETriggerActionTime;
import gudusoft.gsqlparser.nodes.TDmlEventClause;
import gudusoft.gsqlparser.nodes.TDmlEventItem;
import gudusoft.gsqlparser.nodes.TSimpleDmlTriggerClause;
import gudusoft.gsqlparser.stmt.TCreateTriggerStmt;
import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TDeclareVariable;
import gudusoft.gsqlparser.stmt.mssql.*;

public class testMssqlTrigger extends TestCase {
    private TGSqlParser parser = null;

    protected void setUp() throws Exception {
        super.setUp();
        parser = new TGSqlParser(EDbVendor.dbvmssql);
    }

    protected void tearDown() throws Exception {
        parser = null;
        super.tearDown();
    }

    public void test1(){
        parser.sqltext = "CREATE TRIGGER Purchasing.LowCredit ON Purchasing.PurchaseOrderHeader\n" +
                "AFTER INSERT\n" +
                "AS\n" +
                "DECLARE @creditrating tinyint, @vendorid int;\n" +
                "IF EXISTS (SELECT *\n" +
                "           FROM Purchasing.PurchaseOrderHeader p \n" +
                "           JOIN inserted AS i \n" +
                "           ON p.PurchaseOrderID = i.PurchaseOrderID \n" +
                "           JOIN Purchasing.Vendor AS v \n" +
                "           ON v.BusinessEntityID = p.VendorID\n" +
                "           WHERE v.CreditRating = 5\n" +
                "          )\n" +
                "BEGIN\n" +
                "RAISERROR ('This vendor''s credit rating is too low to accept new purchase orders.', 16, 1);\n" +
                "ROLLBACK TRANSACTION;\n" +
                "RETURN \n" +
                "END;" ;
        assertTrue(parser.parse() == 0);
        TCreateTriggerStmt stmt = (TCreateTriggerStmt)parser.sqlstatements.get(0);
        assertEquals(true, stmt.getTriggerName().toString().equalsIgnoreCase("Purchasing.LowCredit"));
        assertTrue(stmt.getOnTable().toString().equalsIgnoreCase("Purchasing.PurchaseOrderHeader"));

        // dml trigger
        assertTrue(stmt.getTriggeringClause() instanceof TSimpleDmlTriggerClause);
        TSimpleDmlTriggerClause dmlTriggerClause = (TSimpleDmlTriggerClause)stmt.getTriggeringClause();
        assertTrue(dmlTriggerClause.getActionTime() == ETriggerActionTime.tatAfter);
        TDmlEventClause dmlEventClause = (TDmlEventClause)dmlTriggerClause.getEventClause();
        assertTrue(dmlEventClause.getTableName().toString().equalsIgnoreCase("Purchasing.PurchaseOrderHeader"));
        TDmlEventItem eventItem = (TDmlEventItem)dmlEventClause.getEventItems().get(0);
        assertTrue(eventItem.getDmlType() == ESqlStatementType.sstinsert);

        assertTrue(stmt.getBodyStatements().size() == 2);
        TMssqlDeclare declare = (TMssqlDeclare)stmt.getBodyStatements().get(0);
        assertTrue(declare.getVariables().size() == 2);
        TDeclareVariable var1 = (TDeclareVariable)declare.getVariables().getDeclareVariable(0);
        assertTrue(var1.getVariableName().toString().equalsIgnoreCase("@creditrating"));
        assertTrue(var1.getDatatype().toString().equalsIgnoreCase("tinyint"));

        TMssqlIfElse ifstmt = (TMssqlIfElse)stmt.getBodyStatements().get(1);
        TMssqlBlock block = (TMssqlBlock)ifstmt.getStmt();
        TMssqlRaiserror raiserror = (TMssqlRaiserror)block.getStatements().get(0);
        assertTrue(raiserror.getMessageText().toString().equalsIgnoreCase("'This vendor''s credit rating is too low to accept new purchase orders.'"));
        //System.out.println(block.getStatements().get(2).sqlstatementtype);
    }
    
}
