package sybase;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.mssql.TMssqlExecute;
import junit.framework.TestCase;

public class testExecWithNoKeyword extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsybase);
        sqlparser.sqltext = "myStoredProc @Param1='XXXXX', @Param2='XXXXX', @Param3=@p0, @Param4=0, @Param5=00";
        int i = sqlparser.parse() ;
        assertTrue(i == 0);
        TMssqlExecute executeStmt  = (TMssqlExecute)sqlparser.sqlstatements.get(0);
        assertTrue(executeStmt.getModuleName().toString().equalsIgnoreCase("myStoredProc"));
        assertTrue(executeStmt.getParameters().size() == 5);
        assertTrue(executeStmt.getParameters().getExecParameter(0).getParameterName().toString().equalsIgnoreCase("@Param1"));
        assertTrue(executeStmt.getParameters().getExecParameter(0).getParameterValue().toString().equalsIgnoreCase("'XXXXX'"));
    }

}
