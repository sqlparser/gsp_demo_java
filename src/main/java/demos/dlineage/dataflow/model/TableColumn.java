
package demos.dlineage.dataflow.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private Map<String, Set<TObjectName>> starLinkColumns = new LinkedHashMap<String, Set<TObjectName>>();
    
    private boolean showStar;
    
	private int columnIndex;

	public TableColumn(Table view, TObjectName columnObject, int index) {
		this(view, columnObject);
		this.columnIndex = index;
	}
	
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
        
        if("*".equals(this.name) && !table.isCreateTable()){
        	showStar = true;
        }
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

    public void bindStarLinkColumns(Map<String, Set<TObjectName>> starLinkColumns) {
        if (starLinkColumns != null && !starLinkColumns.isEmpty()) {
            this.starLinkColumns = starLinkColumns;
        }
    }

    public Map<String, Set<TObjectName>> getStarLinkColumns() {
        return starLinkColumns;
    }
    
	public boolean hasStarLinkColumn(){
		return starLinkColumns!=null && !starLinkColumns.isEmpty();
	}
    
    public void bindStarLinkColumn(TObjectName objectName) {
    	if(objectName == null){
    		return;
    	}
    	
    	String columnName = SQLUtil.getColumnName(objectName);
    	
    	if("*".equals(columnName)){
    		return;
    	}
        if (!starLinkColumns.containsKey(columnName)) {
        	starLinkColumns.putIfAbsent(columnName, new LinkedHashSet<>());
        }
        
        starLinkColumns.get(columnName).add(objectName);
    }
    
	public List<TObjectName> getStarLinkColumnList() {
		List<TObjectName> columns = new ArrayList<>();
		if (starLinkColumns != null) {
			for (Set<TObjectName> columnSet : starLinkColumns.values()) {
				columns.addAll(columnSet);
			}
		}
		return columns;
	}
	
	public List<String> getStarLinkColumnNames() {
		List<String> columns = new ArrayList<>();
		if (starLinkColumns != null) {
			columns.addAll(starLinkColumns.keySet());
		}
		return columns;
	}

	public boolean isShowStar() {
		return showStar;
	}

	public void setShowStar(boolean showStar) {
		this.showStar = showStar;
	}
	
	public Table getView() {
		return getTable();
	}

	public int getColumnIndex() {
		return columnIndex;
	}



}
