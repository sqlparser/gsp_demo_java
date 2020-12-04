package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.redshift.TRedshiftDeallocate;
import junit.framework.TestCase;


public class testDeallocate extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "deallocate plan_name";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftDeallocate);
        TRedshiftDeallocate deallocate = (TRedshiftDeallocate) sqlparser.sqlstatements.get(0);
        assertTrue(deallocate.getPlanName().toString().equals("plan_name"));
    }
}