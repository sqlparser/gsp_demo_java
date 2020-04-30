package test.mssql;
/*
 * Date: 12-3-9
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDropFunctionStmt;
import gudusoft.gsqlparser.stmt.TDropProcedureStmt;
import gudusoft.gsqlparser.stmt.mssql.TMssqlDropDbObject;
import junit.framework.TestCase;

public class testDropDBObject extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "DROP PROCEDURE ProcedureA";
        assertTrue(sqlparser.parse() == 0);

        TDropProcedureStmt dropDbObject = (TDropProcedureStmt)sqlparser.sqlstatements.get(0);
        assertTrue(dropDbObject.getProcedureName().toString().equalsIgnoreCase("ProcedureA"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "DROP FUNCTION if exists FunctionA";
        assertTrue(sqlparser.parse() == 0);

        TDropFunctionStmt dropDbObject = (TDropFunctionStmt)sqlparser.sqlstatements.get(0);
        assertTrue(dropDbObject.getFunctionName().toString().equalsIgnoreCase("FunctionA"));
    }
}
