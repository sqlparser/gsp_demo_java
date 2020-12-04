package oracle;
/*
 * Date: 12-5-11
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testInsertMultiTable extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "INSERT ALL\n" +
                "INTO sales (prod_id, cust_id, time_id, amount)\n" +
                "VALUES (product_id, customer_id, weekly_start_date, sales_sun)\n" +
                "INTO sales (prod_id, cust_id, time_id, amount)\n" +
                "VALUES (product_id, customer_id, weekly_start_date+1, sales_mon)\n" +
                "INTO sales (prod_id, cust_id, time_id, amount)\n" +
                "VALUES (product_id, customer_id, weekly_start_date+2, sales_tue)\n" +
                "INTO sales (prod_id, cust_id, time_id, amount)\n" +
                "VALUES (product_id, customer_id, weekly_start_date+3, sales_wed)\n" +
                "INTO sales (prod_id, cust_id, time_id, amount)\n" +
                "VALUES (product_id, customer_id, weekly_start_date+4, sales_thu)\n" +
                "INTO sales (prod_id, cust_id, time_id, amount)\n" +
                "VALUES (product_id, customer_id, weekly_start_date+5, sales_fri)\n" +
                "INTO sales (prod_id, cust_id, time_id, amount)\n" +
                "VALUES (product_id, customer_id, weekly_start_date+6, sales_sat)\n" +
                "SELECT product_id, customer_id, weekly_start_date, sales_sun,\n" +
                "sales_mon, sales_tue, sales_wed, sales_thu, sales_fri, sales_sat\n" +
                "FROM sales_input_table;";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(insert.getInsertIntoValues().size() == 7);
        TInsertIntoValue insertIntoValue = insert.getInsertIntoValues().getElement(6);
        assertTrue(insertIntoValue.getTable().toString().equalsIgnoreCase("sales"));
        assertTrue(insertIntoValue.getColumnList().getObjectName(0).toString().equalsIgnoreCase("prod_id"));
        assertTrue(insertIntoValue.getColumnList().getObjectName(3).toString().equalsIgnoreCase("amount"));

        assertTrue(insertIntoValue.getTargetList().getMultiTarget(0).getColumnList().getResultColumn(0).toString().equalsIgnoreCase("product_id"));
        assertTrue(insertIntoValue.getTargetList().getMultiTarget(0).getColumnList().getResultColumn(3).toString().equalsIgnoreCase("sales_sat"));
       // System.out.println(insertIntoValue.getTargetList().size());

        TSelectSqlStatement select = insert.getSubQuery();
       assertTrue(select.getResultColumnList().getResultColumn(0).toString().equalsIgnoreCase("product_id"));
       assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("sales_input_table"));

    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "INSERT ALL\n" +
                "WHEN order_total < 1000000 THEN\n" +
                "INTO small_orders\n" +
                "WHEN order_total > 1000000 AND order_total < 2000000 THEN\n" +
                "INTO medium_orders\n" +
                "WHEN order_total > 2000000 THEN\n" +
                "INTO large_orders\n" +
                "SELECT order_id, order_total, sales_rep_id, customer_id\n" +
                "FROM orders;";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);

        assertTrue(insert.getInsertConditions().size() == 3);
        TInsertCondition condition = insert.getInsertConditions().getElement(0);

        assertTrue(condition.getCondition().getExpressionType() == EExpressionType.simple_comparison_t);
        assertTrue(condition.getCondition().toString().equalsIgnoreCase("order_total < 1000000"));

        TInsertIntoValue intoValue = condition.getInsertIntoValues().getElement(0);
        assertTrue(intoValue.getTable().toString().equalsIgnoreCase("small_orders"));

        TSelectSqlStatement select = insert.getSubQuery();
       assertTrue(select.getResultColumnList().getResultColumn(0).toString().equalsIgnoreCase("order_id"));
       assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("orders"));

    }

    public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "INSERT ALL\n" +
                "WHEN order_total < 100000 THEN\n" +
                "INTO small_orders\n" +
                "WHEN order_total > 100000 AND order_total < 200000 THEN\n" +
                "INTO medium_orders\n" +
                "ELSE\n" +
                "INTO large_orders\n" +
                "SELECT order_id, order_total, sales_rep_id, customer_id\n" +
                "FROM orders;";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insert = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);

        assertTrue(insert.getInsertConditions().size() == 2);
        TInsertCondition condition = insert.getInsertConditions().getElement(0);

        assertTrue(condition.getCondition().getExpressionType() == EExpressionType.simple_comparison_t);
        assertTrue(condition.getCondition().toString().equalsIgnoreCase("order_total < 100000"));

        TInsertIntoValue intoValue = condition.getInsertIntoValues().getElement(0);
        assertTrue(intoValue.getTable().toString().equalsIgnoreCase("small_orders"));


        TInsertIntoValue elseIntoValue = insert.getElseIntoValues().getElement(0);
        assertTrue(elseIntoValue.getTable().toString().equalsIgnoreCase("large_orders"));

        TSelectSqlStatement select = insert.getSubQuery();
       assertTrue(select.getResultColumnList().getResultColumn(0).toString().equalsIgnoreCase("order_id"));
       assertTrue(select.tables.getTable(0).toString().equalsIgnoreCase("orders"));

    }

}
