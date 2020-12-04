package db2;
/*
 * Date: 12-6-11
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testCall extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdb2);

        sqlparser.sqltext = "CALL test2()";

     //   System.out.println(sqlparser.sqltext);

        assertTrue(sqlparser.parse() == 0);
    }
}
