package common;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ELockMode;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TLockTableStmt;
import junit.framework.TestCase;

public class testLockTable extends TestCase {

    public void testNetezza(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "LOCK TABLE IDP_PRD_LEVEL2.L2_IDP_ENTERPRISE.FINANCIAL_INSTRUMENT_ALIAS IN EXCLUSIVE MODE;";
        assertTrue(sqlparser.parse() == 0);
        TLockTableStmt lockTableStmt = (TLockTableStmt)sqlparser.sqlstatements.get(0);
       assertTrue(lockTableStmt.getTalbe().getTableName().toString().equalsIgnoreCase("IDP_PRD_LEVEL2.L2_IDP_ENTERPRISE.FINANCIAL_INSTRUMENT_ALIAS"));
        assertTrue(lockTableStmt.getLockMode() == ELockMode.exclusive);

    }
}
