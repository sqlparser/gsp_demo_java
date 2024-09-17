package gudusoft.gsqlparser.commonTest;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import junit.framework.TestCase;

public class testModifyExpr extends TestCase {

    public void testAddAndRemove() {
        TGSqlParser parser = new TGSqlParser(EDbVendor.dbvoracle);
        parser.sqltext = "select datediff(month,trunc(datecola),trunc(datecolb))\n" +
                "from testtable";
        parser.parse();
        TSelectSqlStatement select = (TSelectSqlStatement) parser.sqlstatements.get(0);
        assertTrue(select.toString().equalsIgnoreCase("select datediff(month,trunc(datecola),trunc(datecolb))\n" +
                "from testtable"));

        String condition = "((d.fund_coll_attrb IN ( '$Institute$' )\n" +
                "AND d.fund_acct IN ( '$Fund$' )\n" +
                "AND d.cntrb_date >= '$From_Date$'\n" +
                "AND d.cntrb_date <= '$Thru_Date$'))";

        TWhereClause whereClause = new TWhereClause(condition);
        select.setWhereClause(whereClause);

        assertTrue(select.toString().trim().equalsIgnoreCase("select datediff(month,trunc(datecola),trunc(datecolb))\n" +
                "from testtable\n" +
                "where ((d.fund_coll_attrb IN ( '$Institute$' )\n" +
                "AND d.fund_acct IN ( '$Fund$' )\n" +
                "AND d.cntrb_date >= '$From_Date$'\n" +
                "AND d.cntrb_date <= '$Thru_Date$'))"));

        // remove condition with $Institute$ and $Fund$, replace $From_Date$, $Thru_Date$ with the date value
        whereClause.getCondition().postOrderTraverse(new IExpressionVisitor() {
            public boolean exprVisit(TParseTreeNode pnode, boolean pIsLeafNode) {
                TExpression expression = (TExpression) pnode;
                if (expression.getNodeStatus() != ENodeStatus.nsRemoved) {
                    if ((expression.toString().indexOf("$Institute$") != -1)||(expression.toString().indexOf("$Fund$") != -1)){
                        expression.removeMe();
                    }else if (expression.toString().indexOf("$From_Date$") != -1){
                        expression.setString("09/01/2020");
                    }else if (expression.toString().indexOf("$Thru_Date$") != -1){
                        expression.setString("09/30/2020");
                    }

                }
                return true;
            }
        });

        //System.out.println("\nAssertFailed:\n"+select.toString());
        assertTrue(select.toString().trim().equalsIgnoreCase("select datediff(month,trunc(datecola),trunc(datecolb))\n" +
                "from testtable\n" +
                "where ((d.cntrb_date >= 09/01/2020\n" +
                "AND d.cntrb_date <= 09/30/2020))"));
    }

    public void testRemvoe1() {
        TGSqlParser parser = new TGSqlParser(EDbVendor.dbvoracle);
        TExpression expression = parser.parseExpression("d.cntrb_date1 >= '$From_Date$'");
        expression.getLeftOperand().removeMe();
        assertTrue(expression.getExpressionType() == EExpressionType.removed_t);
        assertTrue(expression.getNodeStatus() == ENodeStatus.nsRemoved);
        assertTrue(expression.toString() == null);

        expression = parser.parseExpression("d.cntrb_date1 >= '$From_Date$'");
        expression.getRightOperand().removeMe();
        assertTrue(expression.getExpressionType() == EExpressionType.removed_t);
        assertTrue(expression.getNodeStatus() == ENodeStatus.nsRemoved);
        assertTrue(expression.toString() == null);

        expression = parser.parseExpression("d.cntrb_date1 + '$From_Date$'");
        expression.getLeftOperand().removeMe();
        assertTrue(expression.toString().equalsIgnoreCase("'$From_Date$'"));
        expression.getRightOperand().removeMe();
        assertTrue(expression.getExpressionType() == EExpressionType.removed_t);
        assertTrue(expression.getNodeStatus() == ENodeStatus.nsRemoved);
        assertTrue(expression.toString() == null);

        expression = parser.parseExpression("d.cntrb_date1 + '$From_Date$'");
        expression.getRightOperand().removeMe();
        assertTrue(expression.toString().equalsIgnoreCase("d.cntrb_date1"));
        expression.getLeftOperand().removeMe();
        assertTrue(expression.getExpressionType() == EExpressionType.removed_t);
        assertTrue(expression.getNodeStatus() == ENodeStatus.nsRemoved);
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

    public void testDonotRemoveEmptyLine() {
        TGSqlParser parser = new TGSqlParser(EDbVendor.dbvoracle);
        parser.sqltext = "SELECT SUM (d.amt) \n" +
                "FROM   summit.cntrb_detail d \n" +
                "WHERE d.cntrb_date1 >= '$From_Date$' \n\n" +
                "AND d.cntrb_date2 <= '$Thru_Date$' \n" +
                "GROUP  BY d.id;";
        parser.parse();

        TSelectSqlStatement select = (TSelectSqlStatement) parser.sqlstatements.get(0);
        assertTrue(select.toString().equalsIgnoreCase("SELECT SUM (d.amt) \n" +
                "FROM   summit.cntrb_detail d \n" +
                "WHERE d.cntrb_date1 >= '$From_Date$' \n" +
                "\n" +
                "AND d.cntrb_date2 <= '$Thru_Date$' \n" +
                "GROUP  BY d.id;"));
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
        expr_cntrb_date1.removeMe();
        expr_From_Date.removeMe();
        expr_cntrb_date2.removeMe();
        expr_Thru_Date.removeMe();

        //System.out.println(select.getWhereClause().getCondition().toString());
        if (select.getWhereClause().getCondition().getNodeStatus() == ENodeStatus.nsRemoved){
            select.setWhereClause(null);
        }
       // System.out.println(select.toString());
        assertTrue(select.toString().equalsIgnoreCase("SELECT SUM (d.amt) \n" +
                "FROM   summit.cntrb_detail d \n" +
                " GROUP  BY d.id;"));
    }

    public void test11(){
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

        expr_Thru_Date.removeMe();
        expr_cntrb_date2.removeMe();
        expr_From_Date.removeMe();
        expr_cntrb_date1.removeMe();

        if (select.getWhereClause().getCondition().getNodeStatus() == ENodeStatus.nsRemoved){
            select.setWhereClause(null);
        }

        // System.out.println(select.toString());
        assertTrue(select.toString().equalsIgnoreCase("SELECT SUM (d.amt) \n" +
                "FROM   summit.cntrb_detail d \n" +
                " GROUP  BY d.id;"));
    }

    public void test12(){
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

        expr_Thru_Date.removeMe();
        expr_From_Date.removeMe();

        if (select.getWhereClause().getCondition().getNodeStatus() == ENodeStatus.nsRemoved){
            select.setWhereClause(null);
        }

        // System.out.println(select.toString());
        assertTrue(select.toString().equalsIgnoreCase("SELECT SUM (d.amt) \n" +
                "FROM   summit.cntrb_detail d \n" +
                " GROUP  BY d.id;"));
    }

    public void test13(){
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

        expr_From_Date.removeMe();

        if (select.getWhereClause().getCondition().getNodeStatus() == ENodeStatus.nsRemoved){
            select.setWhereClause(null);
        }

         //System.out.println(select.toString());
        assertTrue(select.toString().equalsIgnoreCase("SELECT SUM (d.amt) \n" +
                "FROM   summit.cntrb_detail d \n" +
                "WHERE d.cntrb_date2 <= '$Thru_Date$' \n" +
                "GROUP  BY d.id;"));
    }


    public void test14(){
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

        expr_cntrb_date2.removeMe();
        expr_From_Date.removeMe();
        expr_cntrb_date1.removeMe();
        expr_Thru_Date.removeMe();

        if (select.getWhereClause().getCondition().getNodeStatus() == ENodeStatus.nsRemoved){
            select.setWhereClause(null);
        }

       // System.out.println(select.toString());
        assertTrue(select.toString().equalsIgnoreCase("SELECT SUM (d.amt) \n" +
                "FROM   summit.cntrb_detail d \n" +
                " GROUP  BY d.id;"));
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
                    expression.removeMe();
                } else {
                    if (expression.getExpressionType() == EExpressionType.simple_comparison_t
                            || expression.getExpressionType() == EExpressionType.in_t) {
                        if (expression.getRightOperand() == null && expression.getLeftOperand() != null) {
                            expression.getLeftOperand().removeMe();
                        }
                        if (expression.getRightOperand() != null && expression.getLeftOperand() == null) {
                            expression.getRightOperand().removeMe();
                        }
                        if (expression.getLeftOperand() == null && expression.getRightOperand() == null) {
                            expression.removeMe();
                        }
                    } else if (expression.getExpressionType() == EExpressionType.logical_and_t
                            || expression.getExpressionType() == EExpressionType.logical_or_t) {
                        if (expression.getLeftOperand() == null && expression.getRightOperand() == null) {
                            expression.removeMe();
                        }
                    }
                }
                return true;
            }
        });

        if (stmt.getWhereClause().getCondition().getNodeStatus() == ENodeStatus.nsRemoved) {
            stmt.setWhereClause(null);
        }

//        System.out.println("AssertFailed:\n"+stmt.toString());
        assertTrue(stmt.toString().trim().equalsIgnoreCase("SELECT SUM (d.amt)\r\n" +
                "FROM summit.cntrb_detail d\r\n" +
                "GROUP BY d.id;"));

    }

    public void testExpr239() {

        String sql = "SELECT SUM (d.amt) \r\n "
                + "FROM   summit.cntrb_detail d \r\n "
                + "WHERE  d.id = summit.mstr.id \r\n "
                + "       AND (d.cntrb_date || d.cntrb_time) >= ('$From_Date$'  || '$From_Time$')\r\n "
                + "       AND (d.cntrb_date || d.cntrb_time) <= ('$Thru_Date$'  || '$Thru_Date$')\r\n "
                + "GROUP  BY d.id ";

        TGSqlParser parser = new TGSqlParser(EDbVendor.dbvoracle);
        parser.sqltext = sql;
        parser.parse();
        TSelectSqlStatement select = (TSelectSqlStatement) parser.sqlstatements.get(0);


        select.getWhereClause().getCondition().postOrderTraverse(new IExpressionVisitor() {
            public boolean exprVisit(TParseTreeNode pnode, boolean pIsLeafNode) {
                TExpression expression = (TExpression) pnode;
                if (expression.getNodeStatus() != ENodeStatus.nsRemoved) {
                    if (expression.toString().indexOf("$From_Date$") != -1){
                        expression.setString("09/01/2020");
                    }else if (expression.toString().indexOf("$") != -1){
                        expression.removeMe();
                    }
                }
                return true;
            }
        });

       // System.out.println("\nAssertFailed:\n"+select.toString());
        assertTrue(select.toString().trim().equalsIgnoreCase("SELECT SUM (d.amt) \r\n" +
                " FROM   summit.cntrb_detail d \r\n" +
                " WHERE  d.id = summit.mstr.id \r\n" +
                "        AND (d.cntrb_date || d.cntrb_time) >= (09/01/2020)\r\n" +
                " GROUP  BY d.id"));
    }

    public void testExprRemoveBetween() {

        String sql = "SELECT SUM (d.amt)\r\n" + "FROM summit.cntrb_detail d\r\n"
                + "WHERE d.cntrb_date BETWEEN '$start_date$' AND '$end_date$'\r\n" + "GROUP BY d.id;";
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.setSqltext(sql);
        sqlparser.parse();
        TSelectSqlStatement stmt = (TSelectSqlStatement) sqlparser.getSqlstatements().get(0);
        TExpression whereExpression = stmt.getWhereClause().getCondition();
        whereExpression.postOrderTraverse(new IExpressionVisitor() {
            public boolean exprVisit(TParseTreeNode pnode, boolean pIsLeafNode) {
                TExpression expression = (TExpression) pnode;
                if (expression.getLeftOperand() == null && expression.getRightOperand() == null
                        && expression.toString() != null && expression.toString().indexOf("$") != -1) {
                    expression.removeMe();
                } else {
                    if (expression.getExpressionType() == EExpressionType.simple_comparison_t
                            || expression.getExpressionType() == EExpressionType.in_t) {
                        if (expression.getRightOperand() == null && expression.getLeftOperand() != null) {
                            expression.getLeftOperand().removeMe();
                        }
                        if (expression.getRightOperand() != null && expression.getLeftOperand() == null) {
                            expression.getRightOperand().removeMe();
                        }
                        if (expression.getLeftOperand() == null && expression.getRightOperand() == null) {
                            expression.removeMe();
                        }
                    } else if (expression.getExpressionType() == EExpressionType.logical_and_t
                            || expression.getExpressionType() == EExpressionType.logical_or_t) {
                        if (expression.getLeftOperand() == null && expression.getRightOperand() == null) {
                            expression.removeMe();
                        }
                    }
                }
                return true;
            }
        });

        if (stmt.getWhereClause().getCondition() == null
                || stmt.getWhereClause().getCondition().getNodeStatus() == ENodeStatus.nsRemoved) {
            stmt.setWhereClause(null);
        }

        //System.out.println(stmt.toString());
        assertTrue(stmt.toString().equalsIgnoreCase("SELECT SUM (d.amt)\r\n" +
                "FROM summit.cntrb_detail d\r\n" +
                "GROUP BY d.id;"));

    }

    public void testExprRemoveHaving() {
        String sql = "select c.id as sms.id from summit.cntrb_detail c where c.cntrb_date >='2011-01-01' and c.cntrb_date<='2013-01-01' group by c.id having sum(c.amt) >= '$GivingFromAmount$' and  sum(c.amt) <= '$GivingThruAmount$'";
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.setSqltext(sql);
        sqlparser.parse();
        TSelectSqlStatement stmt = (TSelectSqlStatement) sqlparser.getSqlstatements().get(0);
        TExpression havingExpression = stmt.getGroupByClause().getHavingClause();
        havingExpression.postOrderTraverse(new IExpressionVisitor() {
            public boolean exprVisit(TParseTreeNode pnode, boolean pIsLeafNode) {
                TExpression expression = (TExpression) pnode;
                if (expression.getLeftOperand() == null && expression.getRightOperand() == null
                        && expression.toString() != null && expression.toString().indexOf("$") != -1) {
                    expression.removeMe();
                } else {
                    if (expression.getExpressionType() == EExpressionType.simple_comparison_t
                            || expression.getExpressionType() == EExpressionType.in_t) {
                        if (expression.getRightOperand() == null && expression.getLeftOperand() != null) {
                            expression.getLeftOperand().removeMe();
                        }
                        if (expression.getRightOperand() != null && expression.getLeftOperand() == null) {
                            expression.getRightOperand().removeMe();
                        }
                        if (expression.getLeftOperand() == null && expression.getRightOperand() == null) {
                            expression.removeMe();
                        }
                    } else if (expression.getExpressionType() == EExpressionType.logical_and_t
                            || expression.getExpressionType() == EExpressionType.logical_or_t) {
                        if (expression.getLeftOperand() == null && expression.getRightOperand() == null) {
                            expression.removeMe();
                        }
                    }
                }
                return true;
            }
        });

        if (havingExpression == null
                || havingExpression.getNodeStatus() == ENodeStatus.nsRemoved) {
            stmt.getGroupByClause().setHavingClause(null);
        }

        assertTrue(stmt.toString().trim().equalsIgnoreCase("select c.id as sms.id from summit.cntrb_detail c where c.cntrb_date >='2011-01-01' and c.cntrb_date<='2013-01-01' group by c.id"));
    }

    public void testExprRemoveAndSetInBetween() {
        String sql = "SELECT * FROM lookup.recognition_master_table crg WHERE crg.datedb BETWEEN '$start_date$' AND '$end_date$' GROUP BY crg.id";
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.setSqltext(sql);
        sqlparser.parse();
        TSelectSqlStatement stmt = (TSelectSqlStatement) sqlparser.getSqlstatements().get(0);
        TExpression whereExpression = stmt.getWhereClause().getCondition();
        whereExpression.postOrderTraverse(new IExpressionVisitor() {
            public boolean exprVisit(TParseTreeNode pnode, boolean pIsLeafNode) {
                TExpression expression = (TExpression) pnode;
                if (expression.getLeftOperand() == null && expression.getRightOperand() == null
                        && expression.toString() != null && expression.toString().indexOf("$") != -1) {
                    if (expression.toString().indexOf("'$start_date$'") != -1) {
                        expression.removeMe();
                    } else {
                        expression.setString("2010-01-01");
                    }
                }
                return true;
            }
        });

        if (stmt.getWhereClause().getCondition() == null
                || stmt.getWhereClause().getCondition().getNodeStatus() == ENodeStatus.nsRemoved)
        {
            stmt.setWhereClause(null);
        }

        //stmt.setWhereClause(null);
        assertTrue(stmt.toString().equalsIgnoreCase("SELECT * FROM lookup.recognition_master_table crg  GROUP BY crg.id"));
    }

    public void testExprRemoveComaInList() {
        String sql = "SELECT name, $start_date$ as startDate, $end_date$ as endDate, sex FROM lookup.recognition_master_table";
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.setSqltext(sql);
        sqlparser.parse();
        TSelectSqlStatement stmt = (TSelectSqlStatement) sqlparser.getSqlstatements().get(0);
        for (int i = 0; i < stmt.getResultColumnList().size(); i++) {
            TExpression expression = stmt.getResultColumnList().getResultColumn(i).getExpr();
            expression.postOrderTraverse(new IExpressionVisitor() {
                public boolean exprVisit(TParseTreeNode pnode, boolean pIsLeafNode) {
                    TExpression expression = (TExpression) pnode;
                    if (expression.getLeftOperand() == null && expression.getRightOperand() == null
                            && expression.toString() != null && expression.toString().indexOf("$") != -1) {
                        expression.removeMe();
                    }
                    return true;
                }
            });

            if(expression.getNodeStatus() == ENodeStatus.nsRemoved){
                stmt.getResultColumnList().removeElementAt(i);
                i--;
            }
        }

        assertTrue(stmt.toString().equalsIgnoreCase("SELECT name,   sex FROM lookup.recognition_master_table"));
    }

}
