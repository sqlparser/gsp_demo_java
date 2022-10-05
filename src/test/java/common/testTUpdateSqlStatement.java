package common;
/*
 * Date: 2010-11-2
 * Time: 16:23:08
 */

import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;

public class testTUpdateSqlStatement extends TestCase {

    public void testSQLServerUpdateTableAlias(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "UPDATE p SET p.pname = u.uname FROM PTEST p INNER JOIN UTEST u on p.id = u.id";
        assertTrue(sqlparser.parse() == 0);
        TUpdateSqlStatement update = (TUpdateSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(update.getTargetTable().toString().equalsIgnoreCase("PTEST"));
    }

    public void testSetColumns(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "update a set a.c = b";
        assertTrue(sqlparser.parse() == 0);
        TUpdateSqlStatement update = (TUpdateSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn column = update.getResultColumnList().getResultColumn(0);
        TExpression expr = column.getExpr();
        assertTrue(expr.getExpressionType() == EExpressionType.assignment_t);

       assertTrue(expr.getLeftOperand().toString().equalsIgnoreCase("a.c"));
        assertTrue(expr.getRightOperand().toString().equalsIgnoreCase("b"));
    }

    public void testMySQLUpdateToken(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);
        sqlparser.sqltext = "UPDATE DevisFE2000 , DevisFE2000Tmp \n" +
                "Set DevisFE2000.PREUnitaireHT = DevisFE2000Tmp.PREUnitaireHT ";
        boolean b = sqlparser.parse() == 0;
        assertTrue(sqlparser.getErrormessage(),b);

        TUpdateSqlStatement updatestmt = (TUpdateSqlStatement)sqlparser.sqlstatements.get(0);
        assertTrue(updatestmt.getUpdateToken().toString().equalsIgnoreCase("update"));
    }

    public void testSetColumns3(){

         TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvgreenplum);
         sqlparser.sqltext = "update _target t1\n" +
                 "set t1.col1 = t1.col2\n";

         assertTrue(sqlparser.parse() == 0);
         TUpdateSqlStatement update = (TUpdateSqlStatement)sqlparser.sqlstatements.get(0);
         TResultColumn column = update.getResultColumnList().getResultColumn(0);
         TExpression expr = column.getExpr();
         assertTrue(expr.getExpressionType() == EExpressionType.assignment_t);

        assertTrue(expr.getLeftOperand().toString().equalsIgnoreCase("t1.col1"));
         assertTrue(expr.getRightOperand().toString().equalsIgnoreCase("t1.col2"));
     }


    public void testTargetTable(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "UPDATE TABLE(SELECT h.people FROM hr_info h WHERE h.department_id = 280) p\n" +
                "SET p.salary = p.salary + 100;";
        assertTrue(sqlparser.parse() == 0);
        TUpdateSqlStatement update = (TUpdateSqlStatement)sqlparser.sqlstatements.get(0);
        TTable table = update.tables.getTable(0);
        assertTrue(table.getTableType() == ETableSource.subquery);
        TSelectSqlStatement subquery = table.getSubquery();
        assertTrue(subquery.getResultColumnList().getResultColumn(0).toString().equalsIgnoreCase("h.people"));
    }

}
