package test.gettablecolumn;


import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testOracle extends TestCase {

    static void doTest(String inputQuery, String desireResult){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvoracle);
        getTableColumn.isConsole = false;
        getTableColumn.showTableEffect = false;
        getTableColumn.showColumnLocation = false;
        getTableColumn.showTreeStructure = false;
        getTableColumn.runText(inputQuery);
        //  System.out.println(getTableColumn.outList.toString().trim());
        assertTrue(getTableColumn.outList.toString().trim().equalsIgnoreCase(desireResult));
    }

    public static void testInsertAll() {
        doTest(" INSERT ALL\n" +
                        "  WHEN id <= 3 THEN\n" +
                        "    INTO dest_tab1 VALUES(id, description1)\n" +
                        "  WHEN id BETWEEN 4 AND 7 THEN\n" +
                        "    INTO dest_tab2 VALUES(id, description2)\n" +
                        "  WHEN 1=1 THEN\n" +
                        "    INTO dest_tab3 VALUES(id, description3)\n" +
                        "SELECT id, description1, description2,description3\n" +
                        "FROM   source_tab;",
                "Tables:\n" +
                        "dest_tab1\n" +
                        "dest_tab2\n" +
                        "dest_tab3\n" +
                        "source_tab\n" +
                        "\nFields:\n" +
                        "source_tab.description1\n" +
                        "source_tab.description2\n" +
                        "source_tab.description3\n" +
                        "source_tab.id");
    }
}
