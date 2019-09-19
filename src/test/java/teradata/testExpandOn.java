package test.teradata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.nodes.teradata.TExpandOnClause;
import junit.framework.TestCase;

public class testExpandOn extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "    SELECT id, BEGIN(bg)\n" +
                "    FROM tdate\n" +
                "    EXPAND ON pd AS bg BY ANCHOR MONDAY;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpandOnClause expandOnClause = select.getExpandOnClause();
        assertTrue(expandOnClause.getExpandExpression().toString().equalsIgnoreCase("pd"));
        assertTrue(expandOnClause.getExpandColumnAlias().toString().equalsIgnoreCase("bg"));
        assertTrue(expandOnClause.getAnchorName().toString().equalsIgnoreCase("MONDAY"));

    }

}