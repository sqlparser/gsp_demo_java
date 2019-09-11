package demos.expressionTraverser;
/*
 * Date: 2010-11-3
 * Time: 10:38:15
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.IExpressionVisitor;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class expressionTraverser  {

    public static void main(String args[])
     {
         //
          oracleWhereCondition();
         // sqlServerSelectList();
        // functionArg();
     }

    static void functionArg(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmysql);

        sqlparser.sqltext = "select sum(col1-(col2+col3)) from table1";

        int ret = sqlparser.parse();
        if (ret == 0){
            TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
            TExpression expr = select.getResultColumnList().getResultColumn(0).getExpr();
            TFunctionCall functionCall = expr.getFunctionCall();

            doTraverse(functionCall.getArgs().getExpression(0),"pre");
            doTraverse(functionCall.getArgs().getExpression(0),"in");
            doTraverse(functionCall.getArgs().getExpression(0),"post");

        }else{
            System.out.println(sqlparser.getErrormessage());
        }
    }


    static void oracleWhereCondition(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvoracle);

        //sqlparser.sqltext = "select col1, col2,sum(col3) from table1, table2 where col4 > col5 and col6= 1000 or c1 = 1 and not sal";
        sqlparser.sqltext = "SELECT * FROM Customers\n" +
                "WHERE Country='Germany' AND City='Berlin';";

        int ret = sqlparser.parse();
        if (ret == 0){
            TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
            TExpression expr = select.getWhereClause().getCondition();

            doTraverse(expr,"pre");
            doTraverse(expr,"in");
            doTraverse(expr,"post");

        }else{
            System.out.println(sqlparser.getErrormessage());
        }
    }

    static void sqlServerSelectList(){
        TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvmssql);

        sqlparser.sqltext = "\t\tSELECT \n" +
                "\t\t\tCustomerKey\n" +
                "\t\t\t,REPLACE(\n" +
                "\t\t\t  REPLACE(\n" +
                "\t\t\t   REPLACE(\n" +
                "\t\t\t\tREPLACE(\n" +
                "\t\t\t\t REPLACE(\n" +
                "\t\t\t\t  REPLACE(\n" +
                "\t\t\t\t   REPLACE(\n" +
                "\t\t\t\t\tREPLACE(\n" +
                "\t\t\t\t\t REPLACE(\n" +
                "\t\t\t\t\t  REPLACE(\n" +
                "\t\t\t\t\t   REPLACE(\n" +
                "\t\t\t\t\t\tREPLACE(\n" +
                "\t\t\t\t\t\t REPLACE(\n" +
                "\t\t\t\t\t\t  REPLACE(\n" +
                "\t\t\t\t\t\t   REPLACE(\n" +
                "\t\t\t\t\t\t\tREPLACE(\n" +
                "\t\t\t\t\t\t\t REPLACE(\n" +
                "\t\t\t\t\t\t\t  REPLACE(\n" +
                "\t\t\t\t\t\t\t   REPLACE(\n" +
                "\t\t\t\t\t\t\t\tREPLACE(\n" +
                "\t\t\t\t\t\t\t\t REPLACE(\n" +
                "\t\t\t\t\t\t\t\t  REPLACE(\n" +
                "\t\t\t\t\t\t\t\t   REPLACE(\n" +
                "\t\t\t\t\t\t\t\t\tREPLACE(\n" +
                "\t\t\t\t\t\t\t\t\t REPLACE(\n" +
                "\t\t\t\t\t\t\t\t\t  REPLACE(CustLower,' a',' A')\n" +
                "\t\t\t\t\t\t\t\t\t ,' b',' B')\n" +
                "\t\t\t\t\t\t\t\t\t,' c',' C')\n" +
                "\t\t\t\t\t\t\t\t   ,' d',' D')\n" +
                "\t\t\t\t\t\t\t\t  ,' e',' E')\n" +
                "\t\t\t\t\t\t\t\t ,' f',' F')\n" +
                "\t\t\t\t\t\t\t\t,' g',' G')\n" +
                "\t\t\t\t\t\t\t   ,' h',' H')\n" +
                "\t\t\t\t\t\t\t  ,' i',' I')\n" +
                "\t\t\t\t\t\t\t ,' j',' J')\n" +
                "\t\t\t\t\t\t\t,' k',' K')\n" +
                "\t\t\t\t\t\t   ,' l',' L')\n" +
                "\t\t\t\t\t\t  ,' m',' M')\n" +
                "\t\t\t\t\t\t ,' n',' N')\n" +
                "\t\t\t\t\t\t,' o',' O')\n" +
                "\t\t\t\t\t   ,' p',' P')\n" +
                "\t\t\t\t\t  ,' q',' Q')\n" +
                "\t\t\t\t\t ,' r',' R')\n" +
                "\t\t\t\t\t,' s',' S')\n" +
                "\t\t\t\t   ,' t',' T')\n" +
                "\t\t\t\t  ,' u',' U')\n" +
                "\t\t\t\t ,' v',' V')\n" +
                "\t\t\t\t,' w',' W')\n" +
                "\t\t\t   ,' x',' X')\n" +
                "\t\t\t  ,' y',' Y')\n" +
                "\t\t\t ,' z',' Z') AS CustomerName\n" +
                "\t\t\t,Type\n" +
                "\t\tFROM Strategic.td_Customer";

        int ret = sqlparser.parse();
        if (ret == 0){
            TSelectSqlStatement select = (TSelectSqlStatement)sqlparser.sqlstatements.get(0);
            TExpression expr = select.getResultColumnList().getResultColumn(1).getExpr();

            doTraverse(expr,"pre");
            doTraverse(expr,"in");
            doTraverse(expr,"post");

        }else{
            System.out.println(sqlparser.getErrormessage());
        }
    }

    static void doTraverse(TExpression expr, String traverseStyle){
        if (traverseStyle.equalsIgnoreCase("pre")){
            System.out.println("pre order");
            expr.preOrderTraverse(new exprVisitor());
        }else if (traverseStyle.equalsIgnoreCase("in")){
            System.out.println("\nin order");
            expr.inOrderTraverse(new exprVisitor());
        }else if (traverseStyle.equalsIgnoreCase("post")){
            System.out.println("\npost order");
            expr.postOrderTraverse(new exprVisitor());
        }else{
            System.out.println("Invalid traverse style:"+traverseStyle);
        }
    }

}

class exprVisitor implements IExpressionVisitor {

    public boolean exprVisit(TParseTreeNode pNode,boolean isLeafNode){
        String sign = "";
        if (isLeafNode){
            sign ="*";
        }
         System.out.println(sign+pNode.getClass().toString()+" "+ pNode.toString());
        return true;
    };
}
