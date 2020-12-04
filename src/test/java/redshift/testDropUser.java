package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.redshift.TRedshiftDropUser;
import junit.framework.TestCase;


public class testDropUser extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "drop user if exists danny;";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftDropUser);
        TRedshiftDropUser dropUser = (TRedshiftDropUser) sqlparser.sqlstatements.get(0);
        assertTrue(dropUser.getUserNameList().size() == 1);
        assertTrue(dropUser.getUserNameList().getObjectName(0).toString().equalsIgnoreCase("danny"));
    }
}