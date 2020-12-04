package mssql;
/*
 * Date: 14-10-17
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.mssql.TMssqlThrow;
import junit.framework.TestCase;

public class testThrow extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "THROW";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstmssqlthrow);
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "THROW 1,'sb',4000;";
        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstmssqlthrow);
        TMssqlThrow mssqlThrow = (TMssqlThrow)sqlparser.sqlstatements.get(0);
        assertTrue(mssqlThrow.getErrorCode().toString().equalsIgnoreCase("1"));
        assertTrue(mssqlThrow.getErrorMessage().toString().equalsIgnoreCase("'sb'"));
        assertTrue(mssqlThrow.getErrorState().toString().equalsIgnoreCase("4000"));
    }
}
