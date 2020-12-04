package oracle;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EJoinType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TJoinItem;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testNaturalJoin extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "SELECT times.time_id, product, quantity FROM inventory NATURAL LEFT OUTER JOIN t1;";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TJoin lcJoin = select.joins.getJoin(0);

        TJoinItem lcitem = lcJoin.getJoinItems().getJoinItem(0);
        assertTrue(lcitem.getJoinType() == EJoinType.natural_leftouter);


    }

}
