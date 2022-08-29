package common;
/*
 * Date: 12-9-11
 */

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.mssql.TMssqlSetRowCount;
import junit.framework.TestCase;

public class testOperator extends TestCase {

        public void testNotEqual(){
        checkNotEqualCode(new TGSqlParser(EDbVendor.dbvoracle),"!=");
        checkNotEqualCode(new TGSqlParser(EDbVendor.dbvmssql),"!=");
        checkNotEqualCode(new TGSqlParser(EDbVendor.dbvmysql),"!=");
        checkNotEqualCode(new TGSqlParser(EDbVendor.dbvdb2),"!=");
        checkNotEqualCode(new TGSqlParser(EDbVendor.dbvpostgresql),"!=");
        checkNotEqualCode(new TGSqlParser(EDbVendor.dbvsybase),"!=");
        checkNotEqualCode(new TGSqlParser(EDbVendor.dbvteradata),"!=");
        checkNotEqualCode(new TGSqlParser(EDbVendor.dbvnetezza),"!=");
    }

    void checkNotEqualCode( TGSqlParser sqlparser,String op){
        sqlparser.sqltext = "select * from dual where 1"+op+"1";
        sqlparser.parse();
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression condition = select.getWhereClause().getCondition();
        assertTrue(condition.getOperatorToken().tokencode == TBaseType.not_equal);
    }


    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "select *\n" +
                "from T\n" +
                "where t.f in (12,3) and t.g not in (2,3)";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.getSqlstatements().get(0);
        TExpression expression = selectSqlStatement.getWhereClause().getCondition();
        TExpression left = expression.getLeftOperand();
        TExpression right = expression.getRightOperand();
        assertTrue(left.getExpressionType() == EExpressionType.in_t);
        assertTrue(left.getNotToken() == null);
        assertTrue(left.getOperatorToken().toString().equalsIgnoreCase("in"));

        assertTrue(right.getExpressionType() == EExpressionType.in_t);
        assertTrue(right.getNotToken() != null);
        assertTrue(right.getOperatorToken().toString().equalsIgnoreCase("in"));
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext = "select *\n" +
                "from T\n" +
                "where t.f like 'abc%' and t.g not like 'abc%'";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement)sqlparser.getSqlstatements().get(0);
        TExpression expression = selectSqlStatement.getWhereClause().getCondition();
        TExpression left = expression.getLeftOperand();
        TExpression right = expression.getRightOperand();
        assertTrue(left.getExpressionType() == EExpressionType.pattern_matching_t);
        assertTrue(left.getNotToken() == null);
        assertTrue(left.getOperatorToken().toString().equalsIgnoreCase("like"));

        assertTrue(right.getExpressionType() == EExpressionType.pattern_matching_t);
        assertTrue(right.getNotToken() != null);
        assertTrue(right.getOperatorToken().toString().equalsIgnoreCase("like"));
    }


}
