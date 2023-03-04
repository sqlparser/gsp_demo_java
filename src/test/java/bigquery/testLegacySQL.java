package bigquery;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import gudusoft.gsqlparser.TGSqlParser;

import junit.framework.TestCase;

public class testLegacySQL extends TestCase {

    public void test2() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        TBaseType.bigquery_legacysql_compatible = true;
        sqlparser.sqltext = "SELECT \n" +
                "WEEK_NUM,\n" +
                "SERVICE_ORDER_NUMBER,\n" +
                "UID,\n" +
                "ECT\n" +
                "FROM [motorola.com:dataservices:Service.SVC_MDS_REPAIR_ORDERS_CLOSED_PNA_VW] \n" +
                "where week_num>'2020-W01'";

        assertTrue(sqlparser.parse() == 0);
        TBaseType.bigquery_legacysql_compatible = false;
    }

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        TBaseType.bigquery_legacysql_compatible = true;
        sqlparser.sqltext = "-- https://cloud.google.com/bigquery/table-decorators\n" +
                "\n" +
                "-- \n" +
                "\n" +
                "#legacySQL\n" +
                "SELECT COUNT(*) FROM [PROJECT_ID:DATASET.TABLE@-3600000];\n" +
                "\n" +
                "SELECT [0, 1, 1, 2, 3, 5] FROM [PROJECT_ID:DATASET.TABLE@-3600000];\n" +
                "\n" +
                "\n" +
                "-- https://cloud.google.com/bigquery/docs/reference/standard-sql/arrays\n" +
                "\n" +
                "WITH sequences AS\n" +
                "  (SELECT 1 AS id, [0, 1, 1, 2, 3, 5] AS some_numbers\n" +
                "   UNION ALL SELECT 2 AS id, [2, 4, 8, 16, 32] AS some_numbers\n" +
                "   UNION ALL SELECT 3 AS id, [5, 10] AS some_numbers)\n" +
                "SELECT id, flattened_numbers\n" +
                "FROM sequences\n" +
                "CROSS JOIN UNNEST(sequences.some_numbers) AS flattened_numbers;\n" +
                "\n" +
                "\n" +
                "MERGE dataset.DetailedInventory T\n" +
                "USING dataset.Inventory S\n" +
                "ON T.product = S.product\n" +
                "WHEN NOT MATCHED AND quantity < 20 THEN\n" +
                "  INSERT(product, quantity, supply_constrained, comments)\n" +
                "  VALUES(product, quantity, true, ARRAY<STRUCT<created DATE, comment STRING>>[(DATE('2016-01-01'), 'comment1')])\n" +
                "WHEN NOT MATCHED THEN\n" +
                "  INSERT(product, quantity, supply_constrained)\n" +
                "  VALUES(product, quantity, false)\n" +
                ";\n" +
                "\n" +
                "\n" +
                "select \n" +
                "Metric,\n" +
                "COUNTRY_CODE,\n" +
                "WARR_FLAG,\n" +
                "DATE,\n" +
                "GEO_REGION,\n" +
                "SUBREGION_CODE,\n" +
                "TAT,\n" +
                "WEEK_NUM,\n" +
                "SERVICE_ORDER_CID,\n" +
                "SERVICE_ORDER_NUMBER,\n" +
                "APC_CODE,\n" +
                "SERVICE_MACHINE_MODEL,\n" +
                "Num_Qty,\n" +
                "Denom_Qty\n" +
                "\n" +
                "from\n" +
                "/*\n" +
                "(select 'LClaims_3_Days_OTD' as Metric,\n" +
                "COUNTRY_CODE as COUNTRY_CODE,\n" +
                "WARR_FLAG as WARR_FLAG,\n" +
                "HEADER_DATE as DATE,\n" +
                "REGION_CODE as GEO_REGION,\n" +
                "SUBREGION_CODE as SUBREGION_CODE,\n" +
                "TAT as TAT,\n" +
                "'Not CID' AS SERVICE_ORDER_CID,\n" +
                "WEEK_NUM as WEEK_NUM,\n" +
                "CLM_CLAIM_NUMBER AS SERVICE_ORDER_NUMBER,\n" +
                "APC_MT_CODE AS APC_CODE,\n" +
                "'' AS SERVICE_MACHINE_MODEL,\n" +
                "(case when TAT <= 3 then integer(count(unique(CLM_CLAIM_NUMBER))) else 0 end) as NUM_QTY,\n" +
                "count(unique(CLM_CLAIM_NUMBER)) AS DENOM_QTY\n" +
                "from [motorola.com:dataservices:Service.SVC_DLYOPS_LCLAIMS_TAT] where REGION_CODE ='Europe-East' and Week_Num >='2018-W31' and Week_Num is not Null and REGION_CODE is not null\n" +
                "group by 1,2,3,4,5,6,7,8,9,10,11,12\n" +
                "order by WEEK_NUM)\n" +
                "\n" +
                ",*/\n" +
                "(select 'Consumer_3_Day_OTD' as Metric,\n" +
                "STATION_COUNTRY as COUNTRY_CODE,\n" +
                "SERVICE_ORDER_WARRANTY_STATUS as WARR_FLAG,\n" +
                "SHIP_DATE as DATE,\n" +
                "'North America' as GEO_REGION,\n" +
                "' ' as SUBREGION_CODE,\n" +
                "TAT_PAUSE as TAT,         /*  Update NA TAT to TAT_PAUSE on 06/16/2021 */\n" +
                "SERVICE_ORDER_CID as SERVICE_ORDER_CID,\n" +
                "WEEK_NUM as WEEK_NUM,\n" +
                "RNT_REF AS SERVICE_ORDER_NUMBER,\n" +
                "APC_CODE AS APC_CODE,\n" +
                "SERVICE_MACHINE_MODEL AS SERVICE_MACHINE_MODEL,\n" +
                "(case when TAT <= 3 then integer(count(unique(RNT_REF))) else 0 end) as NUM_QTY,   \n" +
                "count(unique(RNT_REF)) AS DENOM_QTY\n" +
                "from [motorola.com:dataservices:Service.SVC_MDS_NA_OTD_VW] where WEEK_NUM >'2019-W01' and WEEK_NUM is not Null\n" +
                "group by 1,2,3,4,5,6,7,8,9,10,11,12\n" +
                "order by WEEK_NUM)\n" +
                ",\n" +
                "\n" +
                "(select 'MDS_2_Day_OTD' as Metric,\n" +
                "STATION_COUNTRY as COUNTRY_CODE,\n" +
                "SERVICE_ORDER_WARRANTY_STATUS as WARR_FLAG,\n" +
                "SERVICE_ORDER_REPAIR_END_DATETIME as DATE,\n" +
                "REGION_DESCRIPTION as GEO_REGION,\n" +
                "SUBREGION as SUBREGION_CODE,\n" +
                "TAT as TAT,\n" +
                "SERVICE_ORDER_CID as SERVICE_ORDER_CID,\n" +
                "WEEK_NUM as WEEK_NUM,\n" +
                "SERVICE_ORDER_NUMBER AS SERVICE_ORDER_NUMBER,\n" +
                "APC_CODE AS APC_CODE,\n" +
                "SERVICE_MACHINE_MODEL AS SERVICE_MACHINE_MODEL,\n" +
                "(case when TAT <= 2 then integer(count(unique(SERVICE_ORDER_NUMBER))) else 0 end) as NUM_QTY,\n" +
                "count(unique(SERVICE_ORDER_NUMBER)) AS DENOM_QTY\n" +
                "from [motorola.com:dataservices:Service.MDS_SVC_REPAIR_ORDERS_OTD_VW] where REGION_DESCRIPTION ='Brazil' and Week_Num >'2018-W31' and Week_Num is not Null and REGION_DESCRIPTION is not null\n" +
                "group by 1,2,3,4,5,6,7,8,9,10,11,12\n" +
                "order by WEEK_NUM)\n" +
                ",\n" +
                "(select 'MDS_3_Day_OTD' as Metric,\n" +
                "STATION_COUNTRY as COUNTRY_CODE,\n" +
                "SERVICE_ORDER_WARRANTY_STATUS as WARR_FLAG,\n" +
                "SERVICE_ORDER_REPAIR_END_DATETIME as DATE,\n" +
                "REGION_DESCRIPTION as GEO_REGION,\n" +
                "SUBREGION as SUBREGION_CODE,\n" +
                "TAT as TAT,\n" +
                "WEEK_NUM as WEEK_NUM,\n" +
                "SERVICE_ORDER_CID as SERVICE_ORDER_CID,\n" +
                "SERVICE_ORDER_NUMBER AS SERVICE_ORDER_NUMBER,\n" +
                "APC_CODE AS APC_CODE,\n" +
                "SERVICE_MACHINE_MODEL AS SERVICE_MACHINE_MODEL,\n" +
                "(case when TAT <= 3 then integer(count(unique(SERVICE_ORDER_NUMBER))) else 0 end) as NUM_QTY,\n" +
                "count(unique(SERVICE_ORDER_NUMBER)) AS DENOM_QTY\n" +
                "from [motorola.com:dataservices:Service.MDS_SVC_REPAIR_ORDERS_OTD_VW] where REGION_DESCRIPTION in('LAS','India','West Europe','East Europe','SW Asia') and Week_Num >'2018-W31' and Week_Num is not Null and REGION_DESCRIPTION is not null\n" +
                "group by 1,2,3,4,5,6,7,8,9,10,11,12\n" +
                "order by WEEK_NUM)\n" +
                ",\n" +
                "(select 'MDS_5_Day_OTD' as Metric,\n" +
                "STATION_COUNTRY as COUNTRY_CODE,\n" +
                "SERVICE_ORDER_WARRANTY_STATUS as WARR_FLAG,\n" +
                "SERVICE_ORDER_REPAIR_END_DATETIME as DATE,\n" +
                "REGION_DESCRIPTION as GEO_REGION,\n" +
                "SUBREGION as SUBREGION_CODE,\n" +
                "SERVICE_ORDER_CID as SERVICE_ORDER_CID,\n" +
                "TAT as TAT,\n" +
                "WEEK_NUM as WEEK_NUM,\n" +
                "SERVICE_ORDER_NUMBER AS SERVICE_ORDER_NUMBER,\n" +
                "APC_CODE AS APC_CODE,\n" +
                "SERVICE_MACHINE_MODEL AS SERVICE_MACHINE_MODEL,\n" +
                "(case when TAT <= 5 then integer(count(unique(SERVICE_ORDER_NUMBER))) else 0 end) as NUM_QTY,\n" +
                "count(unique(SERVICE_ORDER_NUMBER)) AS DENOM_QTY\n" +
                "from [motorola.com:dataservices:Service.MDS_SVC_REPAIR_ORDERS_OTD_VW] where REGION_DESCRIPTION in('META','JANZ','Rest of Asia') and Week_Num >'2018-W31' and Week_Num is not Null and REGION_DESCRIPTION is not null\n" +
                "group by 1,2,3,4,5,6,7,8,9,10,11,12\n" +
                "order by WEEK_NUM)\n" +
                "group by 1,2,3,4,5,6,7,8,9,10,11,12,NUM_QTY,DENOM_QTY";

        assertTrue(sqlparser.parse() == 0);
        TBaseType.bigquery_legacysql_compatible = false;
    }
}
