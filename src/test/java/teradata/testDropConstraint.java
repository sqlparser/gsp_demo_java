package teradata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;

import gudusoft.gsqlparser.stmt.teradata.TDropConstraintStmt;
import junit.framework.TestCase;

public class testDropConstraint extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "DROP constraint group_membership;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstteradatadropconstraint);
        TDropConstraintStmt drop = (TDropConstraintStmt)sqlparser.sqlstatements.get(0);
        assertTrue(drop.getConstraintName().toString().equalsIgnoreCase("group_membership"));
    }
}
