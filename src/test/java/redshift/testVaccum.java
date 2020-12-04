package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.redshift.TRedshiftVacuum;
import junit.framework.TestCase;


public class testVaccum extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "vacuum delete only sales";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftVacuum);
        TRedshiftVacuum vacuum = (TRedshiftVacuum) sqlparser.sqlstatements.get(0);
        assertTrue(vacuum.getTableName().toString().equalsIgnoreCase("sales"));

    }
}