
package demos.dlineage.dataflow.model;

import demos.dlineage.util.Pair;
import demos.dlineage.util.SQLUtil;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.ESetOperatorType;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.nodes.*;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.stmt.*;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@SuppressWarnings({
        "unchecked", "rawtypes"
})
public class ModelBindingManager {

    private final Map modelBindingMap = new LinkedHashMap();
    private final Map viewModelBindingMap = new LinkedHashMap();
    private final Map insertModelBindingMap = new LinkedHashMap();
    private final Map createModelBindingMap = new LinkedHashMap();
    private final Map createModelQuickBindingMap = new LinkedHashMap();
    private final Map mergeModelBindingMap = new LinkedHashMap();
    private final Map updateModelBindingMap = new LinkedHashMap();
    private final Map cursorModelBindingMap = new LinkedHashMap();
    private final Map tableNamesMap = new LinkedHashMap();
    private final Set<Relation> relationHolder = Collections.synchronizedSet(new LinkedHashSet<Relation>());

    private final Map<String, TTable> tableAliasMap = new LinkedHashMap();
    private final Set tableSet = new LinkedHashSet();
    
    public int TABLE_COLUMN_ID = 0;
    public Map<String, Integer> DISPLAY_ID = new LinkedHashMap<String, Integer>();
    public Map<Integer, String> DISPLAY_NAME = new LinkedHashMap<Integer, String>();
    public int RELATION_ID = 0;
    public int virtualTableIndex = -1;
    public Map<String, String> virtualTableNames = new LinkedHashMap<String, String>();

    private final static ThreadLocal localManager = new ThreadLocal();
    private final static ThreadLocal globalDatabase = new ThreadLocal();
    private final static ThreadLocal globalSchema = new ThreadLocal();
    private final static ThreadLocal globalVendor = new ThreadLocal();
    private final static ThreadLocal globalSQLEnv = new ThreadLocal();
    private final static ThreadLocal globalHash = new ThreadLocal();

    
    public static void set(ModelBindingManager modelManager) {
        if (localManager.get() == null) {
            localManager.set(modelManager);
        }
    }
    
    public static ModelBindingManager get() {
       return (ModelBindingManager)localManager.get();
    }
    
    public static void setGlobalDatabase(String database) {
        if (globalDatabase.get() == null && database!=null) {
        	globalDatabase.set(database);
        }
    }

    public static String getGlobalDatabase() {
        return (String) globalDatabase.get();
    }
    
    public static void removeGlobalDatabase() {
    	globalDatabase.remove();
    }
    
    public static void setGlobalSchema(String schema) {
        if (globalSchema.get() == null && schema!=null) {
        	globalSchema.set(schema);
        }
    }
    
    public static void removeGlobalSchema() {
    	globalSchema.remove();
    }

    public static String getGlobalSchema() {
        return (String) globalSchema.get();
    }
    
    public static void setGlobalHash(String hash) {
        if (globalHash.get() == null && hash!=null) {
        	globalHash.set(hash);
        }
    }
    
    public static void removeGlobalHash() {
    	globalHash.remove();
    }

    public static String getGlobalHash() {
        return (String) globalHash.get();
    }

    public static void setGlobalVendor(EDbVendor vendor) {
        if (globalVendor.get() == null && vendor!=null) {
        	globalVendor.set(vendor);
        }
    }
    
    public static void removeGlobalVendor() {
    	globalVendor.remove();
    }

    public static EDbVendor getGlobalVendor() {
        return (EDbVendor) globalVendor.get();
    }
    
    public static void setGlobalSQLEnv(TSQLEnv sqlenv) {
        if (globalSQLEnv.get() == null && sqlenv!=null) {
        	globalSQLEnv.set(sqlenv);
        }
    }
    
    public static void removeGlobalSQLEnv() {
    	globalSQLEnv.remove();
    }

    public static TSQLEnv getGlobalSQLEnv() {
        return (TSQLEnv) globalSQLEnv.get();
    }

    
    public static void remove() {
        localManager.remove();
        globalDatabase.remove();
        globalSchema.remove();
        globalVendor.remove();
    }

    public void bindModel(Object gspModel, Object relationModel) {
        modelBindingMap.put(gspModel, relationModel);

        TTable table = null;
        if (gspModel instanceof TTable) {
            table = ((TTable) gspModel);
        } else if (gspModel instanceof Table) {
            table = ((Table) gspModel).getTableObject();
        } else if (gspModel instanceof QueryTable) {
            table = ((QueryTable) gspModel).getTableObject();
        } else if (gspModel instanceof TCTE) {
            TTableList tables = ((TCTE) gspModel).getPreparableStmt().tables;
            for (int j = 0; j < tables.size(); j++) {
                TTable item = tables.getTable(j);
                if (item != null && item.getAliasName() != null) {
                    tableAliasMap.put(SQLUtil.getIdentifierNormalName(item.getAliasName()),
                            item);
                }

                if (item != null) {
                    tableSet.add(item);
                }
            }
        } else if (gspModel instanceof Pair
                && ((Pair) gspModel).first instanceof Table) {
            table = ((Table) ((Pair) gspModel).first).getTableObject();
        }

        if (table == null && relationModel instanceof QueryTable) {
            table = ((QueryTable) relationModel).getTableObject();
        }

        if (table != null && table.getAliasName() != null) {
            tableAliasMap.put(SQLUtil.getIdentifierNormalName(table.getAliasName()), table);
        }

        if (table != null) {
            tableSet.add(table);
        }
    }

    public Object getModel(Object gspModel) {
        if (gspModel == null) {
            return null;
        }

        if (gspModel instanceof TTable) {
            TTable table = (TTable) gspModel;
            if (table.getCTE() != null) {
                return modelBindingMap.get(table.getCTE());
            }
            if (table.getSubquery() != null
                    && !table.getSubquery().isCombinedQuery()) {
                return modelBindingMap
                        .get(table.getSubquery().getResultColumnList());
            }
        }
        if (gspModel instanceof TSelectSqlStatement) {
            TSelectSqlStatement select = (TSelectSqlStatement) gspModel;
            if (!select.isCombinedQuery()) {
                return modelBindingMap.get(select.getResultColumnList());
            }
        }
        Object result = modelBindingMap.get(gspModel);
        if (result == null) {
            result = createModelBindingMap.get(gspModel);
        }
        if (result == null) {
            result = insertModelBindingMap.get(gspModel);
        }
        if (result == null) {
            result = updateModelBindingMap.get(gspModel);
        }
        if (result == null) {
            result = mergeModelBindingMap.get(gspModel);
        }
        if (result == null) {
            result = viewModelBindingMap.get(gspModel);
        }
        if (result == null) {
            result = cursorModelBindingMap.get(gspModel);
        }
        
        if(result == null && gspModel instanceof TTable){
        	result = getCreateTable((TTable)gspModel);
        }
        
        if(result == null && gspModel instanceof TTable){
        	result = (Table) getCreateModel((TTable)gspModel);
        }
            
        if(result == null && gspModel instanceof TTable){
        	result = (Table) getTableByName(SQLUtil.getTableFullName(((TTable)gspModel).getTableName().toString()));
        }
        
        if(result == null && gspModel instanceof TTable){
        	result = (Table) getTableByName(SQLUtil.getTableFullName(((TTable)gspModel).getTableName().toString()));
        }
        	
        return result;
    }

    public void bindViewModel(Object gspModel, Object relationModel) {
        viewModelBindingMap.put(gspModel, relationModel);
    }

    public Object getViewModel(Object gspModel) {
        return viewModelBindingMap.get(gspModel);
    }

    public void bindUpdateModel(Object gspModel, Object relationModel) {
        updateModelBindingMap.put(gspModel, relationModel);
    }

    public Object getUpdateModel(Object gspModel) {
        return updateModelBindingMap.get(gspModel);
    }

    public void bindMergeModel(Object gspModel, Object relationModel) {
        mergeModelBindingMap.put(gspModel, relationModel);
    }

    public Object getMergeModel(Object gspModel) {
        return mergeModelBindingMap.get(gspModel);
    }

    public void bindInsertModel(Object gspModel, Object relationModel) {
        insertModelBindingMap.put(gspModel, relationModel);
    }

    public Object getInsertModel(Object gspModel) {
        return insertModelBindingMap.get(gspModel);
    }

    public Table getCreateTable(TTable table) {
        if (table != null && table.getFullName() != null) {
            // Iterator iter = createModelBindingMap.keySet( ).iterator( );
            // while ( iter.hasNext( ) )
            // {
            // TTable node = (TTable) iter.next( );
            // if ( node.getFullName( ).equals( table.getFullName( ) ) )
            // {
            // return (Table) createModelBindingMap.get( node );
            // }
            // }
            return (Table) createModelQuickBindingMap
                    .get(SQLUtil.getTableFullName(table.getTableName().toString()));
        }
        return null;
    }

    public Table getCreateModel(TTable table) {
        return (Table) createModelBindingMap.get(table);
    }

    public void bindCreateModel(TTable table, Table tableModel) {
        createModelBindingMap.put(table, tableModel);
        if (!createModelQuickBindingMap.containsKey(SQLUtil.getTableFullName(table.getTableName().toString()))) {
            createModelQuickBindingMap.put(SQLUtil.getTableFullName(table.getTableName().toString()), tableModel);
        }
    }

    public TObjectName[] getTableColumns(TTable table) {
        Table createTable = getCreateTable(table);
        if (createTable != null) {
            List<TableColumn> columnList = createTable.getColumns();
            TObjectName[] columns = new TObjectName[columnList.size()];
            for (int i = 0; i < columns.length; i++) {
                columns[i] = columnList.get(i).getColumnObject();
            }
            Arrays.sort(columns, new Comparator<TObjectName>() {

                @Override
                public int compare(TObjectName o1, TObjectName o2) {
                	if(o1 == null){
                		return -1;
                	}
                	if(o2 == null){
                		return 1;
                	}
                    return o1.getStartToken().posinlist
                            - o2.getStartToken().posinlist;
                }
            });
            return columns;
        }

        TObjectNameList list = table.getLinkedColumns();
        List<TObjectName> columns = new ArrayList<TObjectName>();

        if (table.getCTE() != null) {
            ResultSet resultSet = (ResultSet) getModel(table.getCTE());
            if (resultSet != null) {
                List<ResultColumn> columnList = resultSet.getColumns();
                for (int i = 0; i < columnList.size(); i++) {
                    ResultColumn resultColumn = columnList.get(i);
                    if (resultColumn
                            .getColumnObject() instanceof TResultColumn) {
                        TResultColumn columnObject = ((TResultColumn) resultColumn
                                .getColumnObject());
                        TAliasClause alias = columnObject.getAliasClause();
                        if (alias != null && alias.getAliasName() != null) {
                            columns.add(alias.getAliasName());
                        } else {
                            if (columnObject.getFieldAttr() != null) {
                                columns.add(columnObject.getFieldAttr());
                            } else {
                                continue;
                            }
                        }
                    } else if (resultColumn
                            .getColumnObject() instanceof TObjectName) {
                        columns.add(
                                (TObjectName) resultColumn.getColumnObject());
                    }
                }
            }
		} else if (list.size() == 1 && list.toString().indexOf("*") != -1 && table.getSubquery() != null) {
            addSubqueryColumns(table, columns);
        } else {
            for (int i = 0; i < list.size(); i++) {
				TObjectName object = list.getObjectName(i);
				if(object.toString().indexOf("*")!=-1 && table.getSubquery()!=null){
					addSubqueryColumns(table, columns);
				}
				columns.add(object);
            }
        }
        Collections.sort(columns, new Comparator<TObjectName>() {

            @Override
            public int compare(TObjectName o1, TObjectName o2) {
                return o1.getStartToken().posinlist
                        - o2.getStartToken().posinlist;
            }
        });
        return columns.toArray(new TObjectName[0]);
    }

	private void addSubqueryColumns(TTable table, List<TObjectName> columns) {
		ResultSet resultSet = (ResultSet) getModel(table.getSubquery());
		if (resultSet != null) {
		    List<ResultColumn> columnList = resultSet.getColumns();
		    for (int i = 0; i < columnList.size(); i++) {
		        ResultColumn resultColumn = columnList.get(i);
		        if (resultColumn
		                .getColumnObject() instanceof TResultColumn) {
		            TResultColumn columnObject = ((TResultColumn) resultColumn
		                    .getColumnObject());
		            TAliasClause alias = columnObject.getAliasClause();
		            if (alias != null && alias.getAliasName() != null) {
		                columns.add(alias.getAliasName());
		            } else {
		                if (columnObject.getFieldAttr() != null) {
		                    columns.add(columnObject.getFieldAttr());
		                } else {
		                    continue;
		                }
		            }
		        } else if (resultColumn
		                .getColumnObject() instanceof TObjectName) {
		            columns.add(
		                    (TObjectName) resultColumn.getColumnObject());
		        }
		    }
		}
	}

    public TTable getTable(TCustomSqlStatement stmt,
                           TObjectName column) {
    	if(column.getSourceTable()!=null){
    		return column.getSourceTable();
    	}
    	
        if (column.getTableString() != null
                && column.getTableString().trim().length() > 0) {
            TTable table = tableAliasMap
                    .get(SQLUtil.getIdentifierNormalName(column.getTableString()));

            if (table != null && table.getSubquery() != stmt)
                return table;
        }

		if (stmt.getTables() != null) {
			for (int j = 0; j < stmt.getTables().size(); j++) {
				 TTable table = (TTable)  stmt.getTables().getTable(j);
				 if (table.getSubquery() == stmt)
		                continue;

	            TObjectName[] columns = getTableColumns(table);
	            for (int i = 0; i < columns.length; i++) {
	                TObjectName columnName = columns[i];
	                if (columnName == null || "*".equals(columnName.getColumnNameOnly()))
	                    continue;
	                if (SQLUtil.getIdentifierNormalName(columnName.toString()).equals(SQLUtil.getIdentifierNormalName(column.toString()))) {
	                    if (columnName.getSourceTable() == null
	                            || columnName.getSourceTable() == table) {
	                        return table;
	                    }
	                }
	                if (columnName.getColumnNameOnly()!=null && SQLUtil.getIdentifierNormalName(columnName.getColumnNameOnly()).equals(SQLUtil.getIdentifierNormalName(column.getColumnNameOnly()))) {
	                    if (columnName.getSourceTable() == null
	                            || columnName.getSourceTable() == table) {
	                        return table;
	                    }
	                }
	            }
			}
		}
		
//        Iterator iter = tableSet.iterator();
//        while (iter.hasNext()) {
//            TTable table = (TTable) iter.next();
//
//            if (table.getSubquery() == stmt)
//                continue;
//            
//			if (table.getSubquery() != null) {
//				int start = table.getSubquery().getStartToken().posinlist;
//				int end = table.getSubquery().getEndToken().posinlist;
//				if (start <= stmt.getStartToken().posinlist && end >= stmt.getEndToken().posinlist) {
//					continue;
//				}
//			}
//
//            TObjectName[] columns = getTableColumns(table);
//            for (int i = 0; i < columns.length; i++) {
//                TObjectName columnName = columns[i];
//                if (columnName == null || "*".equals(columnName.getColumnNameOnly()))
//                    continue;
//                if (SQLUtil.getIdentifierNormalName(columnName.toString()).equals(SQLUtil.getIdentifierNormalName(column.toString()))) {
//                    if (columnName.getSourceTable() == null
//                            || columnName.getSourceTable() == table) {
//                        return table;
//                    }
//                }
//                if (columnName.getColumnNameOnly()!=null && SQLUtil.getIdentifierNormalName(columnName.getColumnNameOnly()).equals(SQLUtil.getIdentifierNormalName(column.getColumnNameOnly()))) {
//                    if (columnName.getSourceTable() == null
//                            || columnName.getSourceTable() == table) {
//                        return table;
//                    }
//                }
//            }
//        }
        return null;
    }

    public TTable guessTable( TCustomSqlStatement stmt,
                                     TObjectName column )
    {
        if ( column.getTableString( ) != null
                && column.getTableString( ).trim( ).length( ) > 0 )
        {
            TTable table = tableAliasMap.get(SQLUtil.getIdentifierNormalName(column.getTableString( )));

            if ( table != null && table.getSubquery( ) != stmt )
                return table;
        }

        Iterator iter = tableSet.iterator( );
        while ( iter.hasNext( ) )
        {
            TTable table = (TTable) iter.next( );

            if ( table.getSubquery( ) == stmt )
                continue;

            TObjectName[] columns = getTableColumns( table );
            for ( int i = 0; i < columns.length; i++ )
            {
                TObjectName columnName = columns[i];
                if ( "*".equals( columnName.getColumnNameOnly( ) ) )
                    continue;
                if ( columnName.toString( )
                        .equalsIgnoreCase( column.toString( ) ) )
                {
                    if ( columnName.getSourceTable( ) == null
                            || columnName.getSourceTable( ) == table )
                    {
                        return table;
                    }
                }
            }
        }
        return null;
    }

	public List<Table> getTablesByName() {
		List<Table> tables = new ArrayList<Table>();
		Iterator iter = tableNamesMap.values().iterator();
		while (iter.hasNext()) {
			tables.add((Table) iter.next());
		}
		return tables;
	}
    
    public List<TTable> getBaseTables() {
        List<TTable> tables = new ArrayList<TTable>();

        Iterator iter = modelBindingMap.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            if (!(key instanceof TTable)) {
                continue;
            }
            TTable table = (TTable) key;
            if (table.getSubquery() == null) {
                tables.add(table);
            }
        }

        iter = createModelBindingMap.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            if (!(key instanceof TTable)) {
                continue;
            }
            TTable table = (TTable) key;
            tables.add(table);
        }

        iter = insertModelBindingMap.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            if (!(key instanceof TTable)) {
                continue;
            }
            TTable table = (TTable) key;
            tables.add(table);
        }

        iter = mergeModelBindingMap.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            if (!(key instanceof TTable)) {
                continue;
            }
            TTable table = (TTable) key;
            tables.add(table);
        }

        iter = updateModelBindingMap.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            if (!(key instanceof TTable)) {
                continue;
            }
            TTable table = (TTable) key;
            tables.add(table);
        }

        return tables;
    }

    public List<TCustomSqlStatement> getViews() {
        List<TCustomSqlStatement> views = new ArrayList<TCustomSqlStatement>();

        Iterator iter = viewModelBindingMap.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            if (!(key instanceof TCreateViewSqlStatement) && !(key instanceof TCreateMaterializedSqlStatement)) {
                continue;
            }
            TCustomSqlStatement view = (TCustomSqlStatement) key;
            views.add(view);
        }
        return views;
    }

    public List<TResultColumnList> getSelectResultSets() {
        List<TResultColumnList> resultSets = new ArrayList<TResultColumnList>();

        Iterator iter = modelBindingMap.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            if (!(key instanceof TResultColumnList)) {
                continue;
            }
            TResultColumnList resultset = (TResultColumnList) key;
            resultSets.add(resultset);
        }
        return resultSets;
    }

    public List<TSelectSqlStatement> getSelectSetResultSets() {
        List<TSelectSqlStatement> resultSets = new ArrayList<TSelectSqlStatement>();

        Iterator iter = modelBindingMap.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            if (!(key instanceof TSelectSqlStatement)) {
                continue;
            }

            TSelectSqlStatement stmt = (TSelectSqlStatement) key;
            if (stmt.getSetOperatorType() == ESetOperatorType.none)
                continue;

            resultSets.add(stmt);
        }
        return resultSets;
    }

    public List<TCTE> getCTEs() {
        List<TCTE> resultSets = new ArrayList<TCTE>();

        Iterator iter = modelBindingMap.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            if (!(key instanceof TCTE)) {
                continue;
            }

            TCTE cte = (TCTE) key;
            resultSets.add(cte);
        }
        return resultSets;
    }

    public List<TTable> getTableWithSelectSetResultSets() {
        List<TTable> resultSets = new ArrayList<TTable>();

        Iterator iter = modelBindingMap.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            if (!(key instanceof TTable)) {
                continue;
            }

            if (((TTable) key).getSubquery() == null)
                continue;
            TSelectSqlStatement stmt = ((TTable) key).getSubquery();
            if (stmt.getSetOperatorType() == ESetOperatorType.none)
                continue;

            resultSets.add((TTable) key);
        }
        return resultSets;
    }

    public List<TParseTreeNode> getMergeResultSets() {
        List<TParseTreeNode> resultSets = new ArrayList<TParseTreeNode>();

        Iterator iter = modelBindingMap.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            if (!(key instanceof TMergeUpdateClause)
                    && !(key instanceof TMergeInsertClause)) {
                continue;
            }
            TParseTreeNode resultset = (TParseTreeNode) key;
            resultSets.add(resultset);
        }
        return resultSets;
    }

    public List<TParseTreeNode> getUpdateResultSets() {
        List<TParseTreeNode> resultSets = new ArrayList<TParseTreeNode>();

        Iterator iter = modelBindingMap.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            if (!(key instanceof TUpdateSqlStatement)) {
                continue;
            }
            TParseTreeNode resultset = (TParseTreeNode) key;
            resultSets.add(resultset);
        }
        return resultSets;
    }
    
    public List<TParseTreeNode> getFunctoinCalls() {
        List<TParseTreeNode> resultSets = new ArrayList<TParseTreeNode>();

        Iterator iter = modelBindingMap.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            if (!(key instanceof TFunctionCall) && !(key instanceof TCaseExpression)) {
                continue;
            }
            TParseTreeNode resultset = (TParseTreeNode) key;
            resultSets.add(resultset);
        }
        return resultSets;
    }
    
    public List<TParseTreeNode> getCursors() {
        List<TParseTreeNode> resultSets = new ArrayList<TParseTreeNode>();

        Iterator iter = modelBindingMap.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            if (!(key instanceof TCursorDeclStmt)) {
                continue;
            }
            TParseTreeNode resultset = (TParseTreeNode) key;
            resultSets.add(resultset);
        }
        return resultSets;
    }

    public void addRelation(Relation relation) {
        if (relation != null && !relationHolder.contains(relation)) {
            relationHolder.add(relation);
        }
    }

    public Relation[] getRelations() {
        return relationHolder.toArray(new Relation[0]);
    }

    public void reset() {
        relationHolder.clear();
        modelBindingMap.clear();
        viewModelBindingMap.clear();
        insertModelBindingMap.clear();
        mergeModelBindingMap.clear();
        updateModelBindingMap.clear();
        createModelBindingMap.clear();
        createModelBindingMap.clear();
        createModelQuickBindingMap.clear();
        tableSet.clear();
        tableAliasMap.clear();
        cursorModelBindingMap.clear();
        tableNamesMap.clear();
    }

    public void bindCursorModel(TCursorDeclStmt stmt,
                                CursorResultSet resultSet) {
        createModelBindingMap.put(SQLUtil.getIdentifierNormalName(stmt.getCursorName().toString()),
                resultSet);
    }

    public void bindCursorIndex(TObjectName indexName,
                                TObjectName cursorName) {
        bindModel(SQLUtil.getIdentifierNormalName(indexName.toString()),
                modelBindingMap.get(((CursorResultSet) createModelBindingMap
                        .get(SQLUtil.getIdentifierNormalName(cursorName.toString())))
                        .getResultColumnObject()));
    }
    
	public List<TStoredProcedureSqlStatement> getProcedures() {
		List<TStoredProcedureSqlStatement> procedures = new ArrayList();
		Iterator iter = this.modelBindingMap.keySet().iterator();

		while (iter.hasNext()) {
			Object key = iter.next();
			if (key instanceof TStoredProcedureSqlStatement) {
				TStoredProcedureSqlStatement procedure = (TStoredProcedureSqlStatement) key;
				procedures.add(procedure);
			}
		}

		return procedures;
	}

//	public void bindTableByName(TObjectName tableName, Table tableModel) {
//		tableNamesMap.put(tableName, tableModel);
//		
//	}
//	
	public void bindTableByName(String tableName, Table tableModel) {
		tableNamesMap.put(tableName, tableModel);
		
	}

//	public Table getTableByName(TObjectName tableName) {
//		return (Table)tableNamesMap.get(tableName);
//	}
	
	public Table getTableByName(String tableName) {
		return (Table)tableNamesMap.get(tableName);
	}

}
