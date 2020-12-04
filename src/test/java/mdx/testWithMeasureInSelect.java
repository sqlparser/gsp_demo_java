package mdx;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.EFunctionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.stmt.mdx.TMdxSelect;
import junit.framework.TestCase;

public class testWithMeasureInSelect extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmdx);
        sqlparser.sqltext = "with measure  'Sales Territory'[Total Sales Amount] = SUM('Internet Sales'[Sales Amount]) + SUM('Reseller Sales'[Sales Amount])\n" +
                "select measures.[Total Sales Amount] on columns\n" +
                "     ,NON EMPTY [Date].[Calendar Year].children on rows\n" +
                "from [Model]";
        int i = sqlparser.parse();
        assertTrue( i== 0);
        TMdxSelect select = (TMdxSelect)sqlparser.sqlstatements.get(0);
        assertTrue(select.getWithMeasure().getTableName().equalsIgnoreCase("'Sales Territory'"));
        assertTrue(select.getWithMeasure().getMeasureName().equalsIgnoreCase("[Total Sales Amount]"));
        TExpression daxExpression = select.getWithMeasure().getDaxExpression();
        assertTrue(daxExpression.getExpressionType() == EExpressionType.arithmetic_plus_t);
        TFunctionCall functionCall = daxExpression.getLeftOperand().getFunctionCall();
        assertTrue(functionCall.getFunctionType() == EFunctionType.builtin_t);
        assertTrue(functionCall.getFunctionName().toString().equalsIgnoreCase("SUM"));
    }

}
