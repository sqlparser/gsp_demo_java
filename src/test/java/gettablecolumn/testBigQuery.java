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
        getTableColumn.listStarColumn = true;
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


    public static void testbacktick1() {
        doTest("SELECT schema1.table1.id1, schema1.table2.id2 \n" +
                        "FROM `schema1.table1` \n" +
                        "LEFT JOIN schema1.table2\n" +
                        "USING (id1)",
                "Tables:\n" +
                        "`schema1.table1`\n" +
                        "schema1.table2\n" +
                        "\n" +
                        "Fields:\n" +
                        "`schema1.table1`.id1\n" +
                        "schema1.table2.id1\n" +
                        "schema1.table2.id2");
    }

    public static void testbacktick2() {
        doTest("SELECT schema1.table1.id2, schema1.table2.id3\n" +
                        "FROM `schema1.table1`\n" +
                        "LEFT JOIN `schema1.table2`\n" +
                        "USING (id1);\n",
                "Tables:\n" +
                        "`schema1.table1`\n" +
                        "`schema1.table2`\n" +
                        "\n" +
                        "Fields:\n" +
                        "`schema1.table1`.id1\n" +
                        "`schema1.table1`.id2\n" +
                        "`schema1.table2`.id1\n" +
                        "`schema1.table2`.id3");
    }

    public static void testExceptColumns() {
        doTest("SELECT COMMON.* EXCEPT (column1, column2) FROM dataset1.table1 COMMON;",
                "Tables:\n" +
                        "dataset1.table1\n" +
                        "\n" +
                        "Fields:\n" +
                        "dataset1.table1.*\n" +
                        "dataset1.table1.column1\n" +
                        "dataset1.table1.column2");
    }
}
