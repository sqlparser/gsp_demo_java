package mysql;
/*
 * Date: 13-5-29
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TJoinItem;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testFromTableList extends TestCase {

    public void testMultitables(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "SELECT * FROM t1 LEFT JOIN (t2, t3, t4) ON (t2.a=t1.a AND t3.b=t1.b AND t4.c=t1.c)";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TJoinItem joinItem = select.joins.getJoin(0).getJoinItems().getJoinItem(0);
        assertTrue(joinItem.getKind() == TBaseType.join_source_table);
        assertTrue(joinItem.getTable().getFromTableList().size() == 3);
        assertTrue(joinItem.getTable().getFromTableList().getFromTable(0).toString().equalsIgnoreCase("t2"));
        assertTrue(joinItem.getTable().getFromTableList().getFromTable(1).toString().equalsIgnoreCase("t3"));
        assertTrue(joinItem.getTable().getFromTableList().getFromTable(2).toString().equalsIgnoreCase("t4"));


    }

}
