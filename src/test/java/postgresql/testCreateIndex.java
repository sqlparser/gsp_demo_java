package postgresql;
/*
 * Date: 11-6-22
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateIndexSqlStatement;
import junit.framework.TestCase;

public class testCreateIndex extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "CREATE UNIQUE INDEX CONCURRENTLY dist_id_temp_idx ON distributors (dist_id)";

        assertTrue(sqlparser.parse() == 0);

        TCreateIndexSqlStatement createIndexSqlStatement = (TCreateIndexSqlStatement)sqlparser.sqlstatements.get(0);
        //System.out.println(createIndexSqlStatement.getIndexName().toString());
       //  System.out.println(createIndexSqlStatement.toString());
    }

}
