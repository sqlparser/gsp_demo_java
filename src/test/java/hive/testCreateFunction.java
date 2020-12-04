package hive;
/*
 * Date: 13-8-16
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.hive.THiveCreateFunction;
import junit.framework.TestCase;

public class testCreateFunction extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "CREATE TEMPORARY FUNCTION function_name AS 'class_name';";
          assertTrue(sqlparser.parse() == 0);

        THiveCreateFunction createFunction = (THiveCreateFunction)sqlparser.sqlstatements.get(0);
        assertTrue(createFunction.getFunctionName().toString().equalsIgnoreCase("function_name"));
        assertTrue(createFunction.getAsName().toString().equalsIgnoreCase("'class_name'"));
    }
}
