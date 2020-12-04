package couchbase;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TMultiTarget;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testInsert extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvcouchbase);

        sqlparser.sqltext = "INSERT INTO `travel-sample` (KEY UUID()) \n" +
                "    SELECT x.name, x.city, \"landmark_hotels\" AS type \n" +
                "      FROM `travel-sample` x \n" +
                "      WHERE x.type = \"hotel\" and x.city WITHIN \n" +
                "        ( SELECT DISTINCT t.city \n" +
                "            FROM `travel-sample` t \n" +
                "            WHERE t.type = \"landmark\" ) \n" +
                "      LIMIT 4 \n" +
                "RETURNING *;";

        assertTrue(sqlparser.parse() == 0);
        TInsertSqlStatement insertSqlStatement = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(insertSqlStatement.getTargetTable().toString().equalsIgnoreCase("`travel-sample`"));
        TSelectSqlStatement selectSqlStatement = insertSqlStatement.getSubQuery();
        //System.out.println(selectSqlStatement.tables.getTable(0).getTableName());
        assertTrue(selectSqlStatement.tables.getTable(0).getTableName().toString().equalsIgnoreCase("`travel-sample`"));
    }


    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvcouchbase);

        sqlparser.sqltext = "INSERT INTO `travel-sample` (KEY,VALUE) \n" +
                "  VALUES ( \"1025\", \n" +
                "            {     \"callsign\": \"MY-AIR\",\n" +
                "                  \"country\": \"United States\",\n" +
                "                  \"iata\": \"Z1\",\n" +
                "                  \"icao\": \"AQZ\",\n" +
                "                  \"id\": \"1011\",\n" +
                "                  \"name\": \"80-My Air\",\n" +
                "                  \"type\": \"airline\" \n" +
                "            } ) \n" +
                "RETURNING *;";

        assertTrue(sqlparser.parse() == 0);
        TInsertSqlStatement insertSqlStatement = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(insertSqlStatement.getTargetTable().toString().equalsIgnoreCase("`travel-sample`"));
        TMultiTarget multiTarget = insertSqlStatement.getValues().getMultiTarget(0);
        TResultColumn resultColumn0 = multiTarget.getColumnList().getResultColumn(0);
        TResultColumn resultColumn1 = multiTarget.getColumnList().getResultColumn(1);
        assertTrue(resultColumn0.getExpr().toString().equalsIgnoreCase("\"1025\""));
        TExpression expression  = resultColumn1.getExpr();
        assertTrue(expression.getExpressionType() == EExpressionType.objectConstruct_t);

    }
}
