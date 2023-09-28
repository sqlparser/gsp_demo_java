package gettablecolumn;

import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import junit.framework.TestCase;

public class testColumnResolver extends TestCase {

    static void doTest(String inputQuery, String desireResult){
        doTest(EDbVendor.dbvmssql,inputQuery,desireResult);
    }
    static void doTest(EDbVendor dbVendor,String inputQuery, String desireResult){
        if (!TBaseType.ENABLE_RESOLVER) return;

        TGetTableColumn getTableColumn = new TGetTableColumn(dbVendor);
        getTableColumn.isConsole = false;
        getTableColumn.showTableEffect = false;
        getTableColumn.showColumnLocation = false;
        getTableColumn.showTreeStructure = false;
        getTableColumn.listStarColumn = true;
        boolean b = TBaseType.ENABLE_RESOLVER;
        TBaseType.ENABLE_RESOLVER = true;
        TBaseType.DUMP_RESOLVER_LOG_TO_CONSOLE = false;
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

    public static void test8() {
        doTest("    SELECT\n" +
                        "      col_1,\n" +
                        "      col_2\n" +
                        "    FROM (\n" +
                        "        SELECT * FROM table_1 WHERE day = '2000-01-01'\n" +
                        "        UNION ALL\n" +
                        "        SELECT * FROM table_2 WHERE day = '2000-01-01'\n" +
                        "        UNION ALL\n" +
                        "        SELECT * FROM table_3 WHERE day = '2000-01-01'\n" +
                        "        UNION ALL\n" +
                        "        SELECT * FROM table_4 WHERE day = '2000-01-01'\n" +
                        "      ) Combined",
                "Tables:\n" +
                        "table_1\n" +
                        "table_2\n" +
                        "table_3\n" +
                        "table_4\n" +
                        "\n" +
                        "Fields:\n" +
                        "table_1.*\n" +
                        "table_1.col_1\n" +
                        "table_1.col_2\n" +
                        "table_1.day\n" +
                        "table_2.*\n" +
                        "table_2.col_1\n" +
                        "table_2.col_2\n" +
                        "table_2.day\n" +
                        "table_3.*\n" +
                        "table_3.col_1\n" +
                        "table_3.col_2\n" +
                        "table_3.day\n" +
                        "table_4.*\n" +
                        "table_4.col_1\n" +
                        "table_4.col_2\n" +
                        "table_4.day");
    }

    public static void test9() {

        doTest(EDbVendor.dbvpostgresql,"SELECT * FROM (\n" +
                        "    WITH\n" +
                        "      Combined AS (\n" +
                        "        SELECT * FROM table_1 WHERE day = '2000-01-01'\n" +
                        "        UNION ALL\n" +
                        "        SELECT * FROM table_2 WHERE day = '2000-01-01'\n" +
                        "        UNION ALL\n" +
                        "        SELECT * FROM table_3 WHERE day = '2000-01-01'\n" +
                        "      )\n" +
                        "    SELECT\n" +
                        "      col_1,\n" +
                        "      col_2\n" +
                        "    FROM Combined\n" +
                        ") outer_select_wrapper",
                "Tables:\n" +
                        "table_1\n" +
                        "table_2\n" +
                        "table_3\n" +
                        "\n" +
                        "Fields:\n" +
                        "table_1.*\n" +
                        "table_1.col_1\n" +
                        "table_1.col_2\n" +
                        "table_1.day\n" +
                        "table_2.*\n" +
                        "table_2.col_1\n" +
                        "table_2.col_2\n" +
                        "table_2.day\n" +
                        "table_3.*\n" +
                        "table_3.col_1\n" +
                        "table_3.col_2\n" +
                        "table_3.day");
    }

    public static void test10() {

        doTest(EDbVendor.dbvpostgresql,"SELECT * FROM (\n" +
                        "    WITH\n" +
                        "      Combined1 AS (\n" +
                        "        SELECT * FROM actor \n" +
                        "        UNION ALL\n" +
                        "        SELECT * FROM actor2 \n" +
                        "        UNION ALL\n" +
                        "        SELECT * FROM actor3\n" +
                        "      )\n" +
                        "       , Combined2 AS (\n" +
                        "        SELECT * FROM  Combined1\n" +
                        "      )\n" +
                        "    SELECT\n" +
                        "      actor_id,\n" +
                        "      first_name\n" +
                        "    FROM Combined2\n" +
                        ") outer_select_wrapper",
                "Tables:\n" +
                        "actor\n" +
                        "actor2\n" +
                        "actor3\n" +
                        "\n" +
                        "Fields:\n" +
                        "actor.*\n" +
                        "actor.actor_id\n" +
                        "actor.first_name\n" +
                        "actor2.*\n" +
                        "actor2.actor_id\n" +
                        "actor2.first_name\n" +
                        "actor3.*\n" +
                        "actor3.actor_id\n" +
                        "actor3.first_name");
    }
    public static void test11() {

        doTest(EDbVendor.dbvpostgresql,"SELECT * FROM (\n" +
                        "    WITH\n" +
                        "      Combined AS (\n" +
                        "        SELECT * FROM table_1 WHERE day = '2000-01-01'\n" +
                        "        UNION ALL\n" +
                        "        SELECT * FROM table_2 WHERE day = '2000-01-01'\n" +
                        "      )\n" +
                        "    SELECT\n" +
                        "      col_1,\n" +
                        "      col_2\n" +
                        "    FROM Combined\n" +
                        ") outer_select_wrapper",
                "Tables:\n" +
                        "table_1\n" +
                        "table_2\n" +
                        "\n" +
                        "Fields:\n" +
                        "table_1.*\n" +
                        "table_1.col_1\n" +
                        "table_1.col_2\n" +
                        "table_1.day\n" +
                        "table_2.*\n" +
                        "table_2.col_1\n" +
                        "table_2.col_2\n" +
                        "table_2.day");
    }

    public static void test12() {

        doTest(EDbVendor.dbvoracle,"SELECT T.OPERATE_OBJECT_ID,\n" +
                        "        T.OPERATE_START_TIME,\n" +
                        "        T.OPERATE_END_TIME,\n" +
                        "        T.OPERATION,\n" +
                        "        T.APPLICATION_NAME,\n" +
                        "        T.MODULE\n" +
                        "FROM (SELECT T.* \n" +
                        "        FROM DWI_TREASURY_LOG_EVENT T\n" +
                        "        WHERE T.MODULE = 'GuaranteeLedger Management'\n" +
                        "          AND T.APPLICATION_NAME = 'etreasury') T,\n" +
                        "      DWI_MD_CLASS C,\n" +
                        "      DWI_TREASURY_GUARANTEE_LEDGER F\n" +
                        "WHERE T.OPERATE_OBJECT_ID = F.GUARANTEE_LEDGER_ID(+)\n" +
                        "  AND C.CLASS_TYPE_ID(+) = 500611\n" +
                        "  AND C.SS_ID(+) = 2670\n" +
                        "  AND UPPER(C.EN_NAME(+)) = T.LEDGER_STATUS;",
                "Tables:\n" +
                        "DWI_MD_CLASS\n" +
                        "DWI_TREASURY_GUARANTEE_LEDGER\n" +
                        "DWI_TREASURY_LOG_EVENT\n" +
                        "\n" +
                        "Fields:\n" +
                        "DWI_MD_CLASS.CLASS_TYPE_ID\n" +
                        "DWI_MD_CLASS.EN_NAME\n" +
                        "DWI_MD_CLASS.SS_ID\n" +
                        "DWI_TREASURY_GUARANTEE_LEDGER.GUARANTEE_LEDGER_ID\n" +
                        "DWI_TREASURY_LOG_EVENT.*\n" +
                        "DWI_TREASURY_LOG_EVENT.APPLICATION_NAME\n" +
                        "DWI_TREASURY_LOG_EVENT.LEDGER_STATUS\n" +
                        "DWI_TREASURY_LOG_EVENT.MODULE\n" +
                        "DWI_TREASURY_LOG_EVENT.OPERATE_END_TIME\n" +
                        "DWI_TREASURY_LOG_EVENT.OPERATE_OBJECT_ID\n" +
                        "DWI_TREASURY_LOG_EVENT.OPERATE_START_TIME\n" +
                        "DWI_TREASURY_LOG_EVENT.OPERATION");
    }

    public static void test13() {

        doTest(EDbVendor.dbvoracle,"select * from \n" +
                        "( \n" +
                        "  select B.*, A.createtimefroma from A, B \n" +
                        "  where A.ID(+) = B.ID \n" +
                        "  and A.startDate(+) > B.startDate \n" +
                        ") TEMP,C \n" +
                        "where TEMP.namefromb = C.name(+) \n" +
                        "and TEMP.createtimefromb < c.createtime(+);",
                "Tables:\n" +
                        "A\n" +
                        "B\n" +
                        "C\n" +
                        "\n" +
                        "Fields:\n" +
                        "A.createtimefroma\n" +
                        "A.ID\n" +
                        "A.startDate\n" +
                        "B.*\n" +
                        "B.createtimefromb\n" +
                        "B.ID\n" +
                        "B.namefromb\n" +
                        "B.startDate\n" +
                        "C.*\n" +
                        "C.createtime\n" +
                        "C.name");
    }

    public static void test14() {

        doTest(EDbVendor.dbvoracle,"SELECT * FROM REGIONS r ,\n" +
                        "(\n" +
                        "\tSELECT *\n" +
                        "\tFROM HR.DEPARTMENTS d,\n" +
                        "\t(SELECT job_id,job_title FROM JOBS j ) a_subquery\n" +
                        ") t_subquery",
                "Tables:\n" +
                        "HR.DEPARTMENTS\n" +
                        "JOBS\n" +
                        "REGIONS\n" +
                        "\n" +
                        "Fields:\n" +
                        "HR.DEPARTMENTS.*\n" +
                        "JOBS.job_id\n" +
                        "JOBS.job_title\n" +
                        "REGIONS.*");
    }

    public static void test15() {

        doTest(EDbVendor.dbvoracle,"SELECT D.DEPTNO, D.DEPTNAME,\n" +
                        "EMPINFO.AVGSAL, EMPINFO.EMPCOUNT\n" +
                        "FROM DEPT D,\n" +
                        "TABLE (SELECT AVG(E.SALARY) AS AVGSAL,\n" +
                        "COUNT(*) AS EMPCOUNT\n" +
                        "FROM EMP E\n" +
                        "WHERE E.WORKDEPT = D.DEPTNO)\n" +
                        "AS EMPINFO;",
                "Tables:\n" +
                        "DEPT\n" +
                        "EMP\n" +
                        "\n" +
                        "Fields:\n" +
                        "DEPT.DEPTNAME\n" +
                        "DEPT.DEPTNO\n" +
                        "EMP.*\n" +
                        "EMP.SALARY\n" +
                        "EMP.WORKDEPT");
    }

    public static void test16() {

        doTest(EDbVendor.dbvsybase,"update titles\n" +
                        "set total_sales = total_sales + qty\n" +
                        "from titles, salesdetail, sales\n" +
                        "where titles.title_id = salesdetail.title_id\n" +
                        "and salesdetail.stor_id = sales.stor_id\n" +
                        "and salesdetail.ord_num = sales.ord_num\n" +
                        "and sales.date in\n" +
                        "(select max (sales.date) from sales)",
                "Tables:\n" +
                        "sales\n" +
                        "salesdetail\n" +
                        "titles\n" +
                        "\n" +
                        "Fields:\n" +
                        "sales.date\n" +
                        "sales.ord_num\n" +
                        "sales.stor_id\n" +
                        "salesdetail.ord_num\n" +
                        "salesdetail.stor_id\n" +
                        "salesdetail.title_id\n" +
                        "titles.qty\n" +
                        "titles.title_id\n" +
                        "titles.total_sales");
    }

    public static void test17() {

        doTest(EDbVendor.dbvgreenplum,"WITH RECURSIVE search_graph(id, link, data, depth, path, cycle) AS (\n" +
                        "        SELECT g.id, g.link, g.data, 1,\n" +
                        "          ARRAY[g.id],\n" +
                        "          false\n" +
                        "        FROM graph g\n" +
                        "      UNION ALL\n" +
                        "        SELECT g.id, g.link, g.data, sg.depth + 1,\n" +
                        "          path || g.id,\n" +
                        "          g.id = ANY(path)\n" +
                        "        FROM graph g, search_graph sg\n" +
                        "        WHERE g.id = sg.link AND NOT cycle\n" +
                        ")\n" +
                        "SELECT * FROM search_graph;",
                "Tables:\n" +
                        "graph\n" +
                        "\n" +
                        "Fields:\n" +
                        "graph.data\n" +
                        "graph.id\n" +
                        "graph.link");
    }

    public static void test18() {

        doTest(EDbVendor.dbvdb2,"MERGE INTO archive ar\n" +
                        "using (SELECT activity,\n" +
                        "              description\n" +
                        "       FROM   activities)ac\n" +
                        "ON ( ar.activity = ac.activity )\n" +
                        "WHEN matched THEN\n" +
                        "  UPDATE SET description = ac.description\n" +
                        "WHEN NOT matched THEN\n" +
                        "  INSERT (activity,\n" +
                        "          description)\n" +
                        "  VALUES (ac.activity,\n" +
                        "          ac.description); " ,
                "Tables:\n" +
                        "activities\n" +
                        "archive\n" +
                        "\n" +
                        "Fields:\n" +
                        "activities.activity\n" +
                        "activities.description\n" +
                        "archive.activity\n" +
                        "archive.description");
    }

    public static void test19() {


        doTest(EDbVendor.dbvmssql,"SELECT [p].[ADDRESS_ID]\n" +
                        "                      ,[p].[3] AS ADDRESS_LINE3\n" +
                        "                      ,[p].[2] AS ADDRESS_LINE2\n" +
                        "                      ,[p].[1] AS ADDRESS_LINE1 FROM [ADDRESS_LINES_CTE]\n" +
                        "\t\t\t\tPIVOT(MAX(value) FOR id IN ([1],[2],[3])) p" ,
                "Tables:\n" +
                        "[ADDRESS_LINES_CTE]\n" +
                        "p(piviot_table)\n" +
                        "\n" +
                        "Fields:\n" +
                        "(pivot-table:p(piviot_table)).[1]\n" +
                        "(pivot-table:p(piviot_table)).[2]\n" +
                        "(pivot-table:p(piviot_table)).[3]\n" +
                        "[ADDRESS_LINES_CTE].[ADDRESS_ID]\n" +
                        "[ADDRESS_LINES_CTE].id\n" +
                        "[ADDRESS_LINES_CTE].value");
    }

    public static void test20Pivot() {

        doTest(EDbVendor.dbvmssql,"SELECT [Date] AS 'Day',\n" +
                        "[Sammich], [Pickle], [Apple], [Cake]\n" +
                        "FROM (\n" +
                        "    SELECT [Date], FoodName, AmountEaten FROM FoodEaten\n" +
                        ") AS SourceTable\n" +
                        "PIVOT (\n" +
                        "    MAX(AmountEaten)\n" +
                        "    FOR FoodName IN ([Sammich], [Pickle], [Apple], [Cake])\n" +
                        ") AS PivotTable" ,
                "Tables:\n" +
                        "FoodEaten\n" +
                        "PivotTable(piviot_table)\n" +
                        "\n" +
                        "Fields:\n" +
                        "(pivot-table:PivotTable(piviot_table)).[Apple]\n" +
                        "(pivot-table:PivotTable(piviot_table)).[Cake]\n" +
                        "(pivot-table:PivotTable(piviot_table)).[Pickle]\n" +
                        "(pivot-table:PivotTable(piviot_table)).[Sammich]\n" +
                        "FoodEaten.[Date]\n" +
                        "FoodEaten.AmountEaten\n" +
                        "FoodEaten.FoodName");
    }

    public static void test21Pivot() {

        doTest(EDbVendor.dbvmssql,"SELECT dim_patient_bk\n" +
                        "\t\t,FALNR\n" +
                        "\t\t,tage_imc\t\t\t\t= [IMC]\n" +
                        "\t\t,tage_ips\t\t\t\t= [IPS]\n" +
                        "\t\t,tage_isolierstation\t= [Isolierstation]\n" +
                        "\t\t,tage_bettenstation\t\t= [Bettenstation]\n" +
                        "FROM (\n" +
                        "\tSELECT dim_patient_bk\n" +
                        "\t\t\t,FALNR\n" +
                        "\t\t\t,stat_typ\n" +
                        "\t\t\t,sum_days = SUM(day_diff)\n" +
                        "\tFROM (\n" +
                        "\t\tSELECT\tdim_patient_bk\n" +
                        "\t\t\t\t,FALNR\n" +
                        "\t\t\t\t,day_diff = DATEDIFF(DAY, BWIDT\n" +
                        "\t\t\t\t\t, CASE BEWTY WHEN 2 THEN BWIDT ELSE ISNULL(LEAD(BWIDT)\n" +
                        "\t\t\t\t\t\t\tOVER (PARTITION BY dim_patient_bk, FALNR ORDER BY BWIDT),CAST(GETDATE() AS DATE)) END)\n" +
                        "\t\t\t\t,stat_typ = stellplatz_typ\n" +
                        "\t\tFROM\tatl.covid_patient_bewegung\n" +
                        "\t\tWHERE BEWTY <> 4\n" +
                        "\t) AS b\n" +
                        "\tGROUP BY b.dim_patient_bk\n" +
                        "\t\t\t,b.FALNR\n" +
                        "\t\t\t,b.stat_typ\n" +
                        ") AS src\n" +
                        "PIVOT\n" +
                        "(\n" +
                        "MAX(sum_days)\n" +
                        "FOR stat_typ IN ([IMC], [IPS], [Isolierstation], [Bettenstation])\n" +
                        ") AS bPivot" ,
                "Tables:\n" +
                        "atl.covid_patient_bewegung\n" +
                        "bPivot(piviot_table)\n" +
                        "\n" +
                        "Fields:\n" +
                        "(pivot-table:bPivot(piviot_table)).[Bettenstation]\n" +
                        "(pivot-table:bPivot(piviot_table)).[IMC]\n" +
                        "(pivot-table:bPivot(piviot_table)).[IPS]\n" +
                        "(pivot-table:bPivot(piviot_table)).[Isolierstation]\n" +
                        "atl.covid_patient_bewegung.BEWTY\n" +
                        "atl.covid_patient_bewegung.BWIDT\n" +
                        "atl.covid_patient_bewegung.dim_patient_bk\n" +
                        "atl.covid_patient_bewegung.FALNR\n" +
                        "atl.covid_patient_bewegung.stellplatz_typ");
    }

    public static void test22Pivot() {

        doTest(EDbVendor.dbvbigquery,"INSERT INTO `dev.TEST_BACKLOG.EMPLOYEE_INEO_EXCEPT`\n" +
                        "SELECT emp_id, name as Test_Name, LONDON as Test_Dept_Id\n" +
                        "FROM `dev.TEST_BACKLOG.EMPLOYEE_INFO`\n" +
                        "PIVOT (MAX(dept_id) FOR city IN (\"SINGAPORE\",\"LONDON\",\"HOUSTON\"))" ,
                "Tables:\n" +
                        "`dev`.`TEST_BACKLOG`.`EMPLOYEE_INEO_EXCEPT`\n" +
                        "`dev`.`TEST_BACKLOG`.`EMPLOYEE_INFO`\n" +
                        "pivot_alias(piviot_table)\n" +
                        "\n" +
                        "Fields:\n" +
                        "(pivot-table:pivot_alias(piviot_table)).\"HOUSTON\"\n" +
                        "(pivot-table:pivot_alias(piviot_table)).\"LONDON\"\n" +
                        "(pivot-table:pivot_alias(piviot_table)).\"SINGAPORE\"\n" +
                        "(pivot-table:pivot_alias(piviot_table)).LONDON\n" +
                        "`dev`.`TEST_BACKLOG`.`EMPLOYEE_INFO`.city\n" +
                        "`dev`.`TEST_BACKLOG`.`EMPLOYEE_INFO`.dept_id\n" +
                        "`dev`.`TEST_BACKLOG`.`EMPLOYEE_INFO`.emp_id\n" +
                        "`dev`.`TEST_BACKLOG`.`EMPLOYEE_INFO`.name");
    }

    public static void test23CTE() {

        doTest(EDbVendor.dbvnetezza,"WITH manager (mgr_id, mgr_name, mgr_dept) AS\n" +
                        "\t(SELECT id, name, grp\n" +
                        "\tFROM emp_copy\n" +
                        "\tWHERE mgr = id AND grp != 'gone'),\n" +
                        "employee (emp_id, emp_name, emp_mgr) AS\n" +
                        "\t(SELECT id2, name2, mgr_id2\n" +
                        "\tFROM emp_copy JOIN manager ON grp = mgr_dept),\n" +
                        "mgr_cnt (mgr_id, mgr_reports) AS\n" +
                        "\t(SELECT mgr, COUNT (*)\n" +
                        "\tFROM emp_copy\n" +
                        "\tWHERE mgr != id\n" +
                        "\tGROUP BY mgr)\n" +
                        "SELECT *\n" +
                        "FROM employee JOIN manager ON emp_mgr = mgr_id \n" +
                        "\tJOIN mgr_cnt ON emp_mgr = mgr_id \n" +
                        "WHERE emp_id != mgr_id\n" +
                        "ORDER BY mgr_dept;" ,
                "Tables:\n" +
                        "emp_copy\n" +
                        "\n" +
                        "Fields:\n" +
                        "emp_copy.*\n" +
                        "emp_copy.grp\n" +
                        "emp_copy.id\n" +
                        "emp_copy.id2\n" +
                        "emp_copy.mgr\n" +
                        "emp_copy.mgr_id2\n" +
                        "emp_copy.name\n" +
                        "emp_copy.name2");
    }

}
