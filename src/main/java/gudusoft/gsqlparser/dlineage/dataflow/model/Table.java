
package gudusoft.gsqlparser.dlineage.dataflow.model;

import java.util.ArrayList;
import java.util.List;

import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.dlineage.util.Pair3;
import gudusoft.gsqlparser.dlineage.util.SQLUtil;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TTable;

public class Table {


    private int id;
    private String database;
    private String schema;
    private String name;
    private String fullName;
    private String alias;
    private String parent;
    private Pair3<Long, Long, String> startPosition;
    private Pair3<Long, Long, String> endPosition;
    private List<TableColumn> columns = new ArrayList<TableColumn>();
    private boolean subquery = false;

    private TTable tableObject;
    private TObjectName tableName;
    private boolean isCreateTable;
    private String tableType;
    private boolean isView;
    
    
    private TablePseudoRows pseudoRows = new TablePseudoRows(this);
    
	private TCustomSqlStatement viewObject;

	public Table(TCustomSqlStatement view, TObjectName viewName) {
		this(viewName);
		this.viewObject = view;
		setView(true);
	}

	public TCustomSqlStatement getViewObject() {
		return viewObject;
	}

    public Table(TTable table) {
        if (table == null) {
            throw new IllegalArgumentException("Table arguments can't be null.");
        }

        id = ++ModelBindingManager.get().TABLE_COLUMN_ID;

        this.tableObject = table;

        TSourceToken startToken = table.getStartToken();
        TSourceToken endToken = table.getEndToken();
        this.startPosition = new Pair3<Long, Long, String>(startToken.lineNo,
                startToken.columnNo, ModelBindingManager.getGlobalHash());
        this.endPosition = new Pair3<Long, Long, String>(endToken.lineNo,
                endToken.columnNo + endToken.astext.length(), ModelBindingManager.getGlobalHash());

        if (table.getLinkTable() != null) {
            this.fullName = table.getLinkTable().getFullName();
            this.name = table.getLinkTable().getName();
            this.alias = table.getName();
        }
        else if(table.getTableType() == ETableSource.function && table.getFuncCall()!=null) {
        	TFunctionCall function = table.getFuncCall();
        	this.fullName = function.getFunctionName().toString();
            this.name = function.getFunctionName().toString();
            this.alias = table.getAliasName();
        }
        else {
            this.fullName = table.getFullName();
			if (table.getTableName() != null) {
				this.name = table.getTableName().toString();
			} else {
				this.name = table.getName();
			}
            this.alias = table.getAliasName();
        }

        if (table.getSubquery() != null) {
            subquery = true;
        }
        
		if (!SQLUtil.isEmpty(table.getTableName().getSchemaString())) {
			this.schema = table.getTableName().getSchemaString();
		} else {
			this.schema = ModelBindingManager.getGlobalSchema();
		}
		
		if (!SQLUtil.isEmpty(table.getTableName().getDatabaseString())) {
			this.database = table.getTableName().getDatabaseString();
		} else {
			this.database = ModelBindingManager.getGlobalDatabase();
		}
		
		if(table.getTableType() == ETableSource.function){
			tableType = "function";
		}
		
    }
    
    public Table(TObjectName tableName) {
        if (tableName == null) {
            throw new IllegalArgumentException("Table arguments can't be null.");
        }

        id = ++ModelBindingManager.get().TABLE_COLUMN_ID;

        this.tableName = tableName;

        TSourceToken startToken = tableName.getStartToken();
        TSourceToken endToken = tableName.getEndToken();
        this.startPosition = new Pair3<Long, Long, String>(startToken.lineNo,
                startToken.columnNo, ModelBindingManager.getGlobalHash());
        this.endPosition = new Pair3<Long, Long, String>(endToken.lineNo,
                endToken.columnNo + endToken.astext.length(), ModelBindingManager.getGlobalHash());

        
        this.fullName = tableName.toString();
        this.name = tableName.getTableString();
		if (SQLUtil.isEmpty(name)) {
			name = tableName.toString();
		}
        
        if (!SQLUtil.isEmpty(tableName.getSchemaString())) {
			this.schema = tableName.getSchemaString();
		} else {
			this.schema = ModelBindingManager.getGlobalSchema();
		}
		
		if (!SQLUtil.isEmpty(tableName.getDatabaseString())) {
			this.database = tableName.getDatabaseString();
		} else {
			this.database = ModelBindingManager.getGlobalDatabase();
		}
	
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Pair3<Long, Long, String> getStartPosition() {
        return startPosition;
    }

    public Pair3<Long, Long, String> getEndPosition() {
        return endPosition;
    }

    public List<TableColumn> getColumns() {
        return columns;
    }

    public void addColumn(TableColumn column) {
        if (column != null && !this.columns.contains(column)) {
            this.columns.add(column);
        }
    }

    public TTable getTableObject() {
        return tableObject;
    }

    public String getAlias() {
        return alias;
    }

    public String getFullName() {
        return fullName;
    }

    public boolean hasSubquery() {
        return subquery;
    }

    public String getDisplayName() {
        return getFullName() + getAlias() != null ? " [" + getAlias() + "]"
                : "";
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

	public TObjectName getTableName() {
		return tableName;
	}

	public String getDatabase() {
		return database;
	}

	public String getSchema() {
		return schema;
	}
	
	public TablePseudoRows getPseudoRows(){
		return pseudoRows;
	}

	public boolean isCreateTable() {
		return isCreateTable;
	}

	public void setCreateTable(boolean isCreateTable) {
		this.isCreateTable = isCreateTable;
		if (isCreateTable) {
			for (TableColumn column : columns) {
				column.setShowStar(false);
			}
		}
	}

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public boolean isView() {
		return isView;
	}

	public void setView(boolean isView) {
		this.isView = isView;
	}
}
