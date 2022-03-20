package mysql;
/*
 * Date: 12-6-11
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCallStatement;
import junit.framework.TestCase;

public class testCall extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "CALL test2()";
        assertTrue(sqlparser.parse() == 0);

        TCallStatement callStmt = (TCallStatement)sqlparser.sqlstatements.get(0);
        assertTrue(callStmt.getRoutineName().toString().equalsIgnoreCase("test2"));
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "CALL test2(@`a`:=1)";
        assertTrue(sqlparser.parse() == 0);

        TCallStatement callStmt = (TCallStatement)sqlparser.sqlstatements.get(0);
        assertTrue(callStmt.getRoutineName().toString().equalsIgnoreCase("test2"));
        assertTrue(callStmt.getArgs().getExpression(0).getLeftOperand().toString().equalsIgnoreCase("@`a`"));
    }

}
