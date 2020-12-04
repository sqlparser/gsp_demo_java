package redshift;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.stmt.redshift.TRedshiftAlterUser;
import junit.framework.TestCase;


public class testAlterUser extends TestCase {

    public void testAddColumn() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "alter user admin createdb;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftAlterUser);
        TRedshiftAlterUser user = (TRedshiftAlterUser) sqlparser.sqlstatements.get(0);
        assertTrue(user.getUserName().toString().endsWith("admin"));
    }
}