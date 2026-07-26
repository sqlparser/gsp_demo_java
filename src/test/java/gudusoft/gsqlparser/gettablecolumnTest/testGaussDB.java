package gudusoft.gsqlparser.gettablecolumnTest;

import gudusoft.gsqlparser.util.TGetTableColumn;
import gudusoft.gsqlparser.EDbVendor;
import junit.framework.TestCase;

public class testGaussDB extends TestCase {

    static void doTest(String inputQuery, String desireResult){
        TGetTableColumn getTableColumn = new TGetTableColumn(EDbVendor.dbvgaussdb);
        getTableColumn.isConsole = false;
        getTableColumn.showTableEffect = false;
        getTableColumn.showColumnLocation = false;
        getTableColumn.showTreeStructure = false;
        getTableColumn.showDatatype = true;
        getTableColumn.runText(inputQuery);
        // System.out.println(getTableColumn.outList.toString().trim());
        assertTrue(getTableColumn.outList.toString().trim().equalsIgnoreCase(desireResult));
    }

    public  void test0() {
        assertTrue(true);
    }

    public  void test1() {
        doTest("select\n" +
                        " RTAC_PRODLINE_CN,\n" +
                        " RESOLUTION_CLASS,\n" +
                        " substr(esca_pl_begin_date, 0, 7) as year_month,\n" +
                        " sum(timestampdiff(day, to_date(esca_pl_begin_date, 'YYYY-MM-DD HH24:MI:SS'), to_date(esca_solution_date, 'YYYY-MM-DD HH24:MI:SS'))* 24)/ count(esca_num) as esca_provide_day\n" +
                        " from datafab004.rtac_product_view_origin_d\n" +
                        "where\n" +
                        " creation_date >= '2020-01-01'\n" +
                        " and esc_to_manager_flag = 'Y'\n" +
                        " and esca_pl_begin_date is not null\n" +
                        " and esca_solution_date is not null\n" +
                        "group by\n" +
                        " RTAC_PRODLINE_CN,\n" +
                        " substr(esca_pl_begin_date, 0, 7),\n" +
                        " RESOLUTION_CLASS",
                "Tables:\n" +
                        "datafab004.rtac_product_view_origin_d\n" +
                        "\n" +
                        "Fields:\n" +
                        "datafab004.rtac_product_view_origin_d.creation_date\n" +
                        "datafab004.rtac_product_view_origin_d.esc_to_manager_flag\n" +
                        "datafab004.rtac_product_view_origin_d.esca_num\n" +
                        "datafab004.rtac_product_view_origin_d.esca_pl_begin_date\n" +
                        "datafab004.rtac_product_view_origin_d.esca_solution_date\n" +
                        "datafab004.rtac_product_view_origin_d.RESOLUTION_CLASS\n" +
                        "datafab004.rtac_product_view_origin_d.RTAC_PRODLINE_CN");
    }

}
