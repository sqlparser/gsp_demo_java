package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpressionList;
import gudusoft.gsqlparser.nodes.TDistributeBy;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testDistributeBy extends TestCase {

    public void testDistributeByClause() {
        TGSqlParser tgSqlParser = new TGSqlParser(EDbVendor.dbvhive);

        tgSqlParser.sqltext = "select mid, money, name from store distribute by mid sort by mid asc, money asc";

        assertTrue(tgSqlParser.parse() == 0);

        TSelectSqlStatement sqlstatements = (TSelectSqlStatement) tgSqlParser.getSqlstatements().get(0);
        TDistributeBy distributeBy = sqlstatements.getDistributeBy();
        assertTrue(distributeBy.toString().equalsIgnoreCase("distribute by mid"));
    }

    public void testDistributeByExprs() {
        TGSqlParser tgSqlParser = new TGSqlParser(EDbVendor.dbvhive);

        tgSqlParser.sqltext = "select mid, money, name from store distribute by mid,name sort by mid asc, money asc";

        assertTrue(tgSqlParser.parse() == 0);

        TSelectSqlStatement sqlstatements = (TSelectSqlStatement) tgSqlParser.getSqlstatements().get(0);
        TDistributeBy distributeBy = sqlstatements.getDistributeBy();
        assertTrue(distributeBy.toString().equalsIgnoreCase("distribute by mid,name"));

        TExpressionList expressionList = distributeBy.getExpressionList();
        assertTrue(expressionList.toString().equalsIgnoreCase("mid,name"));
        assertTrue(expressionList.getExpression(0).toString().equalsIgnoreCase("mid"));
        assertTrue(expressionList.getExpression(1).toString().equalsIgnoreCase("name"));
    }
}
