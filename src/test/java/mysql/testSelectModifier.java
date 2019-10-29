package mysql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testSelectModifier extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "select all high_priority straight_join sql_small_result sql_big_result sql_buffer_result sql_no_cache sql_calc_found_rows name from t5;";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(select.getResultColumnList().size() == 1);
        assertTrue(select.getResultColumnList().getResultColumn(0).toString().equalsIgnoreCase("name"));
        assertTrue(select.getTables().getTable(0).toString().equalsIgnoreCase("t5"));

    }
}
