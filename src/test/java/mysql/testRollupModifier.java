package mysql;
/*
 * Date: 13-10-29
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TGroupBy;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testRollupModifier extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "SELECT year, SUM(profit) FROM sales GROUP BY year WITH ROLLUP;";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TGroupBy groupBy = selectSqlStatement.getGroupByClause();
        assertTrue(groupBy.isRollupModifier());
    }

}
