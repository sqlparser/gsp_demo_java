package gudusoft.gsqlparser.teradataTest;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExecParameter;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.teradata.TTeradataExecute;
import junit.framework.TestCase;

public class testExecuteMacro  extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "EXEC add_log_info('1', CAST('2016-02-23' AS DATE FORMAT 'YYYY-MM-DD'), 'CAD_RK', 'Blad CAD_RK_P_K42', -1);";
        assertTrue(sqlparser.parse() == 0);
        TTeradataExecute execute = (TTeradataExecute)sqlparser.sqlstatements.get(0);

        assertTrue(execute.getMacroName().toString().equalsIgnoreCase("add_log_info"));
        assertTrue(execute.getParameters().size() == 5);
        assertTrue(execute.getParameters().getExecParameter(0).getParameterValue().toString().equalsIgnoreCase("'1'"));
        TExpression expr = execute.getParameters().getExecParameter(1).getParameterValue();
        assertTrue(expr.getExpressionType() == EExpressionType.function_t);
        assertTrue(expr.getFunctionCall().getFunctionName().toString().equalsIgnoreCase("CAST"));

    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "EXECUTE new_hire (fl_name='Toby Smith', title='Programmer',doh=DATE -1);";
        assertTrue(sqlparser.parse() == 0);
        TTeradataExecute execute = (TTeradataExecute)sqlparser.sqlstatements.get(0);

        assertTrue(execute.getMacroName().toString().equalsIgnoreCase("new_hire"));
        TExecParameter parameter = execute.getParameters().getExecParameter(0);
        assertTrue(parameter.getParameterName().toString().equalsIgnoreCase("fl_name"));
        assertTrue(parameter.getParameterValue().toString().equalsIgnoreCase("'Toby Smith'"));

    }

}