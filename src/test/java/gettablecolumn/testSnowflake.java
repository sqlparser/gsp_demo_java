package gettablecolumn;

import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testSnowflake extends TestCase {

    static void doTest(String inputQuery, String desireResult){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvsnowflake);
        getTableColumn.isConsole = false;
        getTableColumn.showTableEffect = false;
        getTableColumn.showColumnLocation = false;
        getTableColumn.showTreeStructure = false;
        getTableColumn.showDatatype = true;
        getTableColumn.runText(inputQuery);
        // System.out.println(getTableColumn.outList.toString().trim());
        assertTrue(getTableColumn.outList.toString().trim().equalsIgnoreCase(desireResult));
    }

    public static void testCreateFunction() {
        doTest("create function profit()\n" +
                        "  returns numeric(11, 2)\n" +
                        "  as\n" +
                        "  $$\n" +
                        "    select sum((retail_price - wholesale_price) * number_sold) from purchases\n" +
                        "  $$\n" +
                        "  ;",
                "Tables:\n" +
                        "purchases\n" +
                        "\n" +
                        "Fields:\n" +
                        "purchases.number_sold\n" +
                        "purchases.retail_price\n" +
                        "purchases.wholesale_price");
    }

    public static void testUpdate() {
        doTest("update t1\n" +
                        "set t1.number_column = t1.number_column + t2.number_column, t1.text_column = 'ASDF'\n" +
                        "from t2\n" +
                        "where t1.key_column = t2.t1_key and t1.number_column < 10;",
                "Tables:\n" +
                        "t1\n" +
                        "t2\n" +
                        "\n" +
                        "Fields:\n" +
                        "t1.key_column\n" +
                        "t1.number_column\n" +
                        "t1.text_column\n" +
                        "t2.number_column\n" +
                        "t2.t1_key");
    }

    public static void testLateralColumnAlias() {
        doTest(" select \n" +
                        "   account_id\n" +
                        "  , arr_change as x\n" +
                        "  , start_arr as y\n" +
                        "  , avg(x/y) as avg_upgrade\n" +
                        "  from account_product_month\n" +
                        "  group by 1",
                "Tables:\n" +
                        "account_product_month\n" +
                        "\n" +
                        "Fields:\n" +
                        "account_product_month.account_id\n" +
                        "account_product_month.arr_change\n" +
                        "account_product_month.start_arr");
    }
}
