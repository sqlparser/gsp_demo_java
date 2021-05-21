package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateFunctionStmt;
import junit.framework.TestCase;

public class testCreateFunction extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsparksql);
        sqlparser.sqltext = "CREATE FUNCTION simple_udf AS 'SimpleUdf'\n" +
                "    USING JAR '/tmp/SimpleUdf.jar';";
        assertTrue(sqlparser.parse() == 0);

        TCreateFunctionStmt stmt = (TCreateFunctionStmt)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getFunctionName().toString().equalsIgnoreCase("simple_udf"));
        assertTrue(stmt.getClassName().equalsIgnoreCase("'SimpleUdf'"));
        assertTrue(stmt.getResourceType().equalsIgnoreCase("JAR"));
        assertTrue(stmt.getResourceURI().equalsIgnoreCase("'/tmp/SimpleUdf.jar'"));
    }
}
