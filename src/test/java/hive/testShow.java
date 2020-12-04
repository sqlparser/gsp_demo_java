package hive;
/*
 * Date: 13-8-16
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.hive.EHiveDescOption;
import gudusoft.gsqlparser.nodes.hive.EHiveShowType;
import gudusoft.gsqlparser.stmt.hive.THiveShow;
import junit.framework.TestCase;

public class testShow extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "SHOW FORMATTED INDEX ON table03;";
        assertTrue(sqlparser.parse() == 0);

        THiveShow   show = (THiveShow)sqlparser.sqlstatements.get(0);
        assertTrue(show.getShowType() == EHiveShowType.stIndexes);
        assertTrue(show.getShowOptions() == EHiveDescOption.doFormatted);
        assertTrue(show.getTableName().toString().equalsIgnoreCase("table03"));
    }
}
