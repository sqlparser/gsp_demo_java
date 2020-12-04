package db2;
/*
 * Date: 12-6-11
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testParameter extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdb2);

        sqlparser.sqltext = "CREATE PROCEDURE \"SA\".\"TEST2\"(parm1 INT DEFAULT -1, parm2 INT DEFAULT -3)\n" +
                "BEGIN\n" +
                "\tselect a from b;\n" +
                "END";

     //   System.out.println(sqlparser.sqltext);

        assertTrue(sqlparser.parse() == 0);
    }

}
