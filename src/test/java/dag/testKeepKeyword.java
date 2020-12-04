package dag;
/*
 * Date: 11-4-6
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testKeepKeyword extends TestCase {

    public static void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "create view rc_backup_set\n" +
                "as\n" +
                "select decode(keep_options, 0, 'no', 'yes') keep\n" +
                "from db, bs\n" +
                "where db.db_key = bs.db_key";
       assertTrue(sqlparser.parse()==0);
    }

}
