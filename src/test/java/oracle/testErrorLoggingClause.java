package oracle;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.oracle.TErrorLoggingClause;
import gudusoft.gsqlparser.stmt.TMergeSqlStatement;
import junit.framework.TestCase;

public class testErrorLoggingClause extends TestCase {

    public void testMerge(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "merge into table1\n" +
                "using (select a, b from table2)\n" +
                "on (table1.a = table2.a)\n" +
                "when matched then update set table1.b = table2.p\n" +
                "LOG ERRORS INTO table3 (a) REJECT LIMIT 50 ;";
        assertTrue(sqlparser.parse() == 0);

        TMergeSqlStatement mergeSqlStatement = (TMergeSqlStatement)sqlparser.sqlstatements.get(0);
        TErrorLoggingClause errorLoggingClause = mergeSqlStatement.getErrorLoggingClause();
        assertTrue(errorLoggingClause.getTableName().toString().equalsIgnoreCase("table3"));
        assertTrue(errorLoggingClause.getSimpleExpression().getLeftOperand().toString().equalsIgnoreCase("a"));
        assertTrue(errorLoggingClause.getRejectLimitToken().toString().equalsIgnoreCase("50"));

   }

}
