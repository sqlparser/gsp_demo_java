package gudusoft.gsqlparser.commonTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.nodes.TWhereClause;
import junit.framework.TestCase;

class TreeVisitor extends TParseTreeVisitor {
    public static final int NODE_VISITED = 999;
    public void preVisit(TExpression node) {

//        if (node.getDummyTag() == NODE_VISITED) {
//            System.out.println("revisited: "+node.toString());
//            return;
//        }
//        System.out.println(node.toString());

//            switch (node.getExpressionType()) {
//                case simple_object_name_t:
//                    acceptChildrenIfNotNull(node.getObjectOperand());
//                    break;
//                case simple_constant_t:
//                    acceptChildrenIfNotNull(node.getConstantOperand());
//                    break;
//                case function_t:
//                    acceptChildrenIfNotNull(node.getFunctionCall());
//                    break;
//                case cursor_t:
//                case subquery_t:
//                case exists_t:
//                    acceptChildrenIfNotNull(node.getSubQuery());
//                    break;
//                case case_t:
//                    acceptChildrenIfNotNull(node.getCaseExpression());
//                    break;
//                case simple_comparison_t:
//                    String op = node.getComparisonOperator().toString();
//                    break;
//                case group_comparison_t:
//                case in_t:
//                    if (!acceptChildrenIfNotNull(node.getExprList())) {
//                        acceptChildrenIfNotNull(node.getLeftOperand());
//                    }
//                    acceptChildrenIfNotNull(node.getRightOperand());
//                    break;
//                case list_t:
//                    acceptChildrenIfNotNull(node.getExprList());
//                    break;
//                case pattern_matching_t:
//                    acceptChildrenIfNotNull(node.getLeftOperand());
//                    acceptChildrenIfNotNull(node.getRightOperand());
//                    acceptChildrenIfNotNull(node.getLikeEscapeOperand());
//                    break;
//                case between_t:
//                    acceptChildrenIfNotNull(node.getBetweenOperand());
//                    // fall through
//                case logical_and_t:
//                case logical_or_t:
//
//                        node.setDummyTag(NODE_VISITED);
//                        acceptChildrenIfNotNull(node.getLeftOperand());
//                        node.getLeftOperand().setDummyTag(NODE_VISITED);
//                        acceptChildrenIfNotNull(node.getRightOperand());
//                        node.getRightOperand().setDummyTag(NODE_VISITED);
//
//                    break;
//                case null_t:
//                    break;
//                default:
//                    acceptChildrenIfNotNull(node.getLeftOperand());
//                    acceptChildrenIfNotNull(node.getRightOperand());
//                    break;
//            }
        }


    private boolean acceptChildrenIfNotNull(TParseTreeNode node) {
        if (node != null ) {
            node.acceptChildren(this);
            return true;
        }
        return false;
    }
}
public class testAcceptChildren extends TestCase {

    public void test1() {
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);
        sqlparser.sqltext = "SELECT * from b where  T1.tx_typ_cd IN ( 0, 1, 13 ) \n" +
                "       AND T1.str_no = T2.str_no(+) \n" +
                "       AND T1.trml_no = T2.trml_no(+) \n" +
                "       AND T1.tx_no = T2.tx_no(+) \n" +
                "       AND T1.tx_dte_tme = T2.tx_dte_tme(+) \n" +
                "       AND T1.rec_seq_no + 1 = T2.rec_seq_no(+) \n" +
                "       AND T1.str_no = RFND.str_no(+) \n" +
                "       AND T1.trml_no = RFND.trml_no(+) \n" +
                "       AND T1.tx_no = RFND.tx_no(+) \n" +
                "       AND T1.tx_dte_tme = RFND.tx_dte_tme(+) \n" +
                "       AND T1.rec_seq_no = RFND.t1_rec_seq_no(+) \n" +
                "       AND T1.str_no = T11BD.str_no(+) \n" +
                "       AND T1.trml_no = T11BD.trml_no(+) \n" +
                "       AND T1.tx_no = T11BD.tx_no(+) \n" +
                "       AND T1.tx_dte_tme = T11BD.tx_dte_tme(+) \n" +
                "       AND To_number (T1.item_cd) = T11BD.lnk_upc(+) \n" +
                "       AND T1.extd_prc = T11BD.dsc_amt(+) \n" +
                "       AND T1.dpt_no = T11BD.dept_no(+) \n" +
                "       AND T1.rec_seq_no - 2 <= T11BD.rec_seq_no(+) \n" +
                "       AND T1.rec_seq_no >= T11BD.rec_seq_no(+) ";
        assertTrue(sqlparser.parse() == 0);
        TWhereClause whereClause = sqlparser.sqlstatements.get(0).getWhereClause();
        whereClause.getCondition().acceptChildren(new TreeVisitor());
    }
}
