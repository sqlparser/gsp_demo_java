package gettablecolumn;

import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testBigQuery extends TestCase {

    static void doTest(String inputQuery, String desireResult){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvbigquery);
        getTableColumn.isConsole = false;
        getTableColumn.showTableEffect = false;
        getTableColumn.showColumnLocation = false;
        getTableColumn.showTreeStructure = false;
        getTableColumn.showDatatype = true;
        getTableColumn.runText(inputQuery);
        // System.out.println(getTableColumn.outList.toString().trim());
        assertTrue(getTableColumn.outList.toString().trim().equalsIgnoreCase(desireResult));
    }

    public static void testunnest() {
        doTest("CREATE TABLE schema1.table2 AS \n" +
                        "    (SELECT \n" +
                        "        (SELECT value FROM UNNEST(nested_attribute)\n" +
                        "        WHERE value = 'unnested_attribute1'\n" +
                        "        ) AS unnested_attribute1,\n" +
                        "        (SELECT value FROM UNNEST(nested_attribute)\n" +
                        "        WHERE value = 'unnested_attribute2'\n" +
                        "        ) AS unnested_attribute2,\n" +
                        "        (SELECT value FROM UNNEST(nested_attribute)\n" +
                        "        WHERE value = 'unnested_attribute3'\n" +
                        "        ) AS unnested_attribute3,\n" +
                        "    FROM schema1.table1\n" +
                        ");",
                "Tables:\n" +
                        "(unnest table)\n" +
                        "schema1.table1\n" +
                        "schema1.table2\n" +
                        "\n" +
                        "Fields:\n" +
                        "(unnest-table:).value\n" +
                        "schema1.table1.nested_attribute\n" +
                        "schema1.table2.unnested_attribute1\n" +
                        "schema1.table2.unnested_attribute2\n" +
                        "schema1.table2.unnested_attribute3");
    }
}
