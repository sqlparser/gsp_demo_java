package common;
/*
 * Date: 2010-10-9
 * Time: 16:36:08
 */

import gudusoft.gsqlparser.EDbObjectType;
import gudusoft.gsqlparser.nodes.TObjectName;
import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.nodes.TExecParameter;
import gudusoft.gsqlparser.stmt.mssql.TMssqlExecute;

public class testTMssqlExecute extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "EXEC dbo.uspGetEmployeeManagers 6;";
        assertTrue(sqlparser.parse() == 0);
        TMssqlExecute exec = (TMssqlExecute)sqlparser.sqlstatements.get(0);
        assertTrue(exec.getExecType() == TBaseType.metExecSp);
        assertTrue(exec.getModuleName().toString().equalsIgnoreCase("dbo.uspGetEmployeeManagers"));
        TExecParameter p = exec.getParameters().getExecParameter(0);
        assertTrue(p.getParameterValue().toString().equalsIgnoreCase("6"));

    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "dbo.uspGetEmployeeManagers 6;";
        assertTrue(sqlparser.parse() == 0);
        // System.out.println(sqlparser.sqlstatements.get(0).toString());

        TMssqlExecute exec = (TMssqlExecute)sqlparser.sqlstatements.get(0);
        assertTrue(exec.getExecType() == TBaseType.metNoExecKeyword);
        assertTrue(exec.getModuleName().getDbObjectType() == EDbObjectType.procedure);
        assertTrue(exec.getModuleName().toString().equalsIgnoreCase("dbo.uspGetEmployeeManagers"));
        /*
        TExecParameter p = exec.getParameters().getExecParameter(0);
        assertTrue(p.getParameterValue().toString().equalsIgnoreCase("6"));
          */
    }


    public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "exec dbo.uspGetEmployeeManagers @BusinessEntityID = 6;";
        assertTrue(sqlparser.parse() == 0);

        TMssqlExecute exec = (TMssqlExecute)sqlparser.sqlstatements.get(0);
        assertTrue(exec.getExecType() == TBaseType.metExecSp);
        assertTrue(exec.getModuleName().toString().equalsIgnoreCase("dbo.uspGetEmployeeManagers"));

        TExecParameter p = exec.getParameters().getExecParameter(0);
        assertTrue(p.getParameterName().toString().equalsIgnoreCase("@BusinessEntityID"));
        assertTrue(p.getParameterValue().toString().equalsIgnoreCase("6"));

    }

    public void test4(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "dbo.uspGetEmployeeManagers @BusinessEntityID = 6;";
        assertTrue(sqlparser.parse() == 0);

        TMssqlExecute exec = (TMssqlExecute)sqlparser.sqlstatements.get(0);
        assertTrue(exec.getExecType() == TBaseType.metNoExecKeyword);
        assertTrue(exec.getModuleName().toString().equalsIgnoreCase("dbo.uspGetEmployeeManagers"));

        //TExecParameter p = exec.getParameters().getExecParameter(0);
        //assertTrue(p.getParameterName().toString().equalsIgnoreCase("@BusinessEntityID"));
        //assertTrue(p.getParameterValue().toString().equalsIgnoreCase("6"));

    }

}
