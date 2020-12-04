package couchbase;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.couchbase.TKeyspaceRef;
import gudusoft.gsqlparser.stmt.TCreateIndexSqlStatement;
import junit.framework.TestCase;

public class testCreateIndex extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvcouchbase);

        sqlparser.sqltext = "CREATE INDEX iflight_stops \n" +
                "       ON `travel-sample` ( stops, DISTINCT ARRAY v.flight FOR v IN schedule END )\n" +
                "       WHERE type = \"route\" ;";

        assertTrue(sqlparser.parse() == 0);
        TCreateIndexSqlStatement createIndexSqlStatement = (TCreateIndexSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(createIndexSqlStatement.getIndexName().toString().equalsIgnoreCase("iflight_stops"));
        TKeyspaceRef keyspaceRef = createIndexSqlStatement.getKeyspaceRef();
        assertTrue(keyspaceRef.getKeyspace().toString().equalsIgnoreCase("`travel-sample`"));
        assertTrue(createIndexSqlStatement.getIndexTerms().size() == 2);
        TExpression indexWhere = createIndexSqlStatement.getIndexWhere();
        assertTrue(indexWhere.toString().equalsIgnoreCase("type = \"route\""));

    }
}
