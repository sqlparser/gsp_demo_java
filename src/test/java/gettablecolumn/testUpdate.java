package gettablecolumn;


import demos.gettablecolumns.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testUpdate extends TestCase {

    static void doTest(EDbVendor dbVendor,String inputQuery, String desireResult){
        TGetTableColumn getTableColumn = new TGetTableColumn(dbVendor);
        getTableColumn.isConsole = false;
        getTableColumn.showTableEffect = false;
        getTableColumn.showColumnLocation = false;
        getTableColumn.showTreeStructure = false;
        getTableColumn.listStarColumn = true;
        getTableColumn.runText(inputQuery);
       //   System.out.println(getTableColumn.outList.toString().trim());
        assertTrue(getTableColumn.outList.toString().trim().equalsIgnoreCase(desireResult));
    }

    public static void testUpdate1() {
        doTest(EDbVendor.dbvteradata,
                "UPDATE a\n" +
                        "FROM basic_svc a,\n" +
                        "(SELECT emf_config_id AS id, display_value FROM IWVIEWS_SRD.AR_EMF_CONFIG_ID_VALUES \n" +
                        "WHERE close_dt = '2899-12-31') b\n" +
                        "SET product = display_value\n" +
                        "WHERE emf_config_id = id;",
                "Tables:\n" +
                        "basic_svc\n" +
                        "IWVIEWS_SRD.AR_EMF_CONFIG_ID_VALUES\n" +
                        "\nFields:\n" +
                        "basic_svc.emf_config_id\n" +
                        "basic_svc.product\n" +
                        "IWVIEWS_SRD.AR_EMF_CONFIG_ID_VALUES.close_dt\n" +
                        "IWVIEWS_SRD.AR_EMF_CONFIG_ID_VALUES.display_value\n" +
                        "IWVIEWS_SRD.AR_EMF_CONFIG_ID_VALUES.emf_config_id");
    }


    public static void testUpdate2() {
        doTest(EDbVendor.dbvteradata,
                "UPDATE b_rate_plan\n" +
                        "FROM \n" +
                        "(\n" +
                        "SELECT * FROM SPCOMM.L_FIXED_RATE_PLAN_REF\n" +
                        "WHERE rate_plan_ref_eff_dt<= ipshare_ofccplv.cprof_d_period_dates_ref.PERIOD\n" +
                        ") AS ref\n" +
                        "SET accs_fee = REF.accs_fee,\n" +
                        "SVC_TYPE = REF.prod_grp_lvl_1,\n" +
                        "rate_plan_lvl3 = REF.rate_plan_lvl_3,\n" +
                        "prod_grp_lvl3 = REF.prod_grp_lvl_2\n" +
                        "WHERE b_rate_plan.svc_type IS NULL",
                "Tables:\n" +
                        "b_rate_plan\n" +
                        "ipshare_ofccplv.cprof_d_period_dates_ref\n" +
                        "SPCOMM.L_FIXED_RATE_PLAN_REF\n" +
                        "\nFields:\n" +
                        "b_rate_plan.accs_fee\n" +
                        "b_rate_plan.prod_grp_lvl3\n" +
                        "b_rate_plan.rate_plan_lvl3\n" +
                        "b_rate_plan.SVC_TYPE\n" +
                        "ipshare_ofccplv.cprof_d_period_dates_ref.PERIOD\n" +
                        "SPCOMM.L_FIXED_RATE_PLAN_REF.*\n" +
                        "SPCOMM.L_FIXED_RATE_PLAN_REF.accs_fee\n" +
                        "SPCOMM.L_FIXED_RATE_PLAN_REF.prod_grp_lvl_1\n" +
                        "SPCOMM.L_FIXED_RATE_PLAN_REF.prod_grp_lvl_2\n" +
                        "SPCOMM.L_FIXED_RATE_PLAN_REF.rate_plan_lvl_3\n" +
                        "SPCOMM.L_FIXED_RATE_PLAN_REF.rate_plan_ref_eff_dt");
    }

}
