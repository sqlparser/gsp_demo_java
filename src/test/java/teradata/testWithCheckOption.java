package test.teradata;
/*
 * Date: 12-1-4
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TTeradataWithClause;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testWithCheckOption extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "CREATE VIEW DBC.Tables2\n" +
                "AS SELECT TVMNameI,\n" +
                "           TVMId,\n" +
                "           DatabaseId,\n" +
                "           ParentCount,\n" +
                "           ChildCount\n" +
                "    FROM DBC.TVM WITH CHECK OPTION;";

        assertTrue(sqlparser.parse() == 0);
        TCreateViewSqlStatement viewSqlStatement = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0);
        TSelectSqlStatement selectSqlStatement = viewSqlStatement.getSubquery();
        TTeradataWithClause withClause = selectSqlStatement.getTeradataWithClause();
        assertTrue(withClause.toString().equalsIgnoreCase("WITH CHECK OPTION"));

    }

}
