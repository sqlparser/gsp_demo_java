package gettablecolumn;

import common.gspCommon;
import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testBigQuery extends TestCase {

    static void doTestShowCTEColumn(String inputFile, String desireResult){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvbigquery);
        getTableColumn.isConsole = false;
        getTableColumn.showTableEffect = false;
        getTableColumn.showColumnLocation = false;
        getTableColumn.showTreeStructure = false;
        getTableColumn.showDatatype = true;
        getTableColumn.listStarColumn = true;
        getTableColumn.showColumnsOfCTE = true;
        getTableColumn.runFile(inputFile);
        // System.out.println(getTableColumn.outList.toString().trim());
        assertTrue(getTableColumn.outList.toString().trim().equalsIgnoreCase(desireResult));
    }

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
                        "`schema1`.`table1`\n" +
                        "schema1.table2\n" +
                        "\n" +
                        "Fields:\n" +
                        "`schema1`.`table1`.id1\n" +
                        "schema1.table2.id1\n" +
                        "schema1.table2.id2");
    }

    public static void testbacktick2() {
        doTest("SELECT schema1.table1.id2, schema1.table2.id3\n" +
                        "FROM `schema1.table1`\n" +
                        "LEFT JOIN `schema1.table2`\n" +
                        "USING (id1);\n",
                "Tables:\n" +
                        "`schema1`.`table1`\n" +
                        "`schema1`.`table2`\n" +
                        "\n" +
                        "Fields:\n" +
                        "`schema1`.`table1`.id1\n" +
                        "`schema1`.`table1`.id2\n" +
                        "`schema1`.`table2`.id1\n" +
                        "`schema1`.`table2`.id3");
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


    public static void testShowCTEColumns() {
        doTestShowCTEColumn(  gspCommon.BASE_SQL_DIR_PRIVATE_JAVA +"bigquery/solidatus/cte_with_star_columns.sql",
                "Tables:\n" +
                        "`data`.`RETAIL_PROD_EXCEPTIONS_SOURCE`\n" +
                        "`data`.`RETAIL_PROD_SOURCE`\n" +
                        "`solidatus-dev`.`data`.`RETAIL_PROD_REPAIR`\n" +
                        "solidatus-dev.data.FOTC_RD_EXCEPTION_FILE_PROCESSING\n" +
                        "solidatus-dev.data.RETAIL_PROD_EXCEPTIONS_SOURCE\n" +
                        "solidatus-dev.data.RETAIL_PROD_REPAIR\n" +
                        "solidatus-dev.data.RETAIL_PROD_SOURCE\n" +
                        "\n" +
                        "Fields:\n" +
                        "`data`.`RETAIL_PROD_EXCEPTIONS_SOURCE`.*\n" +
                        "`data`.`RETAIL_PROD_EXCEPTIONS_SOURCE`.from_date\n" +
                        "`data`.`RETAIL_PROD_EXCEPTIONS_SOURCE`.to_date\n" +
                        "`data`.`RETAIL_PROD_SOURCE`.*\n" +
                        "`data`.`RETAIL_PROD_SOURCE`.__metadata\n" +
                        "`data`.`RETAIL_PROD_SOURCE`.__uuid\n" +
                        "`data`.`RETAIL_PROD_SOURCE`.adjustment_info\n" +
                        "`data`.`RETAIL_PROD_SOURCE`.entity_uuid\n" +
                        "`data`.`RETAIL_PROD_SOURCE`.from_date\n" +
                        "`data`.`RETAIL_PROD_SOURCE`.to_date\n" +
                        "solidatus-dev.data.FOTC_RD_EXCEPTION_FILE_PROCESSING.Processing_Type:string\n" +
                        "solidatus-dev.data.FOTC_RD_EXCEPTION_FILE_PROCESSING.Source_System_Type:string\n" +
                        "solidatus-dev.data.RETAIL_PROD_EXCEPTIONS_SOURCE.__metadata:array\n" +
                        "solidatus-dev.data.RETAIL_PROD_EXCEPTIONS_SOURCE.__uuid:string\n" +
                        "solidatus-dev.data.RETAIL_PROD_EXCEPTIONS_SOURCE.adjustment_info:array\n" +
                        "solidatus-dev.data.RETAIL_PROD_EXCEPTIONS_SOURCE.entity_uuid:string\n" +
                        "solidatus-dev.data.RETAIL_PROD_EXCEPTIONS_SOURCE.from_date:timestamp\n" +
                        "solidatus-dev.data.RETAIL_PROD_EXCEPTIONS_SOURCE.to_date:timestamp\n" +
                        "solidatus-dev.data.RETAIL_PROD_REPAIR.__metadata:array\n" +
                        "solidatus-dev.data.RETAIL_PROD_REPAIR.__uuid:string\n" +
                        "solidatus-dev.data.RETAIL_PROD_REPAIR.adjustment_info:array\n" +
                        "solidatus-dev.data.RETAIL_PROD_REPAIR.entity_uuid:string\n" +
                        "solidatus-dev.data.RETAIL_PROD_REPAIR.from_date:timestamp\n" +
                        "solidatus-dev.data.RETAIL_PROD_REPAIR.to_date:timestamp\n" +
                        "solidatus-dev.data.RETAIL_PROD_SOURCE.__metadata:array\n" +
                        "solidatus-dev.data.RETAIL_PROD_SOURCE.__uuid:string\n" +
                        "solidatus-dev.data.RETAIL_PROD_SOURCE.adjustment_info:array\n" +
                        "solidatus-dev.data.RETAIL_PROD_SOURCE.entity_uuid:string\n" +
                        "solidatus-dev.data.RETAIL_PROD_SOURCE.from_date:timestamp\n" +
                        "solidatus-dev.data.RETAIL_PROD_SOURCE.to_date:timestamp\n" +
                        "\n" +
                        "Ctes:\n" +
                        "EXCEPTION_RETAIL_000100.__metadata\n" +
                        "EXCEPTION_RETAIL_000100.__uuid\n" +
                        "EXCEPTION_RETAIL_000100.adjustment_info\n" +
                        "EXCEPTION_RETAIL_000100.entity_uuid\n" +
                        "EXCEPTION_RETAIL_000100.from_date\n" +
                        "EXCEPTION_RETAIL_000100.to_date\n" +
                        "EXCEPTION_RETAIL_000130.__metadata\n" +
                        "EXCEPTION_RETAIL_000130.__uuid\n" +
                        "EXCEPTION_RETAIL_000130.adjustment_info\n" +
                        "EXCEPTION_RETAIL_000130.entity_uuid\n" +
                        "EXCEPTION_RETAIL_000130.from_date\n" +
                        "EXCEPTION_RETAIL_000130.to_date\n" +
                        "RETAIL_PROD_SOURCE_000100.__metadata\n" +
                        "RETAIL_PROD_SOURCE_000100.__uuid\n" +
                        "RETAIL_PROD_SOURCE_000100.adjustment_info\n" +
                        "RETAIL_PROD_SOURCE_000100.entity_uuid\n" +
                        "RETAIL_PROD_SOURCE_000100.from_date\n" +
                        "RETAIL_PROD_SOURCE_000100.to_date\n" +
                        "RETAIL_PROD_SOURCE_000200.__metadata\n" +
                        "RETAIL_PROD_SOURCE_000200.__uuid\n" +
                        "RETAIL_PROD_SOURCE_000200.adjustment_info\n" +
                        "RETAIL_PROD_SOURCE_000200.entity_uuid\n" +
                        "RETAIL_PROD_SOURCE_000200.from_date\n" +
                        "RETAIL_PROD_SOURCE_000200.to_date\n" +
                        "RETAIL_PROD_SOURCE_000300.__metadata\n" +
                        "RETAIL_PROD_SOURCE_000300.__uuid\n" +
                        "RETAIL_PROD_SOURCE_000300.adjustment_info\n" +
                        "RETAIL_PROD_SOURCE_000300.entity_uuid\n" +
                        "RETAIL_PROD_SOURCE_000300.from_date\n" +
                        "RETAIL_PROD_SOURCE_000300.to_date\n" +
                        "RETAIL_PROD_SOURCE_000400.__metadata\n" +
                        "RETAIL_PROD_SOURCE_000400.__uuid\n" +
                        "RETAIL_PROD_SOURCE_000400.adjustment_info\n" +
                        "RETAIL_PROD_SOURCE_000400.entity_uuid\n" +
                        "RETAIL_PROD_SOURCE_000400.from_date\n" +
                        "RETAIL_PROD_SOURCE_000400.to_date\n" +
                        "STAGE1.__metadata\n" +
                        "STAGE1.__uuid\n" +
                        "STAGE1.adjustment_info\n" +
                        "STAGE1.entity_uuid\n" +
                        "STAGE1.from_date\n" +
                        "STAGE1.to_date\n" +
                        "STAGE2.__metadata\n" +
                        "STAGE2.__uuid\n" +
                        "STAGE2.adjustment_info\n" +
                        "STAGE2.entity_uuid\n" +
                        "STAGE2.from_date\n" +
                        "STAGE2.to_date");
    }

    public static void testMergeInsertValues() {
        doTest("MERGE INTO EMP1 D USING (\n" +
                        "  SELECT E2.* FROM EMP2 E2, EMP3 E3 WHERE E3.dept = 'prod'\n" +
                        ") S\n" +
                        "ON (D.id = S.id)\n" +
                        "WHEN MATCHED THEN\n" +
                        "  UPDATE SET joining_date = current_date\n" +
                        "WHEN NOT MATCHED THEN\n" +
                        "  INSERT (id, name, joining_date) VALUES (id, TRIM(name2), joining_date);",
                "Tables:\n" +
                        "EMP1\n" +
                        "EMP2\n" +
                        "EMP3\n" +
                        "\n" +
                        "Fields:\n" +
                        "EMP1.id\n" +
                        "EMP1.joining_date\n" +
                        "EMP1.name\n" +
                        "EMP2.*\n" +
                        "EMP2.id\n" +
                        "EMP2.joining_date\n" +
                        "EMP2.name2\n" +
                        "EMP3.dept");
    }

    public static void testBuiltinFunctionKeywordsInArgs() {
        doTest("SELECT TIMESTAMP_DIFF(CURRENT_TIMESTAMP(), date, SECOND) AS seconds_since FROM table_3;",
                "Tables:\n" +
                        "table_3\n" +
                        "\n" +
                        "Fields:\n" +
                        "table_3.date");
    }

}
