package common;
/*
 * Date: 2010-9-24
 * Time: 17:17:51
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.nodes.IExpressionVisitor;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.nodes.TExpression;
import junit.framework.TestCase;

class exprVisitor implements IExpressionVisitor{
   private String exprstr = "";

    public String getExprstr() {
        return exprstr;
    }

    public boolean exprVisit(TParseTreeNode pNode,boolean isLeafNode){
        // System.out.println(sign+((TExpression)pNode).toString());
        exprstr = exprstr + ((TExpression)pNode).toString()+ ",";
        return true;

    };
}

public class testExprTraverse extends TestCase {

    public void test1(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);
        sqlparser.sqltext = "SELECT * from b where a>1 or b=1 and c<3";
        //sqlparser.sqltext = "SELECT INTERVAL - '2' YEAR + CURRENT_DATE;";
        assertTrue(sqlparser.parse()==0);

        TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
        TExpression expr = select.getWhereClause().getCondition();

        //System.out.println("pre order");
        exprVisitor epre = new exprVisitor();
        expr.preOrderTraverse((epre));
        assertTrue(epre.getExprstr().equalsIgnoreCase("a>1 or b=1 and c<3,a>1,a,1,b=1 and c<3,b=1,b,1,c<3,c,3,"));

        exprVisitor epre2 = new exprVisitor();
        expr.preOrderTraverse((epre2));
        assertTrue(epre2.getExprstr().equalsIgnoreCase("a>1 or b=1 and c<3,a>1,a,1,b=1 and c<3,b=1,b,1,c<3,c,3,"));

        //System.out.println("in order");
        exprVisitor ei = new exprVisitor();
        expr.inOrderTraverse(ei);
        assertTrue(ei.getExprstr().equalsIgnoreCase("a,a>1,1,a>1 or b=1 and c<3,b,b=1,1,b=1 and c<3,c,c<3,3,"));

        exprVisitor ei2 = new exprVisitor();
        expr.inOrderTraverse(ei2);
        assertTrue(ei2.getExprstr().equalsIgnoreCase("a,a>1,1,a>1 or b=1 and c<3,b,b=1,1,b=1 and c<3,c,c<3,3,"));

        //System.out.println("post order");
        exprVisitor ep = new exprVisitor();
        expr.postOrderTraverse((ep));
        assertTrue(ep.getExprstr().equalsIgnoreCase("a,1,a>1,b,1,b=1,c,3,c<3,b=1 and c<3,a>1 or b=1 and c<3,"));

        exprVisitor ep2 = new exprVisitor();
        expr.postOrderTraverse((ep2));
        assertTrue(ep2.getExprstr().equalsIgnoreCase("a,1,a>1,b,1,b=1,c,3,c<3,b=1 and c<3,a>1 or b=1 and c<3,"));
    }


}