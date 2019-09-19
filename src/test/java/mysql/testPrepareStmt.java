package test.mysql;
/*
 * Date: 13-3-25
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.mysql.TMySQLPrepareStmt;
import junit.framework.TestCase;

public class testPrepareStmt extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "prepare stmnt from 'insert into Dept values(?,?,?,?,?)';";
        assertTrue(sqlparser.parse() == 0);

        TMySQLPrepareStmt stmt = (TMySQLPrepareStmt)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getStmtName().toString().equalsIgnoreCase("stmnt"));
        assertTrue(stmt.getPreparableStmtStr().equalsIgnoreCase("insert into Dept values(?,?,?,?,?)"));
        if (stmt.getPreparableStmt() != null){
            assertTrue(stmt.getPreparableStmt().sqlstatementtype == ESqlStatementType.sstinsert);
            TInsertSqlStatement insert = (TInsertSqlStatement)stmt.getPreparableStmt();
            assertTrue(insert.getTargetTable().toString().equalsIgnoreCase("Dept"));
            assertTrue(insert.getValues().getMultiTarget(0).getColumnList().size() == 5);
        }
    }

}
