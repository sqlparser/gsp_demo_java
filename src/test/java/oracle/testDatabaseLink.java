package oracle;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateDatabaseLinkStmt;
import gudusoft.gsqlparser.stmt.TDropDatabaseLinkStmt;
import junit.framework.TestCase;


public class testDatabaseLink extends TestCase {

    public void testDrop(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "DROP PUBLIC DATABASE LINK remote";
        assertTrue(sqlparser.parse() == 0);

        TDropDatabaseLinkStmt databaseLinkStmt = (TDropDatabaseLinkStmt)sqlparser.sqlstatements.get(0);
        assertTrue(databaseLinkStmt.getDatabaseLinkName().toString().equalsIgnoreCase("remote"));
    }

    public void testCreate(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE DATABASE LINK chicago\n" +
                "  CONNECT TO admin IDENTIFIED BY 'mypassword'\n" +
                "  USING oci '//127.0.0.1/acctg';";
        assertTrue(sqlparser.parse() == 0);

        TCreateDatabaseLinkStmt databaseLinkStmt = (TCreateDatabaseLinkStmt)sqlparser.sqlstatements.get(0);
        assertTrue(databaseLinkStmt.getDatabaseLinkName().toString().equalsIgnoreCase("chicago"));
    }
}
