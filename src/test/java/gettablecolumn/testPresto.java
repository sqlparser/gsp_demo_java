package gettablecolumn;

import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testPresto extends TestCase {

    static void doTest(String inputQuery, String desireResult){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvpresto);
        getTableColumn.isConsole = false;
        getTableColumn.showTableEffect = false;
        getTableColumn.showColumnLocation = false;
        getTableColumn.showTreeStructure = false;
        getTableColumn.showDatatype = true;
        getTableColumn.runText(inputQuery);
       // System.out.println(getTableColumn.outList.toString().trim());
        assertTrue(getTableColumn.outList.toString().trim().equalsIgnoreCase(desireResult));
    }

    public static void testUnnest1() {
        doTest("SELECT student, score\n" +
                        "FROM tests\n" +
                        "CROSS JOIN UNNEST(scores) AS t (score);",
                "Tables:\n" +
                        "t(unnest table)\n" +
                        "tests\n" +
                        "\n" +
                        "Fields:\n" +
                        "(unnest-table:t).score\n" +
                        "tests.scores\n" +
                        "tests.student");
    }

    public static void testUnnest2() {
        doTest("SELECT numbers, animals, n, a\n" +
                        "FROM (\n" +
                        "  VALUES\n" +
                        "    (ARRAY[2, 5], ARRAY['dog', 'cat', 'bird']),\n" +
                        "    (ARRAY[7, 8, 9], ARRAY['cow', 'pig'])\n" +
                        ") AS x (numbers, animals)\n" +
                        "CROSS JOIN UNNEST(numbers, animals) AS t (n, a);",
                "Tables:\n" +
                        "t(unnest table)\n" +
                        "\n" +
                        "Fields:\n" +
                        "(unnest-table:t).a\n" +
                        "(unnest-table:t).n");
    }

    public static void testUnnest3() {
        doTest("SELECT numbers, n, a\n" +
                        "FROM (\n" +
                        "  VALUES\n" +
                        "    (ARRAY[2, 5]),\n" +
                        "    (ARRAY[7, 8, 9])\n" +
                        ") AS x (numbers)\n" +
                        "CROSS JOIN UNNEST(numbers) WITH ORDINALITY AS t (n, a);",
                "Tables:\n" +
                        "t(unnest table)\n" +
                        "\n" +
                        "Fields:\n" +
                        "(unnest-table:t).a\n" +
                        "(unnest-table:t).n");
    }

    public static void testUnnest4() {
        doTest("SELECT\n" +
                        "    animals, a, n\n" +
                        "FROM (\n" +
                        "    VALUES\n" +
                        "        (MAP(ARRAY['dog', 'cat', 'bird'], ARRAY[1, 2, 0])),\n" +
                        "        (MAP(ARRAY['dog', 'cat'], ARRAY[4, 5]))\n" +
                        ") AS x (animals)\n" +
                        "CROSS JOIN UNNEST(animals) AS t (a, n);",
                "Tables:\n" +
                        "t(unnest table)\n" +
                        "\n" +
                        "Fields:\n" +
                        "(unnest-table:t).a\n" +
                        "(unnest-table:t).n");
    }

}

