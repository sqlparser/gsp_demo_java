package informix;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.informix.TInformixCreateProcedure;
import junit.framework.TestCase;

public class testCreateProcedure extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvinformix);
        sqlparser.sqltext = "create procedure procCustomersAccounts ()\n" +
                "INSERT INTO CUSTOMERACCOUNTS\n" +
                "  SELECT a.C_ID\n" +
                "  , a.C_NAME\n" +
                "  , a.c_NUMBER\n" +
                "  , b.C_ACCOUNT_TYPE\n" +
                "  , b.C_ACCOUNT_DURATION\n" +
                "  , a.C_BALANCE\n" +
                "  FROM CARDSDATA A\n" +
                "  JOIN CUSTOMERDATA B\n" +
                "  ON (A.C_ID = B.C_ID);\n" +
                "end procedure ";
        assertTrue(sqlparser.parse() == 0);

        TInformixCreateProcedure stmt = (TInformixCreateProcedure)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getProcedureName().toString().equalsIgnoreCase("procCustomersAccounts"));
        assertTrue(stmt.getBodyStatements().size() == 1);
        TInsertSqlStatement insertSqlStatement = (TInsertSqlStatement)stmt.getBodyStatements().get(0);
        assertTrue(insertSqlStatement.getTargetTable().toString().equalsIgnoreCase("CUSTOMERACCOUNTS"));

    }
}
