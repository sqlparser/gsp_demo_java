package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testMd5 extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "create or replace view test22 as select /*+  RULE  */ t.account_name, t.account_number from  AP13_BANK_ACCOUNTS t;";
        assertTrue(sqlparser.parse() == 0);
        String md51 = sqlparser.sqlstatements.get(0).getMd5();

        sqlparser.sqltext = "create or replace view test22 " +
                "as select /*+  RULE  */ t.account_name, t.account_number -- comment\n" +
                "from  AP13_BANK_ACCOUNTS t;";
        assertTrue(sqlparser.parse() == 0);
        String md52 = sqlparser.sqlstatements.get(0).getMd5();

        assertTrue(md51.equalsIgnoreCase(md52));

//        System.out.println(md51);
//        System.out.println(md52);
    }

    public void testDelimitedIdentifier(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "select t.account_name, t.account_number from [AP13_BANK_ACCOUNTS] t;";
        assertTrue(sqlparser.parse() == 0);
        String md51 = sqlparser.sqlstatements.get(0).getMd5();

        sqlparser.sqltext = "select t.account_name, t.account_number from [AP13_bank_ACCOUNTS] t;";

        assertTrue(sqlparser.parse() == 0);
        String md52 = sqlparser.sqlstatements.get(0).getMd5();

        assertTrue(!md51.equalsIgnoreCase(md52));
    }

    public void testDelimitedIdentifier2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "select t.account_name, t.account_number from \"AP13_BANK_ACCOUNTS\" t;";
        assertTrue(sqlparser.parse() == 0);
        String md51 = sqlparser.sqlstatements.get(0).getMd5();

        sqlparser.sqltext = "select t.account_name, t.account_number from \"AP13_bank_ACCOUNTS\" t;";

        assertTrue(sqlparser.parse() == 0);
        String md52 = sqlparser.sqlstatements.get(0).getMd5();

        assertTrue(!md51.equalsIgnoreCase(md52));
    }

    public void testConstantInWhere(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "select 1, 'dummy', t.account_name, t.account_number from \"AP13_BANK_ACCOUNTS\" t where id=19 and ename='scott';";
        assertTrue(sqlparser.parse() == 0);
        String md51 = sqlparser.sqlstatements.get(0).getMd5();

        sqlparser.sqltext = "select 1, 'dummy', t.account_name, t.account_number from \"AP13_BANK_ACCOUNTS\" t where id=123 and ename='tiger';";

        assertTrue(sqlparser.parse() == 0);
        String md52 = sqlparser.sqlstatements.get(0).getMd5();

        assertTrue(md51.equalsIgnoreCase(md52));
    }

    public void testConstantInWhere2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "select 1, 'dummy', t.account_name, t.account_number from AP13_BANK_ACCOUNTS t where id=19 and ename='scott';";
        assertTrue(sqlparser.parse() == 0);
        String md51 = sqlparser.sqlstatements.get(0).getMd5();

        sqlparser.sqltext = "select 2, 'dummy2', t.account_name, t.account_number from AP13_BANK_ACCOUNTS t where id=123 and ename='tiger';";

        assertTrue(sqlparser.parse() == 0);
        String md52 = sqlparser.sqlstatements.get(0).getMd5();

        assertTrue(!md51.equalsIgnoreCase(md52));
    }

}
