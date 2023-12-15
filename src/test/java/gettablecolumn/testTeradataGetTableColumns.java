package gettablecolumn;

import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TDeleteSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testTeradataGetTableColumns extends TestCase {

    static void doTest(String inputQuery, String desireResult){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvteradata);
        getTableColumn.isConsole = false;
        getTableColumn.showTableEffect = false;
        getTableColumn.showColumnLocation = false;
        getTableColumn.showTreeStructure = false;
        getTableColumn.runText(inputQuery);
//        System.out.println(inputQuery);
//        System.out.println(desireResult);
//        System.out.println(getTableColumn.outList.toString().trim());
        assertTrue(getTableColumn.outList.toString().trim().equalsIgnoreCase(desireResult));
    }


//    public static void testDateColumn() {
//        doTest("select  year(date),year(date1) from t",
//                "Tables:\n" +
//                        "t\n" +
//                        "\n" +
//                        "Fields:\n" +
//                        "t.date\n" +
//                        "t.date1");
//    }

    public static void testEDate() {
        doTest("update table1 set col = 'value' where table1.id = table2.id2",
                "Tables:\n" +
                        "table1\n" +
                        "table2\n" +
                        "\nFields:\n" +
                        "table1.col\n" +
                        "table1.id\n" +
                        "table2.id2");
    }

    public static void testAliasOfSubquery() {
        int b = TBaseType.GUESS_COLUMN_STRATEGY;
        TBaseType.GUESS_COLUMN_STRATEGY = TBaseType.GUESS_COLUMN_STRATEGY_NEAREST;
        doTest("select id from (select * from A,B)ab, C where ab.id=C.id;",
                "Tables:\n" +
                        "A\n" +
                        "B\n" +
                        "C\n" +
                        "\nFields:\n" +
                        "A.id\n" +
                        "C.id");
        TBaseType.GUESS_COLUMN_STRATEGY = b;
    }

    public static void testTableOnlySelectList() {
        doTest("INSERT INTO DB51_CAD.CAD_RK_1(\n" +
                        "    zabezp_id,\n" +
                        "    nr_rach_trans_knt,\n" +
                        "    sys_zabezp,\n" +
                        "    sys_eksp,\n" +
                        "    data_danych,\n" +
                        "    ZWIAZEK_HIP0,\n" +
                        "    REL_EKSP_HIPO0,\n" +
                        "    ZWIAZEK_HIP1,\n" +
                        "    REL_EKSP_HIPO1,\n" +
                        "    zwiazek0,\n" +
                        "    rel_eksp_zabezp0,\n" +
                        "    zwiazek1,\n" +
                        "    rel_eksp_zabezp1,\n" +
                        "    zwiazek2,\n" +
                        "    rel_eksp_zabezp2,\n" +
                        "    zwiazek25,\n" +
                        "    rel_eksp_zabezp25,\n" +
                        "    t,\n" +
                        "    t_bis,\n" +
                        "    a,\n" +
                        "    hc,\n" +
                        "    hfx,\n" +
                        "    dost_zabezp_grupa,\n" +
                        "\tid_banku\n" +
                        ")\n" +
                        "SELECT \n" +
                        "    DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.zabezp_id,\n" +
                        "    DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.nr_rach_trans_knt,\n" +
                        "    DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.sys_zabezp,\n" +
                        "    DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.sys_eksp,\n" +
                        "    DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.data_danych,\n" +
                        "    DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.ZWIAZEK_HIP0,\n" +
                        "    DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.REL_EKSP_HIPO0,\n" +
                        "    DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.ZWIAZEK_HIP1,\n" +
                        "    DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.REL_EKSP_HIPO1,\n" +
                        "    DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.zwiazek0,\n" +
                        "    DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.rel_eksp_zabezp0,\n" +
                        "    DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.zwiazek1,\n" +
                        "    DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.rel_eksp_zabezp1,\n" +
                        "    DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.zwiazek2,\n" +
                        "    DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.rel_eksp_zabezp2,\n" +
                        "    DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.zwiazek25,\n" +
                        "    DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.rel_eksp_zabezp25,    \n" +
                        "    NULL,\n" +
                        "    NULL,\n" +
                        "    NULL,\n" +
                        "    NULL,\n" +
                        "    NULL,\n" +
                        "    NULL,\n" +
                        "\tDB51_CAD.ESKP_ZABEZP_RLNP_STG_8.id_banku\n" +
                        "WHERE \n" +
                        "     (zabezp_id, nr_rach_trans_knt, sys_zabezp, sys_eksp, data_danych, id_banku) NOT IN \n" +
                        "\t\t(SELECT zabezp_id, nr_rach_trans_knt, sys_zabezp, sys_eksp, data_danych, id_banku FROM DB51_CAD.CAD_RK_1);;",
                "Tables:\n" +
                        "DB51_CAD.CAD_RK_1\n" +
                        "DB51_CAD.ESKP_ZABEZP_RLNP_STG_8\n" +
                        "\nFields:\n" +
                        "DB51_CAD.CAD_RK_1.a\n" +
                        "DB51_CAD.CAD_RK_1.data_danych\n" +
                        "DB51_CAD.CAD_RK_1.dost_zabezp_grupa\n" +
                        "DB51_CAD.CAD_RK_1.hc\n" +
                        "DB51_CAD.CAD_RK_1.hfx\n" +
                        "DB51_CAD.CAD_RK_1.id_banku\n" +
                        "DB51_CAD.CAD_RK_1.nr_rach_trans_knt\n" +
                        "DB51_CAD.CAD_RK_1.REL_EKSP_HIPO0\n" +
                        "DB51_CAD.CAD_RK_1.REL_EKSP_HIPO1\n" +
                        "DB51_CAD.CAD_RK_1.rel_eksp_zabezp0\n" +
                        "DB51_CAD.CAD_RK_1.rel_eksp_zabezp1\n" +
                        "DB51_CAD.CAD_RK_1.rel_eksp_zabezp2\n" +
                        "DB51_CAD.CAD_RK_1.rel_eksp_zabezp25\n" +
                        "DB51_CAD.CAD_RK_1.sys_eksp\n" +
                        "DB51_CAD.CAD_RK_1.sys_zabezp\n" +
                        "DB51_CAD.CAD_RK_1.t\n" +
                        "DB51_CAD.CAD_RK_1.t_bis\n" +
                        "DB51_CAD.CAD_RK_1.zabezp_id\n" +
                        "DB51_CAD.CAD_RK_1.zwiazek0\n" +
                        "DB51_CAD.CAD_RK_1.zwiazek1\n" +
                        "DB51_CAD.CAD_RK_1.zwiazek2\n" +
                        "DB51_CAD.CAD_RK_1.zwiazek25\n" +
                        "DB51_CAD.CAD_RK_1.ZWIAZEK_HIP0\n" +
                        "DB51_CAD.CAD_RK_1.ZWIAZEK_HIP1\n" +
                        "DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.data_danych\n" +
                        "DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.id_banku\n" +
                        "DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.nr_rach_trans_knt\n" +
                        "DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.REL_EKSP_HIPO0\n" +
                        "DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.REL_EKSP_HIPO1\n" +
                        "DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.rel_eksp_zabezp0\n" +
                        "DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.rel_eksp_zabezp1\n" +
                        "DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.rel_eksp_zabezp2\n" +
                        "DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.rel_eksp_zabezp25\n" +
                        "DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.sys_eksp\n" +
                        "DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.sys_zabezp\n" +
                        "DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.zabezp_id\n" +
                        "DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.zwiazek0\n" +
                        "DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.zwiazek1\n" +
                        "DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.zwiazek2\n" +
                        "DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.zwiazek25\n" +
                        "DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.ZWIAZEK_HIP0\n" +
                        "DB51_CAD.ESKP_ZABEZP_RLNP_STG_8.ZWIAZEK_HIP1");
    }

    public static void testUpdate() {

        doTest("UPDATE DB51_CAD.CAD_FX\n" +
                        " SET fund_wlasne = DB51_CAD.TAB_FX_4.fund_wlasne\n" +
                        "WHERE CAD_FX.data_danych = date'2016-10-09'\n" +
                        " AND DB51_CAD.TAB_FX_4.data = date'2016-10-09'\n" +
                        " AND CAD_FX.id_banku = DB51_CAD.TAB_FX_4.id_banku;",
                "Tables:\n" +
                        "DB51_CAD.CAD_FX\n" +
                        "DB51_CAD.TAB_FX_4\n" +
                        "\nFields:\n" +
                        "DB51_CAD.CAD_FX.data_danych\n" +
                        "DB51_CAD.CAD_FX.fund_wlasne\n" +
                        "DB51_CAD.CAD_FX.id_banku\n" +
                        "DB51_CAD.TAB_FX_4.data\n" +
                        "DB51_CAD.TAB_FX_4.fund_wlasne\n" +
                        "DB51_CAD.TAB_FX_4.id_banku");
    }

    public static void testCurrent_Date() {
        doTest("DELETE\n" +
                        "FROM SWSMBBI.F_MOB_MOBILEBASE_EDW\n" +
                        "WHERE per_end_dt = (SELECT calendar_date - EXTRACT(DAY FROM calendar_date)\n" +
                        "                     FROM SYS_CALENDAR.CALENDAR\n" +
                        "                        WHERE calendar_date = CURRENT_DATE)",
                "Tables:\n" +"SWSMBBI.F_MOB_MOBILEBASE_EDW\n" +
                        "SYS_CALENDAR.CALENDAR\n" +
                        "\nFields:\n" +
                        "SWSMBBI.F_MOB_MOBILEBASE_EDW.per_end_dt\n" +
                        "SYS_CALENDAR.CALENDAR.calendar_date");
    }

    public void testColumnSourceTable(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "del from table1 where table1.id = table2.id \n" +
                "and table2.id = table3.id";

        assertTrue(sqlparser.parse() == 0);
        TDeleteSqlStatement delete = (TDeleteSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = delete.getWhereClause().getCondition().getRightOperand().getLeftOperand();
        assertTrue(expr.getObjectOperand().getSourceTable().toString().endsWith("table2"));
    }

    public void testSchema(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "Select * from DBC.B b where DBC.A.id = b.id;";

        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = select.getWhereClause().getCondition().getLeftOperand();
        assertTrue(expr.getObjectOperand().getDatabaseToken().toString().equalsIgnoreCase("DBC"));
        TTable sourceTable = expr.getObjectOperand().getSourceTable();
        assertTrue(sourceTable.toString().endsWith("A"));
        TObjectName tableName = sourceTable.getTableName();
        assertTrue(tableName.getDatabaseToken().toString().equalsIgnoreCase("DBC"));
        assertTrue(sourceTable.getPrefixDatabase().equalsIgnoreCase("DBC"));

    }

    public static void testCreateTableSubquery() {
        doTest("create volatile table test as \n" +
                        "(\n" +
                        "SELECT CAST(ID AS DECIMAL(20,5)) AS ID,\n" +
                        " a.t2,\n" +
                        " t3,\n" +
                        " t4 as t5\n" +
                        "FROM A\n" +
                        ");",
                "Tables:\n" +
                        "A\n" +
                        "test\n" +
                        "\nFields:\n" +
                        "A.ID\n" +
                        "A.t2\n" +
                        "A.t3\n" +
                        "A.t4\n" +
                        "test.ID\n" +
                        "test.t2\n" +
                        "test.t3\n" +
                        "test.t5");
    }

    public static void testCreateTableSubquery2() {
        doTest("create volatile table test as \n" +
                        "(\n" +
                        "SELECT CAST(99 AS INTEGER) AS CVM_TYPE_IND\n" +
                        ",CAST(NULL AS DECIMAL(20,5)) AS COST_VOICE_NETCO\n" +
                        "FROM A\n" +
                        ");",
                "Tables:\n" +
                        "A\n" +
                        "test\n" +
                        "\nFields:\n" +
                        "test.COST_VOICE_NETCO\n" +
                        "test.CVM_TYPE_IND");
    }

    public static void testColumnIsAlias() {
        doTest("CREATE VOLATILE TABLE VT_DATES\n" +
                        "AS (SELECT SVC_NO ,ACCT_ID ,CALENDAR_DATE \n" +
                        "FROM \n" +
                        " (SELECT A.SERVICE_NO AS SVC_NO,KA.ACCT_ID FROM \n" +
                        " VT_ACCT_BASE A\n" +
                        " INNER JOIN KEY_ACCT KA\n" +
                        " ON KA.SRC_ACCOUNT_NUMBER = A.BILLING_ACCOUNT_NO\n" +
                        " WHERE A.BILLING_ACCOUNT_NO = '24011064000152'\n" +
                        " AND KA.SRC_SYSTEM_CD = 'CA'\n" +
                        " AND SVC_NO IS NOT NULL\n" +
                        "  GROUP BY 1,2\n" +
                        "\n" +
                        "  ) A\n" +
                        " CROSS JOIN (SELECT CALENDAR_DATE FROM SYS_CALENDAR.CALENDAR \n" +
                        "                       WHERE CALENDAR_DATE BETWEEN  DATE - 60 AND DATE \n" +
                        "                       ) C\n" +
                        "                       GROUP BY 1,2,3\n" +
                        ") WITH DATA \n" +
                        "ON COMMIT PRESERVE ROWS",
                "Tables:\n" +
                        "KEY_ACCT\n" +
                        "SYS_CALENDAR.CALENDAR\n" +
                        "VT_ACCT_BASE\n" +
                        "VT_DATES\n" +
                        "\nFields:\n" +
                        "KEY_ACCT.ACCT_ID\n" +
                        "KEY_ACCT.SRC_ACCOUNT_NUMBER\n" +
                        "KEY_ACCT.SRC_SYSTEM_CD\n" +
                        "SYS_CALENDAR.CALENDAR.CALENDAR_DATE\n" +
                        "VT_ACCT_BASE.BILLING_ACCOUNT_NO\n" +
                        "VT_ACCT_BASE.SERVICE_NO\n" +
                        "VT_DATES.ACCT_ID\n" +
                        "VT_DATES.CALENDAR_DATE\n" +
                        "VT_DATES.SVC_NO");
    }

    public static void testColumnDate() {
        doTest("SELECT * FROM table1 t1, table2 t2 WHERE t1.date = t2.id",
                "Tables:\n" +
                        "table1\n" +
                        "table2\n" +
                        "\nFields:\n" +
                        "table1.date\n" +
                        "table2.id");
    }

    public static void testTDUnpivot() {
        doTest("SELECT * from TD_UNPIVOT(\n" +
                        "        ON( select * from DBC.Accounts)\n" +
                        "        USING\n" +
                        "            VALUE_COLUMNS('RowType')\n" +
                        "            UNPIVOT_COLUMN('AccountName')\n" +
                        "            COLUMN_LIST('UserId','RowType')\n" +
                        "            COLUMN_ALIAS_LIST('UserId1','RowType1')\n" +
                        ")X; ",
                "Tables:\n" +
                        "DBC.Accounts\n" +
                        "td_unpivot\n" +
                        "\nFields:\n" +
                        "DBC.Accounts.'AccountName'\n" +
                        "DBC.Accounts.'RowType'\n" +
                        "DBC.Accounts.'UserId'");
    }

    public static void testTDUnpivot2() {
        doTest("SELECT * from TD_UNPIVOT(\n" +
                        "        ON( select * from T)\n" +
                        "        USING\n" +
                        "            VALUE_COLUMNS('monthly_sales')\n" +
                        "            UNPIVOT_COLUMN('month')\n" +
                        "            COLUMN_LIST('jan_sales', 'feb_sales', 'dec_sales')\n" +
                        "            COLUMN_ALIAS_LIST('jan', 'feb', 'dec' )\n" +
                        "    )X;",
                "Tables:\n" +
                        "T\n" +
                        "td_unpivot\n" +
                        "\nFields:\n" +
                        "T.'dec_sales'\n" +
                        "T.'feb_sales'\n" +
                        "T.'jan_sales'\n" +
                        "T.'month'\n" +
                        "T.'monthly_sales'");
    }

    public static void testCastDate() {
        doTest("select ydLLdNG_mTzTEMENT_dD\n" +
                        "              ,Col1_DT (date) cust_Dt\n" +
                        "              ,PRORzTE_dND\n" +
                        "         FROM zxxountLevelxhzrgem\n" +
                        "              WHERE ydLL_mTzTEMENT_xHzRGE_zMT > 0"+
                        "    ",
                "Tables:\n" +
                        "zxxountLevelxhzrgem\n" +
                        "\n" +
                        "Fields:\n" +
                        "zxxountLevelxhzrgem.Col1_DT\n" +
                        "zxxountLevelxhzrgem.PRORzTE_dND\n" +
                        "zxxountLevelxhzrgem.ydLL_mTzTEMENT_xHzRGE_zMT\n" +
                        "zxxountLevelxhzrgem.ydLLdNG_mTzTEMENT_dD");
    }

    public static void testSeperateTable() {
        doTest("USING _spVV0 (INTEGER) \n" +
                        "\tINSERT INTO table3 \n" +
                        "\t\tSELECT :_spVV0,x. *,m.col3 \n" +
                        "\t\tfrom ((           select table1.col1, (table1.col1 + table5.col2) c from table1 \n" +
                        "\t\t\t\tunion all select col3,col4 from table2) x \n" +
                        "\t\t\t  cross join (select id from table2) m )",
                "Tables:\n" +
                        "table1\n" +
                        "table2\n" +
                        "table3\n" +
                        "table5\n" +
                        "\n" +
                        "Fields:\n" +
                        "table1.col1\n" +
                        "table2.col3\n" +
                        "table2.col4\n" +
                        "table2.id\n" +
                        "table5.col2");
    }

    public static void testMergeUsingTable() {
        doTest("USING (empno INTEGER,\n" +
                        "name VARCHAR(50),\n" +
                        "salary INTEGER)\n" +
                        "MERGE INTO employee AS t\n" +
                        "USING VALUES (:empno, :name, :salary) AS s(empno, name, salary)\n" +
                        "ON t.empno=s.empno\n" +
                        "WHEN MATCHED THEN UPDATE\n" +
                        "SET salary=s.salary\n" +
                        "WHEN NOT MATCHED THEN INSERT (empno, name, salary)\n" +
                        "VALUES (s.empno, s.name, s.salary);",
                "Tables:\n" +
                        "(values_table)\n" +
                        "employee\n" +
                        "\n" +
                        "Fields:\n" +
                        "(values_table).empno\n" +
                        "(values_table).name\n" +
                        "(values_table).salary\n" +
                        "employee.empno\n" +
                        "employee.name\n" +
                        "employee.salary");
    }

}
