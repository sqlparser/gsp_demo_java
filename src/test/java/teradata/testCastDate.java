package teradata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
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

    public void test2(){
        // mantisbt/view.php?id=1354
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "select hire_date(DATE) \n" +
                "from employee \n" +
                "where hire_date(DATE) = DATE '1971-10-18' \n" +
                "group by 1\n" +
                "having hire_date(DATE) = DATE '1971-10-18' \n" +
                "order by hire_date(DATE)";

        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn resultColumn = select.getResultColumnList().getResultColumn(0);
        //System.out.println(resultColumn.getExpr().getExpressionType());

        TExpression where = select.getWhereClause().getCondition().getLeftOperand();
        //System.out.println(where.getExpressionType());

        TExpression having = select.getGroupByClause().getHavingClause().getLeftOperand();
       // System.out.println(having.getExpressionType());

        TExpression order = select.getOrderbyClause().getItems().getOrderByItem(0).getSortKey();
        //System.out.println(order.getExpressionType());
    }

}
