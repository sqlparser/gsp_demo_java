package test.teradata;
/*
 * Date: 13-10-11
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.teradata.TTeradataLock;
import junit.framework.TestCase;

public class testLock extends TestCase {
    public void test1(){

          TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
          sqlparser.sqltext = " LOCKING TABLE dbc.TDWMExceptionLog FOR ACCESS \n" +
                  " SELECT procId as vprocid, BufferTS, QueryID, UserName, SessionID, RequestNum, \n" +
                  " LogicalHostID, AcctString, WDID, 0 as WDPeriodId, openvId, sysconId, \n" +
                  " ClassificationTime, ExceptionTime, ExceptionValue, ExceptionAction, NewWDID, \n" +
                  " ExceptionCode, ExceptionSubCode, ErrorText, ExtraInfo, ruleid, warningonly, \n" +
                  " rejectioncat \n" +
                  " FROM dbc.TDWMExceptionLog \n" +
                  " WHERE bufferTS > '2013-05-27 14:33:29.83' ORDER BY bufferTS DESC;";
          assertTrue(sqlparser.parse() == 0);
          assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstteradatalock);
        TTeradataLock lock = (TTeradataLock)sqlparser.sqlstatements.get(0);
        assertTrue(lock.getDatabase_table_view().toString().equalsIgnoreCase("TABLE"));
        assertTrue(lock.getObjectName().toString().equalsIgnoreCase("dbc.TDWMExceptionLog"));
        assertTrue(lock.getLockMode().toString().equalsIgnoreCase("ACCESS"));
        assertTrue(lock.getSqlRequest().sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement select = (TSelectSqlStatement)lock.getSqlRequest();
        assertTrue(select.getResultColumnList().size() == 24);
        assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("dbc.TDWMExceptionLog"));

      }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = " locking table t1 for access locking table t2 for access select * from t1,t2;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstteradatalock);
        TTeradataLock lock = (TTeradataLock)sqlparser.sqlstatements.get(0);
        assertTrue(lock.getDatabase_table_view().toString().equalsIgnoreCase("TABLE"));
        assertTrue(lock.getObjectName().toString().equalsIgnoreCase("t1"));
        assertTrue(lock.getLockMode().toString().equalsIgnoreCase("ACCESS"));
        assertTrue(lock.getSqlRequest().sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement select = (TSelectSqlStatement)lock.getSqlRequest();
        assertTrue(select.getResultColumnList().size() == 1);
        assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("t1"));

    }

}
