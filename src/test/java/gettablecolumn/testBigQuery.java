package gettablecolumn;

import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testBigQuery extends TestCase {

    static void doTestShowCTE(String inputQuery, String desireResult){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvbigquery);
        getTableColumn.isConsole = false;
        getTableColumn.showTableEffect = false;
        getTableColumn.showColumnLocation = false;
        getTableColumn.showTreeStructure = false;
        getTableColumn.showDatatype = true;
        getTableColumn.listStarColumn = true;
        getTableColumn.showCTE = true;
        getTableColumn.runText(inputQuery);
        // System.out.println(getTableColumn.outList.toString().trim());
        assertTrue(getTableColumn.outList.toString().trim().equalsIgnoreCase(desireResult));
    }

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

    public static void testunnestWithOffset2() {
        doTest("SELECT *\n" +
                        "FROM UNNEST(['foo', 'bar', 'baz', 'qux', 'corge', 'garply', 'waldo', 'fred']) AS element\n" +
                        "WITH OFFSET",
                "Tables:\n" +
                        "element(unnest table)\n" +
                        "\n" +
                        "Fields:\n" +
                        "(unnest-table:element).*\n" +
                        "(unnest-table:element).element\n" +
                        "(unnest-table:element).offset");
    }

    public static void testunnestWithOffset1() {
        doTest("SELECT *\n" +
                        "FROM UNNEST(['foo', 'bar', 'baz', 'qux', 'corge', 'garply', 'waldo', 'fred']) AS element\n" +
                        "WITH OFFSET AS offset2",
                "Tables:\n" +
                        "element(unnest table)\n" +
                        "\n" +
                        "Fields:\n" +
                        "(unnest-table:element).*\n" +
                        "(unnest-table:element).element\n" +
                        "(unnest-table:element).offset2");
    }

    public static void testunnestAncCTE() {
        doTestShowCTE("WITH\n" +
                        "  combinations AS (\n" +
                        "    SELECT\n" +
                        "      ['a', 'b'] AS letters,\n" +
                        "      [1, 2, 3] AS numbers\n" +
                        "  )\n" +
                        "SELECT\n" +
                        "  ARRAY(\n" +
                        "    SELECT AS STRUCT\n" +
                        "      letters[SAFE_OFFSET(index)] AS letter,\n" +
                        "      numbers[SAFE_OFFSET(index)] AS number\n" +
                        "    FROM combinations\n" +
                        "    CROSS JOIN\n" +
                        "      UNNEST(\n" +
                        "        GENERATE_ARRAY(\n" +
                        "          0,\n" +
                        "          LEAST(ARRAY_LENGTH(letters), ARRAY_LENGTH(numbers)) - 1)) AS index\n" +
                        "    ORDER BY index\n" +
                        "  );",
                "Tables:\n" +
                        "combinations\n" +
                        "index(unnest table)\n" +
                        "\n" +
                        "Fields:\n" +
                        "(unnest-table:index).index\n" +
                        "combinations(CTE).letters\n" +
                        "combinations(CTE).numbers");
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
