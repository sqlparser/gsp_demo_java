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
}
