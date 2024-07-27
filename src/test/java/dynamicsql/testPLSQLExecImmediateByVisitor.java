package dynamicsql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.stmt.TExecImmeStmt;
import junit.framework.TestCase;

public class testPLSQLExecImmediateByVisitor extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "DECLARE\n" +
                "   CURSOR C_PERIODO IS\n" +
                "   SELECT DATE_KEY,DATE_VALUE\n" +
                "   FROM DWO.DW_C_DATE\n" +
                "   WHERE TO_DATE(DATE_KEY,'YYYYMMDD') >= TO_DATE('<P_VAR_START_PROCESS_DATE>','YYYYMMDD') AND TO_DATE(DATE_KEY,'YYYYMMDD') <= TO_DATE('<P_VAR_END_PROCESS_DATE>','YYYYMMDD')\n" +
                "   ORDER BY DATE_KEY;\n" +
                "   V_PROCESSDATE DATE;\n" +
                "   V_PROCESSDATE_S DATE;\n" +
                "   V_THREAD VARCHAR2(8);\n" +
                "   V_THREAD_M VARCHAR2(6);\n" +
                "   FECHA VARCHAR2(16);\n" +
                "BEGIN\n" +
                "    ----------------------------------------------------------------\n" +
                "    DWO.PKG_FRAME_IDEAS_REPORT.SP_SET_PROCESS_STEP_LOG(999,'P_VAR_SCHEMA_TEMP: USRTEMP','PROCESO INICIADO');\n" +
                "    ----------------------------------------------------------------\n" +
                "\n" +
                "  FOR I IN C_PERIODO LOOP\n" +
                "    V_THREAD :=TO_CHAR(I.DATE_VALUE,'YYYYMMDD');\n" +
                "    V_THREAD_M :=TO_CHAR(I.DATE_VALUE,'YYYYMM');\n" +
                "\n" +
                "    ----------------------------------------------------------------\n" +
                "    DWO.PKG_FRAME_IDEAS_REPORT.SP_SET_PROCESS_STEP_LOG(999,'P_VAR_SCHEMA_TEMP: USRTEMP','PROCESO INICIADO PARA EL PERIODO '||V_THREAD);\n" +
                "    ----------------------------------------------------------------\n" +
                "    DWO.PKG_FRAME_IDEAS_REPORT.SP_SET_PROCESS_STEP_LOG(999,'P_VAR_SCHEMA_TEMP: USRTEMP','INICIANDO DROPEO DE TEMPORALES DE AMBITO LOCAL');\n" +
                "    ----------------------------------------------------------------\n" +
                "\n" +
                "    BEGIN\n" +
                "        DM.PKG_MGR_DWO_UTIL.PRC_DROP_TABLE(X_DES_TABLA => 'USRTEMP.T$_AYF_F_D_DEP_TRAF_POST_'||V_THREAD);\n" +
                "        DM.PKG_MGR_DWO_UTIL.PRC_DROP_TABLE(X_DES_TABLA => 'USRTEMP.T$_DW_T_SALES_POST_DEP_T_'||V_THREAD);\n" +
                "    END;\n" +
                "    \n" +
                "    ----------------------------------------------------------------\n" +
                "    DWO.PKG_FRAME_IDEAS_REPORT.SP_SET_PROCESS_STEP_LOG(999,'P_VAR_SCHEMA_TEMP: USRTEMP','FINALIZANDO DROPEO DE TEMPORALES DE AMBITO LOCAL');\n" +
                "    -------------------------------------------------------------------------------------------------------------------------------------------\n" +
                "    DWO.PKG_FRAME_IDEAS_REPORT.SP_SET_PROCESS_STEP_LOG(999,'','Iniciando creacion de la tabla temporal USRTEMP.T$_AYF_F_D_DEP_TRAF_POST_'||V_THREAD);\n" +
                "    -------------------------------------------------------------------------------------------------------------------------------------------\n" +
                "    \n" +
                "    EXECUTE IMMEDIATE 'CREATE TABLE USRTEMP.T$_AYF_F_D_DEP_TRAF_POST_'||V_THREAD||' TABLESPACE <P_DB_TBS_TEMP>\n" +
                "\tAS\n" +
                "    SELECT * FROM DWA.AYF_F_D_DEPART_TRAF_POSTPAGO\n" +
                "    WHERE 1=0';   \n" +
                "    \n" +
                "    V_PROCESSDATE := I.DATE_VALUE;\n" +
                "    V_PROCESSDATE_S := ADD_MONTHS(TO_DATE(TO_CHAR(I.DATE_VALUE,'YYYYMM'),'YYYYMM'),-1)-1;\n" +
                "    \n" +
                "    WHILE V_PROCESSDATE>=V_PROCESSDATE_S LOOP  \n" +
                "        \n" +
                "        BEGIN\n" +
                "            DM.PKG_MGR_DWO_UTIL.PRC_DROP_TABLE(X_DES_TABLA => 'USRTEMP.T$_DW_M_MTH_LT_POST_T1_'||TO_CHAR(V_PROCESSDATE,'YYYYMMDD')||'_'||V_THREAD);\n" +
                "            DM.PKG_MGR_DWO_UTIL.PRC_DROP_TABLE(X_DES_TABLA => 'USRTEMP.T$_DW_M_MTH_LT_POST_T2_'||TO_CHAR(V_PROCESSDATE,'YYYYMMDD')||'_'||V_THREAD);\n" +
                "        END;                                \n" +
                "\n" +
                "\t\t\n" +
                "        EXECUTE IMMEDIATE 'CREATE TABLE USRTEMP.T$_DW_M_MTH_LT_POST_T1_'||TO_CHAR(V_PROCESSDATE,'YYYYMMDD')||'_'||V_THREAD||q'[ AS \n" +
                "\t\tSELECT /*+ PARALLEL (20) */\n" +
                "\t\tU.MSISDN,\n" +
                "\t\tU.LAC_SC,\n" +
                "\t\tU.CELL_SC,\n" +
                "\t\tNVL(F1.DEPARTAMENTO,NVL(F2.DEPARTAMENTO,F3.DEPARTAMENTO)) DEPARTAMENTO_DES,\n" +
                "\t\tU.LAST_TRAFFIC_DATE,\n" +
                "\t\tROW_NUMBER() OVER(PARTITION BY U.MSISDN ORDER BY U.LAST_TRAFFIC_DATE DESC) R\n" +
                "\t\tFROM DWA.DW_M_MONTH_LASTTRAFFIC PARTITION (P_]'||TO_CHAR(V_PROCESSDATE,'YYYYMMDD')||q'[) U\n" +
                "\t\tLEFT JOIN USRTEMP.T$_AYF_F_D_DEP_TRAF_POST_]'||V_THREAD||q'[ D ON U.MSISDN=D.MSISDN\n" +
                "\t\tLEFT JOIN DM.CDR_CELDAS_RED F1 ON TRIM(F1.LAC)=TRIM(U.LAC_SC) AND TRIM(F1.CELL_ID)=TRIM(U.CELL_SC) AND UPPER(F1.PROVINCIA)='CALLAO'\n" +
                "        LEFT JOIN DM.C_LAC_RANGO F2 ON TRIM(F2.INICIO)<=TRIM(U.LAC_SC) AND TRIM(F2.FIN)>=TRIM(U.LAC_SC) AND LENGTH(U.LAC_SC)=4\n" +
                "        LEFT JOIN DM.CDR_CELDAS_RED F3 ON TRIM(F3.LAC)=TRIM(U.LAC_SC) AND TRIM(F3.CELL_ID)=TRIM(U.CELL_SC) AND UPPER(F3.PROVINCIA)<>'CALLAO' AND LENGTH(U.LAC_SC)=5\n" +
                "        WHERE\n" +
                "\t\tNVL(F1.DEPARTAMENTO,NVL(F2.DEPARTAMENTO,F3.DEPARTAMENTO)) IS NOT NULL AND\n" +
                "\t\t(\n" +
                "\t\t\t(U.ROUTING_CATEGORY_SC IN ('18', '19', '0400', '0200', '0800')) AND\n" +
                "\t\t\tD.MSISDN IS NULL\n" +
                "        )\n" +
                "\t\t]';\n" +
                "        \n" +
                "        EXECUTE IMMEDIATE 'CREATE TABLE USRTEMP.T$_DW_M_MTH_LT_POST_T2_'||TO_CHAR(V_PROCESSDATE,'YYYYMMDD')||'_'||V_THREAD||q'[ AS\n" +
                "        SELECT MSISDN,LAC_SC,CELL_SC,DEPARTAMENTO_DES,TO_CHAR(TRUNC(LAST_TRAFFIC_DATE),'YYYYMMDD') ORIGEN_DES\n" +
                "        FROM\n" +
                "        (      \n" +
                "          SELECT /*+ PARALLEL (20) */\n" +
                "          U.MSISDN,U.LAC_SC,U.CELL_SC,U.DEPARTAMENTO_DES,U.LAST_TRAFFIC_DATE,ROW_NUMBER() OVER(PARTITION BY U.MSISDN ORDER BY U.LAST_TRAFFIC_DATE DESC) R\n" +
                "\t\t  FROM USRTEMP.T$_DW_M_MTH_LT_POST_T1_]'||TO_CHAR(V_PROCESSDATE,'YYYYMMDD')||'_'||V_THREAD||q'[ U\n" +
                "        )\n" +
                "        WHERE R=1\n" +
                "        ]';\n" +
                "        \n" +
                "        EXECUTE IMMEDIATE 'INSERT INTO USRTEMP.T$_AYF_F_D_DEP_TRAF_POST_'||V_THREAD||q'[\n" +
                "        SELECT\n" +
                "        TO_DATE(']'||V_THREAD||q'[','YYYYMMDD') FCH_REGION,\n" +
                "        MSISDN,\n" +
                "        DEPARTAMENTO_DES,\n" +
                "        ORIGEN_DES,\n" +
                "        SYSDATE,\n" +
                "        USER\n" +
                "        FROM\n" +
                "        USRTEMP.T$_DW_M_MTH_LT_POST_T2_]'||TO_CHAR(V_PROCESSDATE,'YYYYMMDD')||'_'||V_THREAD||q'[ U\n" +
                "        ]';\n" +
                "        COMMIT;\n" +
                "        \n" +
                "        BEGIN\n" +
                "            DM.PKG_MGR_DWO_UTIL.PRC_DROP_TABLE(X_DES_TABLA => 'USRTEMP.T$_DW_M_MTH_LT_POST_T1_'||TO_CHAR(V_PROCESSDATE,'YYYYMMDD')||'_'||V_THREAD);\n" +
                "            DM.PKG_MGR_DWO_UTIL.PRC_DROP_TABLE(X_DES_TABLA => 'USRTEMP.T$_DW_M_MTH_LT_POST_T2_'||TO_CHAR(V_PROCESSDATE,'YYYYMMDD')||'_'||V_THREAD);\n" +
                "        END;  \n" +
                "        \n" +
                "        V_PROCESSDATE := TO_DATE(TO_CHAR(V_PROCESSDATE,'YYYYMM'),'YYYYMM')-1;\n" +
                "\n" +
                "    END LOOP;\n" +
                "  \n" +
                "    -------------------------------------------------------------------------------------------------------------------------------------------\n" +
                "    DWO.PKG_FRAME_IDEAS_REPORT.SP_SET_PROCESS_STEP_LOG(999,'','Finalizando creacion de la tabla temporal USRTEMP.T$_AYF_F_D_DEP_TRAF_POST_'||V_THREAD);\n" +
                "    ----------------------------------------------------------------\n" +
                "    DWO.PKG_FRAME_IDEAS_REPORT.SP_SET_PROCESS_STEP_LOG(999,'','Iniciando creacion de la tabla temporal USRTEMP.T$_DW_T_SALES_POST_DEP_T_'||V_THREAD);\n" +
                "    -------------------------------------------------------------------------------------------------------------------------------------------\n" +
                "    EXECUTE IMMEDIATE 'CREATE TABLE USRTEMP.T$_DW_T_SALES_POST_DEP_T_'||V_THREAD||q'[ AS\n" +
                "    SELECT /*+ PARALLEL (20) */\n" +
                "    PRODUCT_NUMBER MSISDN,\n" +
                "    DECODE(PDV_DEPARTMENT_DESC,'CALLAO','LIMA',PDV_DEPARTMENT_DESC) DEPARTAMENTO,\n" +
                "    ROW_NUMBER() OVER (PARTITION BY U.PRODUCT_NUMBER ORDER BY U.PDV_DEPARTMENT_DESC DESC) R\n" +
                "    FROM DWA.DW_T_SALES PARTITION (P_]'||SUBSTR(V_THREAD,1,6)||q'[) u\n" +
                "    LEFT JOIN USRTEMP.T$_AYF_F_D_DEP_TRAF_POST_]'||V_THREAD||q'[ D ON U.PRODUCT_NUMBER=D.MSISDN\n" +
                "    WHERE U.SALES_REASON_DESC='VENTA NORMAL/ALTA'\n" +
                "    AND U.PDV_DEPARTMENT_DESC IS NOT NULL\n" +
                "    AND U.PRODUCT_NUMBER LIKE '519%'\n" +
                "    AND LENGTH(U.PRODUCT_NUMBER)=11\n" +
                "    AND TRUNC(U.SALE_DATE)<=TO_DATE(]'||V_THREAD||q'[,'YYYYMMDD')\n" +
                "    AND D.MSISDN IS NULL\n" +
                "    ]';   \n" +
                "    ----------------------------------------------------------------\n" +
                "    DWO.PKG_FRAME_IDEAS_REPORT.SP_SET_PROCESS_STEP_LOG(999,'','Finalizando creacion de la tabla temporal USRTEMP.T$_DW_T_SALES_POST_DEP_T_'||V_THREAD);\n" +
                "    -------------------------------------------------------------------------------------------------------------------------------------------\n" +
                "    DWO.PKG_FRAME_IDEAS_REPORT.SP_SET_PROCESS_STEP_LOG(999,'','Iniciando Insercion de informacion en la tabla  USRTEMP.T$_AYF_F_D_DEP_TRAF_POST_'||V_THREAD);\n" +
                "    -------------------------------------------------------------------------------------------------------------------------------------------\n" +
                "    EXECUTE IMMEDIATE 'INSERT /*+ APPEND*/ INTO USRTEMP.T$_AYF_F_D_DEP_TRAF_POST_'||V_THREAD||q'[\n" +
                "    SELECT\n" +
                "    TO_DATE(']'||V_THREAD||q'[','YYYYMMDD') FCH_REGION,\n" +
                "    U.MSISDN,\n" +
                "    U.DEPARTAMENTO,\n" +
                "    'DW_T_SALES',\n" +
                "    SYSDATE,\n" +
                "    USER\n" +
                "    FROM\n" +
                "    USRTEMP.T$_DW_T_SALES_POST_DEP_T_]'||V_THREAD||q'[ U\n" +
                "    WHERE R=1\n" +
                "    ]';\n" +
                "    COMMIT;\n" +
                "    -------------------------------------------------------------------------------------------------------------------------------------------\n" +
                "    DWO.PKG_FRAME_IDEAS_REPORT.SP_SET_PROCESS_STEP_LOG(999,'','Finzalizando insercion de informacion en la tabla  USRTEMP.T$_AYF_F_D_DEP_TRAF_POST_'||V_THREAD);\n" +
                "    -------------------------------------------------------------------------------------------------------------------------------------------\n" +
                "    \n" +
                "    -------------------------------------------------------------------------------------------------------------------------------------------\n" +
                "    DWO.PKG_FRAME_IDEAS_REPORT.SP_SET_PROCESS_STEP_LOG(999,'','Iniciando Insercion de informacion en la tabla DWA.AYF_F_D_DEPART_TRAF_POSTPAGO');\n" +
                "    -------------------------------------------------------------------------------------------------------------------------------------------\n" +
                "\t\n" +
                "    EXECUTE IMMEDIATE 'ALTER TABLE DWA.AYF_F_D_DEPART_TRAF_POSTPAGO TRUNCATE PARTITION P_'||V_THREAD||' ';    \n" +
                "\t\n" +
                "    EXECUTE IMMEDIATE 'INSERT /*+ APPEND*/ INTO DWA.AYF_F_D_DEPART_TRAF_POSTPAGO \n" +
                "\tSELECT \n" +
                "\tFCH_REGION,\n" +
                "\tMSISDN,\n" +
                "\tDEPARTAMENTO_DES,\n" +
                "\tORIGEN_DES,\n" +
                "\tCREATION_DATE,\n" +
                "\tCREATION_USER\n" +
                "\tFROM USRTEMP.T$_AYF_F_D_DEP_TRAF_POST_'||V_THREAD||'';\n" +
                "\tCOMMIT;\n" +
                "\t\n" +
                "     EXECUTE IMMEDIATE 'ALTER INDEX DWA.IDX1_AYF_F_D_DEPART_TRAF_POSTPAGO REBUILD PARTITION P_'||V_THREAD;\n" +
                "    \n" +
                "    -------------------------------------------------------------------------------------------------------------------------------------------\n" +
                "    DWO.PKG_FRAME_IDEAS_REPORT.SP_SET_PROCESS_STEP_LOG(999,'','Finzalizando insercion de informacion en la tabla DWA.AYF_F_D_DEPART_TRAF_POSTPAGO');\n" +
                "    -------------------------------------------------------------------------------------------------------------------------------------------\n" +
                "  \n" +
                "    ----------------------------------------------------------------\n" +
                "    DWO.PKG_FRAME_IDEAS_REPORT.SP_SET_PROCESS_STEP_LOG(999,'P_VAR_SCHEMA_TEMP: USRTEMP','INICIANDO DROPEO DE TEMPORALES DE AMBITO LOCAL');\n" +
                "    ----------------------------------------------------------------\n" +
                "\n" +
                "    BEGIN\n" +
                "        DM.PKG_MGR_DWO_UTIL.PRC_DROP_TABLE(X_DES_TABLA => 'USRTEMP.T$_AYF_F_D_DEP_TRAF_POST_'||V_THREAD);\n" +
                "        DM.PKG_MGR_DWO_UTIL.PRC_DROP_TABLE(X_DES_TABLA => 'USRTEMP.T$_DW_T_SALES_POST_DEP_T_'||V_THREAD);\n" +
                "    END;\n" +
                "     ----------------------------------------------------------------\n" +
                "     DWO.PKG_FRAME_IDEAS_REPORT.SP_SET_PROCESS_STEP_LOG(999,'P_VAR_SCHEMA_TEMP: USRTEMP','FINALIZANDO DROPEO DE TEMPORALES DE AMBITO LOCAL');\n" +
                "    ----------------------------------------------------------------\n" +
                "    ----------------------------------------------------------------\n" +
                "    DWO.PKG_FRAME_IDEAS_REPORT.SP_SET_PROCESS_STEP_LOG(999,'P_VAR_SCHEMA_TEMP: USRTEMP','PROCESO FINALIZADO PARA EL PERIODO '||V_THREAD);\n" +
                "    ----------------------------------------------------------------\n" +
                "   \n" +
                "  END LOOP; \n" +
                "  ----------------------------------------------------------------\n" +
                "  DWO.PKG_FRAME_IDEAS_REPORT.SP_SET_PROCESS_STEP_LOG(999,'P_VAR_SCHEMA_TEMP: USRTEMP','PROCESO FINALIZADO CORRECTAMENTE');\n" +
                "  ----------------------------------------------------------------\n" +
                "  \n" +
                "END;\n" +
                "/";

        assertTrue(sqlparser.parse() == 0);

        nodeVisitor nodeVisitor = new nodeVisitor();
       // System.out.println(sqlparser.sqlstatements.get(0).getClass().getName());
        sqlparser.sqlstatements.get(0).acceptChildren(nodeVisitor);
        //System.out.println(sqlparser.sqltext);
        assertTrue(nodeVisitor.stmtCount == 9);

    }

    public void test2() {

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = " CREATE OR REPLACE FUNCTION fn_test(p1 IN NUMBER, p2 IN DATE, p3 IN VARCHAR2)\n" +
                " RETURN VARCHAR2\n" +
                " IS\n" +
                " \n" +
                " s_query_stmt     VARCHAR2(1000 CHAR);\n" +
                " r1               VARCHAR2(100 CHAR);\n" +
                " r2               VARCHAR2(100 CHAR);\n" +
                " r3               VARCHAR2(100 CHAR);\n" +
                " \n" +
                " BEGIN\n" +
                " \n" +
                " s_query_stmt := 'SELECT\n" +
                "                        c1, c2, c3\n" +
                "                  FROM t\n" +
                "                  WHERE c8 = :x --Note: p1 value should go here!\n" +
                "                  AND c9 = :y --Note: p2 value should go here!\n" +
                "                  AND c1 = ( SELECT cx FROM t2 WHERE cy = :x AND cz = :y )'; \n" +
                " \n" +
                " EXECUTE IMMEDIATE s_query_stmt INTO r1, r2, r3 USING p1, p2, p1, p2;\n" +
                " return 'x';\n" +
                " \n" +
                " END;";

        assertTrue(sqlparser.parse() == 0);

        getExecSQLTextVisitor nodeVisitor = new getExecSQLTextVisitor();
        sqlparser.sqlstatements.get(0).acceptChildren(nodeVisitor);
        assertTrue(nodeVisitor.sqlText.equalsIgnoreCase("SELECT\n" +
                "                        c1, c2, c3\n" +
                "                  FROM t\n" +
                "                  WHERE c8 = :x --Note: p1 value should go here!\n" +
                "                  AND c9 = :y --Note: p2 value should go here!\n" +
                "                  AND c1 = ( SELECT cx FROM t2 WHERE cy = :x AND cz = :y )"));

    }
}

class nodeVisitor extends TParseTreeVisitor {
    public int stmtCount = 0;

    public void preVisit(TExecImmeStmt node) {
        //System.out.println("\n"+(++stmtCount)+" Statement:\t"+node.sqlstatementtype);
        //System.out.println(node.getDynamicSQL());

        TStatementList statements = node.getDynamicStatements();
        if (statements != null){
            stmtCount++;
            //System.out.println(statements.get(0).sqlstatementtype);
        }else{
            //System.out.println("Not parsed\n"+node.getDynamicSQL());
        }
    }
}

class getExecSQLTextVisitor extends TParseTreeVisitor {
    public String sqlText;

    public void preVisit(TExecImmeStmt node) {
        sqlText = node.getDynamicSQL();
    }
}