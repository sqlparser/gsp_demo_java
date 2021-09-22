package teradata;


import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TDeclareVariable;
import gudusoft.gsqlparser.stmt.*;
import gudusoft.gsqlparser.stmt.mssql.TMssqlDeclare;
import gudusoft.gsqlparser.stmt.mssql.TMssqlFetch;
import gudusoft.gsqlparser.stmt.mssql.TMssqlOpen;
import gudusoft.gsqlparser.stmt.mssql.TMssqlSet;
import junit.framework.TestCase;

import static gudusoft.gsqlparser.ESqlStatementType.*;

public class testCreateProcedure extends TestCase {

    public void testPrepare(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "CREATE PROCEDURE S1.GetEmployeeSalary\n" +
                "(IN EmpName VARCHAR(100), OUT Salary DEC(10,2))\n" +
                "BEGIN\n" +
                "DECLARE SqlStr VARCHAR(1000);\n" +
                "DECLARE C1 CURSOR FOR S1;\n" +
                "SET SqlStr = 'SELECT Salary FROM EmployeeTable WHERE EmpName = ?';\n" +
                "PREPARE S1 FROM SqlStr;\n" +
                "OPEN C1 USING EmpName;\n" +
                "FETCH C1 INTO Salary;\n" +
                "CLOSE C1;\n" +
                "END;";
        assertTrue(sqlparser.parse() == 0);

        TCreateProcedureStmt cp = (TCreateProcedureStmt)sqlparser.sqlstatements.get(0);
        assertTrue(cp.getProcedureName().toString().equalsIgnoreCase("S1.GetEmployeeSalary"));

        assertTrue(cp.getBodyStatements().size() == 7);


        System.out.println(cp.getBodyStatements().get(3).sqlstatementtype);
        assertTrue(cp.getBodyStatements().get(3).sqlstatementtype == sstprepare);
        TPrepareStmt prepareStmt =(TPrepareStmt)cp.getBodyStatements().get(3);
        assertTrue(prepareStmt.getStmtName().toString().equalsIgnoreCase("S1"));
        assertTrue(prepareStmt.getPreparableStmtStr().equalsIgnoreCase("SqlStr"));
    }

    public void testFetch(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "REPLACE PROCEDURE S1.CURSOR_PROC(IN SCENARIO VARCHAR(20), OUT RUN_STATUS VARCHAR(20) )\n" +
                "BEGIN\n" +
                "\n" +
                "DECLARE id INTEGER DEFAULT 0;\n" +
                "DECLARE desc VARCHAR(50);\n" +
                "\n" +
                "DECLARE C4 CURSOR FOR\n" +
                "select department_id, department_description from foodmart.department;\n" +
                "\n" +
                "OPEN C4;\n" +
                "FETCH C4 INTO id, desc;\n" +
                "CLOSE C4;\n" +
                "END;";
        assertTrue(sqlparser.parse() == 0);

        TCreateProcedureStmt cp = (TCreateProcedureStmt)sqlparser.sqlstatements.get(0);
        assertTrue(cp.getProcedureName().toString().equalsIgnoreCase("S1.CURSOR_PROC"));

        assertTrue(cp.getBodyStatements().size() == 6);


        assertTrue(cp.getBodyStatements().get(4).sqlstatementtype == sstmssqlfetch);
        TMssqlFetch fetch =(TMssqlFetch)cp.getBodyStatements().get(4);
        assertTrue(fetch.getCursorName().toString().equalsIgnoreCase("C4"));
        assertTrue(fetch.getVariableNames().getObjectName(0).toString().equalsIgnoreCase("id"));
        assertTrue(fetch.getVariableNames().getObjectName(1).toString().equalsIgnoreCase("desc"));
    }

    public void testSet(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "REPLACE PROCEDURE S1.SET_CHECK()\n" +
                "BEGIN\n" +
                "\tDECLARE A INTEGER DEFAULT 0;\n" +
                "\tSET A = 10;\n" +
                "END;";
        assertTrue(sqlparser.parse() == 0);

        TCreateProcedureStmt cp = (TCreateProcedureStmt)sqlparser.sqlstatements.get(0);
        assertTrue(cp.getProcedureName().toString().equalsIgnoreCase("S1.SET_CHECK"));

        assertTrue(cp.getBodyStatements().size() == 2);


        assertTrue(cp.getBodyStatements().get(1).sqlstatementtype == sstmssqlset);
        TMssqlSet setStmt = (TMssqlSet)cp.getBodyStatements().get(1);
        assertTrue(setStmt.toString().equalsIgnoreCase("SET A = 10;"));
    }

    public void testWhile(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "CREATE PROCEDURE proc (\n" +
                "        IN i INTEGER,\n" +
                "        IN j INTEGER)\n" +
                "BEGIN\n" +
                "    DECLARE ii integer;\n" +
                "    set ii=i;\n" +
                "    while (ii<j) do\n" +
                "    begin\n" +
                "        set ii=ii+1;\n" +
                "    end;\n" +
                "    end while;\n" +
                "END;";
        assertTrue(sqlparser.parse() == 0);

        TCreateProcedureStmt cp = (TCreateProcedureStmt)sqlparser.sqlstatements.get(0);
        assertTrue(cp.getProcedureName().toString().equalsIgnoreCase("proc"));

        assertTrue(cp.getBodyStatements().size() == 3);

        assertTrue(cp.getBodyStatements().get(2).sqlstatementtype == sstWhilestmt);
        TWhileStmt whileStmt = (TWhileStmt)cp.getBodyStatements().get(2);
        assertTrue(whileStmt.getCondition().toString().equalsIgnoreCase("(ii<j)"));
        assertTrue(whileStmt.getBodyStatements().size() == 1);

    }

    public void test1(){

     TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
     sqlparser.sqltext = "create proc merge_salesdetail\n" +
             "as\n" +
             "merge into salesdetail as s\n" +
             "using salesdetailupdates as u \n" +
             "on s.stor_id = u.stor_id and\n" +
             "  s.ord_num = u.ord_num and\n" +
             "  s.title_id = u.title_id\n" +
             "when not matched then   \n" +
             "    insert (stor_id, ord_num, title_id, qty, discount) values(u.stor_id, u.ord_num, u.title_id, u.qty, u.discount) \n" +
             "when matched then   \n" +
             "    update set qty=u.qty, discount=u.discount";
     assertTrue(sqlparser.parse() == 0);

        TCreateProcedureStmt cp = (TCreateProcedureStmt)sqlparser.sqlstatements.get(0);
        assertTrue(cp.getProcedureName().toString().equalsIgnoreCase("merge_salesdetail"));

        assertTrue(cp.getBodyStatements().size() == 1);

       //System.out.println(cp.getBodyStatements().get(0).sqlstatementtype.toString());

        assertTrue(cp.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstmerge);
        TMergeSqlStatement merge = (TMergeSqlStatement)cp.getBodyStatements().get(0);
        assertTrue(merge.getTargetTable().toString().equalsIgnoreCase("salesdetail"));
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "CREATE PROCEDURE EDW_TABLES_DEV.samplesp2()\n" +
                "              DYNAMIC RESULT SETS 1\n" +
                "BEGIN\n" +
                "\n" +
                "      /* SPL Statements*/\n" +
                "\n" +
                "   DECLARE cur1 CURSOR WITH RETURN ONLY FOR\n" +
                "\n" +
                "   --insert into ITEM_COST select * from ITEM_COST_STG;\n" +
                "\n" +
                "   select * from ITEM_COST;\n" +
                "  \n" +
                "\n" +
                "   open cur1;\n" +
                "\n" +
                "END;";
        assertTrue(sqlparser.parse() == 0);

        TCreateProcedureStmt cp = (TCreateProcedureStmt)sqlparser.sqlstatements.get(0);
        assertTrue(cp.getProcedureName().toString().equalsIgnoreCase("EDW_TABLES_DEV.samplesp2"));

        assertTrue(cp.getBodyStatements().size() == 2);

        assertTrue(cp.getBodyStatements().get(0).sqlstatementtype == sstmssqldeclare);
        TMssqlDeclare declare = (TMssqlDeclare)cp.getBodyStatements().get(0);
        assertTrue(declare.getCursorName().toString().equalsIgnoreCase("cur1"));
        TSelectSqlStatement subquery = declare.getSubquery();
        assertTrue(subquery.getTables().getTable(0).toString().equalsIgnoreCase("ITEM_COST"));


        assertTrue(cp.getBodyStatements().get(1).sqlstatementtype == ESqlStatementType.sstmssqlopen);
        TMssqlOpen open = (TMssqlOpen)cp.getBodyStatements().get(1);
        assertTrue(open.getCursorName().toString().equalsIgnoreCase("cur1"));

        //assertTrue(cp.getBodyStatements().get(0).sqlstatementtype == ESqlStatementType.sstmerge);
    }

    public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "Create  PROCEDURE EDW_TABLES_DEV.samplesp7(out mytest integer)\n" +
                "DYNAMIC RESULT SETS 1\n" +
                "P1: BEGIN\n" +
                "   DECLARE V1 INTEGER;\n" +
                "   DECLARE EXIT HANDLER FOR SQLEXCEPTION\n" +
                "\n" +
                "   select loc_id  into v1 from EDW_TABLES_DEV.ITEM_COST_STG;\n" +
                "\n" +
                "   delete from EDW_TABLES_DEV.ITEM_COST_STG where loc_id=10;\n" +
                " \n" +
                "   UPDATE EDW_TABLES.ACCT_ACCT_MSG\n" +
                "   SET MSG_CNT = :v1\n" +
                "   where MSG_CNT = :v1;\n" +
                "\n" +
                "  \n" +
                "END P1;";
        assertTrue(sqlparser.parse() == 0);

        TCreateProcedureStmt cp = (TCreateProcedureStmt)sqlparser.sqlstatements.get(0);
        assertTrue(cp.getProcedureName().toString().equalsIgnoreCase("EDW_TABLES_DEV.samplesp7"));

        assertTrue(cp.getBodyStatements().size() == 4);
        assertTrue(cp.getBodyStatements().get(0).sqlstatementtype == sstmssqldeclare);

        TMssqlDeclare declare = (TMssqlDeclare)cp.getBodyStatements().get(0);
        assertTrue(declare.getDeclareType() == EDeclareType.variable);
        TDeclareVariable variable = declare.getVariables().getDeclareVariable(0);
        assertTrue(variable.getDatatype().getDataType() == EDataType.integer_t);
        assertTrue(variable.getVariableName().toString().equalsIgnoreCase("V1"));

        assertTrue(cp.getBodyStatements().get(1).sqlstatementtype == sstmssqldeclare);
        declare = (TMssqlDeclare)cp.getBodyStatements().get(1);
        assertTrue(declare.getDeclareType() == EDeclareType.handlers);
        TSelectSqlStatement subquery = declare.getSubquery();
        assertTrue(subquery.getTables().getTable(0).toString().equalsIgnoreCase("EDW_TABLES_DEV.ITEM_COST_STG"));

        assertTrue(cp.getBodyStatements().get(2).sqlstatementtype == ESqlStatementType.sstdelete);
        assertTrue(cp.getBodyStatements().get(3).sqlstatementtype == ESqlStatementType.sstupdate);

    }

    public void testDag1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "--TMDW_SVC_LOAN - MSCC\n" +
                "\n" +
                "REPLACE PROCEDURE EDW1_RBG_APPLOGIC.LOAD_TMDW_SVC_LOAN_H_MSCC\n" +
                "\n" +
                "(\n" +
                " IN IN_ORIG_DATE DATE,\n" +
                " OUT OUT_LOAD_QRY VARCHAR(1000), \n" +
                " OUT OUT_LOAD_DATES VARCHAR(10000),\n" +
                " OUT OUT_LOAD_DATE_CNT INTEGER\n" +
                ")\n" +
                "\n" +
                "/*\n" +
                "This procedure loads mortgage servicing loan table with MSCC data. \n" +
                "It loads the data into the tables date by date by looping through a \n" +
                "cursor which contains the distinct dates in the stage table.\n" +
                "*/\n" +
                "\n" +
                "BEGIN \n" +
                "\n" +
                "--Declare variables\n" +
                "DECLARE V_RSQL_SESSION VARCHAR(100) ;\n" +
                "DECLARE V_COUNTER INTEGER;\n" +
                "DECLARE V_ACT_CNT INTEGER;\n" +
                "DECLARE V_LOAD_QRY VARCHAR(1000);\n" +
                "DECLARE V_LOAD_DATE VARCHAR(10);\n" +
                "DECLARE V_LOAD_DATE_CNT INTEGER;\n" +
                "DECLARE V_LOAD_DATES VARCHAR(10000);\n" +
                "\n" +
                "--Declare the cursor\n" +
                "DECLARE CUR_LOAD_DATE CURSOR FOR\n" +
                "\n" +
                "--Get the dates for which the data should be loaded into the table\n" +
                "WITH CTE_REQ_DATE (BUSINESS_DATE) \n" +
                "AS \n" +
                "(\n" +
                "--Get business dates from MSCC servicing data stage table\n" +
                "SELECT DISTINCT\n" +
                "    MSVC.ASOFDATE\n" +
                "\n" +
                "FROM EDW1_RBG_STAGE.TSTG_MRTG_MSCC_SVC MSVC\n" +
                ")\n" +
                "\n" +
                "SELECT \n" +
                "    CAST((RD.BUSINESS_DATE (FORMAT 'YYYY-MM-DD')) AS VARCHAR(10))\n" +
                "FROM CTE_REQ_DATE RD \n" +
                "ORDER BY RD.BUSINESS_DATE;\n" +
                "\n" +
                "--Set the query band\n" +
                "SET V_RSQL_SESSION = 'SET QUERY_BAND =' || '''' || 'APPL=RBG;PROC=LOAD_TMDW_SVC_LOAN_H_MSCC;' || '''' || 'FOR SESSION; ';\n" +
                "CALL DBC.SYSEXECSQL(:V_RSQL_SESSION); \n" +
                "COMMIT ;\n" +
                "\n" +
                "--Open the cursor\n" +
                "OPEN CUR_LOAD_DATE;\n" +
                "SET V_ACT_CNT = ACTIVITY_COUNT;\n" +
                "SET V_COUNTER = 0;\n" +
                "SET V_LOAD_DATE_CNT = 0;\n" +
                "SET V_LOAD_DATES = ''; \n" +
                "\n" +
                "--Loop through the cursor and load data into the table\n" +
                "REPEAT \n" +
                "    FETCH CUR_LOAD_DATE INTO V_LOAD_DATE;\n" +
                " \n" +
                "    SELECT 'SELECT ''' || :V_LOAD_DATE || ''' AS LOAD_DATE;' INTO :V_LOAD_QRY;\n" +
                "\n" +
                "    --Delete rows for loans where servicing vendor code = 'MSCC'\n" +
                "    DELETE FROM EDW1_RBG_BASEVIEW.TMDW_SVC_LOAN\n" +
                "    WHERE DT2_BUSINESS = :V_LOAD_DATE AND CDE_SERVICING_VENDOR = 'MSCC';\n" +
                "    \n" +
                "    --Insert rows for loans where servicing vendor code = 'MSCC' \n" +
                "    INSERT INTO EDW1_RBG_BASEVIEW.TMDW_SVC_LOAN\n" +
                "    (\n" +
                "          AMT_CURTAILMENT\n" +
                "        , AMT_GROSS_PROCEEDS\n" +
                "        , AMT_GROSS_RECOVERY\n" +
                "        , AMT_LAST_PYMT_RCD\n" +
                "        , AMT_LOSS\n" +
                "        , AMT_ORIG_PRINCIPAL_BAL\n" +
                "        , AMT_SERVICING_ADVANCE\n" +
                "        , AMT_TOTAL_EXPENSE\n" +
                "        , AMT_TOTAL_PAYMENT\n" +
                "        , CDE_DELINQUENCY_PERIOD\n" +
                "        , CDE_FORECLOSURE_STOP\n" +
                "        , CDE_LIQUIDATION_TYP\n" +
                "        , CDE_PYMT_IN_FULL_STOP\n" +
                "        , CDE_SERVICING_VENDOR\n" +
                "        , DT2_ACTUAL_REO_CLOSING\n" +
                "        , DT2_BUSINESS\n" +
                "        , DT2_DISPOSITION\n" +
                "        , DT2_FORECLOSURE_SALE\n" +
                "        , DT2_LAST_PAYMENT\n" +
                "        , DT2_NEXT_PAYMENT_DUE\n" +
                "        , DT2_PIF_SERVICER_SOLD\n" +
                "        , IDN_FA_VIEWABLE\n" +
                "        , IDN_LOAN_SYSTEM\n" +
                "        , IDN_MS_LOAN\n" +
                "        , IDN_PRODUCT_SYSTEM\n" +
                "        , IDN_SVC_LOAN_SYSTEM\n" +
                "        , IND_LOSS\n" +
                "        , IND_PBNKR_VIEWABLE\n" +
                "        , NME_DELINQUENCY_PERIOD\n" +
                "        , NME_DISPOSITION_TYPE\n" +
                "        , NME_FORECLOSURE_STOP\n" +
                "        , NME_LIQUIDATION_TYP\n" +
                "        , NME_PYMT_IN_FULL_STOP\n" +
                "        , NME_SERVICING_VENDOR\n" +
                "        , NME_SERVICING_VENDOR_SOLD\n" +
                "        , NUM_DELINQUENT_DAYS\n" +
                "        , NUM_LOAN\n" +
                "        , PCT_ARM_INDEX\n" +
                "        , PCT_LOAN_INTEREST_RATE\n" +
                "    )\n" +
                "    SELECT \n" +
                "          MSVC.CURTAILMENTAMOUNT AS AMT_CURTAILMENT\n" +
                "        , MSVC.GROSSPROCEEDS AS AMT_GROSS_PROCEEDS\n" +
                "        , MSVC.GROSSRECOVERYAMOUNT AS AMT_GROSS_RECOVERY\n" +
                "        , MSVC.LASTPAYMENTRECEIVEDAMOUNT AS AMT_LAST_PYMT_RCD\n" +
                "        , MSVC.LOSSAMOUNT AS AMT_LOSS\n" +
                "        , MSVC.CURRENTBALANCE AS AMT_ORIG_PRINCIPAL_BAL\n" +
                "        , MSVC.SERVICINGADVANCES AS AMT_SERVICING_ADVANCE\n" +
                "        , MSVC.TOTALEXPENSES AS AMT_TOTAL_EXPENSE\n" +
                "        , MSVC.PAYMENTAMOUNTDUE AS AMT_TOTAL_PAYMENT\n" +
                "        , MSVC.MBADELINQUENCYSTATUS AS CDE_DELINQUENCY_PERIOD\n" +
                "        , MSVC.FORECLOSURECODE AS CDE_FORECLOSURE_STOP\n" +
                "        , NULL AS CDE_LIQUIDATION_TYP --lookup?\n" +
                "        , MSVC.CURRENTSTATUS AS CDE_PYMT_IN_FULL_STOP\n" +
                "        , MSVC.CURRENTSERVICINGSYSTEM AS CDE_SERVICING_VENDOR\n" +
                "        , MSVC.SALEDATE AS DT2_ACTUAL_REO_CLOSING\n" +
                "        , MSVC.ASOFDATE AS DT2_BUSINESS\n" +
                "        , MSVC.DISPOSITIONDATE AS DT2_DISPOSITION\n" +
                "        , MSVC.FORECLOSURESALEDATE AS DT2_FORECLOSURE_SALE\n" +
                "        , MSVC.LASTPAYMENTRECEIVEDDATE AS DT2_LAST_PAYMENT\n" +
                "        , MSVC.NEXTPAYMENTDUEDATE AS DT2_NEXT_PAYMENT_DUE\n" +
                "        , MSVC.SERVICINGSOLDDATE AS DT2_PIF_SERVICER_SOLD\n" +
                "        , MSVC.FAVIEWABLE AS IDN_FA_VIEWABLE\n" +
                "        , SK.IDN_LOAN_SYSTEM\n" +
                "        , SK.IDN_MS_LOAN\n" +
                "        , SK.IDN_PRODUCT_SYSTEM\n" +
                "        , SK.IDN_SVC_LOAN_SYSTEM\n" +
                "        , MSVC.LOSSINDICATOR AS IND_LOSS\n" +
                "        , MSVC.PBVIEWABLE AS IND_PBNKR_VIEWABLE\n" +
                "        , MCM.NME_CODE AS NME_DELINQUENCY_PERIOD\n" +
                "        , MSVC.DISPOSITIONTYPE AS NME_DISPOSITION_TYPE\n" +
                "        , NULL AS NME_FORECLOSURE_STOP --lookup?\n" +
                "        , NULL AS NME_LIQUIDATION_TYP --lookup?\n" +
                "        , NULL AS NME_PYMT_IN_FULL_STOP --lookup?\n" +
                "        , 'Morgan Stanley Credit Corporation' AS NME_SERVICING_VENDOR\n" +
                "        , MSVC.NEWSERVICERNAME AS NME_SERVICING_VENDOR_SOLD\n" +
                "        , MSVC.MBADELINQUENTDAYS AS NUM_DELINQUENT_DAYS\n" +
                "        , CASE WHEN SUBSTR(MSVC.LOANIDSERVICER, 1, 3) = '940' AND LENGTH(MSVC.LOANIDSERVICER) = 13 THEN SUBSTR(MSVC.LOANIDSERVICER, 4, 10) ELSE MSVC.LOANIDSERVICER END AS NUM_LOAN\n" +
                "        , MSVC.ARMINDEXRATE AS PCT_ARM_INDEX\n" +
                "        , MSVC.CURRENTINTERESTRATE AS PCT_LOAN_INTEREST_RATE\n" +
                "\n" +
                "    FROM EDW1_RBG_STAGE.TSTG_MRTG_MSCC_SVC MSVC\n" +
                "    \n" +
                "    INNER JOIN EDW1_RBG_STAGE.TSTG_MRTG_MSCC_SVC_SK SK\n" +
                "    ON MSVC.LOANIDSERVICER = SK.LOANID_SERVICER\n" +
                "    \n" +
                "    LEFT JOIN EDW1_RBG_BASEVIEW.TMDW_CODE_MASTER MCM\n" +
                "    ON MSVC.MBADELINQUENCYSTATUS = MCM.IDN_CODE\n" +
                "    AND MCM.NME_CODE_TYPE = 'DELINQUENCY_INDICATOR'\n" +
                "        \n" +
                "    WHERE MSVC.ASOFDATE = :V_LOAD_DATE\n" +
                "    ;\n" +
                "\n" +
                "    SET V_LOAD_DATE_CNT = V_LOAD_DATE_CNT + 1;\n" +
                "    SET V_LOAD_DATES = V_LOAD_DATES || V_LOAD_DATE || ';';\n" +
                "    \n" +
                "    SET V_COUNTER = V_COUNTER + 1;\n" +
                " \n" +
                "UNTIL V_COUNTER >= V_ACT_CNT\n" +
                "END REPEAT ;\n" +
                "\n" +
                "--Close the cursor and commit\n" +
                "CLOSE CUR_LOAD_DATE;\n" +
                "CALL DBC.SYSEXECSQL('COMMIT;');\n" +
                "\n" +
                "--Set output parameter values\n" +
                "SET OUT_LOAD_DATES = V_LOAD_DATES;\n" +
                "SET OUT_LOAD_DATE_CNT = V_LOAD_DATE_CNT ;\n" +
                "SET OUT_LOAD_QRY = V_LOAD_QRY; \n" +
                "\n" +
                "END ;\n";
        //System.out.println(sqlparser.sqltext);
        assertTrue(sqlparser.parse() == 0);

        TCreateProcedureStmt cp = (TCreateProcedureStmt)sqlparser.sqlstatements.get(0);
        assertTrue(cp.getProcedureName().toString().equalsIgnoreCase("EDW1_RBG_APPLOGIC.LOAD_TMDW_SVC_LOAN_H_MSCC"));


        assertTrue(cp.getBodyStatements().size() == 22);
        assertTrue(cp.getBodyStatements().get(9).sqlstatementtype == ESqlStatementType.sstcall);
        assertTrue(cp.getBodyStatements().get(10).sqlstatementtype == ESqlStatementType.sstcommit);
        assertTrue(cp.getBodyStatements().get(16).sqlstatementtype == ESqlStatementType.sstRepeat);
        assertTrue(cp.getBodyStatements().get(17).sqlstatementtype == ESqlStatementType.sst_closestmt);


        TRepeatStmt repeatStmt = (TRepeatStmt)cp.getBodyStatements().get(16);
        assertTrue(repeatStmt.getCondition().toString().equalsIgnoreCase("V_COUNTER >= V_ACT_CNT"));
        assertTrue(repeatStmt.getBodyStatements().size() == 7);

        assertTrue(cp.getBodyStatements().get(7).sqlstatementtype == sstmssqldeclare);
        TMssqlDeclare declare = (TMssqlDeclare)cp.getBodyStatements().get(7);
        assertTrue(declare.getSubquery().getTables().getTable(0).getTableName().toString().equalsIgnoreCase("CTE_REQ_DATE"));

    }

}
