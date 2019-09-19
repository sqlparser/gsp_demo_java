package test.oracle;

/*
 * Date: 11-8-4
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testOracleHint extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "create or replace view test22 as select /*+  RULE  */ t.account_name, t.account_number from  AP13_BANK_ACCOUNTS t;";
        assertTrue(sqlparser.parse() == 0);

        TCreateViewSqlStatement viewSqlStatement = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(viewSqlStatement.getViewName().toString().equalsIgnoreCase("test22"));

        TSelectSqlStatement select  = viewSqlStatement.getSubquery();
       assertTrue(select.getOracleHint().equalsIgnoreCase("/*+  RULE  */"));

    }

}
