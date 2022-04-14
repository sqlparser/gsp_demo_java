package mssql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.mssql.TCreateExternalLanguage;
import junit.framework.TestCase;

public class testExternalTable extends TestCase {

    public void test1() {

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvazuresql);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE ClickStream (\n" +
                "    url varchar(50),\n" +
                "    event_date date,\n" +
                "    user_IP varchar(50)\n" +
                ")\n" +
                "WITH (\n" +
                "        LOCATION='/webdata/employee.tbl',\n" +
                "        DATA_SOURCE = mydatasource,\n" +
                "        FILE_FORMAT = myfileformat\n" +
                "    )";
        int result = sqlparser.parse();
        assertTrue(result == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatetable);
        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(createTable.isExternal());
//        for(int i=0;i<createTable.getExternalTableOptionNames().size();i++){
//            System.out.println(createTable.getExternalTableOptionNames().get(i));
//        }
        assertTrue(createTable.getExternalTableOptionNames().get(0).equalsIgnoreCase("location"));
        assertTrue(createTable.getExternalTableOptionNames().get(1).equalsIgnoreCase("DATA_SOURCE"));
        assertTrue(createTable.getExternalTableOptionNames().get(2).equalsIgnoreCase("FILE_FORMAT"));

        assertTrue(createTable.getExternalTableOption("location").equalsIgnoreCase("'/webdata/employee.tbl'"));
        assertTrue(createTable.getExternalTableOption("DATA_SOURCE").equalsIgnoreCase("mydatasource"));
        assertTrue(createTable.getExternalTableOption("FILE_FORMAT").equalsIgnoreCase("myfileformat"));
    }

    public void test2() {

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvazuresql);
        sqlparser.sqltext = "CREATE EXTERNAL TABLE [dbo].[all_dm_exec_requests]([session_id] smallint NOT NULL,\n" +
                "  [request_id] int NOT NULL,\n" +
                "  [start_time] datetime NOT NULL,\n" +
                "  [status] nvarchar(30) NOT NULL,\n" +
                "  [command] nvarchar(32) NOT NULL,\n" +
                "  [sql_handle] varbinary(64),\n" +
                "  [statement_start_offset] int,\n" +
                "  [statement_end_offset] int,\n" +
                "  [cpu_time] int NOT NULL)\n" +
                "WITH\n" +
                "(\n" +
                "  DATA_SOURCE = MyExtSrc,\n" +
                "  SCHEMA_NAME = 'sys',\n" +
                "  OBJECT_NAME = 'dm_exec_requests',\n" +
                "  DISTRIBUTION=ROUND_ROBIN\n" +
                ");";
        int result = sqlparser.parse();
        assertTrue(result == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatetable);
        TCreateTableSqlStatement createTable = (TCreateTableSqlStatement) sqlparser.sqlstatements.get(0);
        assertTrue(createTable.isExternal());
//        for(int i=0;i<createTable.getExternalTableOptionNames().size();i++){
//            System.out.println(createTable.getExternalTableOptionNames().get(i));
//        }
        assertTrue(createTable.getExternalTableOptionNames().get(0).equalsIgnoreCase("DATA_SOURCE"));
        assertTrue(createTable.getExternalTableOptionNames().get(1).equalsIgnoreCase("SCHEMA_NAME"));
        assertTrue(createTable.getExternalTableOptionNames().get(2).equalsIgnoreCase("OBJECT_NAME"));
        assertTrue(createTable.getExternalTableOptionNames().get(3).equalsIgnoreCase("DISTRIBUTION"));
//
        assertTrue(createTable.getExternalTableOption("DATA_SOURCE").equalsIgnoreCase("MyExtSrc"));
        assertTrue(createTable.getExternalTableOption("SCHEMA_NAME").equalsIgnoreCase("'sys'"));
        assertTrue(createTable.getExternalTableOption("OBJECT_NAME").equalsIgnoreCase("'dm_exec_requests'"));
        assertTrue(createTable.getExternalTableOption("DISTRIBUTION").equalsIgnoreCase("ROUND_ROBIN"));
    }
}
