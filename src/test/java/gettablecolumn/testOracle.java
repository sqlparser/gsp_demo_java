package gettablecolumn;


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

    public static void testJsonObject() {
        doTest("SELECT JSON_OBJECT (\n" +
                        "    KEY 'deptno' IS d.department_id2,\n" +
                        "    KEY 'deptname' IS d.department_name \n" +
                        "    ) \"Department Objects\"\n" +
                        "  FROM departments d\n" +
                        "  ORDER BY d.department_id;",
                "Tables:\n" +
                        "departments\n" +
                        "\n" +
                        "Fields:\n" +
                        "departments.department_id\n" +
                        "departments.department_id2\n" +
                        "departments.department_name");
    }

    public static void testSubqueryWithSameColumn() {
        doTest("SELECT A.PROJECT_ID\n" +
                        "            , A.SQL_AUTO_PERF_CHECK_ID\n" +
                        "         FROM SQL_AUTO_PERF_CHK A\n" +
                        "            , (\n" +
                        "               SELECT PROJECT_ID\n" +
                        "                    , SQL_AUTO_PERF_CHECK_ID\n" +
                        "                    , DBID\n" +
                        "                    , EXEC_SEQ\n" +
                        "                 FROM FILTER_PRED_EXEC\n" +
                        "                WHERE ACCESS_PATH_TYPE = 'AUTOINDEX'\n" +
                        "              ) B\n" +
                        "            , (\n" +
                        "               SELECT IDX_AD_NO\n" +
                        "                    , DBID\n" +
                        "                    , EXEC_SEQ\n" +
                        "                 FROM IDX_AD_MST\n" +
                        "              ) C\n" +
                        "        WHERE A.PROJECT_ID = :project_id\n" +
                        "          AND A.PERF_CHECK_EXEC_END_DT IS NOT NULL\n" +
                        "          AND NVL(A.PERF_CHECK_FORCE_CLOSE_YN, 'N') <> 'Y'\n" +
                        "          AND B.PROJECT_ID = A.PROJECT_ID\n" +
                        "          AND B.SQL_AUTO_PERF_CHECK_ID = A.SQL_AUTO_PERF_CHECK_ID\n" +
                        "          AND C.DBID = B.DBID",
                "Tables:\n" +
                        "FILTER_PRED_EXEC\n" +
                        "IDX_AD_MST\n" +
                        "SQL_AUTO_PERF_CHK\n" +
                        "\n" +
                        "Fields:\n" +
                        "FILTER_PRED_EXEC.ACCESS_PATH_TYPE\n" +
                        "FILTER_PRED_EXEC.DBID\n" +
                        "FILTER_PRED_EXEC.EXEC_SEQ\n" +
                        "FILTER_PRED_EXEC.PROJECT_ID\n" +
                        "FILTER_PRED_EXEC.SQL_AUTO_PERF_CHECK_ID\n" +
                        "IDX_AD_MST.DBID\n" +
                        "IDX_AD_MST.EXEC_SEQ\n" +
                        "IDX_AD_MST.IDX_AD_NO\n" +
                        "SQL_AUTO_PERF_CHK.PERF_CHECK_EXEC_END_DT\n" +
                        "SQL_AUTO_PERF_CHK.PERF_CHECK_FORCE_CLOSE_YN\n" +
                        "SQL_AUTO_PERF_CHK.PROJECT_ID\n" +
                        "SQL_AUTO_PERF_CHK.SQL_AUTO_PERF_CHECK_ID");
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

    public static void testColumnInUnionSelect(){
        doTest("Select\n" +
                        "(select phone_num as ephone1 from emp_phone p where p.emp_num=e.emp_num and primary_flag='Y' \n" +
                        "\tunion select phone_num as ephone1 from emp_phone1 p where p.emp_num=e.emp_num and primary_flag='Y') ephone,\n" +
                        "e.emp_attr_no_alias\n" +
                        "From\n" +
                        "Department d, employee e",
                "Tables:\n" +
                        "Department\n" +
                        "emp_phone\n" +
                        "emp_phone1\n" +
                        "employee\n" +
                        "\n" +
                        "Fields:\n" +
                        "emp_phone.emp_num\n" +
                        "emp_phone.phone_num\n" +
                        "emp_phone.primary_flag\n" +
                        "emp_phone1.emp_num\n" +
                        "emp_phone1.phone_num\n" +
                        "emp_phone1.primary_flag\n" +
                        "employee.emp_attr_no_alias\n" +
                        "employee.emp_num");
    }

    public static void testColumnInSelectListOfJoin(){
        doTest("CREATE OR REPLACE FORCE VIEW \"CATALOG_IT\".\"VIEW1\" (\"COL1\", \"COL2\", \"COL3\", \"COL4\") AS\n" +
                        "SELECT al1.\"COL1\",al1.\"COL2\",al1.\"COL3\", al2.COL1 AS \"COL4\"\n" +
                        "FROM (SELECT COL1, COL2, COL3 FROM TABLE1 t1) al1\n" +
                        "JOIN (SELECT COL1, COL2, COL3 FROM TABLE2 t2) al2 ON al2.COL2 = al1.COL2",
                "Tables:\n" +
                        "TABLE1\n" +
                        "TABLE2\n" +
                        "\n" +
                        "Fields:\n" +
                        "TABLE1.COL1\n" +
                        "TABLE1.COL2\n" +
                        "TABLE1.COL3\n" +
                        "TABLE2.COL1\n" +
                        "TABLE2.COL2\n" +
                        "TABLE2.COL3");
    }

    public static void testRowid(){
        doTest("Select t1.ROWID from table1 t1",
                "Tables:\n" +
                        "table1\n" +
                        "\n" +
                        "Fields:\n" +
                        "table1.ROWID");
    }

    public static void testListAgg(){
        doTest("SELECT LISTAGG(DRIVER_NM, ',') WITHIN GROUP (ORDER BY DRIVER_NM) AS DRIVER_NM FROM DRIVER_INFO",
                "Tables:\n" +
                        "DRIVER_INFO\n" +
                        "\n" +
                        "Fields:\n" +
                        "DRIVER_INFO.DRIVER_NM");
    }
}
