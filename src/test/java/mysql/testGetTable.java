package mysql;
/*
 * Date: 14-1-10
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TTableList;
import junit.framework.TestCase;

public class testGetTable extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
                        sqlparser.sqltext =
                                        "                       SELECT * FROM (" +
                                        "                               SELECT tax_rates.* FROM" +
                                        "                                       wp_woocommerce_tax_rates as tax_rates" +
                                        "                               LEFT OUTER JOIN" +
                                        "                                       wp_woocommerce_tax_rate_locations as locations ON tax_rates.tax_rate_id = locations.tax_rate_id" +
                                        "                               LEFT OUTER JOIN" +
                                        "                                       wp_woocommerce_tax_rate_locations as locations2 ON tax_rates.tax_rate_id = locations2.tax_rate_id" +
                                        "                               WHERE" +
                                        "                                       tax_rate_country IN ( 'GB', '' )" +
                                        "                                       AND tax_rate_state IN ( '', '' )" +
                                        "                                       AND tax_rate_class = ''" +
                                        "                                       AND" +
                                        "                                       (" +
                                        "                                               (" +
                                        "                                                       locations.location_type IS NULL" +
                                        "                                               )" +
                                        "                                               OR" +
                                        "                                               (" +
                                        "                                                       locations.location_type = 'postcode'" +
                                        "                                                       AND locations.location_code IN ('*','')" +
                                        "                                                       AND locations2.location_type = 'city'" +
                                        "                                                       AND locations2.location_code = ''" +
                                        "                                               )" +
                                        "                                               OR" +
                                        "                                               (" +
                                        "                                                       locations.location_type = 'postcode'" +
                                        "                                                       AND locations.location_code IN ('*','')" +
                                        "                                                       AND 0 = (" +
                                        "                                                               SELECT COUNT(*) FROM wp_woocommerce_tax_rate_locations as sublocations" +
                                        "                                                               WHERE sublocations.location_type = 'city'" +
                                        "                                                               AND sublocations.tax_rate_id = tax_rates.tax_rate_id" +
                                        "                                                       )" +
                                        "                                               )" +
                                        "                                               OR" +
                                        "                                               (" +
                                        "                                                       locations.location_type = 'city'" +
                                        "                                                       AND locations.location_code = ''" +
                                        "                                                       AND 0 = (" +
                                        "                                                               SELECT COUNT(*) FROM wp_woocommerce_tax_rate_locations as sublocations" +
                                        "                                                               WHERE sublocations.location_type = 'postcode'" +
                                        "                                                               AND sublocations.tax_rate_id = tax_rates.tax_rate_id" +
                                        "                                                       )" +
                                        "                                               )" +
                                        "                                       )" +
                                        "                               GROUP BY" +
                                        "                                       tax_rate_id" +
                                        "                               ORDER BY" +
                                        "                                       tax_rate_priority, tax_rate_order" +
                                        "                       ) as ordered_taxes" +
                                        "                       GROUP BY" +
                                        "                               tax_rate_priority";

                        int ret = sqlparser.parse();

                        if (ret == 0) {
                                StringBuffer tables = new StringBuffer();
                                TTableList tableList = sqlparser.sqlstatements.get(0).tables;
                                for (int j = 0; j < tableList.size(); j++) {
                                        TTable table = tableList.getTable(j);
                                        switch (table.getTableType()){
                                            case objectname:
                                                //System.out.println("Table = " + table.getName());
                                                break;
                                            case subquery:
                                                //System.out.println(table.getSubquery().toString());
                                                break;
                                            default:
                                                break;
                                        }
                                }
                        }

    }

}
