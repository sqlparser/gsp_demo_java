
package gudusoft.gsqlparser.dlineage.dataflow.model;

import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.stmt.TCursorDeclStmt;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class CursorResultSet extends ResultSet {

    private TCursorDeclStmt cursorStmt;

    public CursorResultSet(TCursorDeclStmt select) {
        super(select, false);
        this.cursorStmt = select;
    }

    public TResultColumnList getResultColumnObject() {
        return cursorStmt.getSubquery().getResultColumnList();
    }

    public TSelectSqlStatement getSelectStmt() {
        return cursorStmt.getSubquery();
    }

    public TCursorDeclStmt getCursorStmt() {
        return cursorStmt;
    }

}
