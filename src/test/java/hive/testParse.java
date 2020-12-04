package hive;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 * User: tako
 * Date: 13-7-21
 * Time: 下午10:43
 * To change this template use File | Settings | File Templates.
 */


public class testParse extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "INSERT OVERWRITE TABLE pv_gender_sum\n" +
                "  SELECT pv_users.gender, count (DISTINCT pv_users.userid)\n" +
                "  FROM pv_users\n" +
                "  GROUP BY pv_users.gender;";
          assertTrue(sqlparser.parse() == 0);
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext =
                "  SELECT pv_users.gender, count (DISTINCT pv_users.userid)\n" +
                "  FROM pv_users\n" +
                "  GROUP BY pv_users.gender;";
          assertTrue(sqlparser.parse() == 0);
    }
}