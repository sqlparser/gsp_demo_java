
package demos.dlineage.dataflow.model;

import java.util.LinkedHashMap;
import java.util.Map;

import demos.dlineage.util.Pair;
import demos.dlineage.util.SQLUtil;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.nodes.TCaseExpression;
import gudusoft.gsqlparser.nodes.TConstant;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TObjectNameList;
import gudusoft.gsqlparser.nodes.TParameterDeclaration;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TWhenClauseItemList;
import gudusoft.gsqlparser.stmt.TCursorDeclStmt;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TStoredProcedureSqlStatement;

public class ModelFactory {

    private ModelBindingManager modelManager;

    public ModelFactory(ModelBindingManager modelManager) {
        this.modelManager = modelManager;
    }

    public SelectResultSet createResultSet(TSelectSqlStatement select,
                                           boolean isTarget) {
        if (modelManager.getModel(select.getResultColumnList()) instanceof ResultSet) {
            return (SelectResultSet) modelManager.getModel(select.getResultColumnList());
        }
        SelectResultSet resultSet = new SelectResultSet(select, isTarget);
        modelManager.bindModel(select.getResultColumnList(), resultSet);
        return resultSet;
    }

    public ResultSet createResultSet(TParseTreeNode gspObject,
                                     boolean isTarget) {
        if (modelManager.getModel(gspObject) instanceof ResultSet) {
            return (ResultSet) modelManager.getModel(gspObject);
        }
        ResultSet resultSet = new ResultSet(gspObject, isTarget);
        modelManager.bindModel(gspObject, resultSet);
        return resultSet;
    }

    public ResultColumn createResultColumn(ResultSet resultSet,
                                           TResultColumn resultColumn) {
        if (modelManager.getModel(resultColumn) instanceof ResultColumn) {
            return (ResultColumn) modelManager.getModel(resultColumn);
        }
        ResultColumn column = new ResultColumn(resultSet, resultColumn);
        modelManager.bindModel(resultColumn, column);
        return column;
    }

    public ResultColumn createSelectSetResultColumn(
            ResultSet resultSet, ResultColumn resultColumn, int index) {
        if (modelManager.getModel(resultColumn) instanceof ResultColumn) {
            return (ResultColumn) modelManager.getModel(resultColumn);
        }
        ResultColumn column = new SelectSetResultColumn(resultSet,
                resultColumn, index);
        modelManager.bindModel(resultColumn, column);
        return column;
    }

    public ResultColumn createSelectSetResultColumn(
            ResultSet resultSet, TResultColumn resultColumn, int index) {
        SelectSetResultColumn column = new SelectSetResultColumn(resultSet,
                resultColumn, index);
        return column;
    }

    public ResultColumn createResultColumn(ResultSet resultSet,
                                           TObjectName resultColumn) {
        if (modelManager.getModel(resultColumn) instanceof ResultColumn) {
            return (ResultColumn) modelManager.getModel(resultColumn);
        }
        ResultColumn column = new ResultColumn(resultSet, resultColumn);
        modelManager.bindModel(resultColumn, column);
        return column;
    }
    
	public ResultColumn createResultColumn(ResultSet resultSet, TObjectName resultColumn, boolean forceCreate) {
		if (!forceCreate) {
			if (modelManager.getModel(resultColumn) instanceof ResultColumn) {
				return (ResultColumn) modelManager.getModel(resultColumn);
			}
		}
		ResultColumn column = new ResultColumn(resultSet, resultColumn);
		modelManager.bindModel(resultColumn, column);
		return column;
	}
    
	public FunctionResultColumn createFunctionResultColumn(Function function, TObjectName functionName) {
		if (modelManager.getModel(functionName) instanceof FunctionResultColumn) {
			return (FunctionResultColumn) modelManager.getModel(functionName);
		}
		FunctionResultColumn column = new FunctionResultColumn(function, functionName);
		modelManager.bindModel(functionName, column);
		return column;
	}
	
	public FunctionResultColumn createFunctionResultColumn(Function function, TWhenClauseItemList caseFunction) {
		if (modelManager.getModel(caseFunction) instanceof FunctionResultColumn) {
			return (FunctionResultColumn) modelManager.getModel(caseFunction);
		}
		FunctionResultColumn column = new FunctionResultColumn(function, caseFunction);
		modelManager.bindModel(caseFunction, column);
		return column;
	}

    public ResultColumn createMergeResultColumn(ResultSet resultSet,
                                                TObjectName resultColumn) {
        if (modelManager.getMergeModel(resultColumn) instanceof ResultColumn) {
            return (ResultColumn) modelManager.getMergeModel(resultColumn);
        }
        ResultColumn column = new ResultColumn(resultSet, resultColumn);
        modelManager.bindMergeModel(resultColumn, column);
        return column;
    }

    public ResultColumn createUpdateResultColumn(ResultSet resultSet,
                                                 TObjectName resultColumn) {
        if (modelManager.getUpdateModel(resultColumn) instanceof ResultColumn) {
            return (ResultColumn) modelManager.getUpdateModel(resultColumn);
        }
        ResultColumn column = new ResultColumn(resultSet, resultColumn);
        modelManager.bindUpdateModel(resultColumn, column);
        return column;
    }

    public ResultColumn createResultColumn(QueryTable queryTableModel,
                                           TResultColumn resultColumn) {
        if (modelManager.getModel(resultColumn) instanceof ResultColumn) {
            return (ResultColumn) modelManager.getModel(resultColumn);
        }
        ResultColumn column = new ResultColumn(queryTableModel, resultColumn);
        modelManager.bindModel(resultColumn, column);
        return column;
    }

    public Table createTableFromCreateDDL(TTable table) {
        if (modelManager.getCreateModel(table) instanceof Table) {
            return (Table) modelManager.getCreateModel(table);
        }
        if (modelManager.getModel(table) instanceof Table) {
            return (Table) modelManager.getModel(table);
        }
        if (modelManager.getTableByName(SQLUtil.getTableFullName(table.getTableName().toString())) instanceof Table) {
        	return (Table) modelManager.getTableByName(SQLUtil.getTableFullName(table.getTableName().toString()));
        }
        Table tableModel = new Table(table);
        tableModel.setCreateTable(true);
        modelManager.bindCreateModel(table, tableModel);
        modelManager.bindTableByName(SQLUtil.getTableFullName(table.getTableName().toString()), tableModel);
        return tableModel;
    }

	public Table createTable(TTable table) {
		if (table.isLinkTable()) {
			table = table.getLinkTable();
		}
    	  
    	if(modelManager.getCreateTable(table)!=null) {
    		return modelManager.getCreateTable(table);
    	}
        if (modelManager.getModel(table) instanceof Table) {
            return (Table) modelManager.getModel(table);
        }
        if (modelManager.getTableByName(SQLUtil.getTableFullName(table.getTableName().toString())) instanceof Table) {
        	return (Table) modelManager.getTableByName(SQLUtil.getTableFullName(table.getTableName().toString()));
        }
      
        Table tableModel = new Table(table);
        modelManager.bindModel(table, tableModel);
        modelManager.bindTableByName(SQLUtil.getTableFullName(table.getTableName().toString()), tableModel);
        return tableModel;
    }
    
    public Table createTriggerOnTable(TTable table) {
    	if(modelManager.getCreateTable(table)!=null) {
    		return modelManager.getCreateTable(table);
    	}
        if (modelManager.getModel(table) instanceof Table) {
            return (Table) modelManager.getModel(table);
        }
        if (modelManager.getTableByName(SQLUtil.getTableFullName(table.getTableName().toString())) instanceof Table) {
        	return (Table) modelManager.getTableByName(SQLUtil.getTableFullName(table.getTableName().toString()));
        }
        Table tableModel = new Table(table);
        modelManager.bindModel(table, tableModel);
        modelManager.bindTableByName(SQLUtil.getTableFullName(table.getTableName().toString()), tableModel);
        return tableModel;
    }
    
    public Table createTableByName(TObjectName tableName) {
    	return createTableByName(tableName, false);
    }
    
    public Table createTableByName(TObjectName tableName, boolean create) {
        if (modelManager.getTableByName(SQLUtil.getTableFullName(tableName.toString())) instanceof Table) {
        	return (Table) modelManager.getTableByName(SQLUtil.getTableFullName(tableName.toString()));
        }
        Table tableModel = new Table(tableName);
        modelManager.bindTableByName(SQLUtil.getTableFullName(tableName.toString()), tableModel);
        if(create){
        	 modelManager.bindCreateModel(tableName, tableModel);
        }
        return tableModel;
    }
    
	public Function createFunction(TFunctionCall functionCall) {
		if (modelManager.getModel(functionCall) instanceof Function) {
			return (Function) modelManager.getModel(functionCall);
		}

		Function function = new Function(functionCall);
		modelManager.bindModel(functionCall, function);

		return function;
	}
	
	public Function createFunction(TCaseExpression caseExpression) {
		if (modelManager.getModel(caseExpression) instanceof Function) {
			return (Function) modelManager.getModel(caseExpression);
		}

		Function function = new Function(caseExpression);
		modelManager.bindModel(caseExpression, function);

		return function;
	}

    public QueryTable createQueryTable(TTable table) {
        QueryTable tableModel = null;

        if (table.getCTE() != null) {
            if (modelManager.getModel(table.getCTE()) instanceof QueryTable) {
                return (QueryTable) modelManager.getModel(table.getCTE());
            }

            tableModel = new QueryTable(table);

            modelManager.bindModel(table.getCTE(), tableModel);
        } else if (table.getAliasClause() != null
                && table.getAliasClause().getColumns() != null) {
            if (modelManager.getModel(table.getAliasClause()
                    .getColumns()) instanceof QueryTable) {
                return (QueryTable) modelManager
                        .getModel(table.getAliasClause().getColumns());
            }

            tableModel = new QueryTable(table);
            TObjectNameList columns = table.getAliasClause().getColumns();
            modelManager.bindModel(columns, tableModel);
            for (int i = 0; i < columns.size(); i++) {
                createResultColumn(tableModel,
                        columns.getObjectName(i));
            }
            modelManager.bindModel(table, tableModel);
        } else if (table.getSubquery() != null
                && !table.getSubquery().isCombinedQuery()) {
            if (modelManager.getModel(table.getSubquery()
                    .getResultColumnList()) instanceof QueryTable) {
                return (QueryTable) modelManager.getModel(table.getSubquery()
                        .getResultColumnList());
            }

            tableModel = new QueryTable(table);
            modelManager.bindModel(table.getSubquery()
                    .getResultColumnList(), tableModel);
        } else {
            if (modelManager.getModel(table) instanceof QueryTable) {
                return (QueryTable) modelManager.getModel(table);
            }
            tableModel = new QueryTable(table);
            modelManager.bindModel(table, tableModel);
        }
        return tableModel;
    }

    public TableColumn createTableColumn(Table table, TObjectName column, boolean fromCreateTable) {
        if (modelManager.getModel(new Pair<Table, TObjectName>(table,
                column)) instanceof TableColumn) {
            return (TableColumn) modelManager.getModel(new Pair<Table, TObjectName>(table,
                    column));
        }
        
		if (table.isCreateTable() && !fromCreateTable) {
			String columnName = SQLUtil.getIdentifierNormalName(column.getColumnNameOnly());
			for (int i = 0; i < table.getColumns().size(); i++) {
				TableColumn columnModel = table.getColumns().get(i);
				if(SQLUtil.getIdentifierNormalName(columnModel.getName()).equals(columnName)){
					return columnModel;
				}
			}
			//return null;
		}
        
//        if(!table.getColumns().isEmpty() && "*".equals(column.getColumnNameOnly())){
//        	return null;
//        }
        
        TableColumn columnModel = new TableColumn(table, column);
        modelManager.bindModel(new Pair<Table, TObjectName>(table,
                column), columnModel);

        if("*".equals(column.getColumnNameOnly()) && column.getSourceTable()!=null){
        	TObjectNameList columns = column.getSourceTable().getLinkedColumns();
        	Map<String, TObjectName> columnMap = new LinkedHashMap<>();
        	for(int i=0;i<columns.size();i++){
        		TObjectName item = columns.getObjectName(i);
        		String columnName = item.getColumnNameOnly();
        		if(item.getSourceTable().equals( column.getSourceTable()) && !"*".equals(columnName)){
        			columnMap.put(SQLUtil.getIdentifierNormalName(columnName), item);
        		}
        	}
        	for(int i=0;i<columns.size();i++){
        		TObjectName item = columns.getObjectName(i);
        		String columnName = item.getColumnNameOnly();
        		if(!item.getSourceTable().equals( column.getSourceTable()) && !"*".equals(columnName)){
        			columnMap.putIfAbsent(SQLUtil.getIdentifierNormalName(columnName), item);
        		}
        	}
        	columnModel.bindStarLinkColumns(new LinkedHashMap<>());
        }
        
        return columnModel;
    }

    public DataFlowRelation createDataFlowRelation() {
        DataFlowRelation relation = new DataFlowRelation();
        modelManager.addRelation(relation);
        return relation;
    }

    public TableColumn createTableColumn(Table table,
                                         TResultColumn column) {
        if (column.getAliasClause() != null
                && column.getAliasClause().getAliasName() != null) {
            TableColumn columnModel = new TableColumn(table,
                    column.getAliasClause().getAliasName());
            modelManager.bindModel(column, columnModel);
            return columnModel;
        }
        return null;
    }

    public RecordSetRelation createRecordSetRelation() {
        RecordSetRelation relation = new RecordSetRelation();
        modelManager.addRelation(relation);
        return relation;
    }

    public ImpactRelation createImpactRelation() {
        ImpactRelation relation = new ImpactRelation();
        modelManager.addRelation(relation);
        return relation;
    }

    public IndirectImpactRelation createIndirectImpactRelation() {
        IndirectImpactRelation relation = new IndirectImpactRelation();
        modelManager.addRelation(relation);
        return relation;
    }

    public JoinRelation createJoinRelation() {
        JoinRelation relation = new JoinRelation();
        modelManager.addRelation(relation);
        return relation;
    }
    
    public Table createView(TCustomSqlStatement viewStmt, TObjectName viewName) {
        return createView(viewStmt, viewName, false);
    }

    public Table createView(TCustomSqlStatement viewStmt, TObjectName viewName, boolean fromCreateView) {
        if (modelManager.getViewModel(viewStmt) instanceof Table) {
        	Table table = (Table) modelManager.getViewModel(viewStmt);
        	table.setView(true);
            return table;
        }
        
        if(modelManager.getTableByName(SQLUtil.getTableFullName(viewName.toString()))!=null){
        	Table table = modelManager.getTableByName(SQLUtil.getTableFullName(viewName.toString()));
        	table.setView(true);
        	return table;
        }
        
        Table viewModel = new Table(viewStmt, viewName);
        viewModel.setCreateTable(fromCreateView);
        viewModel.setView(true);
        modelManager.bindViewModel(viewStmt, viewModel);
        modelManager.bindTableByName(SQLUtil.getTableFullName(viewName.toString()), viewModel);
        return viewModel;
    }

    public TableColumn createViewColumn(Table viewModel,
                                       TObjectName column, int index, boolean fromCreateView) {
        Pair<Table, TObjectName> bindingModel = new Pair<Table, TObjectName>(
                viewModel, column);
        if (modelManager.getViewModel(bindingModel) instanceof TableColumn) {
            return (TableColumn) modelManager.getViewModel(bindingModel);
        }
        
		if (viewModel.isCreateTable() && !fromCreateView) {
			if(index < viewModel.getColumns().size()){
				return (TableColumn) viewModel.getColumns().get(index);
			}
			else{
				return null;
			}
		}
		
		TableColumn columnModel = new TableColumn(viewModel, column, index);
        modelManager.bindViewModel(bindingModel, columnModel);
        return columnModel;
    }

    public TableColumn createInsertTableColumn(Table tableModel,
                                               TObjectName column) {
        Pair<Table, TObjectName> bindingModel = new Pair<Table, TObjectName>(
                tableModel, column);
        if (modelManager
                .getInsertModel(bindingModel) instanceof TableColumn) {
            return (TableColumn) modelManager
                    .getInsertModel(bindingModel);
        }
        TableColumn columnModel = new TableColumn(tableModel, column);
        modelManager.bindInsertModel(bindingModel, columnModel);
        return columnModel;
    }

    public TableColumn createInsertTableColumn(Table tableModel,
            TExpression column, int columnIndex) {
		Pair<Table, TExpression> bindingModel = new Pair<Table, TExpression>(
			tableModel, column);
			
		if (modelManager
			.getInsertModel(bindingModel) instanceof TableColumn) {
			return (TableColumn) modelManager
					.getInsertModel(bindingModel);
		}
		TableColumn columnModel = new TableColumn(tableModel,
				column,
				columnIndex);
		modelManager.bindInsertModel(bindingModel, columnModel);
		return columnModel;
    }
    
    public TableColumn createInsertTableColumn(Table tableModel,
                                               TConstant column, int columnIndex) {
        Pair<Table, TConstant> bindingModel = new Pair<Table, TConstant>(
                tableModel, column);

        if (modelManager
                .getInsertModel(bindingModel) instanceof TableColumn) {
            return (TableColumn) modelManager
                    .getInsertModel(bindingModel);
        }
        TableColumn columnModel = new TableColumn(tableModel,
                column,
                columnIndex);
        modelManager.bindInsertModel(bindingModel, columnModel);
        return columnModel;
    }

    public SelectSetResultSet createSelectSetResultSet(
            TSelectSqlStatement stmt) {
        if (modelManager.getModel(stmt) instanceof SelectSetResultSet) {
            return (SelectSetResultSet) modelManager.getModel(stmt);
        }
        SelectSetResultSet resultSet = new SelectSetResultSet(stmt, stmt.getParentStmt() == null);
        modelManager.bindModel(stmt, resultSet);
        return resultSet;
    }

    public ResultColumn createStarResultColumn(
            SelectResultSet resultSet,
            Pair<TResultColumn, TObjectName> starColumnPair) {
        if (modelManager.getModel(starColumnPair) instanceof ResultColumn) {
            return (ResultColumn) modelManager.getModel(starColumnPair);
        }
        ResultColumn column = new ResultColumn(resultSet, starColumnPair);
        modelManager.bindModel(starColumnPair, column);
        return column;
    }

    public CursorResultSet createCursorResultSet(TCursorDeclStmt stmt) {
        if (modelManager.getModel(stmt) instanceof SelectSetResultSet) {
            return (CursorResultSet) modelManager.getModel(stmt);
        }
        CursorResultSet resultSet = new CursorResultSet(stmt);
        modelManager.bindModel(stmt, resultSet);
        return resultSet;
    }
    
	public Procedure createProcedure(TStoredProcedureSqlStatement stmt) {
		if (this.modelManager.getModel(stmt) instanceof Procedure) {
			return (Procedure) this.modelManager.getModel(stmt);
		} else {
			Procedure procedure = new Procedure(stmt);
			this.modelManager.bindModel(stmt, procedure);
			return procedure;
		}
	}

	public Argument createProcedureArgument(Procedure procedure, TParameterDeclaration parameter) {
		if (this.modelManager.getModel(parameter) instanceof Argument) {
			return (Argument) this.modelManager.getModel(parameter);
		} else {
			Argument argumentModel = new Argument(procedure, parameter);
			this.modelManager.bindModel(parameter, argumentModel);
			return argumentModel;
		}
	}

}
