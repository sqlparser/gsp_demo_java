package test.teradata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import junit.framework.TestCase;


public class testUpsert extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "update table1 set a = 1 where b = 2 else insert into table1 (a,b) values (1,2)";

        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstupdate);
        TUpdateSqlStatement update = (TUpdateSqlStatement)sqlparser.sqlstatements.get(0);
        TInsertSqlStatement insert = update.getInsertSqlStatement();
        assertTrue(insert.getTargetTable().toString().equalsIgnoreCase("table1"));

    }
}
