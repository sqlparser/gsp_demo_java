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
    }


}
