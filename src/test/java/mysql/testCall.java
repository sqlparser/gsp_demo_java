package mysql;
/*
 * Date: 12-6-11
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.mysql.TMySQLCallStmt;
import junit.framework.TestCase;

public class testCall extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "CALL test2()";
        assertTrue(sqlparser.parse() == 0);

        TMySQLCallStmt callStmt = (TMySQLCallStmt)sqlparser.sqlstatements.get(0);
        assertTrue(callStmt.getProcedureName().toString().equalsIgnoreCase("test2"));
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "CALL test2(@`a`:=1)";
        assertTrue(sqlparser.parse() == 0);

        TMySQLCallStmt callStmt = (TMySQLCallStmt)sqlparser.sqlstatements.get(0);
        assertTrue(callStmt.getProcedureName().toString().equalsIgnoreCase("test2"));
        assertTrue(callStmt.getParameters().getExpression(0).getLeftOperand().toString().equalsIgnoreCase("@`a`"));
    }

}
