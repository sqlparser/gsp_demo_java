package gudusoft.gsqlparser.gettablecolumnTest;

import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testNetezza extends TestCase {

    static void doTest(String inputQuery, String desireResult){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvnetezza);
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
        doTest(" update CUST_TO_ADDRESS_XREF T1 \n" +
                        " set COA_DELIV_SCORE = T2.COA_DELIV_SCORE \n" +
                        " from TEMP_UPDATE_BEGIN_END_DATE T2 \n" +
                        " WHERE T1.CUST_TOUCHPOINT_REL_KEY = T2.CUST_TOUCHPOINT_REL_KEY and T1.CUST_TOUCHPOINT_REL_KEY not in (select CUST_TOUCHPOINT_REL_KEY from RETAIN_BASE_COA_ATTRIBUTE_VALUE)",
                "Tables:\n" +
                        "CUST_TO_ADDRESS_XREF\n" +
                        "RETAIN_BASE_COA_ATTRIBUTE_VALUE\n" +
                        "TEMP_UPDATE_BEGIN_END_DATE\n" +
                        "\n" +
                        "Fields:\n" +
                        "CUST_TO_ADDRESS_XREF.COA_DELIV_SCORE\n" +
                        "CUST_TO_ADDRESS_XREF.CUST_TOUCHPOINT_REL_KEY\n" +
                        "RETAIN_BASE_COA_ATTRIBUTE_VALUE.CUST_TOUCHPOINT_REL_KEY\n" +
                        "TEMP_UPDATE_BEGIN_END_DATE.COA_DELIV_SCORE\n" +
                        "TEMP_UPDATE_BEGIN_END_DATE.CUST_TOUCHPOINT_REL_KEY");
    }
}
