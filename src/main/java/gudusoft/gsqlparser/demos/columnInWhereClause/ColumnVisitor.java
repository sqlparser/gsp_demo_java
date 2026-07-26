package demos.columnInWhereClause;

import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TParseTreeVisitor;


public class ColumnVisitor extends TParseTreeVisitor {

    String columnName;
    public ColumnVisitor(String c){
        this.columnName = c;
    }

    @Override
    public void preVisit(TExpression node) {
        super.preVisit(node);
        // System.out.println("Find where clause");
    }

    @Override
    public void preVisit(TFunctionCall node) {
        super.preVisit(node);
        // System.out.println("Find where clause");
    }

    @Override
    public void preVisit(TObjectName node) {
        super.preVisit(node);
        if (node.toString().equalsIgnoreCase(columnName)){
             System.out.println("Find column:"+node.toString()+" at ("+ node.getStartToken().lineNo+","+node.getStartToken().columnNo +")");
        }
    }
}