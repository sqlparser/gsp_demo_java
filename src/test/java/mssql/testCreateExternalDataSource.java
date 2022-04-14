package mssql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.mssql.TCreateExternalDataSourceStmt;
import junit.framework.TestCase;

public class testCreateExternalDataSource extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE EXTERNAL DATA SOURCE WWIStorage\n" +
                "WITH\n" +
                "(\n" +
                "    TYPE = Hadoop,\n" +
                "    LOCATION = 'wasbs://wideworldimporters@sqldwholdata.blob.core.windows.net'\n" +
                ");";
        int result = sqlparser.parse();
        assertTrue(result==0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstmssqlcreateevexternalDataSource);
        TCreateExternalDataSourceStmt createExternalDataSourceStmt = (TCreateExternalDataSourceStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createExternalDataSourceStmt.getDataSourceName().toString().equalsIgnoreCase("WWIStorage"));
        assertTrue(createExternalDataSourceStmt.getOptionNames().get(0).equalsIgnoreCase("TYPE"));
        assertTrue(createExternalDataSourceStmt.getOptionNames().get(1).equalsIgnoreCase("LOCATION"));
        assertTrue(createExternalDataSourceStmt.getOption("type").equalsIgnoreCase("Hadoop"));
        assertTrue(createExternalDataSourceStmt.getOption("LOCATION").equalsIgnoreCase("'wasbs://wideworldimporters@sqldwholdata.blob.core.windows.net'"));
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE EXTERNAL DATA SOURCE MyOracleServer\n" +
                "WITH\n" +
                "  ( LOCATION = 'oracle://145.145.145.145:1521',\n" +
                "    CREDENTIAL = OracleProxyAccount,\n" +
                "    PUSHDOWN = ON\n" +
                "  ) ;";
        int result = sqlparser.parse();
        assertTrue(result==0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstmssqlcreateevexternalDataSource);
        TCreateExternalDataSourceStmt createExternalDataSourceStmt = (TCreateExternalDataSourceStmt)sqlparser.sqlstatements.get(0);
        assertTrue(createExternalDataSourceStmt.getDataSourceName().toString().equalsIgnoreCase("MyOracleServer"));
        assertTrue(createExternalDataSourceStmt.getOption("CREDENTIAL").equalsIgnoreCase("OracleProxyAccount"));
        assertTrue(createExternalDataSourceStmt.getOption("LOCATION").equalsIgnoreCase("'oracle://145.145.145.145:1521'"));
        assertTrue(createExternalDataSourceStmt.getOption("PUSHDOWN").equalsIgnoreCase("ON"));
    }

}
