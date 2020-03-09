
package demos.dlineage.dataflow.model;

import java.util.ArrayList;
import java.util.List;

import demos.dlineage.util.Pair;
import demos.dlineage.util.SQLUtil;
import gudusoft.gsqlparser.ETableSource;
import gudusoft.gsqlparser.TSourceToken;
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
    private Pair<Long, Long> startPosition;
    private Pair<Long, Long> endPosition;
    private List<TableColumn> columns = new ArrayList<TableColumn>();
    private boolean subquery = false;

    private TTable tableObject;
    private TObjectName tableName;
    
    
    private TablePseduoRows pseduoRows = new TablePseduoRows(this);
    

    public Table(TTable table) {
        if (table == null) {
            throw new IllegalArgumentException("Table arguments can't be null.");
        }

        id = ++ModelBindingManager.get().TABLE_COLUMN_ID;

        this.tableObject = table;

        TSourceToken startToken = table.getStartToken();
        TSourceToken endToken = table.getEndToken();
        this.startPosition = new Pair<Long, Long>(startToken.lineNo,
                startToken.columnNo);
        this.endPosition = new Pair<Long, Long>(endToken.lineNo,
                endToken.columnNo + endToken.astext.length());

        if (table.getLinkTable() != null) {
            this.fullName = table.getLinkTable().getFullName();
            this.name = table.getLinkTable().getName();
            this.alias = table.getName();
        }
        else if(table.getTableType() == ETableSource.function && table.getFuncCall()!=null) {
        	TFunctionCall function = table.getFuncCall();
        	this.fullName = function.toString();
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
		
    }
    
    public Table(TObjectName tableName) {
        if (tableName == null) {
            throw new IllegalArgumentException("Table arguments can't be null.");
        }

        id = ++ModelBindingManager.get().TABLE_COLUMN_ID;

        this.tableName = tableName;

        TSourceToken startToken = tableName.getStartToken();
        TSourceToken endToken = tableName.getEndToken();
        this.startPosition = new Pair<Long, Long>(startToken.lineNo,
                startToken.columnNo);
        this.endPosition = new Pair<Long, Long>(endToken.lineNo,
                endToken.columnNo + endToken.astext.length());

        
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

    public Pair<Long, Long> getStartPosition() {
        return startPosition;
    }

    public Pair<Long, Long> getEndPosition() {
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
	
	public TablePseduoRows getPseduoRows(){
		return pseduoRows;
	}

}
