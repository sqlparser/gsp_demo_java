package mysql;
/*
 * Date: 12-10-8
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testIdentifier extends TestCase {

    public void testIdentifierStartWithNumber(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "create table 9t(1a int)";
        assertTrue(sqlparser.parse() == 0);

    }

}
