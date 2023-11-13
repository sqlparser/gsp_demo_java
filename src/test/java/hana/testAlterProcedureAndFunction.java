package hana;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TAlterFunctionStmt;
import gudusoft.gsqlparser.stmt.TAlterProcedureStmt;
import junit.framework.TestCase;

public class testAlterProcedureAndFunction extends TestCase {
    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhana);
        sqlparser.sqltext = "ALTER PROCEDURE p3 RECOMPILE;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstalterprocedure);
        TAlterProcedureStmt alterProcedureStmt = (TAlterProcedureStmt)sqlparser.sqlstatements.get(0);
        assertTrue(alterProcedureStmt.getProcedureName().toString().equalsIgnoreCase("p3"));
        assertTrue(alterProcedureStmt.getAlterType() == TAlterProcedureStmt.AlterType.recompile);

    }

    public void test2() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhana);
        sqlparser.sqltext = "ALTER FUNCTION Scale1 ADD STATIC CACHE RETENTION 10;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstalterfunction);
        TAlterFunctionStmt alterFunctionStmt = (TAlterFunctionStmt)sqlparser.sqlstatements.get(0);
        assertTrue(alterFunctionStmt.getFunctionName().toString().equalsIgnoreCase("Scale1"));
        assertTrue(alterFunctionStmt.getAlterType() == TAlterFunctionStmt.AlterType.addDrop);

    }
}
