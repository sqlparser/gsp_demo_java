
package demos.dlineage.dataflow.model;

import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TCTE;
import gudusoft.gsqlparser.nodes.TTable;
import demos.dlineage.util.Pair;

public class QueryTable extends ResultSet {

    private String alias;
    private Pair<Long, Long> startPosition;
    private Pair<Long, Long> endPosition;

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

        this.startPosition = new Pair<Long, Long>(startToken.lineNo,
                startToken.columnNo);
        this.endPosition = new Pair<Long, Long>(endToken.lineNo,
                endToken.columnNo + endToken.astext.length());

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

    public Pair<Long, Long> getStartPosition() {
        return startPosition;
    }

    public Pair<Long, Long> getEndPosition() {
        return endPosition;
    }

    public TTable getTableObject() {
        return tableObject;
    }

}
