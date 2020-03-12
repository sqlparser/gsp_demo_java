package teradata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testCastDate extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "select ydLLdNG_mTzTEMENT_dD\n" +
                "              ,Col1_DT (date) cust_Dt\n" +
                "              ,PRORzTE_dND\n" +
                "         FROM zxxountLevelxhzrgem\n" +
                "              WHERE ydLL_mTzTEMENT_xHzRGE_zMT > 0";

        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn resultColumn = select.getResultColumnList().getResultColumn(1);
        assertTrue(resultColumn.getAliasClause().toString().equalsIgnoreCase("cust_Dt"));
        assertTrue(resultColumn.getExpr().getExpressionType() == EExpressionType.simple_object_name_t);
        assertTrue(resultColumn.getExpr().getObjectOperand().toString().equalsIgnoreCase("Col1_DT"));

    }
}
