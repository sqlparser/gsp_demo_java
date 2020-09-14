package common;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.ENodeStatus;
import gudusoft.gsqlparser.nodes.IExpressionVisitor;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testModifyExpr extends TestCase {

    static public  void removeExprFromParent(TExpression expr){
        if (expr.getParentExpr() != null){
            TExpression parent  = expr.getParentExpr();
            if (expr == parent.getLeftOperand()){
                parent.setLeftOperand(null);
            }else if (expr == parent.getRightOperand()){
                parent.setRightOperand(null);
            }
        }
    }
    public void testRemvoe1() {
        TGSqlParser parser = new TGSqlParser(EDbVendor.dbvoracle);
        TExpression expression = parser.parseExpression("d.cntrb_date1 >= '$From_Date$'");
        expression.setLeftOperand(null);
        assertTrue(expression.getLeftOperand() == null);
        assertTrue(expression.toString().equalsIgnoreCase("'$From_Date$'"));
        expression.setRightOperand(null);
        assertTrue(expression.getNodeStatus() == ENodeStatus.nsRemoved);
        assertTrue(expression.getStartToken() == null);
        assertTrue(expression.getEndToken() == null);
        assertTrue(expression.toString() == null);

        expression = parser.parseExpression("d.cntrb_date1 >= '$From_Date$'");
        expression.setRightOperand(null);
        assertTrue(expression.getRightOperand() == null);
        assertTrue(expression.toString().equalsIgnoreCase("d.cntrb_date1"));
        expression.setLeftOperand(null);
        assertTrue(expression.getNodeStatus() == ENodeStatus.nsRemoved);
        assertTrue(expression.getStartToken() == null);
        assertTrue(expression.getEndToken() == null);
        assertTrue(expression.toString() == null);

    }

    public void testModify1() {
        TGSqlParser parser = new TGSqlParser(EDbVendor.dbvoracle);
        TExpression expression = parser.parseExpression("d.cntrb_date1 >= '$From_Date$'");
        expression.getRightOperand().setString("1");
        assertTrue(expression.toString().equalsIgnoreCase("d.cntrb_date1 >= 1"));
    }

    public void testModify2() {
        TGSqlParser parser = new TGSqlParser(EDbVendor.dbvoracle);
        TExpression expression = parser.parseExpression("d.cntrb_date1 >= '$From_Date$'");
        expression.getRightOperand().setString(expression.getRightOperand().toString()+" + 1");
        assertTrue(expression.toString().equalsIgnoreCase("d.cntrb_date1 >= '$From_Date$' + 1"));
    }

    public void test10(){
        TGSqlParser parser = new TGSqlParser(EDbVendor.dbvoracle);
        parser.sqltext = "SELECT SUM (d.amt) \n" +
                "FROM   summit.cntrb_detail d \n" +
                "WHERE d.cntrb_date1 >= '$From_Date$' \n" +
                "AND d.cntrb_date2 <= '$Thru_Date$' \n" +
                "GROUP  BY d.id;";
        parser.parse();

        TSelectSqlStatement select = (TSelectSqlStatement)parser.sqlstatements.get(0);
        TExpression expression = select.getWhereClause().getCondition();

        TExpression expr_From_Date = expression.getLeftOperand().getRightOperand();
        TExpression expr_cntrb_date1 = expression.getLeftOperand().getLeftOperand();
        TExpression expr_Thru_Date = expression.getRightOperand().getRightOperand();
        TExpression expr_cntrb_date2 = expression.getRightOperand().getLeftOperand();

        // printNodeEndWithThisToken(expression.getLeftOperand().getEndToken());

        removeExprFromParent(expr_cntrb_date1);
        removeExprFromParent(expr_From_Date);
        removeExprFromParent(expr_cntrb_date2);
        removeExprFromParent(expr_Thru_Date);

//        removeExprFromParent(expr_cntrb_date2);
//        removeExprFromParent(expr_Thru_Date);
//        removeExprFromParent(expr_cntrb_date1);
//        removeExprFromParent(expr_From_Date);

        //System.out.println(select.getWhereClause().getCondition().toString());
        if (select.getWhereClause().getCondition().getNodeStatus() == ENodeStatus.nsRemoved){
            select.setWhereClause(null);
        }
        System.out.println(select.toString());
    }

    public void test20(){
        String sql = "SELECT SUM (d.amt)\r\n" + "FROM summit.cntrb_detail d\r\n"
                + "WHERE d.fund_coll_attrb IN ( '$Institute$' )\r\n" + "AND d.fund_acct IN ( '$Fund$' )\r\n"
                + "AND d.cntrb_date >= '$From_Date$'\r\n" + "AND d.cntrb_date <= '$Thru_Date$'\r\n" + "GROUP BY d.id;";
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.setSqltext(sql);
        sqlparser.parse();
        TSelectSqlStatement stmt = (TSelectSqlStatement) sqlparser.getSqlstatements().get(0);
        TExpression whereExpression = stmt.getWhereClause().getCondition();

        whereExpression.postOrderTraverse(new IExpressionVisitor() {
            public boolean exprVisit(TParseTreeNode pnode, boolean pIsLeafNode) {
                TExpression expression = (TExpression) pnode;
                if (expression.getNodeStatus() != ENodeStatus.nsRemoved && expression != null
                        && expression.getLeftOperand() == null && expression.getRightOperand() == null
                        && expression.toString().indexOf("$") != -1) {
                    //System.out.println(expression.toString());
                    removeExpression(expression);
                } else {
                    if (expression.getExpressionType() == EExpressionType.simple_comparison_t
                            || expression.getExpressionType() == EExpressionType.in_t) {
                        if (expression.getRightOperand() == null && expression.getLeftOperand() != null) {
                            removeExpression(expression.getLeftOperand());
                        }
                        if (expression.getRightOperand() != null && expression.getLeftOperand() == null) {
                            removeExpression(expression.getRightOperand());
                        }
                        if (expression.getLeftOperand() == null && expression.getRightOperand() == null) {
                            removeExpression(expression);
                        }
                    } else if (expression.getExpressionType() == EExpressionType.logical_and_t
                            || expression.getExpressionType() == EExpressionType.logical_or_t) {
                        if (expression.getLeftOperand() == null && expression.getRightOperand() == null) {
                            removeExpression(expression);
                        }
                    }
                }
                return true;
            }

            private void removeExpression(TExpression expression) {
                if (expression.getParentExpr() != null) {
                    TExpression parentExpr = expression.getParentExpr();
                    if (parentExpr.getLeftOperand() == expression) {
                        parentExpr.setLeftOperand(null);
                    }
                    if (parentExpr.getRightOperand() == expression) {
                        parentExpr.setRightOperand(null);
                    }
                }
            }
        });

        if (stmt.getWhereClause().getCondition().getNodeStatus() == ENodeStatus.nsRemoved) {
            stmt.setWhereClause(null);
        }

        System.out.println(stmt.toString().trim());
//        assertTrue(stmt.toString().trim().equalsIgnoreCase("SELECT SUM (d.amt)\n" +
//                "FROM summit.cntrb_detail d\n" +
//                "GROUP BY d.id;"));
    }

}
