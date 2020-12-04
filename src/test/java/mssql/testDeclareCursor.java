package mssql;
/*
 * Date: 12-3-12
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.mssql.TMssqlDeclare;
import junit.framework.TestCase;

public class testDeclareCursor extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "   declare sds_post_asset_identifier cursor for\n" +
                "   select distinct\n" +
                "          asset_id          \n" +
                "        , asset_name_1     \n" +
                "  \tfrom asd_wk_asset_identifier\n" +
                "     where last_mod_tmstmp < @source_start_time\n" +
                "\torder by asset_id, last_mod_tmstmp";
        assertTrue(sqlparser.parse() == 0);

        TMssqlDeclare declare = (TMssqlDeclare)sqlparser.sqlstatements.get(0);
        assertTrue(declare.getCursorName().toString().equalsIgnoreCase("sds_post_asset_identifier"));

    }
}
