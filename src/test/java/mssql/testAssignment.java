package mssql;
/*
 * Date: 14-10-21
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testAssignment extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "SELECT\n" +
                "   @var1 = varA,\n" +
                "   @var2= varB,\n" +
                "   @var3= varC\n" +
                "FROM @TestTable \n" +
                "WHERE Id = @id;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        //assertTrue(select.getSelectDistinct().getDistinctType() == TBaseType.dtDistinct);
        TExpression expr = select.getResultColumnList().getResultColumn(0).getExpr();
        assertTrue(expr.getExpressionType() == EExpressionType.assignment_t);
    }

    public void test2(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "SELECT\n" +
                "CUSTOMER.ADDRESS AS CUST_ADDRESS1,\n" +
                "CUSTOMER.ADDRESS AS [CUST_ADDRESS2],\n" +
                "CUSTOMER.ADDRESS AS \"CUST_ADDRESS3\",\n" +
                "CUSTOMER.ADDRESS AS 'CUST_ADDRESS4',\n" +
                "CUST_ADDRESS5 = CUSTOMER.ADDRESS\n" +
                "FROM CUSTOMER";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn resultColumn = select.getResultColumnList().getResultColumn(0);
        assertTrue(resultColumn.getExpr().getObjectOperand().toString().equalsIgnoreCase("CUSTOMER.ADDRESS"));
        assertTrue(resultColumn.getAliasClause().toString().equalsIgnoreCase("CUST_ADDRESS1"));

        resultColumn = select.getResultColumnList().getResultColumn(4);
        assertTrue(resultColumn.getExpr().toString().equalsIgnoreCase("CUST_ADDRESS5 = CUSTOMER.ADDRESS"));
        assertTrue(resultColumn.getAliasClause().toString().equalsIgnoreCase("CUST_ADDRESS5"));
        assertTrue(resultColumn.getColumnFullname().toString().equalsIgnoreCase("CUSTOMER.ADDRESS"));
        assertTrue(resultColumn.getColumnNameOnly().equalsIgnoreCase("ADDRESS"));
    }

}
