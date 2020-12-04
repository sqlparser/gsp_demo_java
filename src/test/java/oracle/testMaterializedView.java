package oracle;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TDropMaterializedViewStmt;
import junit.framework.TestCase;


public class testMaterializedView  extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "DROP MATERIALIZED VIEW schema1.sampleView PRESERVE TABLE;";
        assertTrue(sqlparser.parse() == 0);

        TDropMaterializedViewStmt dropMaterializedView = (TDropMaterializedViewStmt)sqlparser.sqlstatements.get(0);
        assertTrue(dropMaterializedView.getViewName().toString().equalsIgnoreCase("schema1.sampleView"));

    }

}
