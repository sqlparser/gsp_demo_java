package postgresql;
/*
 * Date: 11-5-23
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testFieldSelection extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "select " +
                "arraycolumn," +
                "$1.somecolumn1," +
                "(rowfunction(a,b)).col3," +
                "(compositecol).somefield," +
                "(mytable.compositecol).somefield," +
                "(compositecol).* " +
                "from t";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expr0 = column0.getExpr();
        TObjectName objectName0 = expr0.getObjectOperand();
        assertTrue(!objectName0.isSubscripts());
        assertTrue(objectName0.toString().equalsIgnoreCase("arraycolumn"));

        TResultColumn column1 = select.getResultColumnList().getResultColumn(1);
        TExpression expr1 = column1.getExpr();
        TObjectName objectName1 = expr1.getObjectOperand();
        assertTrue(!objectName1.isSubscripts());
        assertTrue(objectName1.toString().equalsIgnoreCase("$1.somecolumn1"));

        //(rowfunction(a,b)).col3
        TResultColumn column2 = select.getResultColumnList().getResultColumn(2);
        TExpression expr2 = column2.getExpr();
        assertTrue(!expr2.isSubscripts());
        assertTrue(expr2.toString().equalsIgnoreCase("(rowfunction(a,b)).col3"));
        assertTrue(expr2.getFieldName().toString().equalsIgnoreCase("col3"));
        assertTrue(expr2.getExpressionType() == EExpressionType.fieldselection_t);
        assertTrue(expr2.getLeftOperand().toString().equalsIgnoreCase("rowfunction(a,b)"));

        //(compositecol).somefield
        TResultColumn column3 = select.getResultColumnList().getResultColumn(3);
        TExpression expr3 = column3.getExpr();
        assertTrue(!expr3.isSubscripts());
        assertTrue(expr3.toString().equalsIgnoreCase("(compositecol).somefield"));
        assertTrue(expr3.getFieldName().toString().equalsIgnoreCase("somefield"));
        assertTrue(expr3.getExpressionType() == EExpressionType.fieldselection_t);
        assertTrue(expr3.getLeftOperand().toString().equalsIgnoreCase("compositecol"));

        //(compositecol).*
        TResultColumn column5 = select.getResultColumnList().getResultColumn(5);
        TExpression expr5 = column5.getExpr();
        assertTrue(!expr5.isSubscripts());
        assertTrue(expr5.toString().equalsIgnoreCase("(compositecol).*"));
        assertTrue(expr5.getFieldName().toString().equalsIgnoreCase("*"));
        assertTrue(expr5.getExpressionType() == EExpressionType.fieldselection_t);
        assertTrue(expr5.getLeftOperand().toString().equalsIgnoreCase("compositecol"));

        //System.out.println(expr2.getLeftOperand().toString());
    }

}
