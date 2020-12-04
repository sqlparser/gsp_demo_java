package sybase;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testColumnName extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsybase);
        sqlparser.sqltext = "select P#_prem from tabc";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn resultColumn = select.getResultColumnList().getResultColumn(0);
        TExpression columnExpr = resultColumn.getExpr();
        assertTrue(columnExpr.getExpressionType() == EExpressionType.simple_object_name_t);
        TObjectName columnName = columnExpr.getObjectOperand();
        assertTrue(columnName.toString().equalsIgnoreCase("P#_prem"));
    }

}
