
package gudusoft.gsqlparser.dlineage.dataflow.model;

import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.dlineage.util.Pair3;
import gudusoft.gsqlparser.nodes.TCTE;
import gudusoft.gsqlparser.nodes.TTable;

public class QueryTable extends ResultSet {

    private String alias;
    private Pair3<Long, Long, String> startPosition;
    private Pair3<Long, Long, String> endPosition;

    private TTable tableObject;

    public QueryTable(TTable tableObject) {
        super(tableObject.getCTE() != null ? getCTEQuery(tableObject.getCTE())
                : tableObject.getSubquery(), false);

        this.tableObject = tableObject;

        TSourceToken startToken = tableObject.getStartToken();
        TSourceToken endToken = tableObject.getEndToken();

        if (tableObject.getAliasClause() != null) {
            startToken = tableObject.getAliasClause().getStartToken();
            endToken = tableObject.getAliasClause().getEndToken();
        }

        this.startPosition = new Pair3<Long, Long, String>(startToken.lineNo,
                startToken.columnNo, ModelBindingManager.getGlobalHash());
        this.endPosition = new Pair3<Long, Long, String>(endToken.lineNo,
                endToken.columnNo + endToken.astext.length(), ModelBindingManager.getGlobalHash());

        if (tableObject.getAliasClause() != null) {
            this.alias = tableObject.getAliasName();
        }
    }

    private static TCustomSqlStatement getCTEQuery(TCTE cte) {
        if (cte.getSubquery() != null)
            return cte.getSubquery();
        else if (cte.getUpdateStmt() != null) {
            return cte.getUpdateStmt();
        } else if (cte.getInsertStmt() != null) {
            return cte.getInsertStmt();
        } else if (cte.getDeleteStmt() != null) {
            return cte.getDeleteStmt();
        } else
            return null;
    }

    public String getAlias() {
        return alias;
    }

    public Pair3<Long, Long, String> getStartPosition() {
        return startPosition;
    }

    public Pair3<Long, Long, String> getEndPosition() {
        return endPosition;
    }

    public TTable getTableObject() {
        return tableObject;
    }

}
