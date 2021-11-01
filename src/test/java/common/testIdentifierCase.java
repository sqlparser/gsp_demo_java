package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.sqlenv.ESQLDataObjectType;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testIdentifierCase extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "SELECT employee_id,employee_name,employee_duration from DataMart.[fact].[vwDeed],DataMart.[Fact].[vwDeed];";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstselect);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table0  = select.getTables().getTable(0);
        TTable table1  = select.getTables().getTable(1);
        //System.out.println( String.format("Table0:%s, Table1:%s",table0.getTableName().toString(),table1.getTableName().toString()));
        //TSQLEnv.tableCollationCaseSensitive[EDbVendor.dbvmssql.ordinal()] = false;
        assertTrue(TSQLEnv.compareTable(EDbVendor.dbvmssql,table0.getTableName() ,table1.getTableName()));
        assertTrue(TSQLEnv.compareIdentifier(EDbVendor.dbvmssql, ESQLDataObjectType.dotTable, table0.toString(),table1.toString()));
    }
}
