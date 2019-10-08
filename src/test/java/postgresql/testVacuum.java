package postgresql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TVacuumStmt;
import junit.framework.TestCase;

public class testVacuum extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "VACUUM";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstVacuum);
        TVacuumStmt vacuumStmt = (TVacuumStmt) sqlparser.sqlstatements.get(0);
    }

    public void test2() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "VACUUM full tablename";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstVacuum);
        TVacuumStmt vacuumStmt = (TVacuumStmt) sqlparser.sqlstatements.get(0);
        assertTrue(vacuumStmt.getTableName().toString().equalsIgnoreCase("tablename"));
    }

    public void test3() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "VACUUM full analyze tablename(f1,f2)";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstVacuum);
        TVacuumStmt vacuumStmt = (TVacuumStmt) sqlparser.sqlstatements.get(0);
        assertTrue(vacuumStmt.getTableName().toString().equalsIgnoreCase("tablename"));
        assertTrue(vacuumStmt.getColumList().size() == 2);
    }
}
