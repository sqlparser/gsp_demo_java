package couchbase;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESqlStatementType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TExplainPlan;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import junit.framework.TestCase;

public class testExplain extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvcouchbase);

        sqlparser.sqltext = "EXPLAIN INSERT INTO `travel-sample` (key UUID(), value _country) \n" +
                "    SELECT _country FROM `travel-sample` _country \n" +
                "      WHERE type = \"airport\" AND airportname = \"Heathrow\";";

        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstExplain);
        TExplainPlan explainPlan = (TExplainPlan)sqlparser.sqlstatements.get(0);
        assertTrue(explainPlan.getStatement().sqlstatementtype == ESqlStatementType.sstinsert);
        TInsertSqlStatement insertSqlStatement = (TInsertSqlStatement)explainPlan.getStatement();
        assertTrue(insertSqlStatement.getTargetTable().getTableName().toString().equalsIgnoreCase("`travel-sample`"));

    }

    public void testPrepare1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvcouchbase);

        sqlparser.sqltext = "PREPARE \n" +
                "      INSERT INTO `travel-sample` (KEY, VALUE) \n" +
                "        VALUES ( $key, \n" +
                "                { \"type\" : \"airport\", \n" +
                "                  \"tz\" : \"India Standard Time\", \n" +
                "                  \"country\" : \"India\", \n" +
                "                  \"faa\" : $faa_code} )  \n" +
                "RETURNING *;";

        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstprepare);
        TExplainPlan explainPlan = (TExplainPlan)sqlparser.sqlstatements.get(0);
        assertTrue(explainPlan.getStatement().sqlstatementtype == ESqlStatementType.sstinsert);
        TInsertSqlStatement insertSqlStatement = (TInsertSqlStatement)explainPlan.getStatement();
        assertTrue(insertSqlStatement.getTargetTable().getTableName().toString().equalsIgnoreCase("`travel-sample`"));

    }

    public void testPrepare2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvcouchbase);

        sqlparser.sqltext = "PREPARE ins_india FROM \n" +
                "      INSERT INTO `travel-sample` (KEY, VALUE) \n" +
                "        VALUES ( $key, \n" +
                "                { \"type\" : \"airport\", \n" +
                "                  \"tz\" : \"India Standard Time\", \n" +
                "                  \"country\" : \"India\", \n" +
                "                  \"faa\" : $faa_code} )  \n" +
                "RETURNING *;";

        assertTrue(sqlparser.parse() == 0);
        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstprepare);
        TExplainPlan explainPlan = (TExplainPlan)sqlparser.sqlstatements.get(0);
        assertTrue(explainPlan.getStatement().sqlstatementtype == ESqlStatementType.sstinsert);
        TInsertSqlStatement insertSqlStatement = (TInsertSqlStatement)explainPlan.getStatement();
        assertTrue(insertSqlStatement.getTargetTable().getTableName().toString().equalsIgnoreCase("`travel-sample`"));

    }
}
