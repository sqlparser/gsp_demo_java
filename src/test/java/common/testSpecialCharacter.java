package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testSpecialCharacter extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE OR REPLACE VIEW test_view AS\n" +
                "Select CASE WHEN test.code_number IN ('05','09','10','11','13','14','18','19','31','34','36','37','38','55') then 'Y' else 'N' end as code_number\n" +
                "from test.table";
        assertTrue(sqlparser.parse() == 0);
    }
}
