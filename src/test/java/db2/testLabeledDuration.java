package db2;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;


public class testLabeledDuration extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdb2);
        sqlparser.sqltext = "SELECT 5 SECONDS FROM TABLE_A";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = select.getResultColumnList().getResultColumn(0).getExpr();
       assertTrue(expr.toString().contentEquals("5 SECONDS"));
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdb2);
        sqlparser.sqltext = "SELECT COLUMN_A, CURRENT TIMESTAMP + 5 SECONDS AS COLUMN_B FROM TABLE_A";
        assertTrue(sqlparser.parse() == 0);
//        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
//        TExpression expr = select.getResultColumnList().getResultColumn(0).getExpr();
//        assertTrue(expr.toString().contentEquals("5 SECONDS"));
    }

}