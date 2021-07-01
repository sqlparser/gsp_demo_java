package common;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TAlterViewStatement;
import junit.framework.TestCase;

public class testAlterView extends TestCase {

    public void test0(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvnetezza);
        sqlparser.sqltext = "ALTER VIEW view1 RENAME TO view2";
        assertTrue(sqlparser.parse() == 0);
        TAlterViewStatement alterViewStatement = (TAlterViewStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterViewStatement.getViewName().toString().equalsIgnoreCase("view1"));
        assertTrue(alterViewStatement.getNewViewName().toString().equalsIgnoreCase("view2"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsparksql);
        sqlparser.sqltext = "ALTER VIEW tempdb1.v2 AS SELECT * FROM tempdb1.v1;";
        assertTrue(sqlparser.parse() == 0);
        TAlterViewStatement alterViewStatement = (TAlterViewStatement)sqlparser.sqlstatements.get(0);
        assertTrue(alterViewStatement.getViewName().toString().equalsIgnoreCase("tempdb1.v2"));
        assertTrue(alterViewStatement.getSelectSqlStatement().getTables().getTable(0).toString().equalsIgnoreCase("tempdb1.v1"));
    }
}
