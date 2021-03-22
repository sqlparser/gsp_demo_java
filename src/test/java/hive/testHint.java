package hive;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.hive.THiveHintClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;


public class testHint extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "SELECT /*+ STREAMTABLE(a) */ a.val, b.val, c.val FROM a JOIN b ON (a.key = b.key1) JOIN c ON (c.key = b.key1);";
          assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        THiveHintClause hint = select.getHiveHintClause();
        assertTrue(hint.getHintList().getElement(0).getHintName().toString().equalsIgnoreCase("STREAMTABLE"));
        assertTrue(hint.getHintList().getElement(0).getHintArgs().getObjectName(0).toString().equalsIgnoreCase("a"));
    }

}
