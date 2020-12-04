package sybase;
/*
 * Date: 14-11-11
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.sybase.TSybaseDeleteStatistics;
import junit.framework.TestCase;

public class testDeleteStatistics extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsybase);
        sqlparser.sqltext = "delete statistics titles (pub_id, pubdate)";
        int i = sqlparser.parse() ;
        assertTrue(i == 0);
        TSybaseDeleteStatistics deleteStatistics  = (TSybaseDeleteStatistics)sqlparser.sqlstatements.get(0);
        assertTrue(deleteStatistics.getTableName().toString().equalsIgnoreCase("titles"));
        assertTrue(deleteStatistics.getColumnList().getObjectName(0).toString().equalsIgnoreCase("pub_id"));
        assertTrue(deleteStatistics.getColumnList().getObjectName(1).toString().equalsIgnoreCase("pubdate"));
    }

}
