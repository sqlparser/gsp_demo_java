package common;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TLimitClause;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testLimitClause extends TestCase {

    public void testFlattenExpr(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvredshift);
        sqlparser.sqltext = "SELECT   date_trunc('day', (dep_timestamp)::timestamp)::date as \"date\",\n" +
                "         COUNT(1)\n" +
                "FROM     flights\n" +
                "WHERE 1=1\n" +
                "AND   1=1\n" +
                "AND   1=1\n" +
                "AND   1=1\n" +
                "GROUP BY 1\n" +
                "ORDER BY 1\n" +
                "LIMIT 5000";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TLimitClause limitClause = select.getLimitClause();
        assertTrue(limitClause.getRow_count().toString().equalsIgnoreCase("5000"));

    }
}
