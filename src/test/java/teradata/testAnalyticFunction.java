package test.teradata;


import gudusoft.gsqlparser.EBoundaryType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ELimitRowType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testAnalyticFunction extends TestCase {

    public void test1(){

        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvteradata);
        sqlparser.sqltext = "Select max(employee_id) over (ROWS BETWEEN 1 FOLLOWING AND 2 following) as a from employee;";
        assertTrue(sqlparser.parse() == 0);
        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TResultColumn rc = select.getResultColumnList().getResultColumn(0);
        TExpression e = rc.getExpr();
        TFunctionCall functionCall = e.getFunctionCall();
        TWindowFrame windowFrame = functionCall.getWindowDef().getWindowFrame();
        assertTrue(windowFrame.getLimitRowType() == ELimitRowType.Rows);
        TWindowFrameBoundary startBoundary = windowFrame.getStartBoundary();
        assertTrue(startBoundary.getBoundaryType() == EBoundaryType.ebtFollowing);
        assertTrue(startBoundary.getBoundaryNumber().toString().equalsIgnoreCase("1"));
        TWindowFrameBoundary endBoundary = windowFrame.getEndBoundary();
        assertTrue(endBoundary.getBoundaryType() == EBoundaryType.ebtFollowing);
        assertTrue(endBoundary.getBoundaryNumber().toString().equalsIgnoreCase("2"));
    }
}
