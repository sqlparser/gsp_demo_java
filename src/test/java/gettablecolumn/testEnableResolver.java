package gettablecolumn;

import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import junit.framework.TestCase;

public class testEnableResolver extends TestCase {

    static void doTest(String inputQuery, String desireResult){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvmssql);
        getTableColumn.isConsole = false;
        getTableColumn.showTableEffect = false;
        getTableColumn.showColumnLocation = false;
        getTableColumn.showTreeStructure = false;
        getTableColumn.listStarColumn = true;
        boolean b = TBaseType.ENABLE_RESOLVER;
        TBaseType.ENABLE_RESOLVER = true;
        TBaseType.DUMP_RESOLVER_LOG = false;
        getTableColumn.runText(inputQuery);
        //System.out.println(getTableColumn.outList.toString().trim());
        assertTrue(getTableColumn.outList.toString().trim().equalsIgnoreCase(desireResult));
        TBaseType.ENABLE_RESOLVER = b;
    }

    public static void test1() {
        doTest("SELECT col_1, col_11,col_2\n" +
                        "FROM (\n" +
                        "    select *\n" +
                        "    FROM (select * from table_a) ta\n" +
                        "  ) a\n" +
                        "  LEFT OUTER JOIN (\n" +
                        "    select id, col_2\n" +
                        "    FROM table_b\n" +
                        "  ) b on a.id = b.id",
                "Tables:\n" +
                        "table_a\n" +
                        "table_b\n" +
                        "\n" +
                        "Fields:\n" +
                        "table_a.*\n" +
                        "table_a.col_1\n" +
                        "table_a.col_11\n" +
                        "table_a.id\n" +
                        "table_b.col_2\n" +
                        "table_b.id");
    }

    public static void test2() {
        doTest("SELECT col_1, col_11,col_2\n" +
                        "FROM (\n" +
                        "    select ta.*\n" +
                        "    FROM table_a ta, table_b tb\n" +
                        "  ) a\n" +
                        " LEFT OUTER JOIN (\n" +
                        "    select id, col_2\n" +
                        "    FROM table_b\n" +
                        "  ) b on a.id = b.id",
                "Tables:\n" +
                        "table_a\n" +
                        "table_b\n" +
                        "\n" +
                        "Fields:\n" +
                        "table_a.*\n" +
                        "table_a.col_1\n" +
                        "table_a.col_11\n" +
                        "table_a.id\n" +
                        "table_b.col_2\n" +
                        "table_b.id");
    }

    public static void test3() {
        doTest("SELECT col_1, col_11,col_2\n" +
                        "FROM (\n" +
                        "    select tb.*\n" +
                        "    FROM table_a ta, table_b tb\n" +
                        "  ) a\n" +
                        " LEFT OUTER JOIN (\n" +
                        "    select id, col_2\n" +
                        "    FROM table_b\n" +
                        "  ) b on a.id = b.id",
                "Tables:\n" +
                        "table_a\n" +
                        "table_b\n" +
                        "\n" +
                        "Fields:\n" +
                        "table_b.*\n" +
                        "table_b.col_1\n" +
                        "table_b.col_11\n" +
                        "table_b.col_2\n" +
                        "table_b.id");
    }

    public static void test4() {
        // 当 * 可以连接多个 table时采取不同的策略导致不同的结果
        int t = TBaseType.GUESS_COLUMN_STRATEGY;
        TBaseType.GUESS_COLUMN_STRATEGY = TBaseType.GUESS_COLUMN_STRATEGY_NOT_PICKUP;


        doTest("SELECT col_1, col_11,col_2\n" +
                        "FROM (\n" +
                        "    select *\n" +
                        "    FROM table_a ta, table_c tc\n" +
                        "  ) a\n" +
                        " LEFT OUTER JOIN (\n" +
                        "    select b_id, col_b2\n" +
                        "    FROM table_b\n" +
                        "  ) b on a.a_id = b.b_id",
                "Tables:\n" +
                        "table_a\n" +
                        "table_b\n" +
                        "table_c\n" +
                        "\n" +
                        "Fields:\n" +
                        "missed.col_1(1,8)\n" +
                        "missed.col_11(1,15)\n" +
                        "missed.col_2(1,22)\n" +
                        "table_a.*\n" +
                        "table_b.b_id\n" +
                        "table_b.col_b2\n" +
                        "table_c.*");

        TBaseType.GUESS_COLUMN_STRATEGY = t;
    }

    public static void test5() {
        // 当 * 可以连接多个 table时采取不同的策略导致不同的结果
        int t = TBaseType.GUESS_COLUMN_STRATEGY;
        TBaseType.GUESS_COLUMN_STRATEGY = TBaseType.GUESS_COLUMN_STRATEGY_NEAREST;

        doTest("SELECT col_1, col_11,col_2\n" +
                        "FROM (\n" +
                        "    select *\n" +
                        "    FROM table_a ta, table_c tc\n" +
                        "  ) a\n" +
                        " LEFT OUTER JOIN (\n" +
                        "    select b_id, col_b2\n" +
                        "    FROM table_b\n" +
                        "  ) b on a.a_id = b.b_id",
                "Tables:\n" +
                        "table_a\n" +
                        "table_b\n" +
                        "table_c\n" +
                        "\n" +
                        "Fields:\n" +
                        "table_a.*\n" +
                        "table_a.a_id\n" +
                        "table_a.col_1\n" +
                        "table_a.col_11\n" +
                        "table_a.col_2\n" +
                        "table_b.b_id\n" +
                        "table_b.col_b2\n" +
                        "table_c.*");

        TBaseType.GUESS_COLUMN_STRATEGY = t;
    }

    public static void test6() {
        // 当 * 可以连接多个 table时采取不同的策略导致不同的结果
        int t = TBaseType.GUESS_COLUMN_STRATEGY;
        TBaseType.GUESS_COLUMN_STRATEGY = TBaseType.GUESS_COLUMN_STRATEGY_FARTHEST;

        doTest("SELECT col_1, col_11,col_2\n" +
                        "FROM (\n" +
                        "    select *\n" +
                        "    FROM table_a ta, table_c tc\n" +
                        "  ) a\n" +
                        " LEFT OUTER JOIN (\n" +
                        "    select b_id, col_b2\n" +
                        "    FROM table_b\n" +
                        "  ) b on a.a_id = b.b_id",
                "Tables:\n" +
                        "table_a\n" +
                        "table_b\n" +
                        "table_c\n" +
                        "\n" +
                        "Fields:\n" +
                        "table_a.*\n" +
                        "table_b.b_id\n" +
                        "table_b.col_b2\n" +
                        "table_c.*\n" +
                        "table_c.a_id\n" +
                        "table_c.col_1\n" +
                        "table_c.col_11\n" +
                        "table_c.col_2");

        TBaseType.GUESS_COLUMN_STRATEGY = t;
    }

    public static void test7() {

        doTest("SELECT col_c1, col_c11,col_b2,col_a3\n" +
                        "FROM (\n" +
                        "    select *\n" +
                        "    FROM table_a ta, (select col_c1, col_c11 from table_c) tc\n" +
                        "  ) a\n" +
                        " LEFT OUTER JOIN (\n" +
                        "    select b_id, col_b2\n" +
                        "    FROM table_b\n" +
                        "  ) b on a.a_id = b.b_id",
                "Tables:\n" +
                        "table_a\n" +
                        "table_b\n" +
                        "table_c\n" +
                        "\n" +
                        "Fields:\n" +
                        "table_a.*\n" +
                        "table_a.a_id\n" +
                        "table_a.col_a3\n" +
                        "table_b.b_id\n" +
                        "table_b.col_b2\n" +
                        "table_c.col_c1\n" +
                        "table_c.col_c11");


    }
}
