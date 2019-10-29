package test.mysql;
/*
 * Date: 12-3-9
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TJoinItem;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import junit.framework.TestCase;

public class testUpdateTargetTable extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "UPDATE table1 a\n" +
                "  INNER JOIN table2 b ON(a.field0=b.field0)\n" +
                "    SET a.field1 = 20120221\n" +
                "  WHERE b.field1 = 'D'\n" +
                "        AND b.field2 BETWEEN 20120217 and 20120219\n" +
                "        AND b.field3 != 0";
        assertTrue(sqlparser.parse() == 0);

        TUpdateSqlStatement updateSqlStatement =  (TUpdateSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(updateSqlStatement.getTargetTable().getFullName().equalsIgnoreCase("table1"));
        TJoinItem joinItem = updateSqlStatement.joins.getJoin(0).getJoinItems().getJoinItem(0);
        assertTrue(joinItem.getJoinType().toString().equalsIgnoreCase("inner"));
        assertTrue(joinItem.getTable().toString().equalsIgnoreCase("table2"));
        assertTrue(joinItem.getOnCondition().toString().equalsIgnoreCase("(a.field0=b.field0)"));
    }

    public void testModifier(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "update low_priority ignore t5 set name='B' where id=1";
        assertTrue(sqlparser.parse() == 0);

        TUpdateSqlStatement updateSqlStatement =  (TUpdateSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(updateSqlStatement.getTargetTable().getFullName().equalsIgnoreCase("t5"));
    }


}
