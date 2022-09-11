package teradata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.teradata.TAlterConstraintStmt;
import junit.framework.TestCase;

public class testAlterConstraint extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "ALTER constraint group_membership\n" +
                "     AS VALUES (etl:4, manager:3, clerk:2, peon:1);";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstteradataalterconstraint);
        TAlterConstraintStmt alterConstraintStmt = (TAlterConstraintStmt)sqlparser.sqlstatements.get(0);
        assertTrue(alterConstraintStmt.getConstraintName().toString().equalsIgnoreCase("group_membership"));
    }
}
