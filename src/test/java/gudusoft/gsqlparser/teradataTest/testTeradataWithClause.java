package gudusoft.gsqlparser.teradataTest;
/*
 * Date: 2010-10-18
 * Time: 17:33:46
 */

import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TTeradataWithClause;
import gudusoft.gsqlparser.nodes.TTeradataWithClauseItem;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class testTeradataWithClause extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT name, deptno, salary\n" +
                "FROM employee\n" +
                "WITH SUM(salary),ss BY deptno\n" +
                "WITH SUM(salary);";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TTeradataWithClause withClause = select.getTeradataWithClause();
        assertTrue(withClause.getItems().size() == 2);

        TTeradataWithClauseItem item0 = withClause.getItems().getWithClauseItem(0);
        assertTrue(item0.getExprList().toString().equalsIgnoreCase("SUM(salary),ss"));
        assertTrue(item0.getByItemList().toString().equalsIgnoreCase("deptno"));

        TTeradataWithClauseItem item1 = withClause.getItems().getWithClauseItem(1);
        assertTrue(item1.getExprList().toString().equalsIgnoreCase("SUM(salary)"));


    }

}
