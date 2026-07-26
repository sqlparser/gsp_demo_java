package com.gudusoft.gsqlparser.demo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;

import java.io.IOException;

public class App {

    public static void processFailedQueriesJson(String jsonString) {
        System.out.println("Processing failed queries JSON...");
        int successCount = 0;
        int errorCount = 0;

        try {
            JSONObject jsonObject = JSON.parseObject(jsonString);
            JSONArray failedQueries = jsonObject.getJSONArray("failedSqlQueries");

            if (failedQueries == null) {
                System.out.println("No 'failedSqlQueries' array found in JSON.");
                return;
            }

            TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdatabricks);

            for (int i = 0; i < failedQueries.size(); i++) {
                JSONObject query_obj = failedQueries.getJSONObject(i);
                String sql_query = query_obj.getString("msg");

                System.out.println("\n--- Parsing query " + (i + 1) + " ---");

                sqlparser.sqltext = sql_query;
                int ret = sqlparser.parse();

                if (ret != 0) {
                    errorCount++;
                    System.out.println("Query:\n" + sql_query);
                    System.out.println("Parse error: " + sqlparser.getErrormessage());
                } else {
                    successCount++;
                    System.out.println("Parse success.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error processing JSON string: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        System.out.println("\n--- Summary ---");
        System.out.println("Successful parses: " + successCount);
        System.out.println("Failed parses: " + errorCount);
    }

    public static void main(String args[]) throws IOException {
        String jsonInput = "{\n" +
                "\t\"failedSqlQueries\": [\n" +
                "\t\t{\n" +
                "\t\t\t\"queryType\": \"Native Query\",\n" +
                "\t\t\t\"dialect\": \"end of input, state:2822(10102) near: all(22,10, token code:0)\\r\\nno_root_node(-1000) near: no root node(0,0, token code:0)\",\n" +
                "\t\t\t\"msg\": \"select\\nDCNT.store_num,\\nDCNT.txn_key,\\nDCNT.business_date,\\nDCNT.discount_reasons,\\nDCNT.target_line_num,\\nMND.item_key,\\nMND.item_desc,\\nsum(DCNT.discount_qty) DiscountOccurences,\\nsum(DCNT.fuel_price_rollback_amt_discount_total) FuelDiscountDollars,\\nsum(DCNT.fuel_price_rollback_amt) FuelDiscountDollarsPer,\\nsum(case when upper(DCNT.discount_reasons) in ('MM - NAB - MONSTER 15.5 16OZ', 'MM - NAB - MONSTER 15.5 1','MM - NAB - B2G1 MONSTER REIGN','MM - NAB - B2G1 MONSTER R', 'MM - B2G1 MONSTER 16/15.5OZ', 'MM - B2G1 MONSTER 16/15.5') then DCNT.discount_amt end) InsideDiscountDollars,\\nsum(sale_qty) as units\\n\\nFrom entdata_gold.cdm_pos.sale_transaction_discount DCNT\\nJoin (select * from entdata_gold.cdm_pos.sale_transaction_detail where business_date >= '2025-08-06' and txn_type = 'SALE' and store_banner = 'RaceTrac' and status = 'normal') MND\\non DCNT.txn_key = MND.txn_key\\nand DCNT.target_line_num = MND.detail_line_num\\nwhere (upper(DCNT.discount_reasons) = 'LOY-20COFFFUEL' or \\nupper(DCNT.discount_reasons) in ('MM - NAB - MONSTER 15.5 16OZ', 'MM - NAB - MONSTER 15.5 1','MM - NAB - B2G1 MONSTER REIGN','MM - NAB - B2G1 MONSTER R', 'MM - B2G1 MONSTER 16/15.5OZ', 'MM - B2G1 MONSTER 16/15.5'))\\nand DCNT.business_date between '2025-08-06' and '2025-11-04'\\ngroup by all\",\n" +
                "\t\t\t\"sql\": \"Databricks\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"queryType\": \"Native Query\",\n" +
                "\t\t\t\"dialect\": \"syntax error, state:1517(10102) near: OUTER(53,6, token code:0)\\r\\nno_root_node(-1000) near: no root node(0,0, token code:0)\",\n" +
                "\t\t\t\"msg\": \"WITH Q1 AS\\n(SELECT\\n  ODSFISRV.LOAN.LOAN_NUMBER,\\n  ODSFISRV.FISERV_FINANCIAL_TRANS.HST_TRANS_BUS_DATE,\\n  ODSFISRV.FISERV_FINANCIAL_TRANS.HST_TRANS_CODE,\\n  ODSFISRV.FISERV_FINANCIAL_TRANS.HST_TR_AMT,\\n  ODSFISRV.FISERV_FINANCIAL_TRANS.HST_TELLER,\\n  ODSFISRV.TELLER_INFO.T_TLR_NAME\\nFROM\\n  ODSFISRV.LOAN,\\n  ODSFISRV.FISERV_FINANCIAL_TRANS,\\n  ODSFISRV.TELLER_INFO\\nWHERE\\n       ( ODSFISRV.LOAN.LOAN_NUMBER=ODSFISRV.FISERV_FINANCIAL_TRANS.LOAN_NUMBER(+)  )\\n  AND  ( ODSFISRV.FISERV_FINANCIAL_TRANS.HST_TELLER=ODSFISRV.TELLER_INFO.T_TELLER(+)  )\\n  AND  (NVL(ODSFISRV.LOAN.DELETED_FLAG,'N')  != 'Y')\\n  AND  (ODSFISRV.LOAN.ML_INST!='999')\\n  AND  (\\n  date_trunc('DAY',ODSFISRV.FISERV_FINANCIAL_TRANS.HST_TRANS_BUS_DATE)   BETWEEN dateadd('month', -1,  date_trunc('month', current_date())) AND  date_trunc('DAY', current_date())\\n                                                           \\n\\n  AND  ODSFISRV.FISERV_FINANCIAL_TRANS.HST_TRANS_CODE  =  'PR0'\\n  AND  ODSFISRV.TELLER_INFO.T_TELLER  =  ODSFISRV.FISERV_FINANCIAL_TRANS.HST_TELLER\\n  )\\n),\\nQ2 AS\\n(SELECT    DISTINCT\\n  ODSFISRV.LOAN.LOAN_NUMBER,\\n  ODSFISRV.FISERV_NOTES_TRANS.HST_TRANS_BUS_DATE,\\n  ODSFISRV.FISERV_NOTES_TRANS.HST_NOTES_AREA,\\n  ODSFISRV.FISERV_NOTES_TRANS.HST_NOTE\\nFROM\\n  ODSFISRV.LOAN,\\n  ODSFISRV.FISERV_NOTES_TRANS,\\n  ODSFISRV.FISERV_FINANCIAL_TRANS\\nWHERE\\n  ( ODSFISRV.LOAN.LOAN_NUMBER=ODSFISRV.FISERV_NOTES_TRANS.LOAN_NUMBER(+)  )\\n  AND  ( ODSFISRV.LOAN.LOAN_NUMBER=ODSFISRV.FISERV_FINANCIAL_TRANS.LOAN_NUMBER(+)  )\\n  AND  (NVL(ODSFISRV.LOAN.DELETED_FLAG,'N')  != 'Y')\\n  AND  (ODSFISRV.LOAN.ML_INST!='999')\\n  AND  (\\n  date_trunc('DAY',ODSFISRV.FISERV_FINANCIAL_TRANS.HST_TRANS_BUS_DATE)   BETWEEN dateadd('month', -1,  date_trunc('month', current_date())) AND  date_trunc('DAY', current_date())\\n                                                               \\n\\n  AND  ODSFISRV.FISERV_FINANCIAL_TRANS.HST_TRANS_CODE  =  'PR0'\\n  AND  ODSFISRV.FISERV_NOTES_TRANS.HST_NOTES_AREA  IN  ('MSAP1', 'MSAP2', 'MSAP3', 'MSAP4', 'MSAP5')\\n  )\\n)\\nSELECT Q1.*,Q2.HST_TRANS_BUS_DATE Q2_HST_TRANS_BUS_DATE,Q2.HST_NOTES_AREA,Q2.HST_NOTE\\n\\n\\nFROM Q1\\nLEFT OUTER JOIN Q2 ON Q2.LOAN_NUMBER = Q1.LOAN_NUMBER\",\n" +
                "\t\t\t\"sql\": \"Snowflake\"\n" +
                "\t\t},\n" +
                "\t\t{\n" +
                "\t\t\t\"queryType\": \"Native Query\",\n" +
                "\t\t\t\"dialect\": \"syntax error, state:1517(10102) near: OUTER(59,6, token code:0)\\r\\nno_root_node(-1000) near: no root node(0,0, token code:0)\",\n" +
                "\t\t\t\"msg\": \"WITH Q1 AS (SELECT\\n  ODSFISRV.LOAN.LOAN_NUMBER,\\n  ODSFISRV.BORR_LOAN.T9_PRIOR_ACNT_NBR,\\n  ODSFISRV.ORIG_LOAN.ML_AUDIT_DATE,\\n  ODSFISRV.LOAN_CALCS.CALC_TOT_NEXT_PAYMENT,\\n  ODSFISRV.FISERV_FINANCIAL_TRANS.HST_TRANS_BUS_DATE,\\n  ODSFISRV.FISERV_FINANCIAL_TRANS.HST_TRANS_CODE,\\n  ODSFISRV.FISERV_FINANCIAL_TRANS.HST_TR_AMT,\\n  ODSFISRV.FISERV_FINANCIAL_TRANS.HST_TELLER,\\n  (CASE WHEN ODSFISRV.FISERV_FINANCIAL_TRANS.HST_TELLER IN (93 , 94 , 95) THEN 'Exclude' ELSE 'Include' END) EXCLUDE_HST_TELLER,\\n  (CASE WHEN ODSFISRV.LOAN_CALCS.CALC_TOT_NEXT_PAYMENT = ODSFISRV.FISERV_FINANCIAL_TRANS.HST_TR_AMT THEN 'Y' ELSE 'N' END) PMT_MATCH,\\n  CASE  DAYNAME(current_date()) \\n                        WHEN 'Sun' THEN current_date()-2 \\n                        WHEN 'Mon' THEN current_date()-3 ELSE current_date()-1 \\n\\n \\n END AS PRIOR_BUSINESS_DAY\\nFROM\\n  ODSFISRV.LOAN,\\n  ODSFISRV.BORR_LOAN,\\n  ODSFISRV.ORIG_LOAN,\\n  ODSFISRV.LOAN_CALCS,\\n  ODSFISRV.FISERV_FINANCIAL_TRANS\\nWHERE\\n  ( ODSFISRV.LOAN_CALCS.LOAN_NUMBER(+)=ODSFISRV.LOAN.LOAN_NUMBER  )\\n  AND  ( ODSFISRV.LOAN.LOAN_NUMBER=ODSFISRV.ORIG_LOAN.LOAN_NUMBER(+)  )\\n  AND  ( ODSFISRV.LOAN.LOAN_NUMBER=ODSFISRV.BORR_LOAN.LOAN_NUMBER(+)  )\\n  AND  ( ODSFISRV.LOAN.LOAN_NUMBER=ODSFISRV.FISERV_FINANCIAL_TRANS.LOAN_NUMBER(+)  )\\n  AND  (NVL(ODSFISRV.LOAN.DELETED_FLAG,'N')  != 'Y')\\n  AND  (ODSFISRV.LOAN.ML_INST!='999')\\n  AND  (\\n  ODSFISRV.LOAN.ML_PRIN_BAL  >  0\\n  AND  ODSFISRV.FISERV_FINANCIAL_TRANS.HST_TRANS_CODE  IN  ('AP', 'PA', 'RP')\\n  AND  ODSFISRV.FISERV_FINANCIAL_TRANS.HST_TRANS_BUS_DATE  >=  PRIOR_BUSINESS_DAY)\\n  ) ,\\n\\n  Q2 AS (SELECT\\n  ODSFISRV.LOAN.LOAN_NUMBER NEW_LOAN,\\n  ODSFISRV.BORR_LOAN.T9_PRIOR_ACNT_NBR OLD_LOAN,\\n  ODSFISRV.LOAN_CALCS.CALC_TOT_NEXT_PAYMENT OLD_PAYMENT,\\n  ODSFISRV.ORIG_LOAN.ML_AUDIT_DATE OLD_AUDIT\\nFROM\\n  ODSFISRV.LOAN,\\n  ODSFISRV.BORR_LOAN,\\n  ODSFISRV.LOAN_CALCS,\\n  ODSFISRV.ORIG_LOAN\\nWHERE\\n  ( ODSFISRV.LOAN_CALCS.LOAN_NUMBER(+)=ODSFISRV.LOAN.LOAN_NUMBER  )\\n  AND  ( ODSFISRV.LOAN.LOAN_NUMBER=ODSFISRV.ORIG_LOAN.LOAN_NUMBER(+)  )\\n  AND  ( ODSFISRV.LOAN.LOAN_NUMBER=ODSFISRV.BORR_LOAN.LOAN_NUMBER(+)  )\\n  AND  (NVL(ODSFISRV.LOAN.DELETED_FLAG,'N')  != 'Y')\\n  AND  (ODSFISRV.LOAN.ML_INST!='999')\\n  AND  (  ODSFISRV.ORIG_LOAN.ML_AUDIT_DATE  >=  dateadd('month', -6, date_trunc('month',current_date()))\\n  AND  ODSFISRV.BORR_LOAN.T9_PRIOR_ACNT_NBR  IS NOT NULL )\\n)\\n\\nSELECT Q1.*, Q2.*\\nFROM Q1\\nLEFT OUTER JOIN Q2 ON Q2.OLD_LOAN = Q1.LOAN_NUMBER\",\n" +
                "\t\t\t\"sql\": \"Snowflake\"\n" +
                "\t\t}\n" +
                "\t]\n" +
                "}";

        processFailedQueriesJson(jsonInput);
    }

}
