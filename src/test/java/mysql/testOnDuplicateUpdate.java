package mysql;
/*
 * Date: 12-10-19
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import junit.framework.TestCase;

public class testOnDuplicateUpdate extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "insert into mydb.mytable (c1,c2,c3) select 8,10,20 from mydb.t1 on duplicate key update c1=8";
        assertTrue(sqlparser.parse() == 0);

        TInsertSqlStatement insertSqlStatement = (TInsertSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumnList resultColumnList = insertSqlStatement.getOnDuplicateKeyUpdate();
        TResultColumn resultColumn = resultColumnList.getResultColumn(0);

        assertTrue(resultColumn.getExpr().getExpressionType() == EExpressionType.assignment_t);
        assertTrue(resultColumn.getExpr().getLeftOperand().toString().equalsIgnoreCase("c1"));
        assertTrue(resultColumn.getExpr().getRightOperand().toString().equalsIgnoreCase("8"));
    }

}
