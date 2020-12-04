package postgresql;
/*
 * Date: 11-5-24
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TOrderBy;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testCollationExpression extends TestCase {

    public void testTypedConst(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvpostgresql);
        sqlparser.sqltext = "SELECT a, b, c FROM tbl WHERE a>1 ORDER BY a COLLATE \"C\"";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TOrderBy sortClause = select.getOrderbyClause();
        TExpression expression = sortClause.getItems().getOrderByItem(0).getSortKey();

        assertTrue(expression.getExpressionType() == EExpressionType.collate_t);
        assertTrue(expression.getLeftOperand().toString().equalsIgnoreCase("a"));
        assertTrue(expression.getRightOperand().toString().equalsIgnoreCase("\"C\""));

        //System.out.println(.toString());
        sqlparser.sqltext = "SELECT * FROM tbl WHERE a > 'foo' COLLATE \"C\"";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select1 = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression condition = select1.getWhereClause().getCondition();
        assertTrue(condition.getLeftOperand().toString().equalsIgnoreCase("a"));
        assertTrue(condition.getRightOperand().toString().equalsIgnoreCase("'foo' COLLATE \"C\""));
        //System.out.println(condition.getRightOperand().toString());

        sqlparser.sqltext = "SELECT * FROM tbl WHERE a COLLATE \"C\" > 'foo'";
        assertTrue(sqlparser.parse() == 0);

    }

}
