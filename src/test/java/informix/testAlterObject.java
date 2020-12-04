package informix;
/*
 * Date: 13-1-25
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TAlterIndexStmt;
import junit.framework.TestCase;

public class testAlterObject extends TestCase {

    public void testIndex(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvinformix);
        sqlparser.sqltext = "ALTER INDEX ix_cust TO NOT CLUSTER;";
        assertTrue(sqlparser.parse() == 0);

        TAlterIndexStmt stmt = (TAlterIndexStmt)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getIndexName().toString().equalsIgnoreCase("ix_cust"));
    }

}
