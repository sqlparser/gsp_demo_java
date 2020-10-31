
package gudusoft.gsqlparser.dlineage.dataflow.model;

import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class SelectResultSet extends ResultSet {

    private TSelectSqlStatement selectObject;

    public SelectResultSet(TSelectSqlStatement select, boolean isTarget) {
        super(select.getResultColumnList(), isTarget);
        this.selectObject = select;
    }

    public TResultColumnList getResultColumnObject() {
        return selectObject.getResultColumnList();
    }

    public TSelectSqlStatement getSelectStmt() {
        return selectObject;
    }

}
