package mysql;


import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testDate extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "select date from foo;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select  = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column = select.getResultColumnList().getResultColumn(0);
        assertTrue(column.getExpr().getExpressionType() == EExpressionType.simple_object_name_t);
        TObjectName objectName = column.getExpr().getObjectOperand();
        assertTrue(objectName.getColumnToken().toString().endsWith("date"));
    }
}