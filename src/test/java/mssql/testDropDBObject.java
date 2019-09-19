package test.mssql;
/*
 * Date: 12-3-9
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
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
        sqlparser.sqltext = "DROP FUNCTION FunctionA";
        assertTrue(sqlparser.parse() == 0);

        TMssqlDropDbObject dropDbObject = (TMssqlDropDbObject)sqlparser.sqlstatements.get(0);
        assertTrue(dropDbObject.getDbObjectType().toString().equalsIgnoreCase("function"));
        assertTrue(dropDbObject.getObjectNameList().getObjectName(0).toString().equalsIgnoreCase("FunctionA"));
    }
}
