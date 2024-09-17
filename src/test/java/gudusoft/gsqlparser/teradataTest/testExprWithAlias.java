package gudusoft.gsqlparser.teradataTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testExprWithAlias extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "    SELECT *\n" +
                "    FROM test\n" +
                "    where (\n" +
                "COALESCE(SOR_SECR_POOL_ID,'UNKNOWN') AS SOR_SECR_POOL_ID ,\n" +
                "COALESCE(SOR_SYS_ID,-999) AS SOR_SYS_ID ,\n" +
                "COALESCE(SECR_CNDT_RCV_CO_NO,'UNKNO') AS SECR_CNDT_RCV_CO_NO ,\n" +
                "COALESCE(SECR_CNDT_RCV_CC_NO,'UNKNOWN') AS SECR_CNDT_RCV_CC_NO ,\n" +
                "COALESCE(SECR_CNDT_RCV_DE,'UNKNOWN') AS SECR_CNDT_RCV_DE ,\n" +
                "COALESCE(SECR_PGM_ID,-999) AS SECR_PGM_ID ,\n" +
                "COALESCE(SECR_POOL_SEQ_NO,-999) AS SECR_POOL_SEQ_NO\n" +
                ")\n" +
                "NOT IN\n" +
                "(\n" +
                "SELECT\n" +
                "COALESCE(SOR_SECR_POOL_ID,'UNKNOWN') AS SOR_SECR_POOL_ID ,\n" +
                "COALESCE(SOR_SYS_ID,-999) AS SOR_SYS_ID ,\n" +
                "COALESCE(SECR_CNDT_RCV_CO_NO,'UNKNO') AS SECR_CNDT_RCV_CO_NO ,\n" +
                "COALESCE(SECR_CNDT_RCV_CC_NO,'UNKNOWN') AS SECR_CNDT_RCV_CC_NO ,\n" +
                "COALESCE(SECR_CNDT_RCV_DE,'UNKNOWN') AS SECR_CNDT_RCV_DE ,\n" +
                "COALESCE(SECR_PGM_ID,-999) AS SECR_PGM_ID ,\n" +
                "COALESCE(SECR_POOL_SEQ_NO,-999) AS SECR_POOL_SEQ_NO\n" +
                "FROM ENTR.SECR_PGM_POOL_CTL\n" +
                "WHERE\n" +
                "UPDT_LOAD_BTCH_ID IS NULL\n" +
                "AND SSFA_CLTRL_AS_OF_DT IS NOT NULL\n" +
                "AND (QUAL_MTG_AMT IS NULL AND UNQUAL_MTG_AMT IS NULL AND NON_MTG_AMT IS NULL)\n" +
                ")";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr1 = select.getWhereClause().getCondition();
        //System.out.print(expr1.getExpressionType());
        assertTrue(expr1.getExpressionType() == EExpressionType.in_t);
        TExpression leftexpr = expr1.getLeftOperand();
        assertTrue(leftexpr.getExpressionType() == EExpressionType.list_t);
        assertTrue(leftexpr.getExprList().size() == 7);
        TExpression expr2 = leftexpr.getExprList().getExpression(0);
        assertTrue(expr2.getExprAlias().toString().endsWith("SOR_SECR_POOL_ID"));
        assertTrue(expr2.getExpressionType() == EExpressionType.function_t);
        assertTrue(expr2.getFunctionCall().toString().endsWith("COALESCE(SOR_SECR_POOL_ID,'UNKNOWN')"));
    }

}
