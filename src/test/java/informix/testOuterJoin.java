package informix;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TInformixOuterClause;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;


public class testOuterJoin  extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvinformix);
        sqlparser.sqltext = "SELECT c.company, o.order_date, i.total_price, m.manu_name\n" +
                "   FROM customer c, \n" +
                "      OUTER (orders o, OUTER (items i, OUTER manufact m))\n" +
                "   WHERE c.customer_num = o.customer_num\n" +
                "      AND o.order_num = i.order_num \n" +
                "      AND i.manu_code = m.manu_code;";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement stmt = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(stmt.getJoins().size() == 2);
        TJoin join0 = stmt.getJoins().getJoin(0);
        assertTrue(join0.getTable().toString().equalsIgnoreCase("customer"));

        TJoin join1 = stmt.getJoins().getJoin(1);
        assertTrue(join1.getTable().getTableType() == ETableSource.informixOuter);
        TInformixOuterClause outerClause = join1.getTable().getOuterClause();
        assertTrue(outerClause.getTableList().size() == 2);
        TTable table0 = outerClause.getTableList().getTable(0);
        assertTrue(table0.toString().equalsIgnoreCase("orders"));

        TTable table1 = outerClause.getTableList().getTable(1);
        assertTrue(table1.getTableType() == ETableSource.informixOuter);
        outerClause = table1.getOuterClause();
        assertTrue(outerClause.getTableList().size() == 2);
        table0 = outerClause.getTableList().getTable(0);
        assertTrue(table0.toString().equalsIgnoreCase("items"));
        table1 = outerClause.getTableList().getTable(1);
        assertTrue(table1.getTableType() == ETableSource.informixOuter);
        outerClause = table1.getOuterClause();
        assertTrue(outerClause.getTableList().size() == 1);
        table0 = outerClause.getTableList().getTable(0);
        assertTrue(table0.toString().equalsIgnoreCase("manufact"));
    }
}
