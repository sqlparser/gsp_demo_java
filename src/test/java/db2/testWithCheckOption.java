package db2;
/*
 * Date: 11-6-7
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ERestrictionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TRestrictionClause;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import junit.framework.TestCase;

public class testWithCheckOption extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdb2);
        sqlparser.sqltext = "CREATE VIEW V2 AS SELECT COL1 FROM V1 WITH check option";
        assertTrue(sqlparser.parse() == 0);
        TCreateViewSqlStatement createView = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0);
        TRestrictionClause restrictionClause = createView.getRestrictionClause();
        assertTrue(restrictionClause.getRestrictionType() == ERestrictionType.withCheckOption);// TRestrictionClause.with_check_option);
        //System.out.println(restrictionClause.toString());
        //TSelectSqlStatement select = createView.getSubquery();
        //System.out.println(select.toString());
    }

}
