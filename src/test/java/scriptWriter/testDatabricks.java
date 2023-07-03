package scriptWriter;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import junit.framework.TestCase;

public class testDatabricks extends TestCase
{
    public void test1( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvdatabricks);
        sqlparser.sqltext = "select datepart('days',interval 5 days 3 hours 7 minutes);";
        sqlparser.parse( );
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvdatabricks, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
    }

    public void test2( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvdatabricks);
        sqlparser.sqltext = "select datepart('seconds',interval 5 hours 30 seconds 1 milliseconds 1 microseconds);\n" +
                "select datepart('month',interval '2021-11' year to month);\n" +
                "select datepart('minute',interval '123 23:55:59.002001' day to second);\n" +
                "select interval '1-1' year to month div interval '-1' month;\n" +
                "select extract(days from interval 5 days 3 hours 7 minutes);\n" +
                "select extract(seconds from interval 5 hours 30 seconds 1 milliseconds 1 microseconds);\n" +
                "select extract(month from interval '2021-11' year to month);\n" +
                "select extract(minute from interval '123 23:55:59.002001' day to second);\n" +
                "select sequence(to_date('2018-01-01'),to_date('2018-03-01'),interval 1 month);\n" +
                "select sequence(to_date('2018-01-01'),to_date('2018-03-01'),interval '0-1' year to month);\n" +
                "select sign(interval -'100' year);\n" +
                "select signum(interval -'100' year);\n" +
                "select try_add(date'2021-01-01',interval 1 year);\n" +
                "select try_add(timestamp'2021-01-01 00:00:00',interval 1 day);\n" +
                "select try_add(interval 1 year,interval 2 year);\n" +
                "select try_divide(interval 2 month,2);\n" +
                "select try_divide(interval 2 month,0);\n" +
                "select try_multiply(interval 2 year,3);\n" +
                "select try_subtract(date'2021-01-01',interval 1 year);\n" +
                "select try_subtract(timestamp'2021-01-02 00:00:00',interval 1 day);\n" +
                "select try_subtract(interval 2 year,interval 1 year);\n" +
                "select width_bucket(interval '0' year,interval '0' year,interval '10' year,10);\n" +
                "select width_bucket(interval '1' year,interval '0' year,interval '10' year,10);\n" +
                "select width_bucket(interval '0' day,interval '0' day,interval '10' day,10);\n" +
                "select width_bucket(interval '1' day,interval '0' day,interval '10' day,10);\n";
        sqlparser.parse( );
        for(int i=0;i<sqlparser.sqlstatements.size();i++){
            assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvdatabricks,
                    sqlparser.sqlstatements.get(i).toString(), sqlparser.sqlstatements.get(i).toScript()));
        }
    }

    public void test3( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvdatabricks);
        sqlparser.sqltext = "select contains('spark sql','spark')";
        sqlparser.parse( );
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvdatabricks, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
    }

    public void test4( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvdatabricks);
        sqlparser.sqltext = "select exists(array(1,2,3),x -> x % 2 == 0)";
        sqlparser.parse( );
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvdatabricks, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
    }

    public void test5( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvdatabricks);
        sqlparser.sqltext = "select date_part('year',timestamp '2019-08-12 01:00:00.123456')";
        sqlparser.parse( );
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvdatabricks, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
    }

    public void test6( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvdatabricks);
        sqlparser.sqltext = "SELECT *\n" +
                "FROM table1 t6\n" +
                "  JOIN table2 t7 ON ( (t6.ssn = t7.ssn))\n" +
                "  LEFT OUTER JOIN table3 t8\n" +
                "         ON ( (t6.ssn = t8.ssn\n" +
                "        AND t6.scc = t8.scc\n" +
                "        AND t6.sfy = t8.sfy\n" +
                "        AND t6.sli = t8.sli\n" +
                "        AND t6.sdn = t8.sdn)) LIMIT 10000";
        sqlparser.parse( );
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvdatabricks, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
    }

    public void test7( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvdatabricks);
        sqlparser.sqltext = "SELECT *\n" +
                "FROM ((((((((table0 t0\n" +
                "  JOIN table1 t1 ON ( (t0.ssn = t1.ssn)))\n" +
                "  JOIN table2 t2 ON ( (CAST (t0.cd AS DATE) = t2.cd)))\n" +
                "  JOIN table3 t3\n" +
                "    ON ( (t0.phi = t3.phi\n" +
                "   AND t0.ssn = t3.ssn)))\n" +
                "  JOIN table4 t4\n" +
                "    ON ( (t3.phi = t4.phi\n" +
                "   AND t3.pli = t4.pli\n" +
                "   AND t3.ssn = t4.ssn)))\n" +
                "  JOIN table5 t5\n" +
                "    ON ( (t0.oi = t5.oi\n" +
                "   AND t0.ssn = t5.ssn)))\n" +
                "  JOIN table6 t6\n" +
                "    ON ( (t5.li = t6.li\n" +
                "   AND t5.ssn = t6.ssn)))\n" +
                "  LEFT JOIN table7 t7\n" +
                "         ON ( (t0.vsi = t7.vsi\n" +
                "        AND t0.ssn = t7.ssn)))\n" +
                "  JOIN table8 xgcc\n" +
                "    ON ( (t4.cci = xgcc.cci\n" +
                "   AND t4.ssn = xgcc.ssn)))\n" +
                "  LEFT JOIN table9 t9\n" +
                "         ON ( (t0.terms_id = t9.term_id\n" +
                "        AND t0.ssn = t9.ssn))\n" +
                "  LEFT JOIN table10 t10\n" +
                "         ON t0.ssn = t10.ssn\n" +
                "        AND t0.vsi = t10.vsi";
        sqlparser.parse( );
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvdatabricks, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
    }

    public void test8( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvdatabricks);
        sqlparser.sqltext = "WITH base AS\n" +
                "(\n" +
                "  SELECT ccl1,\n" +
                "         101 AS prev_year_value,\n" +
                "         102 AS prev_qtr_value,\n" +
                "         'Quarterly' AS period,\n" +
                "         103 AS curr_value\n" +
                "  FROM table0 hb\n" +
                "  WHERE 1 = 1\n" +
                ")\n" +
                "SELECT ccl1,\n" +
                "       base.curr_value AS curr_value,\n" +
                "       IF (period LIKE 'Quarterly',COALESCE((base.curr_value - base.prev_qtr_value),0) /COALESCE(ABS(base.prev_qtr_value),1)*100,'-') AS VPQ,\n" +
                "       IF (period IN ('Quarterly','YTD'),COALESCE((base.curr_value - base.prev_year_value),0) /COALESCE(ABS(base.prev_year_value),1)*100,'-') AS VPY\n" +
                "FROM base\n" +
                "WHERE 1 = 1";
        sqlparser.parse( );
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvdatabricks, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
    }
	
	public void test9( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvdatabricks);
		sqlparser.sqltext = "select 3 ^ 5";
        sqlparser.parse( );
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvdatabricks, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
	}

    public void test10( )
    {
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvdatabricks);
        sqlparser.sqltext = "WITH base AS\n" +
                "(\n" +
                "  SELECT cost_category_level1,\n" +
                "         SUM(CASE WHEN (to_date (CONCAT (hb.period_year,'-',hb.period_num,'-01'),'yyyy-MM-dd') BETWEEN to_date ('2023-04-01','yyyy-MM-dd') -INTERVAL 1 YEAR AND to_date ('2023-06-30','yyyy-MM-dd') -INTERVAL 1 YEAR) THEN hb.period_balance_amount ELSE 0 END) AS prev_year_value,\n" +
                "         SUM(CASE WHEN (hb.period_year = YEAR(to_date ('2023-06-30','yyyy-MM-dd')) -(FLOOR((12 -MONTH(to_date ('2023-06-30','yyyy-MM-dd'))) / 9)) AND hb.period_num = (MONTH(to_date ('2023-06-30','yyyy-MM-dd')) + 8) % 12 + 1) THEN hb.qtd_balance_amount ELSE 0 END) AS prev_qtr_value,\n" +
                "         CASE\n" +
                "           WHEN DAY(to_date ('2023-04-01','yyyy-MM-dd')) IN ('01','1') AND MONTH(to_date ('2023-04-01','yyyy-MM-dd')) IN ('01','1') AND DAY(to_date ('2023-06-30','yyyy-MM-dd')) IN ('31') AND MONTH(to_date ('2023-06-30','yyyy-MM-dd')) IN ('03','3') AND YEAR(to_date ('2023-04-01','yyyy-MM-dd')) = YEAR(to_date ('2023-06-30','yyyy-MM-dd')) OR DAY(to_date ('2023-04-01','yyyy-MM-dd')) IN ('01','1') AND MONTH(to_date ('2023-04-01','yyyy-MM-dd')) IN ('04','4') AND DAY(to_date ('2023-06-30','yyyy-MM-dd')) IN ('30') AND MONTH(to_date ('2023-06-30','yyyy-MM-dd')) IN ('06','6') AND YEAR(to_date ('2023-04-01','yyyy-MM-dd')) = YEAR(to_date ('2023-06-30','yyyy-MM-dd')) OR DAY(to_date ('2023-04-01','yyyy-MM-dd')) IN ('01','1') AND MONTH(to_date ('2023-04-01','yyyy-MM-dd')) IN ('07','7') AND DAY(to_date ('2023-06-30','yyyy-MM-dd')) IN ('30') AND MONTH(to_date ('2023-06-30','yyyy-MM-dd')) IN ('09','9') AND YEAR(to_date ('2023-04-01','yyyy-MM-dd')) = YEAR(to_date ('2023-06-30','yyyy-MM-dd')) OR DAY(to_date ('2023-04-01','yyyy-MM-dd')) IN ('01','1') AND MONTH(to_date ('2023-04-01','yyyy-MM-dd')) IN ('10') AND DAY(to_date ('2023-06-30','yyyy-MM-dd')) IN ('31') AND MONTH(to_date ('2023-06-30','yyyy-MM-dd')) IN ('12') AND YEAR(to_date ('2023-04-01','yyyy-MM-dd')) = YEAR(to_date ('2023-06-30','yyyy-MM-dd')) THEN 'Quarterly'\n" +
                "           WHEN DAY(to_date ('2023-04-01','yyyy-MM-dd')) IN ('01','1') AND MONTH(to_date ('2023-04-01','yyyy-MM-dd')) IN ('01','1') AND to_date ('2023-06-30','yyyy-MM-dd') = CURRENT_DATE() THEN 'YTD'\n" +
                "           ELSE 'Custom'\n" +
                "         END AS period,\n" +
                "         SUM(CASE WHEN (to_date (CONCAT (hb.period_year,'-',hb.period_num,'-01'),'yyyy-MM-dd') BETWEEN to_date ('2023-04-01','yyyy-MM-dd') AND to_date ('2023-06-30','yyyy-MM-dd')) THEN hb.period_balance_amount ELSE 0 END) AS curr_value\n" +
                "  FROM fdl_db_qa_dbr_consumption_inc.xx_hfm_balances hb\n" +
                "    JOIN fdl_db_qa_dbr_consumption_inc.xx_drm_hier_account dha\n" +
                "      ON hb.account = dha.name\n" +
                "     AND dha.drm_type = 'ABC_D'\n" +
                "     AND dha.drm_org = 'AB'\n" +
                "    JOIN fdl_db_qa_dbr_consumption_inc.xx_drm_hier_supplementaldata dhs\n" +
                "      ON dhs.name = hb.supplemental_dim\n" +
                "     AND dhs.drm_type = 'ABCD'\n" +
                "     AND dhs.drm_org = 'AB'\n" +
                "    JOIN fdl_db_qa_dbr_consumption_inc.xx_drm_hier_reporting dhr\n" +
                "      ON dhr.name = hb.reporting_dim\n" +
                "     AND dhr.drm_type = 'ABCD'\n" +
                "     AND dhr.drm_org = 'AB'\n" +
                "    JOIN fdl_db_qa_dbr_consumption_inc.xx_gl_code_combinations xgcc\n" +
                "      ON hb.source_system_name = xgcc.source_system_name\n" +
                "     AND hb.code_combination_id = xgcc.CODE_COMBINATION_ID\n" +
                "  WHERE hb.currency = 'USD'\n" +
                "  AND   hb.scenario = 'Actual'\n" +
                "  AND   hb.data_view = 'YTD'\n" +
                "  AND   dhr.level03_parent_node = 'QQ_TorCrAsRettttpo'\n" +
                "  AND   dhs.level03_parent_node = 'AB_RPEE'\n" +
                "  AND   (to_date(CONCAT(hb.period_year,'-',hb.period_num,'-01'),'yyyy-MM-dd') BETWEEN to_date('2023-04-01','yyyy-MM-dd') -INTERVAL 1 YEAR AND to_date('2023-06-30','yyyy-MM-dd')) AND dha.level05_parent_node = 'AC_F512000000'\n" +
                "  GROUP BY cost_category_level1\n" +
                ")\n" +
                "SELECT cost_category_level1,\n" +
                "       base.curr_value AS curr_value,\n" +
                "       IF (period LIKE 'Quarterly',COALESCE((base.curr_value - base.prev_qtr_value),0) /COALESCE(ABS(base.prev_qtr_value),1)*100,'-') AS VPQ,\n" +
                "       IF (period IN ('Quarterly','YTD'),COALESCE((base.curr_value - base.prev_year_value),0) /COALESCE(ABS(base.prev_year_value),1)*100,'-') AS VPY\n" +
                "FROM base\n" +
                "WHERE (FLOOR(ABS(base.curr_value)) != 0)\n" +
                "ORDER BY cost_category_level1";
        sqlparser.parse( );
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvdatabricks, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
    }
    public void test11( )
    {
        String sql = "SELECT\n" +
                "  'TOTAL' AS Week,\n" +
                "  SUM(IF ((T2311468.usd_amount_ph IS NOT NULL AND (xdd.calendar_date BETWEEN to_date ('2022-01-01', 'yyyy-MM-dd') AND to_date ('2022-03-31', 'yyyy-MM-dd'))), T2311468.usd_amount_ph, 0)) AS current_year,\n" +
                "  SUM(IF ((T2311468.usd_amount_ph IS NOT NULL AND (xdd.calendar_date BETWEEN to_date ('2022-01-01', 'yyyy-MM-dd') -INTERVAL 1 YEAR AND to_date ('2022-03-31', 'yyyy-MM-dd') -INTERVAL 1 YEAR)), T2311468.usd_amount_ph, 0)) AS prev_year\n" +
                "FROM\n" +
                "  table0 xdd\n" +
                "INNER JOIN table1 T2311468 ON\n" +
                "  xdd.calendar_date = T2311468.payment_date_ph\n" +
                "LEFT OUTER JOIN table2 T2305475\n" +
                "               ON\n" +
                "  T2305475.invoice_id = T2311468.invoice_id\n" +
                "  AND T2305475.ssn = T2311468.ssn\n" +
                "INNER JOIN table3 xgcc\n" +
                "          ON\n" +
                "  xgcc.cci = T2311468.accts_pay_ccid_actual\n" +
                "  AND xgcc.ssn = T2311468.ssn\n" +
                "WHERE\n" +
                "  1=1";
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvdatabricks);
        sqlparser.sqltext = sql;
        sqlparser.parse( );
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvdatabricks, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
    }

    public void test12( )
    {
        String sql = "SELECT ds_ge_supplier_normalized_name AS vendor_name,\n" +
                "        SUM(curr_value) AS dollar_value\n" +
                "        FROM (SELECT ds_ge_supplier_normalized_name AS ds_ge_supplier_normalized_name,\n" +
                "        SUM(PO_Remaining_amount) AS curr_value\n" +
                "        FROM (SELECT COALESCE(t4.usd_amount_ordered,0) -(COALESCE(t4.usd_amount_billed,0) +COALESCE(t4.usd_amount_cancelled,0)) AS PO_Remaining_amount,\n" +
                "        t0.creation_date,\n" +
                "        t7.ge_supplier_normalized_name AS ds_ge_supplier_normalized_name\n" +
                "        FROM ((((((((fdl_db_qa_dbr_consumption_inc.xx_po_headers_v t0\n" +
                "        JOIN fdl_db_qa_dbr_consumption_inc.xx_source_systems t1 ON ( (t0.source_system_name = t1.source_system_name)))\n" +
                "        JOIN fdl_db_qa_dbr_consumption_inc.xx_date_dim t2 ON ( (CAST (t0.creation_date AS DATE) = t2.calendar_date)))\n" +
                "        JOIN fdl_db_qa_dbr_consumption_inc.xx_po_lines_v t3\n" +
                "        ON ( (t0.po_header_id = t3.po_header_id\n" +
                "        AND t0.source_system_name = t3.source_system_name)))\n" +
                "        JOIN fdl_db_qa_dbr_consumption_inc.xx_po_distributions_v t4\n" +
                "        ON ( (t3.po_header_id = t4.po_header_id\n" +
                "        AND t3.po_line_id = t4.po_line_id\n" +
                "        AND t3.source_system_name = t4.source_system_name)))\n" +
                "        JOIN fdl_db_qa_dbr_consumption_inc.xx_organizations t5\n" +
                "        ON ( (t0.org_id = t5.organization_id\n" +
                "        AND t0.source_system_name = t5.source_system_name)))\n" +
                "        JOIN fdl_db_qa_dbr_consumption_inc.xx_hr_locations t6\n" +
                "        ON ( (t5.location_id = t6.location_id\n" +
                "        AND t5.source_system_name = t6.source_system_name)))\n" +
                "        LEFT JOIN fdl_db_qa_dbr_consumption_inc.xx_po_vendor_sites_v t7\n" +
                "        ON ( (t0.vendor_site_id = t7.vendor_site_id\n" +
                "        AND t0.source_system_name = t7.source_system_name)))\n" +
                "        JOIN fdl_db_qa_dbr_consumption_inc.xx_gl_code_combinations xgcc\n" +
                "        ON ( (t4.code_combination_id = xgcc.code_combination_id\n" +
                "        AND t4.source_system_name = xgcc.source_system_name)))\n" +
                "        LEFT JOIN fdl_db_qa_dbr_consumption_inc.xx_ap_terms t9\n" +
                "        ON ( (t0.terms_id = t9.term_id\n" +
                "        AND t0.source_system_name = t9.source_system_name))\n" +
                "        LEFT JOIN fdl_db_qa_dbr_consumption_inc.xx_po_vendor_sites_v t10 /* DIM_PO_VENDOR_SITES_V */\n" +
                "        ON t0.source_system_name = t10.source_system_name\n" +
                "        AND t0.vendor_site_id = t10.vendor_site_id\n" +
                "        WHERE t0.authorization_status IN ('02 - Active','03 - In release','05 - Release completed','APPROVED','IN PROCESS','PRE-APPROVED')\n" +
                "        AND   t7.internal_external IN ('External','Unspecified')\n" +
                "        AND   t0.closed_code = 'OPEN'\n" +
                "        AND   t0.cancel_flag NOT IN ('Y')\n" +
                "        AND   t3.cancel_flag NOT IN ('Y')\n" +
                "        AND   (t5.attribute17 IS NULL OR t5.attribute17 != 'Buy Only')\n" +
                "        AND   to_date(t0.creation_date,'YYYY-MM-DD') BETWEEN NOW() -INTERVAL '5' YEAR AND NOW()\n" +
                "        AND   ((COALESCE(t4.usd_amount_ordered,0) -(COALESCE(t4.usd_amount_billed,0) +COALESCE(t4.usd_amount_cancelled,0))) != 0)\n" +
                "        AND   t9.due_days < 30\n" +
                "        AND   to_date(t0.creation_date,'YYYY-MM-DD') BETWEEN NOW() -INTERVAL '5' YEAR AND NOW()) s0\n" +
                "        GROUP BY s0.ds_ge_supplier_normalized_name\n" +
                "        ORDER BY curr_value DESC LIMIT 10)\n" +
                "        GROUP BY ds_ge_supplier_normalized_name\n" +
                "        ORDER BY dollar_value DESC";
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvdatabricks);
        sqlparser.sqltext = sql;
        int ret = sqlparser.parse( );
        assertTrue(ret == 0);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvdatabricks, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
    }
    public void test13( )
    {
        String sql = "select * from t where to_date ('2023-06-30','yyyy-MM-dd') = CURRENT_DATE()";
        TGSqlParser sqlparser = new TGSqlParser( EDbVendor.dbvdatabricks);
        sqlparser.sqltext = sql;
        int ret = sqlparser.parse( );
        assertTrue(ret == 0);
        assertTrue(testScriptGenerator.verifyScript(EDbVendor.dbvdatabricks, sqlparser.sqlstatements.get(0).toString(), sqlparser.sqlstatements.get(0).toScript()));
    }
}
