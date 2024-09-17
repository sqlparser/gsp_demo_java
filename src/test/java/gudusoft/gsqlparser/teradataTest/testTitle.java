package gudusoft.gsqlparser.teradataTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;



public class testTitle extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT (1000/salary)*100 (FORMAT 'zz9%')\n" +
                "(TITLE 'Percent Incr')\n" +
                "FROM employee\n" +
                "WHERE empno = 10019 ;";


        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn resultColumn = select.getResultColumnList().getResultColumn(0);
        assertTrue(resultColumn.getExpr().toString().equalsIgnoreCase("(1000/salary)*100 (FORMAT 'zz9%')\n" +
                "(TITLE 'Percent Incr')"));

    }

}