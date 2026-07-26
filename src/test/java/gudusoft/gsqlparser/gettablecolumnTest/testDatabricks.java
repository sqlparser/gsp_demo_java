package gudusoft.gsqlparser.gettablecolumnTest;

import gudusoft.gsqlparser.util.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TBaseType;
import junit.framework.TestCase;

public class testDatabricks extends TestCase {

    static void doTest(String inputQuery, String desireResult){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvdatabricks);
        getTableColumn.isConsole = false;
        getTableColumn.showTableEffect = false;
        getTableColumn.showColumnLocation = false;
        getTableColumn.showTreeStructure = false;
        getTableColumn.showDatatype = true;
        getTableColumn.listStarColumn = true;
        getTableColumn.runText(inputQuery);
//       System.out.println(inputQuery);
//        System.out.println(getTableColumn.outList.toString().trim());
//        System.out.println(desireResult);
        assertTrue(TBaseType.compareStringsLineByLine(
                getTableColumn.outList.toString().trim(),
                desireResult));
        //assertTrue(getTableColumn.outList.toString().trim().equalsIgnoreCase(desireResult));
    }

    public  void test2() {
        doTest("CREATE OR REPLACE TABLE cde_dev.fct_sell_thru USING delta COMMENT 'Our sell through fact table. This is the central, common fact table for sell thru from all sources. TODO: Add tests for: model in model dim table. cust. transaction_date is timestamp.' \n" +
                        "AS WITH most_recent\n" +
                        "\tAS (SELECT   cust_rec_key,\n" +
                        "\t\t\t\t MAX(uploaded_at) AS uploaded_at\n" +
                        "\t\tFROM     cde_dev.fct_sell_thru_all\n" +
                        "\t\tGROUP BY cust_rec_key)  \n" +
                        " SELECT DISTINCT customer,\n" +
                        "\t\t\t\t uploaded_at,\n" +
                        "\t\t\t\t file_path,\n" +
                        "\t\t\t\t upc,\n" +
                        "\t\t\t\t customer_model,\n" +
                        "\t\t\t\t customer_sony_model,\n" +
                        "\t\t\t\t model,\n" +
                        "\t\t\t\t transaction_start_date,\n" +
                        "\t\t\t\t transaction_end_date,\n" +
                        "\t\t\t\t loc,\n" +
                        "\t\t\t\t qty_sold,\n" +
                        "\t\t\t\t qty_in_bond_non_sellable,\n" +
                        "\t\t\t\t qty_in_transtit_btw_cust_locations,\n" +
                        "\t\t\t\t qty_oh_available,\n" +
                        "\t\t\t\t qty_oh_non_sellable,\n" +
                        "\t\t\t\t qty_on_order,\n" +
                        "\t\t\t\t qty_returned_by_customer,\n" +
                        "\t\t\t\t qty_transferred,\n" +
                        "\t\t\t\t qty_withdrawn_from_wh_inv,\n" +
                        "\t\t\t\t cust_rec_key\n" +
                        " FROM   (SELECT a.*\n" +
                        "\t\t FROM   cde_dev.fct_sell_thru_all a\n" +
                        "\t\t\t\tINNER JOIN most_recent r\n" +
                        "\t\t\t\tON r.cust_rec_key = a.cust_rec_key\n" +
                        "\t\t\t\t   AND r.uploaded_at = a.uploaded_at)\n" +
                        " WHERE  deleted IS NOT TRUE"
                ,
                "Tables:\n" +
                        "cde_dev.fct_sell_thru\n" +
                        "cde_dev.fct_sell_thru_all\n" +
                        "\n" +
                        "Fields:\n" +
                        "cde_dev.fct_sell_thru.cust_rec_key\n" +
                        "cde_dev.fct_sell_thru.customer\n" +
                        "cde_dev.fct_sell_thru.customer_model\n" +
                        "cde_dev.fct_sell_thru.customer_sony_model\n" +
                        "cde_dev.fct_sell_thru.file_path\n" +
                        "cde_dev.fct_sell_thru.loc\n" +
                        "cde_dev.fct_sell_thru.model\n" +
                        "cde_dev.fct_sell_thru.qty_in_bond_non_sellable\n" +
                        "cde_dev.fct_sell_thru.qty_in_transtit_btw_cust_locations\n" +
                        "cde_dev.fct_sell_thru.qty_oh_available\n" +
                        "cde_dev.fct_sell_thru.qty_oh_non_sellable\n" +
                        "cde_dev.fct_sell_thru.qty_on_order\n" +
                        "cde_dev.fct_sell_thru.qty_returned_by_customer\n" +
                        "cde_dev.fct_sell_thru.qty_sold\n" +
                        "cde_dev.fct_sell_thru.qty_transferred\n" +
                        "cde_dev.fct_sell_thru.qty_withdrawn_from_wh_inv\n" +
                        "cde_dev.fct_sell_thru.transaction_end_date\n" +
                        "cde_dev.fct_sell_thru.transaction_start_date\n" +
                        "cde_dev.fct_sell_thru.upc\n" +
                        "cde_dev.fct_sell_thru.uploaded_at\n" +
                        "cde_dev.fct_sell_thru_all.*\n" +
                        "cde_dev.fct_sell_thru_all.cust_rec_key\n" +
                        "cde_dev.fct_sell_thru_all.customer\n" +
                        "cde_dev.fct_sell_thru_all.customer_model\n" +
                        "cde_dev.fct_sell_thru_all.customer_sony_model\n" +
                        "cde_dev.fct_sell_thru_all.deleted\n" +
                        "cde_dev.fct_sell_thru_all.file_path\n" +
                        "cde_dev.fct_sell_thru_all.loc\n" +
                        "cde_dev.fct_sell_thru_all.model\n" +
                        "cde_dev.fct_sell_thru_all.qty_in_bond_non_sellable\n" +
                        "cde_dev.fct_sell_thru_all.qty_in_transtit_btw_cust_locations\n" +
                        "cde_dev.fct_sell_thru_all.qty_oh_available\n" +
                        "cde_dev.fct_sell_thru_all.qty_oh_non_sellable\n" +
                        "cde_dev.fct_sell_thru_all.qty_on_order\n" +
                        "cde_dev.fct_sell_thru_all.qty_returned_by_customer\n" +
                        "cde_dev.fct_sell_thru_all.qty_sold\n" +
                        "cde_dev.fct_sell_thru_all.qty_transferred\n" +
                        "cde_dev.fct_sell_thru_all.qty_withdrawn_from_wh_inv\n" +
                        "cde_dev.fct_sell_thru_all.transaction_end_date\n" +
                        "cde_dev.fct_sell_thru_all.transaction_start_date\n" +
                        "cde_dev.fct_sell_thru_all.upc\n" +
                        "cde_dev.fct_sell_thru_all.uploaded_at");
    }

    public  void test1() {
        doTest("CREATE TABLE `kalyan-DB.test-schema.t1` AS SELECT `t2`.`c1` as CC1, `t2`.`c2` as CC2 FROM `kalyan-DB`.`test-schema`.`t2` T2;"
                        ,
                        "Tables:\n" +
                        "`kalyan-DB.test-schema.t1`\n" +
                        "`kalyan-DB`.`test-schema`.`t2`\n" +
                        "\n" +
                        "Fields:\n" +
                        "`kalyan-DB.test-schema.t1`.CC1\n" +
                        "`kalyan-DB.test-schema.t1`.CC2\n" +
                        "`kalyan-DB`.`test-schema`.`t2`.`c1`\n" +
                        "`kalyan-DB`.`test-schema`.`t2`.`c2`");
    }
}
