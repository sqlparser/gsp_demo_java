package mssql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateCredentialStmt;
import junit.framework.TestCase;

public class testCreateCredential  extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE CREDENTIAL AlterEgo WITH IDENTITY = 'Mary5',\n" +
                "    SECRET = '<EnterStrongPasswordHere>';";
        assertTrue(sqlparser.parse() == 0);

        TCreateCredentialStmt createCredentialStmt = (TCreateCredentialStmt)sqlparser.sqlstatements.get(0);
        assertTrue(!createCredentialStmt.isDatabaseScoped());
        assertTrue(createCredentialStmt.getCredentialName().toString().equalsIgnoreCase("AlterEgo"));

    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvazuresql);
        sqlparser.sqltext = "CREATE DATABASE SCOPED CREDENTIAL ADL_User\n" +
                "WITH\n" +
                "    IDENTITY = '<client_id>@<OAuth_2.0_Token_EndPoint>',\n" +
                "    SECRET = '<key>'\n" +
                ";";
        assertTrue(sqlparser.parse() == 0);

        TCreateCredentialStmt createCredentialStmt = (TCreateCredentialStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createCredentialStmt.isDatabaseScoped());
        assertTrue(createCredentialStmt.getCredentialName().toString().equalsIgnoreCase("ADL_User"));

    }

}
