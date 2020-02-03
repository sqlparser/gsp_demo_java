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
}
