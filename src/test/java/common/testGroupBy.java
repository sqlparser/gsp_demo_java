package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TGroupBy;
import gudusoft.gsqlparser.nodes.TGroupByItemList;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testGroupBy extends TestCase {

    public void testGroupByClause() {
        TGSqlParser tgSqlParser = new TGSqlParser(EDbVendor.dbvhive);

        tgSqlParser.sqltext = "SELECT Dept,count(*) as NUM FROM employee GROUP BY DEPT HAVING NUM > 30000";

        assertTrue(tgSqlParser.parse() == 0);
        TSelectSqlStatement sqlstatements = (TSelectSqlStatement) tgSqlParser.getSqlstatements().get(0);
        TGroupBy groupByClause = sqlstatements.getGroupByClause();
        assertTrue(groupByClause.toString().equalsIgnoreCase("GROUP BY DEPT HAVING NUM > 30000"));

        TGroupByItemList items = groupByClause.getItems();
        assertTrue(items.toString().equalsIgnoreCase("DEPT"));
        assertTrue(items.getGroupByItem(0).toString().equalsIgnoreCase("DEPT"));

        TSourceToken group = groupByClause.getGROUP();
        assertTrue(group.toString().equalsIgnoreCase("GROUP"));

        TSourceToken by = groupByClause.getBY();
        assertTrue(by.toString().equalsIgnoreCase("BY"));

        TExpression havingClause = groupByClause.getHavingClause();
        assertTrue(havingClause.toString().equalsIgnoreCase("NUM > 30000"));

    }
}
