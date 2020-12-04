package oracle;



import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TAlterMaterializedViewStmt;

import junit.framework.TestCase;

public class testAlterMaterializedView extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "ALTER MATERIALIZED VIEW s1_mv1 REFRESH COMPLETE START WITH sysdate NEXT SYSDATE + 1/24;";
        assertTrue(sqlparser.parse() == 0);

        TAlterMaterializedViewStmt alterMaterializedViewStmt = (TAlterMaterializedViewStmt)sqlparser.sqlstatements.get(0);
        assertTrue(alterMaterializedViewStmt.getMaterializedViewName().toString().equalsIgnoreCase("s1_mv1"));

    }
}
