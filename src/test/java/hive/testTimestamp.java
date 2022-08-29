package hive;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.EHiveInsertType;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testTimestamp extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvhive);
        sqlparser.sqltext ="SELECT CURRENT_TIMESTAMP() , CURRENT_TIMESTAMP,CURRENT_DATE,CURRENT_DATE()";
        assertTrue(sqlparser.parse() == 0);

        assertTrue(sqlparser.sqlstatements.get(0).sqlstatementtype == ESqlStatementType.sstselect);

        TSelectSqlStatement select  = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);

        TResultColumn resultColumn0 = select.getResultColumnList().getResultColumn(0);
        assertTrue(resultColumn0.getExpr().getExpressionType() == EExpressionType.function_t);

        TResultColumn resultColumn1 = select.getResultColumnList().getResultColumn(0);
        assertTrue(resultColumn1.getExpr().getExpressionType() == EExpressionType.function_t);

        TResultColumn resultColumn2 = select.getResultColumnList().getResultColumn(0);
        assertTrue(resultColumn2.getExpr().getExpressionType() == EExpressionType.function_t);

        TResultColumn resultColumn3 = select.getResultColumnList().getResultColumn(0);
        assertTrue(resultColumn3.getExpr().getExpressionType() == EExpressionType.function_t);

    }
}
