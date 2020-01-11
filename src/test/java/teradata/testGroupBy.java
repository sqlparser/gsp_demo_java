package teradata;
/*
 * Date: 13-2-8
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TGroupBy;
import gudusoft.gsqlparser.nodes.TOrderBy;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testGroupBy extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT\n" +
                "\tSECR_EXPSR_ID\n" +
                "FROM\n" +
                "\tENTR.SECR_EXPSR\n" +
                "GROUP BY (1,2,3,4,5,6,7,8,9,10,11,12)\n" +
                ";\n" +
                "";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);

        TGroupBy groupBy = select.getGroupByClause();
        assertTrue(groupBy.getItems().size() == 12);
        assertTrue(groupBy.getItems().getGroupByItem(0).getExpr().toString().equalsIgnoreCase("1"));
        assertTrue(groupBy.getItems().getGroupByItem(11).getExpr().toString().equalsIgnoreCase("12"));

    }

    public void testHavingAfterOrderBy(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT\n" +
                "       ANA_Code\n" +
                ",      Transaction_Date\n" +
                ",      Transaction_Time\n" +
                ",      Till_Number\n" +
                ",      Transaction_Number\n" +
                "FROM\n" +
                "       WT_EPOSAP_Basket_Sales_Final\n" +
                "GROUP BY\n" +
                "       1,2,3,4,5\n" +
                "ORDER BY\n" +
                "       1,2,3,4,5\n" +
                "HAVING COUNT(*)>1";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);

        TGroupBy groupBy = select.getGroupByClause();
        assertTrue(groupBy.getHavingClause().toString().equalsIgnoreCase("COUNT(*)>1"));
        assertTrue(groupBy.getItems().size() == 5);
        assertTrue(groupBy.getItems().getGroupByItem(0).getExpr().toString().equalsIgnoreCase("1"));
        TOrderBy orderBy = select.getOrderbyClause();
        assertTrue(orderBy.getItems().size() == 5);
        assertTrue(orderBy.getItems().getOrderByItem(1).getSortKey().toString().equalsIgnoreCase("2"));

    }

}
