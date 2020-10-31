
package gudusoft.gsqlparser.dlineage.dataflow.model;

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
        return getResultColumnList(selectObject);
    }
    
	private TResultColumnList getResultColumnList(TSelectSqlStatement stmt) {
		if (stmt.isCombinedQuery()) {
			TResultColumnList columns = getResultColumnList(stmt.getLeftStmt());
			if(columns!=null){
				return columns;
			}
			return getResultColumnList(stmt.getRightStmt());
		} else {
			return stmt.getResultColumnList();
		}
	}

    public TSelectSqlStatement getSelectObject() {
        return selectObject;
    }
}
