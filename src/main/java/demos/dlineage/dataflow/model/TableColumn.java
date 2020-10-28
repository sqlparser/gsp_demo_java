
package demos.dlineage.dataflow.model;

import java.util.ArrayList;
import java.util.List;

import demos.dlineage.util.Pair3;
import demos.dlineage.util.SQLUtil;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.TConstant;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TObjectName;

public class TableColumn {


    private Table table;

    private int id;
    private String name;

    private Pair3<Long, Long, String> startPosition;
    private Pair3<Long, Long, String> endPosition;

    private TObjectName columnObject;
    private List<TObjectName> starLinkColumns = new ArrayList<TObjectName>();
    
    private boolean showStar;

    public TableColumn(Table table, TObjectName columnObject) {
        if (table == null || columnObject == null)
            throw new IllegalArgumentException("TableColumn arguments can't be null.");

        id = ++ModelBindingManager.get().TABLE_COLUMN_ID;

        this.columnObject = columnObject;

        TSourceToken startToken = columnObject.getStartToken();
        TSourceToken endToken = columnObject.getEndToken();
        this.startPosition = new Pair3<Long, Long, String>(startToken.lineNo,
                startToken.columnNo, ModelBindingManager.getGlobalHash());
        this.endPosition = new Pair3<Long, Long, String>(endToken.lineNo,
                endToken.columnNo + endToken.astext.length(), ModelBindingManager.getGlobalHash());

        if (columnObject.getColumnNameOnly() != null
                && !"".equals(columnObject.getColumnNameOnly())) {
            this.name = columnObject.getColumnNameOnly();
        } else {
            this.name = columnObject.toString();
        }
        
        this.name = SQLUtil.trimColumnStringQuote(name);

        this.table = table;
        table.addColumn(this);
    }

    public TableColumn(Table table, TConstant columnObject, int columnIndex) {
        if (table == null || columnObject == null)
            throw new IllegalArgumentException("TableColumn arguments can't be null.");

        id = ++ModelBindingManager.get().TABLE_COLUMN_ID;

        TSourceToken startToken = columnObject.getStartToken();
        TSourceToken endToken = columnObject.getEndToken();
        this.startPosition = new Pair3<Long, Long, String>(startToken.lineNo,
                startToken.columnNo, ModelBindingManager.getGlobalHash());
        this.endPosition = new Pair3<Long, Long, String>(endToken.lineNo,
                endToken.columnNo + endToken.astext.length(), ModelBindingManager.getGlobalHash());

        this.name = "DUMMY" + columnIndex;

        this.name = SQLUtil.trimColumnStringQuote(name);
        
        this.table = table;
        table.addColumn(this);
    }
    
    public TableColumn(Table table, TExpression columnObject, int columnIndex) {
        if (table == null || columnObject == null)
            throw new IllegalArgumentException("TableColumn arguments can't be null.");

        id = ++ModelBindingManager.get().TABLE_COLUMN_ID;

        TSourceToken startToken = columnObject.getStartToken();
        TSourceToken endToken = columnObject.getEndToken();
        this.startPosition = new Pair3<Long, Long, String>(startToken.lineNo,
                startToken.columnNo, ModelBindingManager.getGlobalHash());
        this.endPosition = new Pair3<Long, Long, String>(endToken.lineNo,
                endToken.columnNo + endToken.astext.length(), ModelBindingManager.getGlobalHash());

        this.name = "DUMMY" + columnIndex;

        this.name = SQLUtil.trimColumnStringQuote(name);
        
        this.table = table;
        table.addColumn(this);
    }

    public Table getTable() {
        return table;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Pair3<Long, Long, String> getStartPosition() {
        return startPosition;
    }

    public Pair3<Long, Long, String> getEndPosition() {
        return endPosition;
    }

    public TObjectName getColumnObject() {
        return columnObject;
    }

    public void bindStarLinkColumns(List<TObjectName> starLinkColumns) {
        if (starLinkColumns != null && !starLinkColumns.isEmpty()) {
            this.starLinkColumns = starLinkColumns;
        }
    }

    public List<TObjectName> getStarLinkColumns() {
        return starLinkColumns;
    }

	public boolean isShowStar() {
		return showStar;
	}

	public void setShowStar(boolean showStar) {
		this.showStar = showStar;
	}

}
