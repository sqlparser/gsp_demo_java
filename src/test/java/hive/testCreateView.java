package hive;
/*
 * Date: 13-8-16
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testCreateView extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "CREATE VIEW V4 AS\n" +
                "  SELECT src1.key, src2.value as value1, src3.value as value2\n" +
                "  FROM V1 src1 JOIN V2 src2 on src1.key = src2.key JOIN src src3 ON src2.key = src3.key;";
        assertTrue(sqlparser.parse() == 0);

        TCreateViewSqlStatement createView = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createView.getViewName().toString().equalsIgnoreCase("V4"));

        TSelectSqlStatement select = createView.getSubquery();
        assertTrue(select.getResultColumnList().size() == 3);
    }

}
