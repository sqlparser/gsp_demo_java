package demos.columnInWhereClause;


import gudusoft.gsqlparser.nodes.TParseTreeVisitor;
import gudusoft.gsqlparser.nodes.TWhereClause;

public class WhereVisitor extends TParseTreeVisitor {

    @Override
    public void preVisit(TWhereClause node) {
        super.preVisit(node);
        // System.out.println("Find where clause");
        System.out.println("clause:"+node.getCondition().toString());

        WhereCondition w = new WhereCondition(node.getCondition());
        w.printColumn();
    }
}
