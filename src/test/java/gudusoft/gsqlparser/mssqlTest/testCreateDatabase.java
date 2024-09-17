package gudusoft.gsqlparser.mssqlTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateDatabaseSqlStatement;
import gudusoft.gsqlparser.stmt.mssql.TMssqlIfElse;
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

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "if db_id('AutoSchemaDB') is null create database AutoSchemaDB";
        int result = sqlparser.parse();
        assertTrue(result==0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstmssqlif);
        TMssqlIfElse ifElse = (TMssqlIfElse )sqlparser.sqlstatements.get(0);
        assertTrue(ifElse.getStmt().sqlstatementtype == ESqlStatementType.sstcreatedatabase);
        TCreateDatabaseSqlStatement createDatabaseSqlStatement = (TCreateDatabaseSqlStatement)ifElse.getStmt();
        assertTrue(createDatabaseSqlStatement.getDatabaseName().toString().equalsIgnoreCase("AutoSchemaDB"));
    }

}

