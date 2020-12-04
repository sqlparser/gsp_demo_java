package oracle;
/*
 * Date: 13-10-31
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCallStatement;
import junit.framework.TestCase;

public class testCall extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CALL dbms_java.set_output(2000)";
        assertTrue(sqlparser.parse() == 0);
        TCallStatement callStatement = (TCallStatement)sqlparser.sqlstatements.get(0);
        assertTrue(callStatement.getRoutineName().toString().equalsIgnoreCase("dbms_java.set_output"));
        assertTrue(callStatement.getArgs().getExpression(0).toString().equalsIgnoreCase("2000"));
    }


}
