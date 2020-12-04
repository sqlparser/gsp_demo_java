package postgresql;
/*
 * Date: 11-5-19
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testWindowClause extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "SELECT sum(salary) OVER w, avg(salary) OVER w\n" +
                "  FROM empsalary\n" +
                "  WINDOW w AS (PARTITION BY depname ORDER BY salary DESC);";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
    }

}
