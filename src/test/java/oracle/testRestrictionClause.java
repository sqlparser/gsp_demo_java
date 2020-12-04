package oracle;
/*
 * Date: 13-5-16
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ERestrictionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TRestrictionClause;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import junit.framework.TestCase;

public class testRestrictionClause extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE OR REPLACE VIEW v1(v_c1, v_c2, v_c3) AS SELECT C1, C2, C3 FROM T1\n" +
                "WITH CHECK OPTION CONSTRAINT SYS_Cn;";
        assertTrue(sqlparser.parse() == 0);

        TCreateViewSqlStatement viewSqlStatement = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(viewSqlStatement.getViewName().toString().equalsIgnoreCase("v1"));

        TRestrictionClause r = viewSqlStatement.getRestrictionClause();
//        assertTrue(r.getType() == TRestrictionClause.with_check_option);
        assertTrue(r.getRestrictionType() == ERestrictionType.withCheckOption);
        assertTrue(r.getConstraintName().toString().equalsIgnoreCase("SYS_Cn"));

    }

}
