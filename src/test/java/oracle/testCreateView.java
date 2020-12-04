package oracle;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ERestrictionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TRestrictionClause;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import junit.framework.TestCase;

public class testCreateView extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "CREATE OR REPLACE VIEW rule11001_1 AS SELECT * FROM t1 WITH READ ONLY";
        assertTrue(sqlparser.parse() == 0);
        TCreateViewSqlStatement createViewSqlStatement = (TCreateViewSqlStatement )sqlparser.sqlstatements.get(0);
        assertTrue(createViewSqlStatement.getViewName().toString().equalsIgnoreCase("rule11001_1"));

        TRestrictionClause restrictClause = createViewSqlStatement.getRestrictionClause();
        assertTrue(restrictClause.getRestrictionType() == ERestrictionType.withReadOnly);
    }

}
