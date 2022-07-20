package postgresql;
/*
 * Date: 13-10-22
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testCreateView extends TestCase {

    public void test1(){


    String viewSql = "CREATE OR REPLACE VIEW STG.FIXED_FEE_RATE_PACKAGES_V AS "
                                                + "SELECT SUB.RS_CD, SUB.EFFDT_START, SUB.EFFDT_STOP, SUB.UOM_CD, SUB.TOU_CD, SUB.SQI_CD "
                                                + "FROM (SELECT RS.RS_CD, RV.EFFDT_START, RV.EFFDT_STOP, RC.UOM_CD, RC.TOU_CD, RC.SQI_CD, row_number() OVER (PARTITION BY RS.RS_CD, "
                                                + "RV.EFFDT_START, RV.EFFDT_STOP ORDER BY RC.UOM_CD, RC.TOU_CD, RC.SQI_CD) AS PRIORITY "
                                                + "FROM (((SELECT btrim((O_CIS_CI_RS.RS_CD)::varchar(8)) AS RS_CD FROM COM.O_CIS_CI_RS) RS "
                                                + "JOIN (SELECT btrim((O_CIS_CI_RV.RS_CD)::varchar(8)) AS RS_CD, O_CIS_CI_RV.EFFDT AS EFFDT_START, "
                                                + "(lead(O_CIS_CI_RV.EFFDT, 1, NULL::timestamp) OVER (PARTITION BY btrim((O_CIS_CI_RV.RS_CD)::varchar(8)) "
                                                + "ORDER BY O_CIS_CI_RV.EFFDT) + '-1'::interval second) AS EFFDT_STOP FROM COM.O_CIS_CI_RV "
                                                + "WHERE (btrim((O_CIS_CI_RV.RV_STATUS_FLG)::varchar(4)) = 'F'::varchar(1))) RV "
                                                + "ON ((RV.RS_CD = RS.RS_CD))) JOIN (SELECT btrim((O_CIS_CI_RC.RS_CD)::varchar(8)) "
                                                + "AS RS_CD, O_CIS_CI_RC.EFFDT, btrim((O_CIS_CI_RC.UOM_CD)::varchar(4)) AS UOM_CD, "
                                                + "btrim((O_CIS_CI_RC.TOU_CD)::varchar(8)) AS TOU_CD, btrim((O_CIS_CI_RC.SQI_CD)::varchar(8)) "
                                                + "AS SQI_CD FROM COM.O_CIS_CI_RC WHERE ((btrim(O_CIS_CI_RC.CALC_ONLY_SW) = 'N'::varchar(1)) "
                                                + "AND (((btrim((O_CIS_CI_RC.UOM_CD)::varchar(4)) = 'A'::varchar(1)) AND (btrim((O_CIS_CI_RC.SQI_CD)::varchar(8)) ~~ 'AMP%'::varchar(4))) "
                                                + "OR ((btrim((O_CIS_CI_RC.UOM_CD)::varchar(4)) = 'KW'::varchar(2)) AND ((btrim((O_CIS_CI_RC.SQI_CD)::varchar(8)) = 'CAP-BILL'::varchar(8)) "
                                                + "OR (btrim((O_CIS_CI_RC.TOU_CD)::varchar(8)) = 'DAY'::varchar(3)))) OR ((btrim((O_CIS_CI_RC.UOM_CD)::varchar(4)) = 'UNIT'::varchar(4)) "
                                                + "AND (btrim((O_CIS_CI_RC.SQI_CD)::varchar(8)) = 'MS-Q'::varchar(4)))))) RC ON (((RC.RS_CD = RV.RS_CD) AND (RC.EFFDT = RV.EFFDT_START)))) "
                                                + "WHERE true) SUB WHERE (SUB.PRIORITY = 1)";

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = viewSql;
        //System.out.println(viewSql);

        assertTrue(sqlparser.parse() == 0);

        TCreateViewSqlStatement view = (TCreateViewSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(view.getViewName().toString().equalsIgnoreCase("STG.FIXED_FEE_RATE_PACKAGES_V"));
        TSelectSqlStatement select = view.getSubquery();
        assertTrue(select.getResultColumnList().size() == 6);

    }

}
