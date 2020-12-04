package common;
/*
 * Date: 2010-10-18
 * Time: 16:11:32
 */

import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TSampleClause;
import gudusoft.gsqlparser.nodes.TWhenClauseItem;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class testTSampleClause extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT customer_id, age, income, marital_status, SAMPLEID\n" +
                "FROM customer_table\n" +
                "SAMPLE 0.6, 0.25, 0.15;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TSampleClause sampleClause = select.getSampleClause();
        assertTrue(sampleClause.getCount_fraction_description_list().size() == 3);
        assertTrue(sampleClause.getCount_fraction_description_list().getConstant(0).toString().equalsIgnoreCase("0.6"));
        assertTrue(sampleClause.getCount_fraction_description_list().getConstant(1).toString().equalsIgnoreCase("0.25"));
        assertTrue(sampleClause.getCount_fraction_description_list().getConstant(2).toString().equalsIgnoreCase("0.15"));
    }


    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT city, state, sampleid\n" +
                "FROM stores\n" +
                "SAMPLE RANDOMIZED ALLOCATION\n" +
                "WHEN state = 'WI' THEN 0.25, 0.5\n" +
                "WHEN state = 'CA' THEN 0.25, 0.25\n" +
                "END\n" +
                "ORDER BY 3;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TSampleClause sampleClause = select.getSampleClause();
        assertTrue(sampleClause.getWhenClauseItems().size() == 2);

        TWhenClauseItem item0 = sampleClause.getWhenClauseItems().getWhenClauseItem(0);
        TWhenClauseItem item1 = sampleClause.getWhenClauseItems().getWhenClauseItem(1);

        assertTrue(item0.getComparison_expr().toString().equalsIgnoreCase("state = 'WI'"));
        assertTrue(item0.getCount_fraction_description_list().getConstant(0).toString().equalsIgnoreCase("0.25"));
        assertTrue(item0.getCount_fraction_description_list().getConstant(1).toString().equalsIgnoreCase("0.5"));

        assertTrue(item1.getComparison_expr().toString().equalsIgnoreCase("state = 'CA'"));
        assertTrue(item1.getCount_fraction_description_list().getConstant(0).toString().equalsIgnoreCase("0.25"));
        assertTrue(item1.getCount_fraction_description_list().getConstant(1).toString().equalsIgnoreCase("0.25"));

        //assertTrue(sampleClause.getCount_fraction_description_list().size() == 3);
        //assertTrue(sampleClause.getCount_fraction_description_list().getConstant(0).toString().equalsIgnoreCase("0.6"));
        //assertTrue(sampleClause.getCount_fraction_description_list().getConstant(1).toString().equalsIgnoreCase("0.25"));
        //assertTrue(sampleClause.getCount_fraction_description_list().getConstant(2).toString().equalsIgnoreCase("0.15"));
    }

    public void test3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT city, state, sampleid\n" +
                "FROM stores\n" +
                "SAMPLE WITH REPLACEMENT RANDOMIZED ALLOCATION\n" +
                "WHEN state = 'WI' THEN 3\n" +
                "ELSE 2\n" +
                "END\n" +
                "ORDER BY 3;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TSampleClause sampleClause = select.getSampleClause();
        assertTrue(sampleClause.getWhenClauseItems().size() == 1);

        TWhenClauseItem item0 = sampleClause.getWhenClauseItems().getWhenClauseItem(0);
        assertTrue(item0.getComparison_expr().toString().equalsIgnoreCase("state = 'WI'"));
        assertTrue(item0.getCount_fraction_description_list().getConstant(0).toString().equalsIgnoreCase("3"));

        assertTrue(sampleClause.getCount_fraction_description_list().size() == 1);
        assertTrue(sampleClause.getCount_fraction_description_list().getConstant(0).toString().equalsIgnoreCase("2"));

    }

}
