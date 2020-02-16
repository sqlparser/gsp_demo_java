
package demos.dlineage.dataflow.model;

import gudusoft.gsqlparser.ESetOperatorType;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class SelectSetResultSet extends ResultSet {

    private TSelectSqlStatement selectObject;

    public SelectSetResultSet(TSelectSqlStatement select, boolean isTarget) {
        super(select, isTarget);
        this.selectObject = select;
    }

    public ESetOperatorType getSetOperatorType() {
        return selectObject.getSetOperatorType();
    }

    public TResultColumnList getResultColumnObject() {
        if (selectObject.getLeftStmt() != null
                && selectObject.getLeftStmt().getResultColumnList() != null) {
            return selectObject.getLeftStmt().getResultColumnList();
        } else if (selectObject.getRightStmt() != null
                && selectObject.getRightStmt().getResultColumnList() != null) {
            return selectObject.getRightStmt().getResultColumnList();
        }
        return selectObject.getResultColumnList();
    }

    public TSelectSqlStatement getSelectObject() {
        return selectObject;
    }
}
