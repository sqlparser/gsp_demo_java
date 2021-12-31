package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDeleteSqlStatement;
import junit.framework.TestCase;

public class testDeleteStmt extends TestCase {

    public void testMySQLDropIndex(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "DELETE AA FROM Table1 ACP INNER JOIN Table2 AA ON AA.EventKey = ACP.EventKey WHERE ACP.MarketDate = @MarketDate";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstdelete);
        TDeleteSqlStatement deleteStmt = (TDeleteSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(deleteStmt.getTargetTable().toString().equalsIgnoreCase("Table2"));
        assertTrue(deleteStmt.getTables().getTable(0).toString().equalsIgnoreCase("Table1"));
        assertTrue(deleteStmt.getTables().getTable(1).toString().equalsIgnoreCase("Table2"));
    }

}
