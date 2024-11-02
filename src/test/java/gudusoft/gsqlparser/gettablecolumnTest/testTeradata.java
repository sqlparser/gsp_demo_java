package gudusoft.gsqlparser.gettablecolumnTest;

import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testTeradata extends TestCase {
    public void test0(){
        String inputQuery,desireResult;

        desireResult  = "Tables:\n" +
                "POS_BASE\n" +
                "PRD2_DDS.PHONE_NUMBER_2\n" +
                "PRD2_DDS.SUBS_TALKING_HISTORY\n" +
                "PRD2_DDS.SUBSCRIPTION\n" +
                "PRD2_DIC_v.branch\n" +
                "PRD2_DIC_V.CALC_PLATFORM\n" +
                "PRD2_DIC_V.SALES_CHANNEL_MAPPING\n" +
                "PRD2_ODW.B2B_TELESELLERS\n" +
                "PRD2_ODW.DS_LOY_CONTACT\n" +
                "PRD2_ODW.DS_T2_ARF_USER_LOG\n" +
                "PRD2_ODW.SUBS_HISTORY\n" +
                "PRD2_ODW.SUBS_INFO\n" +
                "PRD2_ODW.SUBS_OPT_DATA\n" +
                "PRD2_ODW.SUBS_USI_HISTORY\n" +
                "PRD2_ODW.SUBSCRIBER_\n" +
                "PRD2_ODW.USI\n" +
                "PRD2_ODW.USI_ADD\n" +
                "SUBS_CLR_FLASH\n" +
                "SUBS_CUST\n" +
                "\n" +
                "Fields:\n" +
                "POS_BASE.DEALER_ID\n" +
                "POS_BASE.POS_ID\n" +
                "POS_BASE.SALES_CHANNEL_CODE\n" +
                "POS_BASE.SUBS_ID\n" +
                "PRD2_DDS.PHONE_NUMBER_2.ETIME\n" +
                "PRD2_DDS.PHONE_NUMBER_2.MSISDN\n" +
                "PRD2_DDS.PHONE_NUMBER_2.STIME\n" +
                "PRD2_DDS.PHONE_NUMBER_2.SUBS_ID\n" +
                "PRD2_DDS.SUBS_TALKING_HISTORY.EDATE\n" +
                "PRD2_DDS.SUBS_TALKING_HISTORY.SDATE\n" +
                "PRD2_DDS.SUBS_TALKING_HISTORY.SUBS_ID\n" +
                "PRD2_DDS.SUBSCRIPTION.ACTIVATION_DTTM\n" +
                "PRD2_DDS.SUBSCRIPTION.BONUS_PS_ID\n" +
                "PRD2_DDS.SUBSCRIPTION.BRANCH_ID\n" +
                "PRD2_DDS.SUBSCRIPTION.BSEGMENT\n" +
                "PRD2_DDS.SUBSCRIPTION.CALC_PLATFORM_ID\n" +
                "PRD2_DDS.SUBSCRIPTION.CUST_ID\n" +
                "PRD2_DDS.SUBSCRIPTION.DEALER_ID\n" +
                "PRD2_DDS.SUBSCRIPTION.DELIVERY_TYPE_ID\n" +
                "PRD2_DDS.SUBSCRIPTION.DIGITAL_SUITE_COUNT_VISIT\n" +
                "PRD2_DDS.SUBSCRIPTION.FIRST_CALL_DTTM\n" +
                "PRD2_DDS.SUBSCRIPTION.FIRST_TP_ID\n" +
                "PRD2_DDS.SUBSCRIPTION.FLASH_ACTIVE\n" +
                "PRD2_DDS.SUBSCRIPTION.FLASH_CODE_ID\n" +
                "PRD2_DDS.SUBSCRIPTION.ICC\n" +
                "PRD2_DDS.SUBSCRIPTION.IF_NEW_CHURN\n" +
                "PRD2_DDS.SUBSCRIPTION.IMSI\n" +
                "PRD2_DDS.SUBSCRIPTION.IS_BLOCK_QUOTA\n" +
                "PRD2_DDS.SUBSCRIPTION.IS_LTE\n" +
                "PRD2_DDS.SUBSCRIPTION.IS_OUT_SMS\n" +
                "PRD2_DDS.SUBSCRIPTION.LAST_FLASH_DTTM\n" +
                "PRD2_DDS.SUBSCRIPTION.LOAD_ID\n" +
                "PRD2_DDS.SUBSCRIPTION.LOY_ACTIVATION_DTTM\n" +
                "PRD2_DDS.SUBSCRIPTION.MSISDN\n" +
                "PRD2_DDS.SUBSCRIPTION.POS_ID\n" +
                "PRD2_DDS.SUBSCRIPTION.REGISTRATION_DTTM\n" +
                "PRD2_DDS.SUBSCRIPTION.REL_CAT_ID\n" +
                "PRD2_DDS.SUBSCRIPTION.REPORT_DATE\n" +
                "PRD2_DDS.SUBSCRIPTION.SALES_CHANNEL_GRP_ID\n" +
                "PRD2_DDS.SUBSCRIPTION.SRVP_COV_CUST_ID\n" +
                "PRD2_DDS.SUBSCRIPTION.STATUS_CHNG_RSN_ID\n" +
                "PRD2_DDS.SUBSCRIPTION.STATUS_ID\n" +
                "PRD2_DDS.SUBSCRIPTION.SUBS_ID\n" +
                "PRD2_DDS.SUBSCRIPTION.SUBS_SEGM_ID\n" +
                "PRD2_DDS.SUBSCRIPTION.SUBS_TYPE_ID\n" +
                "PRD2_DDS.SUBSCRIPTION.TALKING_FLAG\n" +
                "PRD2_DDS.SUBSCRIPTION.TELESELLERS_ID\n" +
                "PRD2_DDS.SUBSCRIPTION.TP_ID\n" +
                "PRD2_DDS.SUBSCRIPTION.WSC_ACCESS_TYPE\n" +
                "PRD2_DIC_v.branch.BRANCH_ID\n" +
                "PRD2_DIC_v.branch.creation_dttm\n" +
                "PRD2_DIC_v.branch.deletion_dttm\n" +
                "PRD2_DIC_V.CALC_PLATFORM.BSEGMENT\n" +
                "PRD2_DIC_V.CALC_PLATFORM.CALC_PLATFORM_ID\n" +
                "PRD2_DIC_V.CALC_PLATFORM.MACRO_BSEGMENT\n" +
                "PRD2_DIC_V.SALES_CHANNEL_MAPPING.MACRO_BSEGMENT\n" +
                "PRD2_DIC_V.SALES_CHANNEL_MAPPING.SALES_CHANNEL_CODE\n" +
                "PRD2_DIC_V.SALES_CHANNEL_MAPPING.SALES_CHANNEL_GRP_ID\n" +
                "PRD2_ODW.B2B_TELESELLERS.POS_ID\n" +
                "PRD2_ODW.DS_LOY_CONTACT.*\n" +
                "PRD2_ODW.DS_LOY_CONTACT.CREATED\n" +
                "PRD2_ODW.DS_LOY_CONTACT.SUBS_ID\n" +
                "PRD2_ODW.DS_T2_ARF_USER_LOG.DATE_TIME\n" +
                "PRD2_ODW.DS_T2_ARF_USER_LOG.DESCRIPTION\n" +
                "PRD2_ODW.DS_T2_ARF_USER_LOG.MASTER_LOGIN\n" +
                "PRD2_ODW.SUBS_HISTORY.ETIME\n" +
                "PRD2_ODW.SUBS_HISTORY.phone_id\n" +
                "PRD2_ODW.SUBS_HISTORY.stat_id\n" +
                "PRD2_ODW.SUBS_HISTORY.STIME\n" +
                "PRD2_ODW.SUBS_HISTORY.subs_id\n" +
                "PRD2_ODW.SUBS_INFO.*\n" +
                "PRD2_ODW.SUBS_INFO.ETIME\n" +
                "PRD2_ODW.SUBS_INFO.num_history\n" +
                "PRD2_ODW.SUBS_INFO.PAY_DT_ID\n" +
                "PRD2_ODW.SUBS_INFO.PC_PAY_ID\n" +
                "PRD2_ODW.SUBS_INFO.SPK_ID\n" +
                "PRD2_ODW.SUBS_INFO.STIME\n" +
                "PRD2_ODW.SUBS_INFO.SUBS_ID\n" +
                "PRD2_ODW.SUBS_OPT_DATA.ETIME\n" +
                "PRD2_ODW.SUBS_OPT_DATA.FIELD_VALUE\n" +
                "PRD2_ODW.SUBS_OPT_DATA.STIME\n" +
                "PRD2_ODW.SUBS_OPT_DATA.SUBS_FLD_ID\n" +
                "PRD2_ODW.SUBS_OPT_DATA.SUBS_ID\n" +
                "PRD2_ODW.SUBS_USI_HISTORY.etime\n" +
                "PRD2_ODW.SUBS_USI_HISTORY.stime\n" +
                "PRD2_ODW.SUBS_USI_HISTORY.SUBS_ID\n" +
                "PRD2_ODW.SUBS_USI_HISTORY.USI_ID\n" +
                "PRD2_ODW.SUBSCRIBER_.*\n" +
                "PRD2_ODW.SUBSCRIBER_.ACTIVATION_DATE\n" +
                "PRD2_ODW.SUBSCRIBER_.FIRST_CALL\n" +
                "PRD2_ODW.SUBSCRIBER_.REGISTRATION_DATE\n" +
                "PRD2_ODW.SUBSCRIBER_.SUBS_ID\n" +
                "PRD2_ODW.USI.*\n" +
                "PRD2_ODW.USI.ICC\n" +
                "PRD2_ODW.USI.USI\n" +
                "PRD2_ODW.USI.USI_ID\n" +
                "PRD2_ODW.USI_ADD.USI_FLD_ID\n" +
                "PRD2_ODW.USI_ADD.USI_ID\n" +
                "PRD2_ODW.USI_ADD.VALUE_\n" +
                "SUBS_CLR_FLASH.FLASH_ACTIVE\n" +
                "SUBS_CLR_FLASH.FLASH_CODE_ID\n" +
                "SUBS_CLR_FLASH.IF_NEW_CHURN\n" +
                "SUBS_CLR_FLASH.LAST_FLASH_DTTM\n" +
                "SUBS_CLR_FLASH.SUBS_ID\n" +
                "SUBS_CUST.BLOCK_QUOTA\n" +
                "SUBS_CUST.BRANCH_ID\n" +
                "SUBS_CUST.CALC_PLATFORM_ID\n" +
                "SUBS_CUST.CT_ID\n" +
                "SUBS_CUST.CUST_ID\n" +
                "SUBS_CUST.DEALER_ID\n" +
                "SUBS_CUST.FIRST_TP_ID\n" +
                "SUBS_CUST.REL_CAT_ID\n" +
                "SUBS_CUST.SRVP_COV_CUST_ID\n" +
                "SUBS_CUST.STATUS_CHNG_RSN_ID\n" +
                "SUBS_CUST.STATUS_ID\n" +
                "SUBS_CUST.SUBS_ID\n" +
                "SUBS_CUST.SUBS_TYPE_ID\n" +
                "SUBS_CUST.TP_ID\n";

        inputQuery = "USING _spVV1 (INTEGER), _spVV2 (TIMESTAMP(0)), _spVV0 (DATE)\n" +
                "INSERT INTO PRD2_DDS.SUBSCRIPTION (\n" +
                "    SUBS_ID, ACTIVATION_DTTM, REGISTRATION_DTTM, FIRST_CALL_DTTM, FIRST_TP_ID,\n" +
                "    CUST_ID, DEALER_ID, ICC, IMSI, SRVP_COV_CUST_ID, POS_ID, TP_ID,\n" +
                "    IS_BLOCK_QUOTA, REL_CAT_ID, IS_LTE, STATUS_CHNG_RSN_ID, STATUS_ID,\n" +
                "    SUBS_TYPE_ID, BONUS_PS_ID, DELIVERY_TYPE_ID, SUBS_SEGM_ID, IS_OUT_SMS,\n" +
                "    WSC_ACCESS_TYPE, BSEGMENT, SALES_CHANNEL_GRP_ID, BRANCH_ID, CALC_PLATFORM_ID,\n" +
                "    LOAD_ID, REPORT_DATE, FLASH_ACTIVE, LAST_FLASH_DTTM, FLASH_CODE_ID,\n" +
                "    IF_NEW_CHURN, MSISDN, DIGITAL_SUITE_COUNT_VISIT, LOY_ACTIVATION_DTTM,\n" +
                "    TALKING_FLAG, TELESELLERS_ID\n" +
                ")\n" +
                "SELECT\n" +
                "    SUBS_ID, ACTIVATION_DTTM, REGISTRATION_DTTM, FIRST_CALL_DTTM, FIRST_TP_ID,\n" +
                "    CUST_ID, DEALER_ID1, ICC, IMSI, SRVP_COV_CUST_ID, POS_ID, TP_ID,\n" +
                "    IS_BLOCK_QUOTA, REL_CAT_ID, IS_LTE, STATUS_CHNG_RSN_ID, STATUS_ID,\n" +
                "    SUBS_TYPE_ID, BONUS_PS_ID, DELIVERY_TYPE_ID, SUBS_SEGM_ID, IS_OUT_SMS,\n" +
                "    WSC_ACCESS_TYPE, BSEGMENT, SALES_CHANNEL_GRP_ID, BRANCH_ID, CALC_PLATFORM_ID,\n" +
                "    LOAD_ID, REPORT_DATE, FLASH_ACTIVE, LAST_FLASH_DTTM, FLASH_CODE_ID,\n" +
                "    IF_NEW_CHURN, MSISDN, DIGITAL_SUITE_COUNT_VISIT, LOY_ACTIVATION_DTTM,\n" +
                "    TALKING_FLAG, TELESELLERS_ID\n" +
                "FROM (\n" +
                "    SELECT\n" +
                "        SUBSCRIBER_.SUBS_ID,\n" +
                "        SUBSCRIBER_.ACTIVATION_DATE (NAMED ACTIVATION_DTTM),\n" +
                "        SUBSCRIBER_.REGISTRATION_DATE (NAMED REGISTRATION_DTTM),\n" +
                "        SUBSCRIBER_.FIRST_CALL (NAMED FIRST_CALL_DTTM),\n" +
                "        SUBSCRIBER_.FIRST_TP_ID,\n" +
                "        SUBSCRIBER_.CUST_ID,\n" +
                "        (CASE\n" +
                "            WHEN (CP.BSEGMENT NOT IN ('B2 Postpaid', 'M2M')) THEN (SUBSCRIBER_.DEALER_ID)\n" +
                "            ELSE (POS_BASE.DEALER_ID)\n" +
                "        END)(NAMED DEALER_ID1),\n" +
                "        USI_BASE.ICC,\n" +
                "        USI_BASE.USI (NAMED IMSI),\n" +
                "        SUBSCRIBER_.SRVP_COV_CUST_ID,\n" +
                "        POS_BASE.POS_ID (NAMED POS_ID),\n" +
                "        SUBSCRIBER_.TP_ID,\n" +
                "        SUBSCRIBER_.BLOCK_QUOTA (NAMED IS_BLOCK_QUOTA),\n" +
                "        (CASE\n" +
                "            WHEN (SUBSCRIBER_.REL_CAT_ID < 0) THEN (NULL)\n" +
                "            ELSE (SUBSCRIBER_.REL_CAT_ID)\n" +
                "        END)(NAMED REL_CAT_ID),\n" +
                "        (CASE (USI_BASE.VALUE_)\n" +
                "            WHEN ('LTE') THEN (1)\n" +
                "            ELSE (0)\n" +
                "        END)(NAMED IS_LTE),\n" +
                "        SUBSCRIBER_.STATUS_CHNG_RSN_ID,\n" +
                "        CAST((SUBSCRIBER_.STATUS_ID) AS DECIMAL(1, 0))(NAMED STATUS_ID),\n" +
                "        CAST((SUBSCRIBER_.SUBS_TYPE_ID) AS DECIMAL(1, 0))(NAMED SUBS_TYPE_ID),\n" +
                "        SUBS_INFO.PC_PAY_ID (NAMED BONUS_PS_ID),\n" +
                "        CAST((SUBS_INFO.PAY_DT_ID) AS DECIMAL(4, 0))(NAMED DELIVERY_TYPE_ID),\n" +
                "        CAST((SUBS_INFO.SPK_ID) AS DECIMAL(2, 0))(NAMED SUBS_SEGM_ID),\n" +
                "        (CASE\n" +
                "            WHEN (CAST((SUBS_OPT_DATA_GROUPED.FIELD_VALUE) AS DECIMAL(1, 0)) IS NULL) THEN (NULL)\n" +
                "            WHEN ((CAST((SUBS_OPT_DATA_GROUPED.FIELD_VALUE) AS DECIMAL(1, 0))) = 0) THEN (0)\n" +
                "            ELSE (1)\n" +
                "        END)(NAMED IS_OUT_SMS),\n" +
                "        SUBS_OPT_DATA_GROUPED.WSC_ACCESS_TYPE,\n" +
                "        CP.BSEGMENT,\n" +
                "        SCM.SALES_CHANNEL_GRP_ID,\n" +
                "        SUBSCRIBER_.BRANCH_ID (NAMED BRANCH_ID),\n" +
                "        CAST((SUBSCRIBER_.CALC_PLATFORM_ID) AS DECIMAL(2, 0))(NAMED CALC_PLATFORM_ID),\n" +
                "        :_spVV1 (NAMED LOAD_ID),\n" +
                "        :_spVV0 (NAMED REPORT_DATE),\n" +
                "        (CASE\n" +
                "            WHEN (((SUBSCRIBER_.BRANCH_ID = 61) AND (SUBSCRIBER_.CALC_PLATFORM_ID IN (6, 7, 8, 9))) AND (:_spVV0 >= DATE '2018-04-30')) THEN (0)\n" +
                "            WHEN (((SUBSCRIBER_.BRANCH_ID IN (60, 62)) AND (SUBSCRIBER_.CALC_PLATFORM_ID IN (6, 7, 8, 9))) AND (:_spVV0 >= '2017-12-31')) THEN (0)\n" +
                "            WHEN ((((SUBSCRIBER_.CALC_PLATFORM_ID <> 0) AND (NOT (SUBSCRIBER_.ACTIVATION_DATE IS NULL))) AND (SUBSCRIBER_.FLASH_CODE_ID IN (1, 2, 4))) AND (NOT (SUBSCRIBER_.BRANCH_ID_MAYBENULL IS NULL))) THEN (SUBSCRIBER_.FLASH_ACTIVE)\n" +
                "            ELSE (0)\n" +
                "        END)(NAMED FLASH_ACTIVE),\n" +
                "        CAST((SUBSCRIBER_.LAST_FLASH_DTTM) AS DATE)(NAMED LAST_FLASH_DTTM),\n" +
                "        (CASE\n" +
                "            WHEN (NOT (SUBSCRIBER_.FLASH_CODE_ID IS NULL)) THEN (SUBSCRIBER_.FLASH_CODE_ID)\n" +
                "            ELSE (0)\n" +
                "        END)(NAMED FLASH_CODE_ID),\n" +
                "        (CASE\n" +
                "            WHEN (NOT (SUBSCRIBER_.IF_NEW_CHURN IS NULL)) THEN (SUBSCRIBER_.IF_NEW_CHURN)\n" +
                "            ELSE (0)\n" +
                "        END)(NAMED IF_NEW_CHURN),\n" +
                "        PHN.MSISDN,\n" +
                "        TD_SYSFNLIB.NVL(USER_LOG.CNT_LOGIN, 0)(NAMED DIGITAL_SUITE_COUNT_VISIT),\n" +
                "        PRD2_ODW.TS6_TO_TS0(LOY_CONTACT.CREATED)(NAMED LOY_ACTIVATION_DTTM),\n" +
                "        (CASE\n" +
                "            WHEN (STH.SUBS_ID IS NULL) THEN (0)\n" +
                "            ELSE (1)\n" +
                "        END)(NAMED TALKING_FLAG),\n" +
                "        TSL.POS_ID (NAMED TELESELLERS_ID)\n" +
                "    FROM (\n" +
                "        SELECT\n" +
                "            SUBSCR.*,\n" +
                "            SUBS_CUST.FIRST_TP_ID,\n" +
                "            SUBS_CUST.CUST_ID,\n" +
                "            SUBS_CUST.SRVP_COV_CUST_ID,\n" +
                "            SUBS_CUST.TP_ID,\n" +
                "            SUBS_CUST.BLOCK_QUOTA,\n" +
                "            SUBS_CUST.REL_CAT_ID,\n" +
                "            SUBS_CUST.STATUS_CHNG_RSN_ID,\n" +
                "            SUBS_CUST.STATUS_ID,\n" +
                "            SUBS_CUST.SUBS_TYPE_ID,\n" +
                "            SUBS_CUST.BRANCH_ID,\n" +
                "            SUBS_CUST.CT_ID,\n" +
                "            SUBS_CUST.DEALER_ID,\n" +
                "            SUBS_CLR_FLASH.FLASH_ACTIVE,\n" +
                "            SUBS_CLR_FLASH.LAST_FLASH_DTTM,\n" +
                "            SUBS_CLR_FLASH.FLASH_CODE_ID,\n" +
                "            SUBS_CLR_FLASH.IF_NEW_CHURN,\n" +
                "            (CASE\n" +
                "                WHEN (NOT (SUBS_CUST.CALC_PLATFORM_ID IS NULL)) THEN (SUBS_CUST.CALC_PLATFORM_ID)\n" +
                "                ELSE (-1)\n" +
                "            END)(NAMED CALC_PLATFORM_ID),\n" +
                "            br.BRANCH_ID (NAMED BRANCH_ID_MAYBENULL)\n" +
                "        FROM PRD2_ODW.SUBSCRIBER_ SUBSCR\n" +
                "        INNER JOIN SUBS_CUST SUBS_CUST ON SUBSCR.SUBS_ID = SUBS_CUST.SUBS_ID\n" +
                "        LEFT OUTER JOIN SUBS_CLR_FLASH SUBS_CLR_FLASH ON SUBSCR.SUBS_ID = SUBS_CLR_FLASH.SUBS_ID\n" +
                "        LEFT OUTER JOIN PRD2_DIC_v.branch br ON\n" +
                "            ((SUBS_CUST.BRANCH_ID = br.BRANCH_ID) AND (:_spVV0 >= ((CASE\n" +
                "                WHEN (NOT (TD_SYSFNLIB.TRUNC(br.creation_dttm) IS NULL)) THEN (TD_SYSFNLIB.TRUNC(br.creation_dttm))\n" +
                "                ELSE (DATE '1960-01-01')\n" +
                "            END))))\n" +
                "            AND (:_spVV0 < ((CASE\n" +
                "                WHEN (NOT (TD_SYSFNLIB.TRUNC(br.deletion_dttm, 'mm') IS NULL)) THEN (TD_SYSFNLIB.TRUNC(br.deletion_dttm, 'mm'))\n" +
                "                ELSE (DATE '2999-12-31')\n" +
                "            END)))\n" +
                "        WHERE SUBSCR.subs_id NOT IN (\n" +
                "            SELECT subs_id\n" +
                "            FROM PRD2_ODW.SUBS_HISTORY\n" +
                "            WHERE (((STIME < (:_spVV0 - 180)) AND (ETIME >= TIMESTAMP '2999-12-31 00:00:00')) AND (stat_id = 3))\n" +
                "                AND (phone_id IS NULL)\n" +
                "        )\n" +
                "    ) SUBSCRIBER_\n" +
                "    LEFT OUTER JOIN (\n" +
                "        SELECT\n" +
                "            SUBS_ID,\n" +
                "            PC_PAY_ID,\n" +
                "            PAY_DT_ID,\n" +
                "            SPK_ID,\n" +
                "            (CAST((:_spVV0) AS TIMESTAMP(0))) + (((TIME '23:59:59') - (TIME '00:00:00')) HOUR(2) TO SECOND(0))(NAMED REPORT_DTTM)\n" +
                "        FROM PRD2_ODW.SUBS_INFO\n" +
                "        WHERE (STIME <= REPORT_DTTM) AND (ETIME > REPORT_DTTM)\n" +
                "        QUALIFY 1 = (COUNT(*) OVER (PARTITION BY subs_id ORDER BY num_history DESC ROWS UNBOUNDED PRECEDING))\n" +
                "    ) SUBS_INFO ON SUBSCRIBER_.SUBS_ID = SUBS_INFO.SUBS_ID\n" +
                "    LEFT OUTER JOIN (\n" +
                "        SELECT\n" +
                "            SUBS_ID,\n" +
                "            MAX((CASE\n" +
                "                WHEN (SUBS_OPT_DATA.SUBS_FLD_ID = 15) THEN (SUBS_OPT_DATA.FIELD_VALUE)\n" +
                "                ELSE (NULL)\n" +
                "            END))(NAMED FIELD_VALUE),\n" +
                "            MAX((CASE\n" +
                "                WHEN (SUBS_OPT_DATA.SUBS_FLD_ID = 134) THEN (SUBS_OPT_DATA.FIELD_VALUE)\n" +
                "                ELSE (NULL)\n" +
                "            END))(NAMED WSC_ACCESS_TYPE),\n" +
                "            MAX((CASE\n" +
                "                WHEN (SUBS_OPT_DATA.SUBS_FLD_ID = 17) THEN (CAST((TD_SYSFNLIB.TO_NUMBER(SUBS_OPT_DATA.FIELD_VALUE)) AS DECIMAL(10, 0)))\n" +
                "                ELSE (NULL)\n" +
                "            END))(NAMED TELESELLERS_ID),\n" +
                "            (CAST((:_spVV0) AS TIMESTAMP(0))) + (((TIME '23:59:59') - (TIME '00:00:00')) HOUR(2) TO SECOND(0))(NAMED REPORT_DTTM)\n" +
                "        FROM PRD2_ODW.SUBS_OPT_DATA SUBS_OPT_DATA\n" +
                "        WHERE (SUBS_OPT_DATA.STIME <= REPORT_DTTM) AND (SUBS_OPT_DATA.ETIME > REPORT_DTTM)\n" +
                "        GROUP BY SUBS_ID\n" +
                "    ) SUBS_OPT_DATA_GROUPED ON SUBSCRIBER_.SUBS_ID = SUBS_OPT_DATA_GROUPED.SUBS_ID\n" +
                "    LEFT OUTER JOIN (\n" +
                "        SELECT\n" +
                "            USI_HISTORY.SUBS_ID,\n" +
                "            USI.USI,\n" +
                "            USI.ICC,\n" +
                "            USI_ADD.VALUE_\n" +
                "        FROM (\n" +
                "            SELECT\n" +
                "                USI_ID,\n" +
                "                SUBS_ID,\n" +
                "                (CAST((:_spVV0) AS TIMESTAMP(0))) + (((TIME '23:59:59') - (TIME '00:00:00')) HOUR(2) TO SECOND(0))(NAMED REPORT_DTTM)\n" +
                "            FROM PRD2_ODW.SUBS_USI_HISTORY\n" +
                "            WHERE (stime <= REPORT_DTTM) AND (etime > REPORT_DTTM)\n" +
                "        ) USI_HISTORY\n" +
                "        LEFT OUTER JOIN (\n" +
                "            SELECT USI_ID, VALUE_\n" +
                "            FROM PRD2_ODW.USI_ADD\n" +
                "            WHERE USI_FLD_ID = 11\n" +
                "        ) USI_ADD ON USI_ADD.USI_ID = USI_HISTORY.USI_ID\n" +
                "        LEFT OUTER JOIN PRD2_ODW.USI ON USI.USI_ID = USI_HISTORY.USI_ID\n" +
                "        QUALIFY 1 = (COUNT(*) OVER (PARTITION BY SUBS_ID ORDER BY USI_ADD.VALUE_ DESC ROWS UNBOUNDED PRECEDING))\n" +
                "    ) USI_BASE ON USI_BASE.SUBS_ID = SUBSCRIBER_.SUBS_ID\n" +
                "    LEFT OUTER JOIN POS_BASE ON POS_BASE.SUBS_ID = SUBSCRIBER_.SUBS_ID\n" +
                "    LEFT OUTER JOIN PRD2_DIC_V.CALC_PLATFORM CP ON CP.CALC_PLATFORM_ID = SUBSCRIBER_.CALC_PLATFORM_ID\n" +
                "    LEFT OUTER JOIN PRD2_DIC_V.SALES_CHANNEL_MAPPING SCM ON\n" +
                "        (SCM.MACRO_BSEGMENT = CP.MACRO_BSEGMENT) AND (SCM.SALES_CHANNEL_CODE = POS_BASE.SALES_CHANNEL_CODE)\n" +
                "    LEFT OUTER JOIN PRD2_DDS.PHONE_NUMBER_2 PHN ON\n" +
                "        ((PHN.SUBS_ID = SUBSCRIBER_.SUBS_ID) AND (PHN.STIME <= :_spVV2)) AND (PHN.ETIME > :_spVV2)\n" +
                "    LEFT OUTER JOIN (\n" +
                "     SELECT\n" +
                "            MASTER_LOGIN (NAMED MSISDN),\n" +
                "            COUNT(1)(NAMED CNT_LOGIN)\n" +
                "        FROM PRD2_ODW.DS_T2_ARF_USER_LOG UL\n" +
                "        WHERE ((DATE_TIME >= (CAST((:_spVV0) AS TIMESTAMP(0)))) AND (DATE_TIME < (CAST((:_spVV0 + 1) AS TIMESTAMP(0)))))\n" +
                "            AND (DESCRIPTION = '')\n" +
                "        GROUP BY MASTER_LOGIN\n" +
                "    ) USER_LOG ON PHN.MSISDN = USER_LOG.MSISDN\n" +
                "    LEFT OUTER JOIN (\n" +
                "        SELECT CREATED, SUBS_ID\n" +
                "        FROM PRD2_ODW.DS_LOY_CONTACT\n" +
                "        QUALIFY (COUNT(*) OVER (PARTITION BY SUBS_ID ORDER BY CREATED ASC ROWS UNBOUNDED PRECEDING)) = 1\n" +
                "    ) LOY_CONTACT ON\n" +
                "        ((((CAST((LOY_CONTACT.SUBS_ID) AS DECIMAL(12, 0))) = SUBSCRIBER_.SUBS_ID) AND (LOY_CONTACT.SUBS_ID <> 'null'))\n" +
                "        AND (NOT (LOY_CONTACT.SUBS_ID LIKE '%@%')))\n" +
                "        AND (LOY_CONTACT.CREATED < (CAST((:_spVV0 + 1) AS TIMESTAMP(6))))\n" +
                "    LEFT OUTER JOIN PRD2_DDS.SUBS_TALKING_HISTORY STH ON\n" +
                "        (STH.SUBS_ID = SUBSCRIBER_.SUBS_ID) AND (:_spVV0 BETWEEN STH.SDATE AND STH.EDATE)\n" +
                "    LEFT OUTER JOIN PRD2_ODW.B2B_TELESELLERS TSL ON SUBS_OPT_DATA_GROUPED.TELESELLERS_ID = TSL.POS_ID\n" +
                ") MS;";

        assertTrue(GetTableColumnBase.doTest(EDbVendor.dbvteradata,inputQuery,desireResult));
    }
}
