package test.db2;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateDatabaseSqlStatement;
import junit.framework.TestCase;


public class testCreateDatabase extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdb2);

        sqlparser.sqltext = "CREATE DATABASE MYDB\n" +
                "   BUFFERPOOL BP1\n" +
                "   INDEXBP BP2\n" +
                "   CCSID EBCDIC\n" +
                "   STOGROUP B0SG0100;";

        assertTrue(sqlparser.parse() == 0);

        TCreateDatabaseSqlStatement db = (TCreateDatabaseSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(db.getDatabaseName().toString().equalsIgnoreCase("MYDB"));
    }
}