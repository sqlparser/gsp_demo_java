package databricks;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testDatatypeCast extends TestCase {
    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvdatabricks);

        sqlparser.sqltext = " SELECT bigint(current_timestamp);";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement selectSqlStatement = (TSelectSqlStatement) sqlparser.sqlstatements.get (0);
        TExpression expression = selectSqlStatement.getResultColumnList().getResultColumn(0).getExpr();
        assertTrue(expression.getExpressionType() == EExpressionType.typecast_datatype_t);
        assertTrue(expression.getTypeName().getDataType() == EDataType.bigint_t);
        assertTrue(expression.getLeftOperand().toString().equalsIgnoreCase("current_timestamp"));
        //System.out.println(expression.getLeftOperand().getExpressionType());
    }
}
