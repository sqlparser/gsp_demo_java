package mssql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateDatabaseSqlStatement;
import junit.framework.TestCase;

public class testCreateDatabase extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "CREATE DATABASE xml";
        int result = sqlparser.parse();
        assertTrue(result==0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstcreatedatabase);
        TCreateDatabaseSqlStatement createDatabaseSqlStatement = (TCreateDatabaseSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createDatabaseSqlStatement.getDatabaseName().toString().equalsIgnoreCase("xml"));
    }


}

