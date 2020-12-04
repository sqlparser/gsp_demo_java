package panayainc;
/*
 * Date: 12-2-8
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TExpressionList;
import gudusoft.gsqlparser.nodes.TGroupByItemList;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testGroupByItemWithParenthesis extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "select \n" +
                "  iim.item_no ITEM,\n" +
                "  sum(gmd.plan_qty-gmd.actual_qty) calc\n" +
                " from\n" +
                "  gme_batch_header gbh,\n" +
                "  gme_material_details gmd,\n" +
                "  ic_item_mst_b iim,\n" +
                "  fm_rout_hdr frh\n" +
                " where\n" +
                "  gbh.batch_type = 0\n" +
                " group by (iim.item_no,iim.item_desc1,gbh.plant_code,frh.routing_no)";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TGroupByItemList groupByItemList  = selectSqlStatement.getGroupByClause().getItems();
//        for(int i=0;i<groupByItemList.size();i++){
//            System.out.println(groupByItemList.getGroupByItem(i).toString());
//        }

        TExpression groupByExpr = groupByItemList.getGroupByItem(0).getExpr();
        assertTrue(groupByExpr.getExpressionType() == EExpressionType.list_t);


        assertTrue(groupByExpr.getExprList().size() == 4);
        assertTrue(groupByExpr.getExprList().getExpression(0).toString().equalsIgnoreCase("iim.item_no"));
        assertTrue(groupByExpr.getExprList().getExpression(1).toString().equalsIgnoreCase("iim.item_desc1"));
        assertTrue(groupByExpr.getExprList().getExpression(2).toString().equalsIgnoreCase("gbh.plant_code"));
        assertTrue(groupByExpr.getExprList().getExpression(3).toString().equalsIgnoreCase("frh.routing_no"));
    }

}
