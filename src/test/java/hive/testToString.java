package hive;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testToString extends TestCase {

    public void testGroupBy(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "select * from t1 group by c1 with rollup";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(select.getGroupByClause().toString().equalsIgnoreCase("group by c1 with rollup"));
       // System.out.println(select.getGroupByClause().toString());

    }

    public void testFromClause(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "select * from t1 join t2 on t1.c1 = t2.c2";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        //System.out.println(select.joins);
        assertTrue(select.joins.toString().equalsIgnoreCase("t1 join t2 on t1.c1 = t2.c2"));
        //assertTrue(select.getGroupByClause().toString().equalsIgnoreCase("group by c1"));

    }

}
