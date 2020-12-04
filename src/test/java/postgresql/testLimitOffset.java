package postgresql;
/*
 * Date: 14-2-7
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TLimitClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testLimitOffset extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "select 1 from t limit 10 offset 5;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TLimitClause selectLimit = select.getLimitClause();
        assertTrue(selectLimit.getRow_count().toString().equalsIgnoreCase("10"));
        assertTrue(selectLimit.getOffset().toString().equalsIgnoreCase("5"));
    }


}
