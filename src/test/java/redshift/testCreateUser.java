package redshift;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.redshift.TRedshiftCreateUser;
import junit.framework.TestCase;


public class testCreateUser extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "create user danny with password 'abcD1234' valid until '2014-06-10';";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstredshiftCreateUser);
        TRedshiftCreateUser user = (TRedshiftCreateUser) sqlparser.sqlstatements.get(0);
        assertTrue(user.getUserName().toString().equals("danny"));
    }
}