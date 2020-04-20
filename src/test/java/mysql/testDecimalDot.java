package mysql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TConstant;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

import static gudusoft.gsqlparser.EExpressionType.unary_plus_t;

public class testDecimalDot extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "select t.84b4ffb8315895bd from t;";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn resultColumn = select.getResultColumnList().getResultColumn(0);
        TExpression expression =resultColumn.getExpr();
        TObjectName objectName = expression.getObjectOperand();
        assertTrue(objectName.getColumnNameOnly().equalsIgnoreCase("84b4ffb8315895bd"));
    }

    public void test2(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "select +.89;";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn resultColumn = select.getResultColumnList().getResultColumn(0);
        TExpression expression =resultColumn.getExpr();
        assertTrue(expression.getExpressionType() == unary_plus_t);
        TExpression rightExpr  = expression.getRightOperand();
        TConstant constant = rightExpr.getConstantOperand();
        assertTrue(constant.toString().equalsIgnoreCase(".89"));
    }

    public void test3(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "select .84b4ffb8315895bd from t;";
        assertTrue(sqlparser.parse() == 0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn resultColumn = select.getResultColumnList().getResultColumn(0);
        TExpression expression =resultColumn.getExpr();

        TConstant constant = expression.getConstantOperand();
        assertTrue(constant.toString().equalsIgnoreCase(".84"));

        assertTrue(resultColumn.getAliasClause().toString().equalsIgnoreCase("b4ffb8315895bd"));
    }

}
