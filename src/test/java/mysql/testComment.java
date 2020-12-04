package mysql;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testComment  extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "select --9223372036854775806, ---9223372036854775807, ----9223372036854775808";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn resultColumn = selectSqlStatement.getResultColumnList().getResultColumn(2);
    //    TExpression expr = resultColumn.getExpr();
      //  System.out.println(expr.getExpressionType());
      //  System.out.println(resultColumn.getExpr().getRightOperand().toString());
        assertTrue(resultColumn.getExpr().toString().equalsIgnoreCase("----9223372036854775808"));
    }

}
