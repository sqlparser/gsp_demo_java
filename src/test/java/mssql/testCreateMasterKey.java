package mssql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.mssql.TCreateMasterKeyStmt;
import junit.framework.TestCase;

public class testCreateMasterKey extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvazuresql);
        sqlparser.sqltext = "CREATE MASTER KEY ENCRYPTION BY PASSWORD = '23987hxJ#KL95234nl0zBe';";
        assertTrue(sqlparser.parse() == 0);

        TCreateMasterKeyStmt createMasterKeyStmt = (TCreateMasterKeyStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createMasterKeyStmt.getPassword().equalsIgnoreCase("'23987hxJ#KL95234nl0zBe'"));
    }
}
