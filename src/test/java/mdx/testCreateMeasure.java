package mdx;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.EFunctionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.stmt.mdx.TMdxCreateMeasure;
import junit.framework.TestCase;

public class testCreateMeasure extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmdx);
        sqlparser.sqltext = "CREATE MEASURE 'Advances'[Dec Notional]\n" +
                "\t=SUMX(FILTER('Advances',Format([CalendarDate],\"MM/DD/YYYY\") =Format( \"12/31/2015\", \"MM/DD/YYYY\")), [Notional Outstanding]);\n";
        int i = sqlparser.parse();
        assertTrue( i== 0);
        TMdxCreateMeasure createMeasure = (TMdxCreateMeasure)sqlparser.sqlstatements.get(0);
        assertTrue(createMeasure.getTableName().equalsIgnoreCase("'Advances'"));
        assertTrue(createMeasure.getMeasureName().equalsIgnoreCase("[Dec Notional]"));
        TExpression daxExpression = createMeasure.getDaxExpression();
        assertTrue(daxExpression.getExpressionType() == EExpressionType.function_t);
        TFunctionCall functionCall = daxExpression.getFunctionCall();
        assertTrue(functionCall.getFunctionType() == EFunctionType.sumx_t);
    }
}
