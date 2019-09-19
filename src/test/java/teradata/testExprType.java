package test.teradata;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testExprType extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "SELECT\n" +
                "           USER_ID\n" +
                "           ,MAX(CASE WHEN RANK_ID = 1 THEN BHVR_TXT ELSE '' END)||\n" +
                "            MAX(CASE WHEN RANK_ID = 2 THEN ','||BHVR_TXT ELSE '' END)||\n" +
                "            MAX(CASE WHEN RANK_ID = 3 THEN ','||BHVR_TXT ELSE '' END)||\n" +
                "            MAX(CASE WHEN RANK_ID = 4 THEN ','||BHVR_TXT ELSE '' END)||\n" +
                "            MAX(CASE WHEN RANK_ID = 5 THEN ','||BHVR_TXT ELSE '' END)||\n" +
                "            MAX(CASE WHEN RANK_ID = 6 THEN ','||BHVR_TXT ELSE '' END)||\n" +
                "            MAX(CASE WHEN RANK_ID = 7 THEN ','||BHVR_TXT ELSE '' END)||\n" +
                "            MAX(CASE WHEN RANK_ID = 8 THEN ','||BHVR_TXT ELSE '' END)||\n" +
                "            MAX(CASE WHEN RANK_ID = 9 THEN ','||BHVR_TXT ELSE '' END)||\n" +
                "            MAX(CASE WHEN RANK_ID = 10 THEN ','||BHVR_TXT ELSE '' END) bhvr_txt\n" +
                "         FROM\n" +
                "         (\n" +
                "           SEL slr_id USER_ID\n" +
                "              ,COALESCE(TRIM(BHVR_ID(FORMAT 'Z(17)9')), '')\n" +
                "               ||':'\n" +
                "               ||COALESCE(TRIM(priority(FORMAT 'Z(17)9.99')), '')\n" +
                "               AS BHVR_TXT\n" +
                "              ,ROW_NUMBER() OVER(PARTITION BY slr_id ORDER BY priority DESC) rank_id\n" +
                "           FROM working.b2p_seller_coaching_us clv\n" +
                "             QUALIFY rank_id<=10\n" +
                "         ) x\n" +
                "         GROUP BY 1";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TSelectSqlStatement subquery = select.tables.getTable(0).subquery;
        TExpression expr = subquery.getResultColumnList().getResultColumn(1).getExpr();
        expr = expr.getLeftOperand().getLeftOperand();
        TFunctionCall func = expr.getFunctionCall();
        expr = func.getArgs().getExpression(0);
        func = expr.getFunctionCall();
        expr = func.getTrimArgument().getStringExpression();
        assertTrue(expr.getExpressionType() == EExpressionType.simple_object_name_t);
        assertTrue(expr.getObjectOperand().toString().equalsIgnoreCase("BHVR_ID"));
    }

}