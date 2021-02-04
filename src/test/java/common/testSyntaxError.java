package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TMergeSqlStatement;
import junit.framework.TestCase;

public class testSyntaxError extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvbigquery);
        sqlparser.sqltext = "MERGE dataset.DetailedInventory T\n" +
                "USING dataset.Inventory S\n" +
                "ON T.product = S.product\n" +
                "WHEN NOT MATCHED AND quantity < 20 THEN\n" +
                "  INSERT(product, quantity, supply_constrained, comments)\n" +
                "  VALUES(product, quantity, true, ARRAY<STRUCT<created DATE, comment STRING>>[(DATE('2016-01-01'), 'comment1')])\n" +
                "WHEN NOT MATCHED THEN\n" +
                "  INSERT(product, quantity, supply_constrained)\n" +
                "  VALUES(product, quantity, false)\n" +
                ";";
        assertTrue(sqlparser.parse() == 0);
        TMergeSqlStatement mergeSqlStatement = (TMergeSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(mergeSqlStatement.getErrorCount() == 0);
        assertTrue(mergeSqlStatement.getSyntaxErrors().size() == 0);
        assertTrue(mergeSqlStatement.getSyntaxHints().size() == 1);
    }
}
