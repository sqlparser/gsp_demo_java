package common;
/*
 * Date: 2010-9-24
 * Time: 15:27:31
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testSqlserverProprietaryColumn extends TestCase {
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
        // sql server proprietary columna alias
        parser.sqltext = "select x = id";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        TResultColumn column = select.getResultColumnList().getResultColumn(0);
        TExpression columnExpr = column.getExpr();
        assertTrue(columnExpr.getExpressionType() == EExpressionType.sqlserver_proprietary_column_alias_t);
        // column alias is x
        assertTrue(columnExpr.getLeftOperand().toString().equalsIgnoreCase("x"));

        // colunn is id
        assertTrue(columnExpr.getRightOperand().toString().equalsIgnoreCase("id"));

        //System.out.println(columnExpr.getLeftOperand().toString());
        //System.out.println(columnExpr.getRightOperand().toString());
  
    }
 
    public void test2(){
        // not sql server proprietary columna alias
        parser.sqltext = "select @x = id";
        assertTrue(parser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        TResultColumn column = select.getResultColumnList().getResultColumn(0);
        TExpression columnExpr = column.getExpr();
        assertTrue(columnExpr.getExpressionType() != EExpressionType.sqlserver_proprietary_column_alias_t);
        //System.out.println(columnExpr.getLeftOperand().toString());
        //System.out.println(columnExpr.getRightOperand().toString());
    }
}