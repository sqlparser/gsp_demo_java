package postgresql;
/*
 * Date: 11-5-23
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testSubscript extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "select " +
                "arraycolumn[4]," +
                "mytable.arraycolumn1[40]," +
                "mytable.two_d_column[17][34]," +
                "c[10:42]," +
                "(arrayfunction(a,b))[42]," +
                "$1[10:42] " +
                "from t";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column0 = select.getResultColumnList().getResultColumn(0);
        TExpression expr0 = column0.getExpr();
        TObjectName objectName0 = expr0.getObjectOperand();
        assertTrue(objectName0.getColumnNameOnly().equalsIgnoreCase("arraycolumn"));
        assertTrue(objectName0.toString().equalsIgnoreCase("arraycolumn[4]"));
        assertTrue(objectName0.isSubscripts());
        assertTrue(objectName0.getIndirection().toString().equalsIgnoreCase("[4]"));
        assertTrue(objectName0.getIndirection().getIndices().getElement(0).getLowerSubscript().toString().equalsIgnoreCase("4"));

        TResultColumn column1 = select.getResultColumnList().getResultColumn(1);
        TExpression expr1 = column1.getExpr();
        TObjectName objectName1 = expr1.getObjectOperand();
        assertTrue(objectName1.getColumnNameOnly().equalsIgnoreCase("arraycolumn1"));
        assertTrue(objectName1.toString().equalsIgnoreCase("mytable.arraycolumn1[40]"));
        assertTrue(objectName1.isSubscripts());
        //System.out.println(objectName1.getIndirection().toString());
        //assertTrue(objectName1.getIndirection().toString().equalsIgnoreCase("[40]"));
       // assertTrue(objectName1.getIndirection().getIndices().getElement(0).getLowerSubscript().toString().equalsIgnoreCase("40"));
        assertTrue(objectName1.getIndirection().getIndices().getElement(0).getAttributeName().toString().equalsIgnoreCase("arraycolumn1"));
        assertTrue(objectName1.getIndirection().getIndices().getElement(1).getLowerSubscript().toString().equalsIgnoreCase("40"));

        //mytable.two_d_column[17][34]
        TResultColumn column2 = select.getResultColumnList().getResultColumn(2);
        TExpression expr2 = column2.getExpr();
        TObjectName objectName2 = expr2.getObjectOperand();
        assertTrue(objectName2.getColumnNameOnly().equalsIgnoreCase("two_d_column"));
        assertTrue(objectName2.toString().equalsIgnoreCase("mytable.two_d_column[17][34]"));
        assertTrue(objectName2.isSubscripts());
        //assertTrue(objectName2.getIndirection().toString().equalsIgnoreCase("[17][34]"));
        assertTrue(objectName2.getIndirection().getIndices().size() == 3);
        assertTrue(objectName2.getIndirection().getIndices().getElement(0).getAttributeName().toString().equalsIgnoreCase("two_d_column"));
        assertTrue(objectName2.getIndirection().getIndices().getElement(1).getLowerSubscript().toString().equalsIgnoreCase("17"));
        assertTrue(objectName2.getIndirection().getIndices().getElement(2).getLowerSubscript().toString().equalsIgnoreCase("34"));

        //c[10:42]
        TResultColumn column3 = select.getResultColumnList().getResultColumn(3);
        TExpression expr3 = column3.getExpr();
        TObjectName objectName3 = expr3.getObjectOperand();
        assertTrue(objectName3.getColumnNameOnly().equalsIgnoreCase("c"));
        assertTrue(objectName3.toString().equalsIgnoreCase("c[10:42]"));
        assertTrue(objectName3.isSubscripts());
        assertTrue(objectName3.getIndirection().toString().equalsIgnoreCase("[10:42]"));
        assertTrue(objectName3.getIndirection().getIndices().getElement(0).getLowerSubscript().toString().equalsIgnoreCase("10"));
        assertTrue(objectName3.getIndirection().getIndices().getElement(0).getUpperSubscript().toString().equalsIgnoreCase("42"));

        //(arrayfunction(a,b))[42]
        TResultColumn column4 = select.getResultColumnList().getResultColumn(4);
        TExpression expr4 = column4.getExpr();
        assertTrue(expr4.toString().equalsIgnoreCase("(arrayfunction(a,b))[42]"));
        assertTrue(expr4.isSubscripts());
        assertTrue(expr4.getIndirection().toString().equalsIgnoreCase("[42]"));
        assertTrue(expr4.getIndirection().getIndices().getElement(0).getLowerSubscript().toString().equalsIgnoreCase("42"));

//        System.out.println(objectName0.toString());

    }

}
