package mssql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.mssql.TCreateEventSession;
import junit.framework.TestCase;

public class testCreateEventSession  extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE EVENT SESSION test_session\n" +
                "ON SERVER\n" +
                "    ADD EVENT sqlos.async_io_requested,\n" +
                "    ADD EVENT sqlserver.lock_acquired\n" +
                "    ADD TARGET package0.etw_classic_sync_target\n" +
                "        (SET default_etw_session_logfile_path = N'C:\\demo\\traces\\sqletw.etl' )\n" +
                "    WITH (MAX_MEMORY=4MB, MAX_EVENT_SIZE=4MB);";
        int result = sqlparser.parse();
        assertTrue(result==0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstmssqlcreateeventsession);
        TCreateEventSession createEventSession = (TCreateEventSession)sqlparser.sqlstatements.get(0);
        assertTrue(createEventSession.getEventSessionName().toString().equalsIgnoreCase("test_session"));
    }


}
