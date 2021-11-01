package presto;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TAlterDatabaseStmt;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TPrepareStmt;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testPrepare extends TestCase {

    public void testSelect() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpresto);
        sqlparser.sqltext = "PREPARE my_select2 FROM\n" +
                "SELECT name FROM nation WHERE regionkey = ? AND nationkey < ?;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstprepare);
        TPrepareStmt prepareStmt = (TPrepareStmt)sqlparser.sqlstatements.get(0);
        assertTrue(prepareStmt.getPreparableStmt().sqlstatementtype  == ESqlStatementType.sstselect);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)prepareStmt.getPreparableStmt();
        assertTrue(selectSqlStatement.getTables().getTable(0).toString().equalsIgnoreCase("nation"));
    }

    public void testInsert() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpresto);
        sqlparser.sqltext = "PREPARE my_insert FROM\n" +
                "INSERT INTO cities VALUES (1, 'San Francisco');";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstprepare);
        TPrepareStmt prepareStmt = (TPrepareStmt)sqlparser.sqlstatements.get(0);
        assertTrue(prepareStmt.getPreparableStmt().sqlstatementtype  == ESqlStatementType.sstinsert);
        TInsertSqlStatement insertSqlStatement = (TInsertSqlStatement)prepareStmt.getPreparableStmt();
        assertTrue(insertSqlStatement.getTables().getTable(0).toString().equalsIgnoreCase("cities"));
    }

}
