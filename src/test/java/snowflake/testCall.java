package snowflake;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TCallStatement;
import junit.framework.TestCase;

public class testCall extends TestCase {
    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
        sqlparser.sqltext = "CALL stproc1(SELECT COUNT(*) FROM stproc_test_table1);";
        assertTrue(sqlparser.parse() == 0);

        TCallStatement callStatement = (TCallStatement)sqlparser.sqlstatements.get(0);
//        System.out.println(callStatement.getArgs().size());
        assertTrue(callStatement.getArgs().size() == 1);
        assertTrue(callStatement.getArgs().getExpression(0).getExpressionType() == EExpressionType.subquery_t);
    }
}
