package test.teradata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;


public class testLiteral extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "INSERT t1 (TIME '10:44:25.123-08:00',\n" +
                "TIMESTAMP '2000-09-20 10:44:25.1234');";
        assertTrue(sqlparser.parse() == 0);

    }

}