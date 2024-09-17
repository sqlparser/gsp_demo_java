package gudusoft.gsqlparser.teradataTest;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EJoinType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TJoinItem;
import gudusoft.gsqlparser.nodes.TJoinList;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testJoins extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "select *\n" +
                "from\n" +
                "   table1 as t1\n" +
                "      cross join table2 as t2\n" +
                "      inner join ttt as s1 on t1.col1 = t2.col2\n" +
                "            ;";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);

        TJoinList joins = selectSqlStatement.joins;
        assertTrue(joins.size() == 1);
        TJoin join = joins.getJoin(0);
        assertTrue(join.getTable().toString().equalsIgnoreCase("table1"));
        assertTrue(join.getJoinItems().size() == 2);
        TJoinItem  joinItem0 = join.getJoinItems().getJoinItem(0);
        TJoinItem  joinItem1 = join.getJoinItems().getJoinItem(1);
        assertTrue(joinItem0.getTable().toString().equalsIgnoreCase("table2"));
        assertTrue(joinItem0.getJoinType() == EJoinType.cross);
        assertTrue(joinItem1.getTable().toString().equalsIgnoreCase("ttt"));
        assertTrue(joinItem1.getJoinType() == EJoinType.inner);
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "select *\n" +
                "from\n" +
                "   table1 as t1\n" +
                "      cross join table2 as t2\n" +
                "      left join ttt as s1 on t1.col1 = t2.col2\n" +
                "            ;";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);

        TJoinList joins = selectSqlStatement.joins;
        assertTrue(joins.size() == 1);
        TJoin join = joins.getJoin(0);
        assertTrue(join.getTable().toString().equalsIgnoreCase("table1"));
        assertTrue(join.getJoinItems().size() == 2);
        TJoinItem  joinItem0 = join.getJoinItems().getJoinItem(0);
        TJoinItem  joinItem1 = join.getJoinItems().getJoinItem(1);
        assertTrue(joinItem0.getTable().toString().equalsIgnoreCase("table2"));
        assertTrue(joinItem0.getJoinType() == EJoinType.cross);
        assertTrue(joinItem1.getTable().toString().equalsIgnoreCase("ttt"));
        assertTrue(joinItem1.getJoinType() == EJoinType.left);
    }
}
