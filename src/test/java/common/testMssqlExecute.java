package common;
/*
 * Date: 2010-8-31
 * Time: 14:45:03
 */

import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TExecParameter;
import gudusoft.gsqlparser.stmt.mssql.TMssqlExecute;

public class testMssqlExecute extends TestCase {
    private TGSqlParser parser = null;

    protected void setUp() throws Exception {
        super.setUp();
        parser = new TGSqlParser(EDbVendor.dbvmssql);
    }

    protected void tearDown() throws Exception {
        parser = null;
        super.tearDown();
    }

    public void test1(){
        parser.sqltext = "EXECUTE @retstat = SQLSERVER1.AdventureWorks2008R2.dbo.uspGetEmployeeManagers @BusinessEntityID = 6;" ;
        assertTrue(parser.parse() == 0);
        TMssqlExecute stmt = (TMssqlExecute)parser.sqlstatements.get(0);
        //System.out.println(stmt.getModuleName().toString());
        assertTrue(stmt.getModuleName().toString().equalsIgnoreCase("SQLSERVER1.AdventureWorks2008R2.dbo.uspGetEmployeeManagers"));
        assertTrue(stmt.getModuleName().getServerToken().toString().equalsIgnoreCase("SQLSERVER1"));
        assertTrue(stmt.getModuleName().getDatabaseToken().toString().equalsIgnoreCase("AdventureWorks2008R2"));
        assertTrue(stmt.getModuleName().getSchemaToken().toString().equalsIgnoreCase("dbo"));
        assertTrue(stmt.getModuleName().getObjectToken().toString().equalsIgnoreCase("uspGetEmployeeManagers"));
        assertTrue(stmt.getReturnStatus().toString().equalsIgnoreCase("@retstat"));
        assertTrue(stmt.getParameters().size() == 1);
        TExecParameter p = (TExecParameter)stmt.getParameters().getExecParameter(0);
        assertTrue(p.getParameterName().toString().equalsIgnoreCase("@BusinessEntityID"));
        assertTrue(p.getParameterValue().toString().equalsIgnoreCase("6"));
    }
    
    public void test2(){
        parser.sqltext = "EXECUTE dbo.ProcTestDefaults DEFAULT, 'I', @p3 = DEFAULT;" ;
        assertTrue(parser.parse() == 0);
        TMssqlExecute stmt = (TMssqlExecute)parser.sqlstatements.get(0);
        //System.out.println(stmt.getModuleName().toString());
        assertTrue(stmt.getModuleName().toString().equalsIgnoreCase("dbo.ProcTestDefaults"));
        assertTrue(stmt.getReturnStatus() == null);
        assertTrue(stmt.getParameters().size() == 3);
        TExecParameter p = (TExecParameter)stmt.getParameters().getExecParameter(0);
        assertTrue(p.getParameterName() == null);
        assertTrue(p.getParameterValue().toString().equalsIgnoreCase("DEFAULT"));

        TExecParameter p3 = (TExecParameter)stmt.getParameters().getExecParameter(2);
        assertTrue(p3.getParameterName().toString().equalsIgnoreCase("@p3"));
        assertTrue(p3.getParameterValue().toString().equalsIgnoreCase("DEFAULT"));

    }
}
