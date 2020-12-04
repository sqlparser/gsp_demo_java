package sybase;
/*
 * Date: 14-6-12
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.sybase.TInsertBulk;
import junit.framework.TestCase;

public class testInsertBulk extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsybase);
        sqlparser.sqltext = "insert bulk tempdb.test.account with nodescribe";
        int i = sqlparser.parse() ;
        assertTrue(i == 0);
        TInsertBulk insertBulk = (TInsertBulk)sqlparser.sqlstatements.get(0);
        assertTrue(insertBulk.getTableName().toString().equalsIgnoreCase("tempdb.test.account"));
    }
}
