package gudusoft.gsqlparser.teradataTest;
/*
 * Date: 12-9-26
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.teradata.TCollectColumnIndex;
import gudusoft.gsqlparser.stmt.teradata.TTeradataCollectStatistics;
import junit.framework.TestCase;

public class testCollectStatistics extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "COLLECT STATS COLUMN (id) on db1.tbl1;";
        assertTrue(sqlparser.parse() == 0);
        TTeradataCollectStatistics collectStatistics = (TTeradataCollectStatistics)sqlparser.sqlstatements.get(0);
        assertTrue(collectStatistics.getTableName().toString().equalsIgnoreCase("db1.tbl1"));

    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "Collect stats \n" +
                "index(member), \n" +
                "column(dateyymmdd)\n" +
                "On db.sampletable";
        assertTrue(sqlparser.parse() == 0);
        TTeradataCollectStatistics collectStatistics = (TTeradataCollectStatistics)sqlparser.sqlstatements.get(0);
        assertTrue(collectStatistics.getTableName().toString().equalsIgnoreCase("db.sampletable"));
        assertTrue(collectStatistics.getColumnIndexList().size() == 2);
        TCollectColumnIndex collectColumnIndex = collectStatistics.getColumnIndexList().get(0);
        assertTrue(collectColumnIndex.isIndex());
        assertTrue(collectColumnIndex.getColumnNameList().getObjectName(0).toString().equalsIgnoreCase("member"));
        collectColumnIndex = collectStatistics.getColumnIndexList().get(1);
        assertTrue(collectColumnIndex.isColumn());
        assertTrue(collectColumnIndex.getColumnNameList().getObjectName(0).toString().equalsIgnoreCase("dateyymmdd"));

    }


    public void test3(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "Collect stats dwh.stg_fact_example column(partition);";
        assertTrue(sqlparser.parse() == 0);
        TTeradataCollectStatistics collectStatistics = (TTeradataCollectStatistics)sqlparser.sqlstatements.get(0);
        assertTrue(collectStatistics.getTableName().toString().equalsIgnoreCase("dwh.stg_fact_example"));

        assertTrue(collectStatistics.getColumnIndexList().size() == 1);
        TCollectColumnIndex collectColumnIndex = collectStatistics.getColumnIndexList().get(0);
        assertTrue(collectColumnIndex.isColumn());
        assertTrue(collectColumnIndex.getColumnNameList().getObjectName(0).toString().equalsIgnoreCase("partition"));


    }


}
