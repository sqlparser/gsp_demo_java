
package demos.dlineage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import demos.dlineage.dataflow.listener.DataFlowHandleListener;
import demos.dlineage.dataflow.metadata.MetadataReader;
import demos.dlineage.dataflow.metadata.sqldep.SQLDepMetadataAnalyzer;
import demos.dlineage.dataflow.model.AbstractRelation;
import demos.dlineage.dataflow.model.Argument;
import demos.dlineage.dataflow.model.Constant;
import demos.dlineage.dataflow.model.ConstantRelationElement;
import demos.dlineage.dataflow.model.CursorResultSet;
import demos.dlineage.dataflow.model.DataFlowRelation;
import demos.dlineage.dataflow.model.EffectType;
import demos.dlineage.dataflow.model.Function;
import demos.dlineage.dataflow.model.FunctionResultColumn;
import demos.dlineage.dataflow.model.ImpactRelation;
import demos.dlineage.dataflow.model.IndirectImpactRelation;
import demos.dlineage.dataflow.model.JoinRelation;
import demos.dlineage.dataflow.model.JoinRelation.JoinClauseType;
import demos.dlineage.dataflow.model.ModelBindingManager;
import demos.dlineage.dataflow.model.ModelFactory;
import demos.dlineage.dataflow.model.Procedure;
import demos.dlineage.dataflow.model.PseudoRowsRelationElement;
import demos.dlineage.dataflow.model.QueryTable;
import demos.dlineage.dataflow.model.RecordSetRelation;
import demos.dlineage.dataflow.model.Relation;
import demos.dlineage.dataflow.model.RelationElement;
import demos.dlineage.dataflow.model.RelationType;
import demos.dlineage.dataflow.model.ResultColumn;
import demos.dlineage.dataflow.model.ResultColumnRelationElement;
import demos.dlineage.dataflow.model.ResultSet;
import demos.dlineage.dataflow.model.ResultSetPseudoRows;
import demos.dlineage.dataflow.model.SelectResultSet;
import demos.dlineage.dataflow.model.SelectSetResultColumn;
import demos.dlineage.dataflow.model.SelectSetResultSet;
import demos.dlineage.dataflow.model.SqlInfo;
import demos.dlineage.dataflow.model.Table;
import demos.dlineage.dataflow.model.TableColumn;
import demos.dlineage.dataflow.model.TableColumnRelationElement;
import demos.dlineage.dataflow.model.TablePseudoRows;
import demos.dlineage.dataflow.model.View;
import demos.dlineage.dataflow.model.ViewColumn;
import demos.dlineage.dataflow.model.ViewColumnRelationElement;
import demos.dlineage.dataflow.model.xml.argument;
import demos.dlineage.dataflow.model.xml.column;
import demos.dlineage.dataflow.model.xml.dataflow;
import demos.dlineage.dataflow.model.xml.procedure;
import demos.dlineage.dataflow.model.xml.relation;
import demos.dlineage.dataflow.model.xml.sourceColumn;
import demos.dlineage.dataflow.model.xml.table;
import demos.dlineage.dataflow.model.xml.targetColumn;
import demos.dlineage.sqlenv.SQLEnvParser;
import demos.dlineage.util.Pair;
import demos.dlineage.util.SHA256;
import demos.dlineage.util.SQLUtil;
import demos.dlineage.util.XML2Model;
import gudusoft.gsqlparser.EAlterTableOptionType;
import gudusoft.gsqlparser.EComparisonType;
import gudusoft.gsqlparser.EDbObjectType;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EExpressionType;
import gudusoft.gsqlparser.EJoinType;
import gudusoft.gsqlparser.ESetOperatorType;
import gudusoft.gsqlparser.ESqlClause;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.nodes.IExpressionVisitor;
import gudusoft.gsqlparser.nodes.TAliasClause;
import gudusoft.gsqlparser.nodes.TAlterTableOption;
import gudusoft.gsqlparser.nodes.TCTE;
import gudusoft.gsqlparser.nodes.TCaseExpression;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.nodes.TConstant;
import gudusoft.gsqlparser.nodes.TDeclareVariable;
import gudusoft.gsqlparser.nodes.TDeclareVariableList;
import gudusoft.gsqlparser.nodes.TExpression;
import gudusoft.gsqlparser.nodes.TExpressionList;
import gudusoft.gsqlparser.nodes.TFunctionCall;
import gudusoft.gsqlparser.nodes.TGroupByItem;
import gudusoft.gsqlparser.nodes.TGroupByItemList;
import gudusoft.gsqlparser.nodes.TInsertCondition;
import gudusoft.gsqlparser.nodes.TInsertIntoValue;
import gudusoft.gsqlparser.nodes.TJoin;
import gudusoft.gsqlparser.nodes.TJoinItem;
import gudusoft.gsqlparser.nodes.TMergeInsertClause;
import gudusoft.gsqlparser.nodes.TMergeUpdateClause;
import gudusoft.gsqlparser.nodes.TMergeWhenClause;
import gudusoft.gsqlparser.nodes.TMultiTarget;
import gudusoft.gsqlparser.nodes.TMultiTargetList;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TObjectNameList;
import gudusoft.gsqlparser.nodes.TOrderByItem;
import gudusoft.gsqlparser.nodes.TOrderByItemList;
import gudusoft.gsqlparser.nodes.TOutputClause;
import gudusoft.gsqlparser.nodes.TParameterDeclaration;
import gudusoft.gsqlparser.nodes.TParameterDeclarationList;
import gudusoft.gsqlparser.nodes.TParseTreeNode;
import gudusoft.gsqlparser.nodes.TResultColumn;
import gudusoft.gsqlparser.nodes.TResultColumnList;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TTableElement;
import gudusoft.gsqlparser.nodes.TTableElementList;
import gudusoft.gsqlparser.nodes.TTableList;
import gudusoft.gsqlparser.nodes.TTrimArgument;
import gudusoft.gsqlparser.nodes.TViewAliasClause;
import gudusoft.gsqlparser.nodes.TViewAliasItemList;
import gudusoft.gsqlparser.nodes.TWhenClauseItem;
import gudusoft.gsqlparser.nodes.TWhenClauseItemList;
import gudusoft.gsqlparser.nodes.couchbase.TObjectConstruct;
import gudusoft.gsqlparser.nodes.couchbase.TPair;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.sqlenv.TSQLSchema;
import gudusoft.gsqlparser.sqlenv.TSQLTable;
import gudusoft.gsqlparser.stmt.TAlterTableStatement;
import gudusoft.gsqlparser.stmt.TCreateMaterializedSqlStatement;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TCreateTriggerStmt;
import gudusoft.gsqlparser.stmt.TCreateViewSqlStatement;
import gudusoft.gsqlparser.stmt.TCursorDeclStmt;
import gudusoft.gsqlparser.stmt.TDeleteSqlStatement;
import gudusoft.gsqlparser.stmt.TInsertSqlStatement;
import gudusoft.gsqlparser.stmt.TLoopStmt;
import gudusoft.gsqlparser.stmt.TMergeSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;
import gudusoft.gsqlparser.stmt.TStoredProcedureSqlStatement;
import gudusoft.gsqlparser.stmt.TUpdateSqlStatement;
import gudusoft.gsqlparser.stmt.TUseDatabase;
import gudusoft.gsqlparser.stmt.mssql.TMssqlDeclare;
import gudusoft.gsqlparser.stmt.teradata.TTeradataCreateProcedure;
import gudusoft.gsqlparser.util.functionChecker;
import gudusoft.gsqlparser.util.keywordChecker;

public class DataFlowAnalyzer {

	private static final List<String> TERADATA_BUILTIN_FUNCTIONS = Arrays
			.asList(new String[] { "ACCOUNT", "CURRENT_DATE", "CURRENT_ROLE", "CURRENT_TIME", "CURRENT_TIMESTAMP",
					"CURRENT_USER", "DATABASE", "DATE", "PROFILE", "ROLE", "SESSION", "TIME", "USER", "SYSDATE", });

	private Stack<TCustomSqlStatement> stmtStack = new Stack<TCustomSqlStatement>();
	private List<ResultSet> appendResultSets = new ArrayList<ResultSet>();
	private Set<TCustomSqlStatement> accessedStatements = new HashSet<TCustomSqlStatement>();

	private File[] sqlFiles;
	private File sqlFile;
	private String sqlContent;
	private String[] sqlContents;
	private SqlInfo[] sqlInfos;
	private Map<String, List<SqlInfo>> sqlInfoMap = new LinkedHashMap<>();
	private EDbVendor vendor;
	private String dataflowString;
	private dataflow dataflowResult;
	private DataFlowHandleListener handleListener;
	private boolean simpleOutput;
	private boolean textFormat = false;
	private boolean showJoin = false;
	private boolean ignoreRecordSet = false;
	private TSQLEnv sqlenv = null;
	private ModelBindingManager modelManager = new ModelBindingManager();
	private ModelFactory modelFactory = new ModelFactory(modelManager);
	private List<Integer> tableIds = new ArrayList<Integer>();

	{
		ModelBindingManager.set(modelManager);
		ModelBindingManager.setGlobalStmtStack(stmtStack);
	}

	public DataFlowAnalyzer(String sqlContent, EDbVendor dbVendor, boolean simpleOutput) {
		this.sqlContent = sqlContent;
		this.vendor = dbVendor;
		this.simpleOutput = simpleOutput;
	}

	public DataFlowAnalyzer(String[] sqlContents, EDbVendor dbVendor, boolean simpleOutput) {
		this.sqlContents = sqlContents;
		this.vendor = dbVendor;
		this.simpleOutput = simpleOutput;
	}

	public DataFlowAnalyzer(SqlInfo[] sqlInfos, EDbVendor dbVendor, boolean simpleOutput) {
		this.sqlInfos = sqlInfos;
		this.vendor = dbVendor;
		this.simpleOutput = simpleOutput;
	}

	public DataFlowAnalyzer(File[] sqlFiles, EDbVendor dbVendor, boolean simpleOutput) {
		this.sqlFiles = sqlFiles;
		this.vendor = dbVendor;
		this.simpleOutput = simpleOutput;
	}

	public DataFlowAnalyzer(File sqlFile, EDbVendor dbVendor, boolean simpleOutput) {
		this.sqlFile = sqlFile;
		this.vendor = dbVendor;
		this.simpleOutput = simpleOutput;
	}

	public boolean isIgnoreRecordSet() {
		return ignoreRecordSet;
	}

	public void setIgnoreRecordSet(boolean ignoreRecordSet) {
		this.ignoreRecordSet = ignoreRecordSet;
	}

	public boolean isShowJoin() {
		return showJoin;
	}

	public void setShowJoin(boolean showJoin) {
		this.showJoin = showJoin;
	}

	public void setHandleListener(DataFlowHandleListener listener) {
		this.handleListener = listener;
	}

	public void setSqlEnv(TSQLEnv sqlenv) {
		this.sqlenv = sqlenv;
	}

	public synchronized String chechSyntax() {
		StringBuilder builder = new StringBuilder();
		if (sqlFile != null) {
			File[] children = listFiles(sqlFile);
			for (int i = 0; i < children.length; i++) {
				String text = SQLUtil.getFileContent(children[i].getAbsolutePath());
				String[] contents = SQLUtil.convertSQL(text);
				for (String content : contents) {
					if (content != null && content.trim().startsWith("{")) {
						JSONObject queryObject = JSON.parseObject(content);
						content = queryObject.getString("sourceCode");

					}
					if(MetadataReader.isMetadata(content)){
						continue;
					}
					TGSqlParser sqlparser = new TGSqlParser(vendor);
					sqlparser.sqltext = content;
					int result = sqlparser.parse();
					if (result != 0) {
						builder.append("Parsing " + children[i].getName()).append("occurs errors.\n")
								.append(sqlparser.getErrormessage()).append("\n");
					}
				}
			}

		} else if (sqlContent != null) {
			if (sqlContent != null && sqlContent.trim().startsWith("{")) {
				JSONObject queryObject = JSON.parseObject(sqlContent);
				sqlContent = queryObject.getString("sourceCode");
			}
			if(MetadataReader.isMetadata(sqlContent)){
				return builder.toString();
			}
			TGSqlParser sqlparser = new TGSqlParser(vendor);
			sqlparser.sqltext = sqlContent;
			int result = sqlparser.parse();
			if (result != 0) {
				builder.append(sqlparser.getErrormessage()).append("\n");
			}

		} else if (sqlContents != null) {
			for (int i = 0; i < sqlContents.length; i++) {
				String content = sqlContents[i];
				if (content != null && content.trim().startsWith("{")) {
					JSONObject queryObject = JSON.parseObject(content);
					content = queryObject.getString("sourceCode");
				}

				if (content == null) {
					continue;
				}

				if(MetadataReader.isMetadata(content)){
					continue;
				}
				TGSqlParser sqlparser = new TGSqlParser(vendor);
				sqlparser.sqltext = content;
				int result = sqlparser.parse();
				if (result != 0) {
					builder.append(sqlparser.getErrormessage()).append("\n");
				}
			}

		} else if (sqlFiles != null) {
			File[] children = sqlFiles;
			for (int i = 0; i < children.length; i++) {
				String content = SQLUtil.getFileContent(children[i].getAbsolutePath());
				if (content != null && content.trim().startsWith("{")) {
					JSONObject queryObject = JSON.parseObject(content);
					content = queryObject.getString("sourceCode");

				}
				if(MetadataReader.isMetadata(content)){
					continue;
				}
				TGSqlParser sqlparser = new TGSqlParser(vendor);
				sqlparser.sqltext = content;
				int result = sqlparser.parse();
				if (result != 0) {
					builder.append("Parsing " + children[i].getName()).append("occurs errors.\n")
							.append(sqlparser.getErrormessage()).append("\n");
				}
			}
		}
		return builder.toString();
	}

	public synchronized String generateDataFlow(StringBuffer errorMessage, boolean withExtraInfo) {
		if (ModelBindingManager.get() == null) {
			ModelBindingManager.set(modelManager);
		}
		PrintStream pw = null;
		ByteArrayOutputStream sw = null;
		PrintStream systemSteam = System.err;

		sw = new ByteArrayOutputStream();
		pw = new PrintStream(sw);
		System.setErr(pw);

		dataflow dataflow = analyzeSqlScript();
		
		if (dataflow!=null && !isShowJoin()) {
			ModelBindingManager.setGlobalVendor(vendor);
			dataflow = mergeTables(dataflow);
			ModelBindingManager.removeGlobalVendor();
		}
		
		if(dataflow!=null && !withExtraInfo){
			dataflow.getResultsets().forEach( t -> t.setIsTarget(null));
			dataflow.getResultsets().forEach(t->{
				t.getColumns().forEach(t1->t1.setIsFunction(null));
			});
		}
		
		if (dataflow != null) {
			if (textFormat) {
				dataflowString = getTextOutput(dataflow);
			} else {
				dataflowString = XML2Model.saveXML(dataflow);
			}
		}
				
		if (handleListener != null) {
			handleListener.endOutputDataFlowXML(dataflowString == null ? 0 : dataflowString.length());
		}

		if (pw != null) {
			pw.close();
		}

		System.setErr(systemSteam);

		if (sw != null) {
			if (errorMessage != null)
				errorMessage.append(sw.toString().trim());
		}

		return dataflowString;
	}
	
	public synchronized String generateDataFlow(StringBuffer errorMessage) {
		return generateDataFlow(errorMessage, false);
	}
	
	public synchronized String generateSqlInfos() {
		return JSON.toJSONString(sqlInfoMap);
	}

	private dataflow mergeTables(dataflow dataflow) {
		List<table> tableCopy = new ArrayList<table>();
		List<table> viewCopy = new ArrayList<table>();
		List<table> resultSetCopy = new ArrayList<table>();
		if (dataflow.getTables() != null) {
			tableCopy.addAll(dataflow.getTables());
		}
		dataflow.setTables(tableCopy);
		if (dataflow.getViews() != null) {
			viewCopy.addAll(dataflow.getViews());
		}
		dataflow.setViews(viewCopy);
		if (dataflow.getResultsets() != null) {
			resultSetCopy.addAll(dataflow.getResultsets());
		}
		dataflow.setResultsets(resultSetCopy);

		Map<String, List<table>> tableMap = new HashMap<String, List<table>>();
		Map<String, String> tableTypeMap = new HashMap<String, String>();
		Map<String, String> tableIdMap = new HashMap<String, String>();

		Map<String, List<column>> columnMap = new HashMap<>();
		Map<String, Set<String>> tableColumnMap = new HashMap<>();
		Map<String, String> columnIdMap = new HashMap<String, String>();
		Map<String, column> columnMergeIdMap = new HashMap<String, column>();

		List<table> tables = new ArrayList<table>();
		tables.addAll(dataflow.getTables());
		tables.addAll(dataflow.getViews());
		tables.addAll(dataflow.getResultsets());

		for (table table : tables) {
			String tableName = SQLUtil.getIdentifierNormalName(table.getFullName());
			if (!tableMap.containsKey(tableName)) {
				tableMap.put(tableName, new ArrayList<table>());
			}

			tableMap.get(tableName).add(table);

			if (!tableTypeMap.containsKey(tableName)) {
				tableTypeMap.put(tableName, table.getType());
			} else if ("view".equals(table.getTableType())) {
				tableTypeMap.put(tableName, table.getType());
			} else if ("table".equals(tableTypeMap.get(tableName))) {
				tableTypeMap.put(tableName, table.getType());
			}

			if (table.getColumns() != null) {
				tableColumnMap.putIfAbsent(tableName, new LinkedHashSet<>());
				for (column column : table.getColumns()) {
					String columnName = SQLUtil.getIdentifierNormalName(table.getFullName() + "." + column.getName());
					if (!SQLUtil.isEmpty(column.getQualifiedTable())) {
						columnName = SQLUtil.getIdentifierNormalName(
								table.getFullName() + "." + column.getQualifiedTable() + "." + column.getName());
					}

					if (!columnMap.containsKey(columnName)) {
						columnMap.put(columnName, new LinkedList<column>());
						tableColumnMap.get(tableName).add(columnName);
					}

					columnMap.get(columnName).add(column);
				}
			}
		}

		Iterator<String> tableNameIter = tableMap.keySet().iterator();
		while (tableNameIter.hasNext()) {
			String tableName = tableNameIter.next();
			List<table> tableList = tableMap.get(tableName);
			table table;
			if (tableList.size() > 1) {
				table firstTable = tableList.get(0);
				String type = tableTypeMap.get(tableName);
				table = new table();
				table.setId(String.valueOf(++modelManager.TABLE_COLUMN_ID));
				table.setDatabase(firstTable.getDatabase());
				table.setSchema(firstTable.getSchema());
				table.setName(firstTable.getName());
				table.setParent(firstTable.getParent());
				table.setColumns(new ArrayList<column>());
				table.setType(type);
				for (table item : tableList) {
					if (!SQLUtil.isEmpty(table.getCoordinate()) && !SQLUtil.isEmpty(item.getCoordinate())) {
						if (table.getCoordinate().indexOf(item.getCoordinate()) == -1) {
							table.setCoordinate(table.getCoordinate() + "," + item.getCoordinate());
						}
					} else if (!SQLUtil.isEmpty(item.getCoordinate())) {
						table.setCoordinate(item.getCoordinate());
					}

					if (!SQLUtil.isEmpty(table.getAlias()) && !SQLUtil.isEmpty(item.getAlias())) {
						table.setAlias(table.getAlias() + "," + item.getAlias());
					} else if (!SQLUtil.isEmpty(item.getAlias())) {
						table.setAlias(item.getAlias());
					}

					tableIdMap.put(item.getId(), table.getId());

					if (item.isView()) {
						dataflow.getViews().remove(item);
					} else if (item.isTable()) {
						dataflow.getTables().remove(item);
					} else if (item.isResultSet()) {
						dataflow.getResultsets().remove(item);
					}
				}

				if (table.isView()) {
					dataflow.getViews().add(table);
				} else if (table.isResultSet()) {
					dataflow.getResultsets().add(table);
				} else {
					dataflow.getTables().add(table);
				}
			} else {
				table = tableList.get(0);
			}

			Set<String> columns = tableColumnMap.get(tableName);
			Iterator<String> columnIter = columns.iterator();
			List<column> mergeColumns = new ArrayList<column>();
			while (columnIter.hasNext()) {
				String columnName = columnIter.next();
				List<column> columnList = columnMap.get(columnName);
				List<column> functions = columnList.stream().filter(t->Boolean.TRUE.toString().equals(t.getIsFunction())).collect(Collectors.toList());
				if(functions!=null && !functions.isEmpty()){
					for(column function: functions){
						mergeColumns.add(function);
						columnIdMap.put(function.getId(), function.getId());
						columnMergeIdMap.put(function.getId(), function);
					}
					
					columnList.removeAll(functions);
				}
				if (!columnList.isEmpty()) {
					column firstColumn = columnList.iterator().next();
					if (columnList.size() > 1) {
						column mergeColumn = new column();
						mergeColumn.setId(String.valueOf(++modelManager.TABLE_COLUMN_ID));
						mergeColumn.setName(firstColumn.getName());
						mergeColumn.setSource(firstColumn.getSource());
						mergeColumn.setQualifiedTable(firstColumn.getQualifiedTable());
						mergeColumns.add(mergeColumn);
						for (column item : columnList) {
							if (!SQLUtil.isEmpty(mergeColumn.getCoordinate())
									&& !SQLUtil.isEmpty(item.getCoordinate())) {
								if (mergeColumn.getCoordinate().indexOf(item.getCoordinate()) == -1) {
									mergeColumn.setCoordinate(mergeColumn.getCoordinate() + "," + item.getCoordinate());
								}
							} else if (!SQLUtil.isEmpty(item.getCoordinate())) {
								mergeColumn.setCoordinate(item.getCoordinate());
							}
							columnIdMap.put(item.getId(), mergeColumn.getId());
						}
						columnMergeIdMap.put(mergeColumn.getId(), mergeColumn);
					} else {
						mergeColumns.add(firstColumn);
						columnIdMap.put(firstColumn.getId(), firstColumn.getId());
						columnMergeIdMap.put(firstColumn.getId(), firstColumn);
					}
				}
			}
			table.setColumns(mergeColumns);
		}

		if (dataflow.getRelations() != null) {
			Map<String, relation> mergeRelations = new LinkedHashMap<String, relation>();
			for (relation relation : dataflow.getRelations()) {
				targetColumn target = relation.getTarget();
				if (target != null && tableIdMap.containsKey(target.getParent_id())) {
					target.setParent_id(tableIdMap.get(target.getParent_id()));
				}

				if (columnIdMap.containsKey(target.getId())) {
					target.setId(columnIdMap.get(target.getId()));
					target.setCoordinate(columnMergeIdMap.get(target.getId()).getCoordinate());
				}

				List<sourceColumn> sources = relation.getSources();
				Set<sourceColumn> sourceSet = new LinkedHashSet<>();
				if (sources != null) {
					for (sourceColumn source : sources) {
						if (tableIdMap.containsKey(source.getParent_id())) {
							source.setParent_id(tableIdMap.get(source.getParent_id()));
						}
						if (tableIdMap.containsKey(source.getSource_id())) {
							source.setSource_id(tableIdMap.get(source.getSource_id()));
						}
						if (columnIdMap.containsKey(source.getId())) {
							source.setId(columnIdMap.get(source.getId()));
							source.setCoordinate(columnMergeIdMap.get(source.getId()).getCoordinate());
						}
					}

					sourceSet.addAll(sources);
					relation.setSources(new ArrayList<>(sourceSet));
				}

				JSONObject relationJSON = (JSONObject) JSON.toJSON(relation);
				String jsonString = relationJSON.toJSONString().replaceAll("\"id\":\".+?\"", "");
				String key = SHA256.getMd5(SQLUtil.getIdentifierNormalName(jsonString));
				if (!mergeRelations.containsKey(key)) {
					mergeRelations.put(key, relation);
				}
			}

			dataflow.setRelations(new ArrayList<>(mergeRelations.values()));
		}

		tableMap.clear();
		tableTypeMap.clear();
		tableIdMap.clear();
		columnMap.clear();
		tableColumnMap.clear();
		columnIdMap.clear();
		columnMergeIdMap.clear();
		tables.clear();

		return dataflow;
	}

	public synchronized dataflow getDataFlow() {
		if (dataflowResult != null) {
			return dataflowResult;
		} else if (dataflowString != null) {
			dataflowResult = XML2Model.loadXML(dataflow.class, dataflowString);
			return dataflowResult;
		}
		return null;
	}

	private File[] listFiles(File sqlFiles) {
		List<File> children = new ArrayList<File>();
		if (sqlFiles != null)
			listFiles(sqlFiles, children);
		return children.toArray(new File[0]);
	}

	private void listFiles(File rootFile, List<File> children) {
		if (handleListener != null && handleListener.isCanceled()) {
			return;
		}

		if (rootFile.isFile())
			children.add(rootFile);
		else {
			File[] files = rootFile.listFiles();
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					listFiles(files[i], children);
				}
			}
		}
	}

	private synchronized dataflow analyzeSqlScript() {
		init();

		try {

			dataflow dataflow = new dataflow();

			if (sqlFile != null) {
				File[] children = listFiles(sqlFile);

				if (handleListener != null) {
					if (sqlFile.isDirectory()) {
						handleListener.startAnalyze(sqlFile, children.length, true);
					} else {
						handleListener.startAnalyze(sqlFile, sqlFile.length(), false);
					}
				}
				
				for (int i = 0; i < children.length; i++) {
					if (handleListener != null && handleListener.isCanceled()) {
						break;
					}

					String text = SQLUtil.getFileContent(children[i].getAbsolutePath());
					TSQLEnv fileSQLEnv = sqlenv;
					if (fileSQLEnv == null) {
						fileSQLEnv = SQLEnvParser.getSQLEnv(vendor, text);
					}
					String[] contents = SQLUtil.convertSQL(text);
					for (String content : contents) {
						ModelBindingManager.removeGlobalHash();
						ModelBindingManager.removeGlobalDatabase();
						ModelBindingManager.removeGlobalSchema();
						ModelBindingManager.removeGlobalSQLEnv();

						if (content != null && content.trim().startsWith("{")) {
							JSONObject queryObject = JSON.parseObject(content);
							content = queryObject.getString("sourceCode");
							ModelBindingManager.setGlobalDatabase(queryObject.getString("database"));
							ModelBindingManager.setGlobalSchema(queryObject.getString("schema"));
						}

						if (handleListener != null) {
							handleListener.startParse(children[i], content.length(), i);
						}

						TGSqlParser sqlparser = new TGSqlParser(vendor);
						sqlparser.setSqlEnv(fileSQLEnv);
						if (sqlenv != null) {
							sqlparser.setSqlEnv(sqlenv);
							ModelBindingManager.setGlobalSQLEnv(sqlenv);
						}
						sqlparser.sqltext = content;
						// SQLUtil.writeToFile(new File("D:\\null.txt"),
						// content);
						analyzeAndOutputResult(sqlparser);
					}
				}

				appendProcedures(dataflow);
				appendTables(dataflow);
				appendViews(dataflow);
				appendResultSets(dataflow);
				appendRelations(dataflow);

			} else if (sqlContent != null) {
				if (handleListener != null) {
					handleListener.startAnalyze(null, sqlContent.length(), false);
				}

				if (handleListener != null) {
					handleListener.startParse(null, sqlContent.length(), 0);
				}

				ModelBindingManager.removeGlobalDatabase();
				ModelBindingManager.removeGlobalSchema();

				if (sqlContent != null && sqlContent.trim().startsWith("{")) {
					JSONObject queryObject = JSON.parseObject(sqlContent);
					sqlContent = queryObject.getString("sourceCode");
					ModelBindingManager.setGlobalDatabase(queryObject.getString("database"));
					ModelBindingManager.setGlobalSchema(queryObject.getString("schema"));
				}

				TGSqlParser sqlparser = new TGSqlParser(vendor);
				if (sqlenv != null) {
					sqlparser.setSqlEnv(sqlenv);
					ModelBindingManager.setGlobalSQLEnv(sqlenv);
				}
				sqlparser.sqltext = sqlContent;
				analyzeAndOutputResult(sqlparser);

				appendProcedures(dataflow);
				appendTables(dataflow);
				appendViews(dataflow);
				appendResultSets(dataflow);
				appendRelations(dataflow);

			} else if (sqlContents != null) {
				if (handleListener != null) {
					if (sqlContents.length == 1) {
						handleListener.startAnalyze(null, sqlContents[0].length(), false);
					} else {
						handleListener.startAnalyze(null, sqlContents.length, true);
					}
				}

				if (sqlenv != null) {
					Map<String, StringBuilder> databaseMap = new LinkedHashMap<String, StringBuilder>();
					for (int i = 0; i < sqlContents.length; i++) {
						String content = sqlContents[i];
						if (content != null && content.trim().startsWith("{")) {
							JSONObject queryObject = JSON.parseObject(content);
							content = queryObject.getString("sourceCode");
							String database = queryObject.getString("database");
							String schema = queryObject.getString("schema");
							String group = database + "." + schema;
							databaseMap.putIfAbsent(group, new StringBuilder());
							databaseMap.get(group).append(content).append(";\r\n");
						}
					}

					Iterator<String> schemaIter = databaseMap.keySet().iterator();
					while (schemaIter.hasNext()) {
						if (handleListener != null && handleListener.isCanceled()) {
							break;
						}
						String group = schemaIter.next();
						String[] split = group.split("\\.");

						ModelBindingManager.removeGlobalDatabase();
						ModelBindingManager.removeGlobalSchema();
						ModelBindingManager.removeGlobalSQLEnv();

						ModelBindingManager.setGlobalDatabase(split[0]);
						ModelBindingManager.setGlobalSchema(split[1]);
						if (handleListener != null) {
							handleListener.startParse(null, databaseMap.get(group).length(), 0);
						}

						TGSqlParser sqlparser = new TGSqlParser(vendor);
						sqlparser.setSqlEnv(sqlenv);
						ModelBindingManager.setGlobalSQLEnv(sqlenv);
						sqlparser.sqltext = databaseMap.get(group).toString();
						analyzeAndOutputResult(sqlparser);
					}
				} else {
					for (int i = 0; i < sqlContents.length; i++) {
						if (handleListener != null && handleListener.isCanceled()) {
							break;
						}

						String content = sqlContents[i];

						ModelBindingManager.removeGlobalDatabase();
						ModelBindingManager.removeGlobalSchema();

						if (content != null && content.trim().startsWith("{")) {
							JSONObject queryObject = JSON.parseObject(content);
							content = queryObject.getString("sourceCode");
							ModelBindingManager.setGlobalDatabase(queryObject.getString("database"));
							ModelBindingManager.setGlobalSchema(queryObject.getString("schema"));
						}

						if (content == null) {
							continue;
						}

						if (handleListener != null) {
							handleListener.startParse(null, content.length(), 0);
						}

						TGSqlParser sqlparser = new TGSqlParser(vendor);
						sqlparser.sqltext = content;
						analyzeAndOutputResult(sqlparser);
					}
				}

				appendProcedures(dataflow);
				appendTables(dataflow);
				appendViews(dataflow);
				appendResultSets(dataflow);
				appendRelations(dataflow);

			} else if (sqlInfos != null) {
				if (handleListener != null) {
					if (sqlInfos.length == 1) {
						handleListener.startAnalyze(null, sqlInfos[0].getSql().length(), false);
					} else {
						handleListener.startAnalyze(null, sqlInfos.length, true);
					}
				}

				if (sqlenv != null) {
					Map<String, Pair<StringBuilder, AtomicInteger>> databaseMap = new LinkedHashMap<String, Pair<StringBuilder, AtomicInteger>>();
					for (int i = 0; i < sqlInfos.length; i++) {
						SqlInfo sqlInfo = sqlInfos[i];
						String sql = sqlInfo.getSql();
						if (sql != null && sql.trim().startsWith("{")) {
							JSONObject queryObject = JSON.parseObject(sql);
							String content = queryObject.getString("sourceCode");
							String database = queryObject.getString("database");
							String schema = queryObject.getString("schema");
							String group = database + "." + schema;
							String hash = SHA256.getMd5(group);
							databaseMap.putIfAbsent(group, new Pair<>(new StringBuilder(), new AtomicInteger()));
							StringBuilder buffer = new StringBuilder(content);
							if (content.trim().endsWith(";")) {
								buffer.append("\n");
							} else {
								buffer.append(";\n");
							}

							int lineStart = databaseMap.get(group).first.toString().split("\n").length;
							if(databaseMap.get(group).first.toString().length() == 0){
								lineStart = 0;
							}
							databaseMap.get(group).first.append(buffer.toString());
							SqlInfo sqlInfoItem = new SqlInfo();
							sqlInfoItem.setFileName(sqlInfo.getFileName());
							sqlInfoItem.setSql(buffer.toString());
							sqlInfoItem.setOriginIndex(i);
							sqlInfoItem.setOriginLineStart(0);
							sqlInfoItem.setOriginLineEnd(buffer.toString().split("\n").length - 1);
							sqlInfoItem.setIndex(databaseMap.get(group).second.getAndIncrement());
							sqlInfoItem.setLineStart(lineStart);
							sqlInfoItem.setLineEnd(databaseMap.get(group).first.toString().split("\n").length - 1);
							sqlInfoItem.setGroup(group);
							sqlInfoItem.setHash(hash);
							
							sqlInfoMap.putIfAbsent(hash, new ArrayList<>());
							sqlInfoMap.get(hash).add(sqlInfoItem);
						}
					}

					Iterator<String> schemaIter = databaseMap.keySet().iterator();
					while (schemaIter.hasNext()) {
						if (handleListener != null && handleListener.isCanceled()) {
							break;
						}
						String group = schemaIter.next();
						String[] split = group.split("\\.");

						ModelBindingManager.removeGlobalDatabase();
						ModelBindingManager.removeGlobalSchema();
						ModelBindingManager.removeGlobalSQLEnv();
						ModelBindingManager.removeGlobalHash();

						ModelBindingManager.setGlobalDatabase(split[0]);
						ModelBindingManager.setGlobalSchema(split[1]);
						if (handleListener != null) {
							handleListener.startParse(null, databaseMap.get(group).first.length(), 0);
						}

						TGSqlParser sqlparser = new TGSqlParser(vendor);
						sqlparser.setSqlEnv(sqlenv);
						ModelBindingManager.setGlobalSQLEnv(sqlenv);
						sqlparser.sqltext = databaseMap.get(group).first.toString();
						ModelBindingManager.setGlobalHash(SHA256.getMd5(group));
						analyzeAndOutputResult(sqlparser);
					}
				} else {
					for (int i = 0; i < sqlInfos.length; i++) {
						if (handleListener != null && handleListener.isCanceled()) {
							break;
						}

						SqlInfo sqlInfo = sqlInfos[i];
						String sql = sqlInfo.getSql();

						ModelBindingManager.removeGlobalDatabase();
						ModelBindingManager.removeGlobalSchema();
						ModelBindingManager.removeGlobalHash();

						String content = null;
						
						if (sql != null && sql.trim().startsWith("{")) {
							JSONObject queryObject = JSON.parseObject(sql);
							content = queryObject.getString("sourceCode");
							ModelBindingManager.setGlobalDatabase(queryObject.getString("database"));
							ModelBindingManager.setGlobalSchema(queryObject.getString("schema"));
						}
						else{
							content = sql;
						}

						if (content == null) {
							continue;
						}
						
						sqlInfo.setSql(content);

						if (handleListener != null) {
							handleListener.startParse(null, content.length(), 0);
						}
						
						if(MetadataReader.isMetadata(content)){
							String hash = SHA256.getMd5(content);
							ModelBindingManager.setGlobalHash(hash);
							dataflow temp = new SQLDepMetadataAnalyzer().analyzeMetadata(vendor, content);
							if (temp.getProcedures() != null) {
								dataflow.getProcedures().addAll(temp.getProcedures());
							}
							if (temp.getTables() != null) {
								dataflow.getTables().addAll(temp.getTables());
							}
							if (temp.getViews() != null) {
								dataflow.getViews().addAll(temp.getViews());
							}
							if (temp.getResultsets() != null) {
								dataflow.getResultsets().addAll(temp.getResultsets());
							}
							if (temp.getRelations() != null) {
								dataflow.getRelations().addAll(temp.getRelations());
							}
						}else{
							TGSqlParser sqlparser = new TGSqlParser(vendor);
							sqlparser.sqltext = content;
							String hash = SHA256.getMd5(sqlparser.sqltext);
							ModelBindingManager.setGlobalHash(hash);
							sqlInfo.setHash(hash);
							sqlInfo.setLineEnd(sqlparser.sqltext.split("\n").length-1);
							sqlInfo.setOriginLineEnd(sqlparser.sqltext.split("\n").length-1);
							sqlInfoMap.putIfAbsent(hash, new ArrayList<>());
							sqlInfoMap.get(hash).add(sqlInfo);
							analyzeAndOutputResult(sqlparser);
						}
					}
				}

				appendProcedures(dataflow);
				appendTables(dataflow);
				appendViews(dataflow);
				appendResultSets(dataflow);
				appendRelations(dataflow);

			} else if (sqlFiles != null) {
				if (handleListener != null) {
					if (sqlFiles.length == 1) {
						handleListener.startAnalyze(sqlFiles[0], sqlFiles[0].length(), false);
					} else {
						handleListener.startAnalyze(null, sqlFiles.length, true);
					}
				}

				File[] children = sqlFiles;
				for (int i = 0; i < children.length; i++) {
					if (handleListener != null && handleListener.isCanceled()) {
						break;
					}

					String content = SQLUtil.getFileContent(children[i].getAbsolutePath());

					ModelBindingManager.removeGlobalDatabase();
					ModelBindingManager.removeGlobalSchema();

					if (content != null && content.trim().startsWith("{")) {
						JSONObject queryObject = JSON.parseObject(content);
						content = queryObject.getString("sourceCode");
						ModelBindingManager.setGlobalDatabase(queryObject.getString("database"));
						ModelBindingManager.setGlobalSchema(queryObject.getString("schema"));
					}

					if (handleListener != null) {
						handleListener.startParse(children[i], content.length(), i);
					}

					TGSqlParser sqlparser = new TGSqlParser(vendor);
					if (sqlenv != null) {
						sqlparser.setSqlEnv(sqlenv);
						ModelBindingManager.setGlobalSQLEnv(sqlenv);
					}
					sqlparser.sqltext = content;
					analyzeAndOutputResult(sqlparser);
				}

				appendProcedures(dataflow);
				appendTables(dataflow);
				appendViews(dataflow);
				appendResultSets(dataflow);
				appendRelations(dataflow);
			}

			if (handleListener != null) {
				handleListener.endAnalyze();
				handleListener.startOutputDataFlowXML();
			}

			if (simpleOutput || ignoreRecordSet) {
				dataflow simpleDataflow = getSimpleDataflow(dataflow);
				simpleDataflow.getResultsets().forEach(t -> t.setIsTarget(null));
				return simpleDataflow;
			} else {
				return dataflow;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		modelManager.reset();
		return null;
	}

	private String getTextOutput(dataflow dataflow) {
		StringBuffer buffer = new StringBuffer();
		List<relation> relations = dataflow.getRelations();
		if (relations != null) {
			for (int i = 0; i < relations.size(); i++) {
				relation relation = relations.get(i);
				targetColumn target = relation.getTarget();
				List<sourceColumn> sources = relation.getSources();
				if (target != null && sources != null && sources.size() > 0) {
					buffer.append(target.getColumn()).append(" depends on: ");
					Set<String> columnSet = new LinkedHashSet<String>();
					for (int j = 0; j < sources.size(); j++) {
						sourceColumn sourceColumn = sources.get(j);
						String columnName = sourceColumn.getColumn();
						if (sourceColumn.getParent_name() != null && sourceColumn.getParent_name().length() > 0) {
							columnName = sourceColumn.getParent_name() + "." + columnName;
						}
						columnSet.add(columnName);
					}
					String[] columns = columnSet.toArray(new String[0]);
					for (int j = 0; j < columns.length; j++) {
						buffer.append(columns[j]);
						if (j == columns.length - 1) {
							buffer.append("\n");
						} else
							buffer.append(", ");
					}
				}
			}
		}
		return buffer.toString();
	}

	private String mergeRelationType(List<Pair<sourceColumn, List<String>>> typePaths) {
		RelationType relationType = RelationType.join;
		for (int i = 0; i < typePaths.size(); i++) {
			List<String> path = typePaths.get(i).second;
			RelationType type = RelationType.valueOf(getRelationType(path));
			if (type.ordinal() < relationType.ordinal()) {
				relationType = type;
			}
		}
		return relationType.name();
	}

	private String getRelationType(List<String> typePaths) {
		if (typePaths.contains("join"))
			return "join";
		if (typePaths.contains("fdr"))
			return "fdr";
		if (typePaths.contains("frd"))
			return "frd";
		if (typePaths.contains("fddi"))
			return "fddi";
		return "fdd";
	}

	public dataflow getSimpleDataflow(dataflow instance) throws Exception {
		ModelBindingManager.setGlobalVendor(vendor);
		targetTables.clear();
		resultSetMap.clear();
		tableMap.clear();
		viewMap.clear();
		dataflow simple = new dataflow();
		List<relation> simpleRelations = new ArrayList<relation>();
		List<relation> relations = instance.getRelations();
		instance.getResultsets().forEach(t -> resultSetMap.put(t.getId().toLowerCase(), t));
		instance.getTables().forEach(t -> tableMap.put(t.getId().toLowerCase(), t));
		instance.getViews().forEach(t -> viewMap.put(t.getId().toLowerCase(), t));
		if (relations != null) {
			// if (relations.size() > 1000) {
			// relations = relations.stream().filter(t ->
			// "fdd".equals(t.getType())).collect(Collectors.toList());
			// }
			Map<String, Set<relation>> targetIdRelationMap = new HashMap<>();
			for (relation relation : relations) {
				if (relation.getTarget() != null) {
					String key = relation.getTarget().getParent_id() + "." + relation.getTarget().getId();
					targetIdRelationMap.putIfAbsent(key, new HashSet<>());
					targetIdRelationMap.get(key).add(relation);
				}
			}

			for (int i = 0; i < relations.size(); i++) {
				relation relationElem = relations.get(i);
				targetColumn target = relationElem.getTarget();
				String targetParent = target.getParent_id();
				if (isTarget(instance, targetParent)) {
					List<Pair<sourceColumn, List<String>>> relationSources = new ArrayList<>();
					findSourceRaltions(instance, targetIdRelationMap, relationElem, relationSources,
							new String[] { relationElem.getType() });
					if (relationSources.size() > 0) {
						Map<sourceColumn, List<Pair<sourceColumn, List<String>>>> columnMap = relationSources.stream()
								.collect(Collectors.groupingBy(t -> ((Pair<sourceColumn, List<String>>) t).first));
						Iterator<sourceColumn> iter = columnMap.keySet().iterator();
						while (iter.hasNext()) {
							sourceColumn column = iter.next();
							relation simpleRelation = (relation) relationElem.clone();
							simpleRelation.setSources(Arrays.asList(column));
							simpleRelation.setType(mergeRelationType(columnMap.get(column)));
							simpleRelation.setId(String.valueOf(++ModelBindingManager.get().RELATION_ID));
							simpleRelations.add(simpleRelation);
						}
					}
				}
			}
		}
		simple.setProcedures(instance.getProcedures());
		simple.setTables(instance.getTables().stream().filter(t->!isMemoryTempTable(t.getName())).collect(Collectors.toList()));
		simple.setViews(instance.getViews());
		if (instance.getResultsets() != null) {
			List<table> resultSets = new ArrayList<table>();
			for (int i = 0; i < instance.getResultsets().size(); i++) {
				table resultSet = instance.getResultsets().get(i);
				if (resultSet.isTarget()) {
					resultSets.add(resultSet);
				}
			}
			simple.setResultsets(resultSets);
		}
		simple.setRelations(simpleRelations);
		targetTables.clear();
		resultSetMap.clear();
		tableMap.clear();
		viewMap.clear();
		return simple;
	}

	private void findSourceRaltions(dataflow instance, Map<String, Set<relation>> sourceIdRelationMap,
			relation targetRelation, List<Pair<sourceColumn, List<String>>> relationSources, String[] pathTypes) {
		findStarSourceRaltions(instance, null, sourceIdRelationMap, targetRelation, relationSources, pathTypes,
				new ArrayList<String>());
	}

	private void findStarSourceRaltions(dataflow instance, targetColumn starRelationTarget,
			Map<String, Set<relation>> sourceIdRelationMap, relation targetRelation,
			List<Pair<sourceColumn, List<String>>> relationSources, String[] pathTypes, List<String> paths) {
		if (targetRelation != null && targetRelation.getSources() != null) {
			for (int i = 0; i < targetRelation.getSources().size(); i++) {
				sourceColumn source = targetRelation.getSources().get(i);
				if (starRelationTarget != null && !"*".equals(source.getColumn())
						&& !SQLUtil.getIdentifierNormalName(starRelationTarget.getColumn())
								.equals(SQLUtil.getIdentifierNormalName(source.getColumn()))) {
					continue;
				}

				String sourceColumnId = source.getId();
				String sourceParentId = source.getParent_id();
				if (sourceParentId == null || sourceColumnId == null) {
					continue;
				}
				if (isTarget(instance, sourceParentId)) {
					relationSources.add(new Pair<sourceColumn, List<String>>(source, Arrays.asList(pathTypes)));
				} else {
					Set<relation> sourceRelations = sourceIdRelationMap
							.get(source.getParent_id() + "." + source.getId());
					if (sourceRelations != null) {
						if (paths.contains(source.getParent_id() + "." + source.getId())) {
							continue;
						} else {
							paths.add(source.getParent_id() + "." + source.getId());
						}
						for (relation relation : sourceRelations) {
							String[] types = new String[pathTypes.length + 1];
							types[0] = relation.getType();
							System.arraycopy(pathTypes, 0, types, 1, pathTypes.length);
							if (!"*".equals(source.getColumn())) {
								findStarSourceRaltions(instance, null, sourceIdRelationMap, relation, relationSources,
										types, paths);
							} else {
								findStarSourceRaltions(instance,
										starRelationTarget == null ? targetRelation.getTarget() : starRelationTarget,
										sourceIdRelationMap, relation, relationSources, types, paths);
							}
						}
					}
				}
			}
		}
	}

	private Map<String, Boolean> targetTables = new HashMap<String, Boolean>();
	private Map<String, table> resultSetMap = new HashMap<String, table>();
	private Map<String, table> tableMap = new HashMap<String, table>();
	private Map<String, table> viewMap = new HashMap<String, table>();

	private boolean isTarget(dataflow instance, String targetParentId) {
		if (targetTables.containsKey(targetParentId))
			return targetTables.get(targetParentId);
		if (isTable(instance, targetParentId)) {
			targetTables.put(targetParentId, true);
			return true;
		} else if (isView(instance, targetParentId)) {
			targetTables.put(targetParentId, true);
			return true;
		} else if (isTargetResultSet(instance, targetParentId)) {
			targetTables.put(targetParentId, true);
			return true;
		}
		targetTables.put(targetParentId, false);
		return false;
	}

	private boolean isTargetResultSet(dataflow instance, String targetParent) {
		if (resultSetMap.containsKey(targetParent.toLowerCase())) {
			return resultSetMap.get(targetParent.toLowerCase()).isTarget();
		}
		return false;
	}

	private boolean isView(dataflow instance, String targetParent) {
		if (viewMap.containsKey(targetParent.toLowerCase())) {
			return true;
		}
		return false;
	}

	private boolean isTable(dataflow instance, String targetParent) {
		if (tableMap.containsKey(targetParent.toLowerCase())) {
			if(isMemoryTempTable(tableMap.get(targetParent).getName())){
				return false;
			}
			return true;
		}
		return false;
	}
	
	private boolean isMemoryTempTable(String tableName) {	
		if(tableName.startsWith("@") || getColumnName(tableName).startsWith("@")){
			return true;
		}
		return false;
	}

	private void init() {
		sqlInfoMap.clear();
		dataflowString = null;
		dataflowResult = null;
		ModelBindingManager.removeGlobalDatabase();
		ModelBindingManager.removeGlobalSchema();
		ModelBindingManager.removeGlobalVendor();
		ModelBindingManager.removeGlobalSQLEnv();
		ModelBindingManager.removeGlobalHash();
		appendResultSets.clear();
		modelManager.TABLE_COLUMN_ID = 0;
		modelManager.RELATION_ID = 0;
		modelManager.DISPLAY_ID.clear();
		modelManager.DISPLAY_NAME.clear();
		tableIds.clear();
		ModelBindingManager.setGlobalVendor(vendor);
		modelManager.reset();
	}

	private void analyzeAndOutputResult(TGSqlParser sqlparser) {
		try {
			accessedStatements.clear();
			stmtStack.clear();

			try {
				int result = sqlparser.parse();
				if (result != 0) {
					System.err.println(sqlparser.getErrormessage());
				}

				if (handleListener != null) {
					handleListener.endParse(result == 0);
				}
			} catch (Exception e) {
				if (handleListener != null) {
					handleListener.endParse(false);
				}

				e.printStackTrace();
				return;
			}

			if (handleListener != null) {
				handleListener.startAnalyzeDataFlow(sqlparser.sqlstatements.size());
			}

			for (int i = 0; i < sqlparser.sqlstatements.size(); i++) {
				if (handleListener != null && handleListener.isCanceled()) {
					break;
				}

				if (handleListener != null) {
					handleListener.startAnalyzeStatment(i);
				}

				TCustomSqlStatement stmt = sqlparser.getSqlstatements().get(i);
				if (stmt.getErrorCount() == 0) {
					if (stmt.getParentStmt() == null) {
						if (stmt instanceof TUseDatabase 
								|| stmt instanceof TCreateTableSqlStatement
								|| stmt instanceof TMssqlDeclare) {
							analyzeCustomSqlStmt(stmt);
						}
					}
				}

				if (handleListener != null) {
					handleListener.endAnalyzeStatment(i);
				}
			}

			for (int i = 0; i < sqlparser.sqlstatements.size(); i++) {
				if (handleListener != null && handleListener.isCanceled()) {
					break;
				}

				if (handleListener != null) {
					handleListener.startAnalyzeStatment(i);
				}

				TCustomSqlStatement stmt = sqlparser.getSqlstatements().get(i);
				if (stmt.getErrorCount() == 0) {
					if (stmt.getParentStmt() == null) {
						if (stmt instanceof TUseDatabase 
								|| stmt instanceof TCreateViewSqlStatement) {
							analyzeCustomSqlStmt(stmt);
						}
					}
				}

				if (handleListener != null) {
					handleListener.endAnalyzeStatment(i);
				}
			}

			for (int i = 0; i < sqlparser.sqlstatements.size(); i++) {
				if (handleListener != null && handleListener.isCanceled()) {
					break;
				}

				if (handleListener != null) {
					handleListener.startAnalyzeStatment(i);
				}

				TCustomSqlStatement stmt = sqlparser.getSqlstatements().get(i);
				if (stmt.getErrorCount() == 0) {
					if (stmt.getParentStmt() == null) {
						if (!(stmt instanceof TCreateViewSqlStatement) && !(stmt instanceof TCreateViewSqlStatement) && !(stmt instanceof TMssqlDeclare)) {
							analyzeCustomSqlStmt(stmt);
						}
					}
				}

				if (handleListener != null) {
					handleListener.endAnalyzeStatment(i);
				}
			}

			if (handleListener != null) {
				handleListener.endAnalyzeDataFlow(sqlparser.sqlstatements.size());
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	private void analyzeCustomSqlStmt(TCustomSqlStatement stmt) {
		if (!accessedStatements.contains(stmt)) {
			accessedStatements.add(stmt);
		} else if(!(stmt instanceof TUseDatabase)){
			return;
		}

		if (stmt instanceof TUseDatabase) {
			ModelBindingManager.setGlobalDatabase(SQLUtil.getIdentifierNormalName(vendor,((TUseDatabase)stmt).getDatabaseName().toString()));
		} 
		else if (stmt instanceof TStoredProcedureSqlStatement) {
			this.stmtStack.push(stmt);
			this.analyzeStoredProcedureStmt((TStoredProcedureSqlStatement) stmt);
			this.stmtStack.pop();
		} else if (stmt instanceof TCreateTableSqlStatement) {
			stmtStack.push(stmt);
			analyzeCreateTableStmt((TCreateTableSqlStatement) stmt);
			stmtStack.pop();
		} else if (stmt instanceof TSelectSqlStatement) {
			analyzeSelectStmt((TSelectSqlStatement) stmt);
		} else if (stmt instanceof TCreateMaterializedSqlStatement) {
			stmtStack.push(stmt);
			TCreateMaterializedSqlStatement view = (TCreateMaterializedSqlStatement) stmt;
			analyzeCreateViewStmt(view, view.getSubquery(), view.getViewAliasClause(), view.getViewName());
			stmtStack.pop();
		} else if (stmt instanceof TCreateViewSqlStatement) {
			stmtStack.push(stmt);
			TCreateViewSqlStatement view = (TCreateViewSqlStatement) stmt;
			analyzeCreateViewStmt(view, view.getSubquery(), view.getViewAliasClause(), view.getViewName());
			stmtStack.pop();
		} else if (stmt instanceof TMssqlDeclare) {
			stmtStack.push(stmt);
			TMssqlDeclare declare = (TMssqlDeclare) stmt;
			analyzeMssqlDeclare(declare);
			stmtStack.pop();
		} else if (stmt instanceof TInsertSqlStatement) {
			stmtStack.push(stmt);
			analyzeInsertStmt((TInsertSqlStatement) stmt);
			stmtStack.pop();
		} else if (stmt instanceof TUpdateSqlStatement) {
			stmtStack.push(stmt);
			analyzeUpdateStmt((TUpdateSqlStatement) stmt);
			stmtStack.pop();
		} else if (stmt instanceof TMergeSqlStatement) {
			stmtStack.push(stmt);
			analyzeMergeStmt((TMergeSqlStatement) stmt);
			stmtStack.pop();
		} else if (stmt instanceof TDeleteSqlStatement) {
			stmtStack.push(stmt);
			analyzeDeleteStmt((TDeleteSqlStatement) stmt);
			stmtStack.pop();
		} else if (stmt instanceof TCursorDeclStmt) {
			stmtStack.push(stmt);
			analyzeCursorDeclStmt((TCursorDeclStmt) stmt);
			stmtStack.pop();
		} else if (stmt instanceof TLoopStmt) {
			stmtStack.push(stmt);
			analyzeLoopStmt((TLoopStmt) stmt);
			stmtStack.pop();
		} else if (stmt instanceof TAlterTableStatement) {
			stmtStack.push(stmt);
			analyzeAlterTableStmt((TAlterTableStatement) stmt);
			stmtStack.pop();
		} else if (stmt.getStatements() != null && stmt.getStatements().size() > 0) {
			for (int i = 0; i < stmt.getStatements().size(); i++) {
				analyzeCustomSqlStmt(stmt.getStatements().get(i));
			}
		}
	}

	private void analyzeAlterTableStmt(TAlterTableStatement stmt) {
		TTable oldNameTable = stmt.getTargetTable();
		Table oldNameTableModel = modelFactory.createTable(oldNameTable);
		for (int i = 0; stmt.getAlterTableOptionList()!=null && i < stmt.getAlterTableOptionList().size(); i++) {
			TAlterTableOption option = stmt.getAlterTableOptionList().getAlterTableOption(i);
			if (option.getOptionType() == EAlterTableOptionType.RenameTable
					|| option.getOptionType() == EAlterTableOptionType.swapWith) {
				TObjectName newTableName = option.getNewTableName();
				Stack<TParseTreeNode> list = newTableName.getStartToken().getNodesStartFromThisToken();
				boolean containsTable = false;
				for (int j = 0; j < list.size(); j++) {
					if (list.get(j) instanceof TTable) {
						TTable newTableTable = (TTable) list.get(j);
						Table newNameTableModel = modelFactory.createTable(newTableTable);
						DataFlowRelation realtion = modelFactory.createDataFlowRelation();
						realtion.setEffectType(option.getOptionType() == EAlterTableOptionType.RenameTable
								? EffectType.rename_table : EffectType.swap_table);
						realtion.setTarget(
								new PseudoRowsRelationElement<TablePseudoRows>(newNameTableModel.getPseudoRows()));
						realtion.addSource(
								new PseudoRowsRelationElement<TablePseudoRows>(oldNameTableModel.getPseudoRows()));
						containsTable = true;
					}
				}
				if (!containsTable) {
					Table newNameTableModel = modelFactory.createTableByName(newTableName);
					DataFlowRelation realtion = modelFactory.createDataFlowRelation();
					realtion.setEffectType(option.getOptionType() == EAlterTableOptionType.RenameTable
							? EffectType.rename_table : EffectType.swap_table);
					realtion.setTarget(
							new PseudoRowsRelationElement<TablePseudoRows>(newNameTableModel.getPseudoRows()));
					realtion.addSource(
							new PseudoRowsRelationElement<TablePseudoRows>(oldNameTableModel.getPseudoRows()));

				}
			}
		}
	}

	private void analyzeDeleteStmt(TDeleteSqlStatement stmt) {
		TTable table = stmt.getTargetTable();
		Table tableModel = modelFactory.createTable(table);
		if (table.getLinkedColumns() != null && table.getLinkedColumns().size() > 0) {
			for (int j = 0; j < table.getLinkedColumns().size(); j++) {
				TObjectName object = table.getLinkedColumns().getObjectName(j);

				if (object.getDbObjectType() == EDbObjectType.variable) {
					continue;
				}

				if (!isFunctionName(object)) {
					if (object.getSourceTable() == null || object.getSourceTable() == table) {
						modelFactory.createTableColumn(tableModel, object, false);
					}
				}
			}
		}

		if (stmt.getWhereClause() != null && stmt.getWhereClause().getCondition() != null) {
			analyzeFilterCondtion(stmt.getWhereClause().getCondition(), null, JoinClauseType.where, EffectType.delete);
		}
	}

	private TObjectName getProcedureName(TStoredProcedureSqlStatement stmt) {
		if (stmt instanceof TTeradataCreateProcedure) {
			return ((TTeradataCreateProcedure) stmt).getProcedureName();
		}
		return stmt.getStoredProcedureName();
	}

	private void analyzeStoredProcedureStmt(TStoredProcedureSqlStatement stmt) {

		if (stmt instanceof TCreateTriggerStmt) {
			TCreateTriggerStmt trigger = (TCreateTriggerStmt) stmt;
			if (trigger.getTables() != null) {
				for (int i = 0; i < trigger.getTables().size(); i++) {
					this.modelFactory.createTriggerOnTable(trigger.getTables().getTable(i));
				}
			}
		}

		TObjectName procedureName = getProcedureName(stmt);
		if (procedureName != null) {
			Procedure procedure = this.modelFactory.createProcedure(stmt);

			if (stmt.getParameterDeclarations() != null) {
				TParameterDeclarationList parameters = stmt.getParameterDeclarations();

				for (int i = 0; i < parameters.size(); ++i) {
					TParameterDeclaration parameter = parameters.getParameterDeclarationItem(i);
					if (parameter.getParameterName() != null) {
						this.modelFactory.createProcedureArgument(procedure, parameter);
					}
				}
			}
		}

		if (stmt.getStatements().size() > 0) {
			for (int i = 0; i < stmt.getStatements().size(); ++i) {
				this.analyzeCustomSqlStmt(stmt.getStatements().get(i));
			}
		} else if (stmt.getBodyStatements().size() > 0) {
			for (int i = 0; i < stmt.getBodyStatements().size(); ++i) {
				this.analyzeCustomSqlStmt(stmt.getBodyStatements().get(i));
			}
		}

	}

	private void analyzeLoopStmt(TLoopStmt stmt) {

		if (stmt.getCursorName() != null && stmt.getIndexName() != null) {
			modelManager.bindCursorIndex(stmt.getIndexName(), stmt.getCursorName());
		}

		for (int i = 0; i < stmt.getStatements().size(); i++) {
			analyzeCustomSqlStmt(stmt.getStatements().get(i));
		}
	}

	private void analyzeCursorDeclStmt(TCursorDeclStmt stmt) {
		if (stmt.getSubquery() == null) {
			return;
		}

		CursorResultSet resultSet = modelFactory.createCursorResultSet(stmt);
		modelManager.bindCursorModel(stmt, resultSet);
		analyzeSelectStmt(stmt.getSubquery());

		ResultSet resultSetModel = (ResultSet) modelManager.getModel(stmt.getSubquery());
		if (resultSetModel != null && resultSetModel != resultSet
				&& !resultSetModel.getPseudoRows().getHoldRelations().isEmpty()) {
			ImpactRelation impactRelation = modelFactory.createImpactRelation();
			impactRelation.setEffectType(EffectType.cursor);
			impactRelation
					.addSource(new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSetModel.getPseudoRows()));
			impactRelation.setTarget(new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSet.getPseudoRows()));
		}
	}

	
	private void analyzeMssqlDeclare(TMssqlDeclare stmt) {
		TDeclareVariableList variables = stmt.getVariables();
		if(variables == null){
			return;
		}
		for(int i=0;i<variables.size();i++){
			TDeclareVariable variable = variables.getDeclareVariable(i);
			if(variable.getTableTypeDefinitions() == null || variable.getTableTypeDefinitions().size()==0){
				continue;
			}
			
			TObjectName tableName = variable.getVariableName();
			TTableElementList columns = variable.getTableTypeDefinitions();
			
			Table tableModel = modelFactory.createTableByName(tableName, true);
			tableModel.setCreateTable(true);
			String procedureParent = getProcedureParentName(stmt);
			if (procedureParent != null) {
				tableModel.setParent(procedureParent);
			}
			
			for(int j=0;j<columns.size();j++){
				TTableElement tableElement = columns.getTableElement(j);
				TColumnDefinition column = tableElement.getColumnDefinition();
				modelFactory.createTableColumn(tableModel, column.getColumnName(), true);
			}
		}
	}
	
	private void analyzeCreateTableStmt(TCreateTableSqlStatement stmt) {
		TTable table = stmt.getTargetTable();
		if (table != null) {
			Table tableModel = modelFactory.createTableFromCreateDDL(table);

			String procedureParent = getProcedureParentName(stmt);
			if (procedureParent != null) {
				tableModel.setParent(procedureParent);
			}

			if (stmt.getColumnList() != null && stmt.getColumnList().size() > 0) {
				for (int i = 0; i < stmt.getColumnList().size(); i++) {
					TColumnDefinition column = stmt.getColumnList().getColumn(i);
					modelFactory.createTableColumn(tableModel, column.getColumnName(), true);
				}
			}

			if (stmt.getSubQuery() != null) {
				analyzeSelectStmt(stmt.getSubQuery());

				ResultSet resultSetModel = (ResultSet) modelManager.getModel(stmt.getSubQuery());
				if (resultSetModel != null && !resultSetModel.getPseudoRows().getHoldRelations().isEmpty()) {
					ImpactRelation impactRelation = modelFactory.createImpactRelation();
					impactRelation.setEffectType(EffectType.create_table);
					impactRelation.addSource(
							new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSetModel.getPseudoRows()));
					impactRelation
							.setTarget(new PseudoRowsRelationElement<TablePseudoRows>(tableModel.getPseudoRows()));
				}
			}

			if (stmt.getSubQuery() != null && !stmt.getSubQuery().isCombinedQuery()) {
				SelectResultSet resultSetModel = (SelectResultSet) modelManager
						.getModel(stmt.getSubQuery().getResultColumnList());
				for (int i = 0; i < resultSetModel.getColumns().size(); i++) {
					ResultColumn resultColumn = resultSetModel.getColumns().get(i);
					if (resultColumn.getColumnObject() instanceof TResultColumn) {
						TResultColumn columnObject = (TResultColumn) resultColumn.getColumnObject();

						TAliasClause alias = columnObject.getAliasClause();
						if (alias != null && alias.getAliasName() != null) {
							TableColumn tableColumn = modelFactory.createTableColumn(tableModel, alias.getAliasName(),
									true);
							DataFlowRelation relation = modelFactory.createDataFlowRelation();
							relation.setEffectType(EffectType.create_table);
							relation.setTarget(new TableColumnRelationElement(tableColumn));
							relation.addSource(new ResultColumnRelationElement(resultColumn));
						} else if (columnObject.getFieldAttr() != null) {
							TableColumn tableColumn = modelFactory.createTableColumn(tableModel,
									columnObject.getFieldAttr(), true);

							ResultColumn column = (ResultColumn) modelManager.getModel(resultColumn.getColumnObject());
							if (column != null && !column.getStarLinkColumns().isEmpty()) {
								tableColumn.bindStarLinkColumns(column.getStarLinkColumns());
							}

							DataFlowRelation relation = modelFactory.createDataFlowRelation();
							relation.setEffectType(EffectType.create_table);
							relation.setTarget(new TableColumnRelationElement(tableColumn));
							relation.addSource(new ResultColumnRelationElement(resultColumn));
						} else {
							System.err.println();
							System.err.println("Can't handle table column, the create table statement is");
							System.err.println(stmt.toString());
							continue;
						}
					} else if (resultColumn.getColumnObject() instanceof TObjectName) {
						TableColumn tableColumn = modelFactory.createTableColumn(tableModel,
								(TObjectName) resultColumn.getColumnObject(), true);
						DataFlowRelation relation = modelFactory.createDataFlowRelation();
						relation.setEffectType(EffectType.create_table);
						relation.setTarget(new TableColumnRelationElement(tableColumn));
						relation.addSource(new ResultColumnRelationElement(resultColumn));
					}
				}
			} else if (stmt.getSubQuery() != null) {
				SelectSetResultSet resultSetModel = (SelectSetResultSet) modelManager.getModel(stmt.getSubQuery());
				for (int i = 0; i < resultSetModel.getColumns().size(); i++) {
					ResultColumn resultColumn = resultSetModel.getColumns().get(i);
					if (resultColumn.getColumnObject() instanceof TResultColumn) {
						TResultColumn columnObject = (TResultColumn) resultColumn.getColumnObject();

						TAliasClause alias = columnObject.getAliasClause();
						if (alias != null && alias.getAliasName() != null) {
							TableColumn tableColumn = modelFactory.createTableColumn(tableModel, alias.getAliasName(),
									true);
							DataFlowRelation relation = modelFactory.createDataFlowRelation();
							relation.setEffectType(EffectType.create_table);
							relation.setTarget(new TableColumnRelationElement(tableColumn));
							relation.addSource(new ResultColumnRelationElement(resultColumn));
						} else if (columnObject.getFieldAttr() != null) {
							TableColumn tableColumn = modelFactory.createTableColumn(tableModel,
									columnObject.getFieldAttr(), true);

							ResultColumn column = (ResultColumn) modelManager.getModel(resultColumn.getColumnObject());
							if (column != null && !column.getStarLinkColumns().isEmpty()) {
								tableColumn.bindStarLinkColumns(column.getStarLinkColumns());
							}

							DataFlowRelation relation = modelFactory.createDataFlowRelation();
							relation.setEffectType(EffectType.create_table);
							relation.setTarget(new TableColumnRelationElement(tableColumn));
							relation.addSource(new ResultColumnRelationElement(resultColumn));
						} else {
							System.err.println();
							System.err.println("Can't handle table column, the create table statement is");
							System.err.println(stmt.toString());
							continue;
						}
					} else if (resultColumn.getColumnObject() instanceof TObjectName) {
						TableColumn tableColumn = modelFactory.createTableColumn(tableModel,
								(TObjectName) resultColumn.getColumnObject(), true);
						DataFlowRelation relation = modelFactory.createDataFlowRelation();
						relation.setEffectType(EffectType.create_table);
						relation.setTarget(new TableColumnRelationElement(tableColumn));
						relation.addSource(new ResultColumnRelationElement(resultColumn));
					}
				}
			}
		} else {
			System.err.println();
			System.err.println("Can't get target table. CreateTableSqlStatement is ");
			System.err.println(stmt.toString());
		}
	}

	private String getProcedureParentName(TCustomSqlStatement stmt) {

		stmt = stmt.getParentStmt();
		if (stmt == null)
			return null;

		if (stmt instanceof TStoredProcedureSqlStatement) {
			if (((TStoredProcedureSqlStatement) stmt).getStoredProcedureName() != null) {
				return ((TStoredProcedureSqlStatement) stmt).getStoredProcedureName().toString();
			}
		}
		if (stmt instanceof TTeradataCreateProcedure) {
			if (((TTeradataCreateProcedure) stmt).getProcedureName() != null) {
				return ((TTeradataCreateProcedure) stmt).getProcedureName().toString();
			}
		}

		return getProcedureParentName(stmt);
	}

	private void analyzeMergeStmt(TMergeSqlStatement stmt) {
		if (stmt.getUsingTable() != null) {
			TTable table = stmt.getTargetTable();
			Table tableModel = modelFactory.createTable(table);

			if (stmt.getUsingTable().getSubquery() != null) {
				modelFactory.createQueryTable(stmt.getUsingTable());
				analyzeSelectStmt(stmt.getUsingTable().getSubquery());

				ResultSet resultSetModel = (ResultSet) modelManager.getModel(stmt.getUsingTable().getSubquery());
				if (resultSetModel != null && !resultSetModel.getPseudoRows().getHoldRelations().isEmpty()) {
					ImpactRelation impactRelation = modelFactory.createImpactRelation();
					impactRelation.setEffectType(EffectType.merge);
					impactRelation.addSource(
							new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSetModel.getPseudoRows()));
					impactRelation
							.setTarget(new PseudoRowsRelationElement<TablePseudoRows>(tableModel.getPseudoRows()));
				}

			} else {
				modelFactory.createTable(stmt.getUsingTable());
			}

			if (stmt.getWhenClauses() != null && stmt.getWhenClauses().size() > 0) {
				for (int i = 0; i < stmt.getWhenClauses().size(); i++) {
					TMergeWhenClause clause = stmt.getWhenClauses().getElement(i);
					if (clause.getUpdateClause() != null) {
						TResultColumnList columns = clause.getUpdateClause().getUpdateColumnList();
						if (columns == null || columns.size() == 0)
							continue;

						ResultSet resultSet = modelFactory.createResultSet(clause.getUpdateClause(), true);
						createPseudoImpactRelation(stmt, resultSet, EffectType.merge_update);

						for (int j = 0; j < columns.size(); j++) {
							TResultColumn resultColumn = columns.getResultColumn(j);
							if (resultColumn.getExpr().getLeftOperand()
									.getExpressionType() == EExpressionType.simple_object_name_t) {
								TObjectName columnObject = resultColumn.getExpr().getLeftOperand().getObjectOperand();

								if (columnObject.getDbObjectType() == EDbObjectType.variable) {
									continue;
								}

								ResultColumn updateColumn = modelFactory.createMergeResultColumn(resultSet,
										columnObject);

								TExpression valueExpression = resultColumn.getExpr().getRightOperand();
								if (valueExpression == null)
									continue;

								columnsInExpr visitor = new columnsInExpr();
								valueExpression.inOrderTraverse(visitor);
								List<TObjectName> objectNames = visitor.getObjectNames();
								List<TParseTreeNode> functions = visitor.getFunctions();

								if (functions != null && !functions.isEmpty()) {
									analyzeFunctionDataFlowRelation(updateColumn, functions, EffectType.merge_update);
								}
								
								List<TSelectSqlStatement> subquerys = visitor.getSubquerys();
								if (subquerys!=null && !subquerys.isEmpty()) {
									analyzeSubqueryDataFlowRelation(updateColumn, subquerys, EffectType.merge_update);
								}

								analyzeDataFlowRelation(updateColumn, objectNames, EffectType.merge_update, functions);

								List<TConstant> constants = visitor.getConstants();
								analyzeConstantDataFlowRelation(updateColumn, constants, EffectType.merge_update,
										functions);

								TableColumn tableColumn = modelFactory.createTableColumn(tableModel, columnObject,
										false);

								DataFlowRelation relation = modelFactory.createDataFlowRelation();
								relation.setEffectType(EffectType.merge_update);
								relation.setTarget(new TableColumnRelationElement(tableColumn));
								relation.addSource(new ResultColumnRelationElement(updateColumn));
							}
						}
					}
					if (clause.getInsertClause() != null) {
						TExpression insertValue = clause.getInsertClause().getInsertValue();
						if (insertValue != null
								&& insertValue.getExpressionType() == EExpressionType.objectConstruct_t) {
							ResultSet resultSet = modelFactory.createResultSet(clause.getInsertClause(), true);

							createPseudoImpactRelation(stmt, resultSet, EffectType.merge_insert);

							TObjectConstruct objectConstruct = insertValue.getObjectConstruct();
							for (int z = 0; z < objectConstruct.getPairs().size(); z++) {
								TPair pair = objectConstruct.getPairs().getElement(z);

								if (pair.getKeyName().getExpressionType() == EExpressionType.simple_constant_t) {
									TObjectName columnObject = new TObjectName();
									TConstant constant = pair.getKeyName().getConstantOperand();
									TSourceToken newSt = new TSourceToken(
											constant.getValueToken().getTextWithoutQuoted());
									columnObject.setPartToken(newSt);
									columnObject.setSourceTable(stmt.getTargetTable());
									columnObject.setStartToken(constant.getStartToken());
									columnObject.setEndToken(constant.getEndToken());

									ResultColumn insertColumn = modelFactory.createMergeResultColumn(resultSet,
											columnObject);

									TExpression valueExpression = pair.getKeyValue();
									if (valueExpression == null)
										continue;

									columnsInExpr visitor = new columnsInExpr();
									valueExpression.inOrderTraverse(visitor);
									List<TObjectName> objectNames = visitor.getObjectNames();
									List<TParseTreeNode> functions = visitor.getFunctions();

									if (functions != null && !functions.isEmpty()) {
										analyzeFunctionDataFlowRelation(insertColumn, functions,
												EffectType.merge_insert);
									}
									
									List<TSelectSqlStatement> subquerys = visitor.getSubquerys();
									if (subquerys!=null && !subquerys.isEmpty()) {
										analyzeSubqueryDataFlowRelation(insertColumn, subquerys, EffectType.merge_insert);
									}

									analyzeDataFlowRelation(insertColumn, objectNames, EffectType.merge_insert,
											functions);

									List<TConstant> constants = visitor.getConstants();
									analyzeConstantDataFlowRelation(insertColumn, constants, EffectType.merge_insert,
											functions);

									TableColumn tableColumn = modelFactory.createTableColumn(tableModel, columnObject,
											false);

									DataFlowRelation relation = modelFactory.createDataFlowRelation();
									relation.setEffectType(EffectType.merge_insert);
									relation.setTarget(new TableColumnRelationElement(tableColumn));
									relation.addSource(new ResultColumnRelationElement(insertColumn));
								}
							}
						} else {
							TObjectNameList columns = clause.getInsertClause().getColumnList();
							TResultColumnList values = clause.getInsertClause().getValuelist();
							if (columns == null || columns.size() == 0 || values == null || values.size() == 0)
								continue;

							ResultSet resultSet = modelFactory.createResultSet(clause.getInsertClause(), true);

							createPseudoImpactRelation(stmt, resultSet, EffectType.merge_insert);

							for (int j = 0; j < columns.size() && j < values.size(); j++) {
								TObjectName columnObject = columns.getObjectName(j);

								ResultColumn insertColumn = modelFactory.createMergeResultColumn(resultSet,
										columnObject);

								TExpression valueExpression = values.getResultColumn(j).getExpr();
								if (valueExpression == null)
									continue;

								columnsInExpr visitor = new columnsInExpr();
								valueExpression.inOrderTraverse(visitor);
								List<TObjectName> objectNames = visitor.getObjectNames();
								List<TParseTreeNode> functions = visitor.getFunctions();

								if (functions != null && !functions.isEmpty()) {
									analyzeFunctionDataFlowRelation(insertColumn, functions, EffectType.merge_insert);
								}
								
								List<TSelectSqlStatement> subquerys = visitor.getSubquerys();
								if (subquerys!=null && !subquerys.isEmpty()) {
									analyzeSubqueryDataFlowRelation(insertColumn, subquerys, EffectType.merge_insert);
								}

								analyzeDataFlowRelation(insertColumn, objectNames, EffectType.merge_insert, functions);

								List<TConstant> constants = visitor.getConstants();
								analyzeConstantDataFlowRelation(insertColumn, constants, EffectType.merge_insert,
										functions);

								TableColumn tableColumn = modelFactory.createTableColumn(tableModel, columnObject,
										false);

								DataFlowRelation relation = modelFactory.createDataFlowRelation();
								relation.setEffectType(EffectType.merge_insert);
								relation.setTarget(new TableColumnRelationElement(tableColumn));
								relation.addSource(new ResultColumnRelationElement(insertColumn));
							}
						}
					}
				}

			}

			if (stmt.getCondition() != null) {
				analyzeFilterCondtion(stmt.getCondition(), null, JoinClauseType.on, EffectType.merge);
			}
		}
	}

	private List<TableColumn> bindInsertTableColumn(Table tableModel, TInsertIntoValue value, List<TObjectName> keyMap,
			List<TResultColumn> valueMap) {
		List<TableColumn> tableColumns = new ArrayList<TableColumn>();
		if (value.getColumnList() != null) {
			for (int z = 0; z < value.getColumnList().size(); z++) {
				TableColumn tableColumn = modelFactory.createInsertTableColumn(tableModel,
						value.getColumnList().getObjectName(z));
				tableColumns.add(tableColumn);
				keyMap.add(tableColumn.getColumnObject());
			}
		}

		if (value.getTargetList() != null) {
			for (int z = 0; z < value.getTargetList().size(); z++) {
				TMultiTarget target = value.getTargetList().getMultiTarget(z);
				TResultColumnList columns = target.getColumnList();
				for (int i = 0; i < columns.size(); i++) {
					if (value.getColumnList() == null) {
						TableColumn tableColumn = modelFactory.createInsertTableColumn(tableModel,
								columns.getResultColumn(i).getFieldAttr());
						tableColumns.add(tableColumn);
					}
					valueMap.add(columns.getResultColumn(i));
				}
			}
		}

		return tableColumns;
	}

	private TableColumn matchColumn(List<TableColumn> tableColumns, TObjectName columnName) {
		for (int i = 0; i < tableColumns.size(); i++) {
			TableColumn column = tableColumns.get(i);
			if (column.getColumnObject().toString().equalsIgnoreCase(columnName.toString()))
				return column;
		}
		return null;
	}

	private ResultColumn matchResultColumn(List<ResultColumn> resultColumns, TObjectName columnName) {
		for (int i = 0; i < resultColumns.size(); i++) {
			ResultColumn column = resultColumns.get(i);
			if (column.getAlias() != null && column.getAlias().equalsIgnoreCase(columnName.getColumnNameOnly()))
				return column;
			if (column.getName() != null && column.getName().equalsIgnoreCase(columnName.getColumnNameOnly()))
				return column;
		}
		return null;
	}

	private void analyzeInsertStmt(TInsertSqlStatement stmt) {
		Map<Table, List<TObjectName>> insertTableKeyMap = new LinkedHashMap<Table, List<TObjectName>>();
		Map<Table, List<TResultColumn>> insertTableValueMap = new LinkedHashMap<Table, List<TResultColumn>>();
		Map<String, List<TableColumn>> tableColumnMap = new LinkedHashMap<String, List<TableColumn>>();
		List<Table> inserTables = new ArrayList<Table>();
		List<TExpression> expressions = new ArrayList<TExpression>();
		if (stmt.getInsertConditions() != null && stmt.getInsertConditions().size() > 0) {
			for (int i = 0; i < stmt.getInsertConditions().size(); i++) {
				TInsertCondition condition = stmt.getInsertConditions().getElement(i);
				if (condition.getCondition() != null) {
					expressions.add(condition.getCondition());
				}
				for (int j = 0; j < condition.getInsertIntoValues().size(); j++) {
					TInsertIntoValue value = condition.getInsertIntoValues().getElement(j);
					TTable table = value.getTable();
					Table tableModel = modelFactory.createTable(table);

					inserTables.add(tableModel);
					List<TObjectName> keyMap = new ArrayList<TObjectName>();
					List<TResultColumn> valueMap = new ArrayList<TResultColumn>();
					insertTableKeyMap.put(tableModel, keyMap);
					insertTableValueMap.put(tableModel, valueMap);

					List<TableColumn> tableColumns = bindInsertTableColumn(tableModel, value, keyMap, valueMap);
					if (tableColumnMap.get(SQLUtil.getIdentifierNormalName(table.getFullName())) == null
							&& !tableColumns.isEmpty()) {
						tableColumnMap.put(SQLUtil.getIdentifierNormalName(tableModel.getFullName()), tableColumns);
					}
				}
			}
		} else if (stmt.getInsertIntoValues() != null && stmt.getInsertIntoValues().size() > 0) {
			for (int i = 0; i < stmt.getInsertIntoValues().size(); i++) {
				TInsertIntoValue value = stmt.getInsertIntoValues().getElement(i);
				TTable table = value.getTable();
				Table tableModel = modelFactory.createTable(table);

				inserTables.add(tableModel);
				List<TObjectName> keyMap = new ArrayList<TObjectName>();
				List<TResultColumn> valueMap = new ArrayList<TResultColumn>();
				insertTableKeyMap.put(tableModel, keyMap);
				insertTableValueMap.put(tableModel, valueMap);

				List<TableColumn> tableColumns = bindInsertTableColumn(tableModel, value, keyMap, valueMap);
				if (tableColumnMap.get(table.getName()) == null && !tableColumns.isEmpty()) {
					tableColumnMap.put(tableModel.getName(), tableColumns);
				}
			}
		} else {
			TTable table = stmt.getTargetTable();
			if (table == null) {
				System.err.println("Can't handle insert statement: " + stmt.toString());
				return;
			}
			Table tableModel = modelFactory.createTable(table);
			inserTables.add(tableModel);
			if (tableColumnMap.get(SQLUtil.getIdentifierNormalName(table.getFullName())) == null) {
				if (tableModel.getColumns() != null && !tableModel.getColumns().isEmpty()) {
					tableColumnMap.put(SQLUtil.getIdentifierNormalName(tableModel.getFullName()),
							tableModel.getColumns());
				} else {
					tableColumnMap.put(SQLUtil.getIdentifierNormalName(tableModel.getFullName()), null);
				}
			}
		}

		if (stmt.getSubQuery() != null) {
			analyzeSelectStmt(stmt.getSubQuery());
		}

		Iterator<Table> tableIter = inserTables.iterator();
		while (tableIter.hasNext()) {
			Table tableModel = tableIter.next();
			List<TableColumn> tableColumns = tableColumnMap.get(tableModel.getName());
			List<TObjectName> keyMap = insertTableKeyMap.get(tableModel);
			List<TResultColumn> valueMap = insertTableValueMap.get(tableModel);
			boolean initColumn = (tableColumns != null && !containStarColumn(tableColumns));

			if (stmt.getSubQuery() != null) {

				if (stmt.getColumnList() != null && stmt.getColumnList().size() > 0) {
					TObjectNameList items = stmt.getColumnList();

					ResultSet resultSetModel = null;

					if (stmt.getSubQuery() != null) {
						resultSetModel = (ResultSet) modelManager.getModel(stmt.getSubQuery());
					} 
					
					if(resultSetModel == null && stmt.getSubQuery().getResultColumnList()!=null)
					{
						resultSetModel = (ResultSet) modelManager.getModel(stmt.getSubQuery().getResultColumnList());
					}

					if (resultSetModel == null) {
						System.err.println("Can't get resultset model");
					}

					if (!resultSetModel.getPseudoRows().getHoldRelations().isEmpty()) {
						ImpactRelation impactRelation = modelFactory.createImpactRelation();
						impactRelation.setEffectType(EffectType.insert);
						impactRelation.addSource(
								new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSetModel.getPseudoRows()));
						impactRelation
								.setTarget(new PseudoRowsRelationElement<TablePseudoRows>(tableModel.getPseudoRows()));
					}

					int resultSetSize = resultSetModel.getColumns().size();
					int j = 0;
					for (int i = 0; i < items.size(); i++) {
						TObjectName column = items.getObjectName(i);

						if (column.getDbObjectType() == EDbObjectType.variable) {
							continue;
						}

						ResultColumn resultColumn = resultSetModel.getColumns().get(j);
						if (!resultSetModel.getColumns().get(j).getName().contains("*")) {
							j++;
						} else if (resultSetSize - j == items.size() - i) {
							j++;
						}
						if (column != null) {
							TableColumn tableColumn;
							if (!initColumn) {
								tableColumn = modelFactory.createTableColumn(tableModel, column, false);

							} else {
								tableColumn = matchColumn(tableColumns, column);
								if (tableColumn == null) {
									continue;
								}
							}
							DataFlowRelation relation = modelFactory.createDataFlowRelation();
							relation.setEffectType(EffectType.insert);
							relation.setTarget(new TableColumnRelationElement(tableColumn));
							relation.addSource(new ResultColumnRelationElement(resultColumn));
						}
					}
				} else if (!stmt.getSubQuery().isCombinedQuery()) {
					SelectResultSet resultSetModel = (SelectResultSet) modelManager
							.getModel(stmt.getSubQuery().getResultColumnList());

					if (resultSetModel != null && !resultSetModel.getPseudoRows().getHoldRelations().isEmpty()) {
						ImpactRelation impactRelation = modelFactory.createImpactRelation();
						impactRelation.setEffectType(EffectType.insert);
						impactRelation.addSource(
								new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSetModel.getPseudoRows()));
						impactRelation
								.setTarget(new PseudoRowsRelationElement<TablePseudoRows>(tableModel.getPseudoRows()));
					}

					for (int i = 0; i < resultSetModel.getColumns().size(); i++) {
						ResultColumn resultColumn = resultSetModel.getColumns().get(i);
						if (resultColumn.getColumnObject() instanceof TObjectName) {
							TableColumn tableColumn;
							if (!initColumn) {
								tableColumn = modelFactory.createInsertTableColumn(tableModel,
										(TObjectName) resultColumn.getColumnObject());
								if(containStarColumn(tableColumns)){
									getStarColumn(tableColumns).getStarLinkColumns().add((TObjectName) resultColumn.getColumnObject());
								}
							} else {
								TObjectName matchedColumnName = (TObjectName) resultColumn.getColumnObject();
								tableColumn = matchColumn(tableColumns, matchedColumnName);
								if (tableColumn == null) {
									if (!isEmptyCollection(valueMap)) {
										int index = indexOfColumn(valueMap, matchedColumnName);
										if (index != -1) {
											if (!isEmptyCollection(keyMap) && index < keyMap.size()) {
												tableColumn = matchColumn(tableColumns, keyMap.get(index));
											} else if (isEmptyCollection(keyMap) && index < tableColumns.size()) {
												tableColumn = tableColumns.get(index);
											} else {
												continue;
											}
										} else {
											continue;
										}
									} else if (!isEmptyCollection(keyMap) && i < keyMap.size()) {
										tableColumn = matchColumn(tableColumns, keyMap.get(i));
									} else if (isEmptyCollection(keyMap) && isEmptyCollection(valueMap)
											&& i < tableColumns.size()) {
										tableColumn = tableColumns.get(i);
									} else {
										continue;
									}
								}
							}

							DataFlowRelation relation = modelFactory.createDataFlowRelation();
							relation.setEffectType(EffectType.insert);
							relation.setTarget(new TableColumnRelationElement(tableColumn));
							relation.addSource(new ResultColumnRelationElement(resultColumn));
						} else {
							TAliasClause alias = ((TResultColumn) resultColumn.getColumnObject()).getAliasClause();
							if (alias != null && alias.getAliasName() != null) {
								TableColumn tableColumn;
								if (!initColumn) {
									tableColumn = modelFactory.createInsertTableColumn(tableModel,
											alias.getAliasName());
									if(containStarColumn(tableColumns)){
										getStarColumn(tableColumns).getStarLinkColumns().add(alias.getAliasName());
									}
								} else {
									TObjectName matchedColumnName = alias.getAliasName();
									tableColumn = matchColumn(tableColumns, matchedColumnName);
									if (tableColumn == null) {
										if (!isEmptyCollection(valueMap)) {
											int index = indexOfColumn(valueMap, matchedColumnName);
											if (index != -1) {
												if (!isEmptyCollection(keyMap) && index < keyMap.size()) {
													tableColumn = matchColumn(tableColumns, keyMap.get(index));
												} else if (isEmptyCollection(keyMap) && index < tableColumns.size()) {
													tableColumn = tableColumns.get(index);
												} else {
													continue;
												}
											} else {
												continue;
											}
										} else if (!isEmptyCollection(keyMap) && i < keyMap.size()) {
											tableColumn = matchColumn(tableColumns, keyMap.get(i));
										} else if (isEmptyCollection(keyMap) && isEmptyCollection(valueMap)
												&& i < tableColumns.size()) {
											tableColumn = tableColumns.get(i);
										} else {
											continue;
										}
									}

								}

								DataFlowRelation relation = modelFactory.createDataFlowRelation();
								relation.setEffectType(EffectType.insert);
								relation.setTarget(new TableColumnRelationElement(tableColumn));
								relation.addSource(new ResultColumnRelationElement(resultColumn));

							} else if (((TResultColumn) resultColumn.getColumnObject()).getFieldAttr() != null) {
								TObjectName fieldAttr = ((TResultColumn) resultColumn.getColumnObject()).getFieldAttr();

								TableColumn tableColumn;
								if (!initColumn) {
									if(tableModel.isCreateTable() && !containStarColumn(tableModel.getColumns())){
										tableColumn = tableModel.getColumns().get(i);
									}
									else{
										tableColumn = modelFactory.createInsertTableColumn(tableModel, fieldAttr);
									}
								} else {
									TObjectName matchedColumnName = fieldAttr;
									tableColumn = matchColumn(tableColumns, matchedColumnName);
									if (tableColumn == null) {
										if (!isEmptyCollection(valueMap)) {
											int index = indexOfColumn(valueMap, matchedColumnName);
											if (index != -1) {
												if (!isEmptyCollection(keyMap) && index < keyMap.size()) {
													tableColumn = matchColumn(tableColumns, keyMap.get(index));
												} else if (isEmptyCollection(keyMap) && index < tableColumns.size()) {
													tableColumn = tableColumns.get(index);
												} else {
													continue;
												}
											} else {
												continue;
											}
										} else if (!isEmptyCollection(keyMap) && i < keyMap.size()) {
											tableColumn = matchColumn(tableColumns, keyMap.get(i));
										} else if (isEmptyCollection(keyMap) && isEmptyCollection(valueMap)
												&& i < tableColumns.size()) {
											tableColumn = tableColumns.get(i);
										} else {
											continue;
										}
									}
								}

								if (!"*".equals(getColumnName(tableColumn.getColumnObject()))
										&& "*".equals(getColumnName(fieldAttr))) {
									TObjectName columnObject = fieldAttr;
									TTable sourceTable = columnObject.getSourceTable();
									if (columnObject.getTableToken() != null && sourceTable != null) {
										TObjectName[] columns = modelManager.getTableColumns(sourceTable);
										for (int j = 0; j < columns.length; j++) {
											TObjectName columnName = columns[j];
											if (columnName == null || "*".equals(getColumnName(columnName))) {
												continue;
											}
											resultColumn.bindStarLinkColumn(columnName);
										}
									} else {
										TTableList tables = stmt.getTables();
										for (int k = 0; k < tables.size(); k++) {
											TTable tableElement = tables.getTable(k);
											TObjectName[] columns = modelManager.getTableColumns(tableElement);
											for (int j = 0; j < columns.length; j++) {
												TObjectName columnName = columns[j];
												if (columnName == null || "*".equals(getColumnName(columnName))) {
													continue;
												}
												resultColumn.bindStarLinkColumn(columnName);
											}
										}
									}
								}

								if (resultColumn != null && !resultColumn.getStarLinkColumns().isEmpty()) {
									tableColumn.bindStarLinkColumns(resultColumn.getStarLinkColumns());
								}

								DataFlowRelation relation = modelFactory.createDataFlowRelation();
								relation.setEffectType(EffectType.insert);
								relation.setTarget(new TableColumnRelationElement(tableColumn));
								relation.addSource(new ResultColumnRelationElement(resultColumn));
							} else if (((TResultColumn) resultColumn.getColumnObject()).getExpr()
									.getExpressionType() == EExpressionType.simple_constant_t) {
								if (!initColumn) {
									TableColumn tableColumn = modelFactory.createInsertTableColumn(tableModel,
											((TResultColumn) resultColumn.getColumnObject()).getExpr()
													.getConstantOperand(),
											i);
									if (SQLUtil.isTempTable(tableModel, vendor) && sqlenv != null
											&& tableModel.getDatabase() != null && tableModel.getSchema() != null) {
										TSQLSchema schema = sqlenv.getSQLSchema(
												tableModel.getDatabase() + "." + tableModel.getSchema(), true);
										if (schema != null) {
											TSQLTable tempTable = schema.createTable(tableModel.getName());
											tempTable.addColumn(tableColumn.getName());
										}
									}
									DataFlowRelation relation = modelFactory.createDataFlowRelation();
									relation.setEffectType(EffectType.insert);
									relation.setTarget(new TableColumnRelationElement(tableColumn));
									relation.addSource(new ResultColumnRelationElement(resultColumn));
								} else {
									DataFlowRelation relation = modelFactory.createDataFlowRelation();
									relation.setEffectType(EffectType.insert);
									relation.setTarget(new TableColumnRelationElement(tableModel.getColumns().get(i)));
									relation.addSource(new ResultColumnRelationElement(resultColumn));
								}
							}
						}
					}
				} else if (stmt.getSubQuery() != null) {
					SelectSetResultSet resultSetModel = (SelectSetResultSet) modelManager.getModel(stmt.getSubQuery());
					if (resultSetModel != null) {

						if (!resultSetModel.getPseudoRows().getHoldRelations().isEmpty()) {
							ImpactRelation impactRelation = modelFactory.createImpactRelation();
							impactRelation.setEffectType(EffectType.insert);
							impactRelation.addSource(
									new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSetModel.getPseudoRows()));
							impactRelation.setTarget(
									new PseudoRowsRelationElement<TablePseudoRows>(tableModel.getPseudoRows()));
						}

						for (int i = 0; i < resultSetModel.getColumns().size(); i++) {
							ResultColumn resultColumn = resultSetModel.getColumns().get(i);
							TAliasClause alias = ((TResultColumn) resultColumn.getColumnObject()).getAliasClause();
							if (alias != null && alias.getAliasName() != null) {
								TableColumn tableColumn;
								if (!initColumn) {
									tableColumn = modelFactory.createInsertTableColumn(tableModel,
											alias.getAliasName());
								} else {
									tableColumn = matchColumn(tableColumns, alias.getAliasName());
									if (tableColumn == null) {
										continue;
									}
								}

								DataFlowRelation relation = modelFactory.createDataFlowRelation();
								relation.setEffectType(EffectType.insert);
								relation.setTarget(new TableColumnRelationElement(tableColumn));
								relation.addSource(new ResultColumnRelationElement(resultColumn));
							} else if (((TResultColumn) resultColumn.getColumnObject()).getFieldAttr() != null) {
								TObjectName fieldAttr = ((TResultColumn) resultColumn.getColumnObject()).getFieldAttr();
								TableColumn tableColumn;
								if (!initColumn) {
									tableColumn = modelFactory.createInsertTableColumn(tableModel, fieldAttr);
								} else {
									tableColumn = matchColumn(tableColumns, fieldAttr);
									if (tableColumn == null) {
										continue;
									}
								}

								DataFlowRelation relation = modelFactory.createDataFlowRelation();
								relation.setEffectType(EffectType.insert);
								relation.setTarget(new TableColumnRelationElement(tableColumn));
								relation.addSource(new ResultColumnRelationElement(resultColumn));
							} else if (((TResultColumn) resultColumn.getColumnObject()).getExpr()
									.getExpressionType() == EExpressionType.simple_constant_t) {
								if (!initColumn) {
									TableColumn tableColumn = modelFactory.createInsertTableColumn(tableModel,
											((TResultColumn) resultColumn.getColumnObject()).getExpr()
													.getConstantOperand(),
											i);
									DataFlowRelation relation = modelFactory.createDataFlowRelation();
									relation.setEffectType(EffectType.insert);
									relation.setTarget(new TableColumnRelationElement(tableColumn));
									relation.addSource(new ResultColumnRelationElement(resultColumn));
								}
							}
						}
					}
				}
			} else if (stmt.getColumnList() != null && stmt.getColumnList().size() > 0) {
				TObjectNameList items = stmt.getColumnList();
				TMultiTargetList values = stmt.getValues();
				for (int k = 0; values!=null && k < values.size(); k++) {
					int j = 0;
					for (int i = 0; i < items.size(); i++) {
						TObjectName column = items.getObjectName(i);
						TableColumn tableColumn;
						if (!initColumn) {
							tableColumn = modelFactory.createInsertTableColumn(tableModel, column);
						} else {
							tableColumn = matchColumn(tableColumns, column);
							if (tableColumn == null) {
								continue;
							}
						}
						TResultColumn columnObject = values.getMultiTarget(k).getColumnList().getResultColumn(j);
						TExpression valueExpr = columnObject.getExpr();
						columnsInExpr visitor = new columnsInExpr();
						valueExpr.inOrderTraverse(visitor);
						List<TObjectName> objectNames = visitor.getObjectNames();
						List<TConstant> constants = visitor.getConstants();

						for (int x = 0; x < objectNames.size(); x++) {
							TObjectName value = objectNames.get(x);
							if (value.getObjectString() != null) {
								Object resultSet = modelManager.getModel(value.getObjectString());
								if (resultSet == null)
									continue;

								if (resultSet instanceof CursorResultSet) {
									resultSet = modelManager
											.getModel(((CursorResultSet) resultSet).getResultColumnObject());
								}
								SelectResultSet model = (SelectResultSet) resultSet;

								ResultColumn sourceColumn = null;
								for (int y = 0; y < model.getColumns().size(); y++) {
									ResultColumn temp = model.getColumns().get(y);
									if (temp.getName().equalsIgnoreCase(value.getEndToken().toString())) {
										sourceColumn = temp;
										break;
									}
								}
								if (sourceColumn != null) {
									DataFlowRelation relation = modelFactory.createDataFlowRelation();
									relation.setEffectType(EffectType.insert);
									relation.setTarget(new TableColumnRelationElement(tableColumn));
									relation.addSource(new ResultColumnRelationElement(sourceColumn));
								}
							}
						}

						for (int x = 0; x < constants.size(); x++) {
							TConstant value = constants.get(x);
							DataFlowRelation relation = modelFactory.createDataFlowRelation();
							relation.setEffectType(EffectType.insert);
							relation.setTarget(new TableColumnRelationElement(tableColumn));
							relation.addSource(new ConstantRelationElement(new Constant(value)));
						}

						j++;
					}
				}
			}
		}

		if (!expressions.isEmpty() && stmt.getSubQuery() != null) {
			analyzeInsertImpactRelation(stmt.getSubQuery(), tableColumnMap, expressions);
		}
	}

	private TableColumn getStarColumn(List<TableColumn> columns) {
		for(TableColumn column: columns){
			if(column.getName().endsWith("*")){
				return column;
			}
		}
		return null;
	}

	private boolean containStarColumn(List<TableColumn> columns) {
		if(columns == null)
			return false;
		for(TableColumn column: columns){
			if(column.getName().endsWith("*")){
				return true;
			}
		}
		return false;
	}

	private int indexOfColumn(List<TResultColumn> columns, TObjectName objectName) {
		for (int i = 0; i < columns.size(); i++) {
			if (columns.get(i).toString().trim().equalsIgnoreCase(objectName.toString().trim())) {
				return i;
			}
		}
		return -1;
	}

	private boolean isEmptyCollection(Collection<?> keyMap) {
		return keyMap == null || keyMap.isEmpty();
	}

	private void analyzeInsertImpactRelation(TSelectSqlStatement stmt, Map<String, List<TableColumn>> insertMap,
			List<TExpression> expressions) {
		List<TObjectName> objectNames = new ArrayList<TObjectName>();
		for (int i = 0; i < expressions.size(); i++) {
			TExpression condition = expressions.get(i);
			columnsInExpr visitor = new columnsInExpr();
			condition.inOrderTraverse(visitor);
			objectNames.addAll(visitor.getObjectNames());
		}

		Iterator<String> iter = insertMap.keySet().iterator();
		while (iter.hasNext()) {
			String table = iter.next();
			List<TableColumn> tableColumns = insertMap.get(table);
			for (int i = 0; i < tableColumns.size(); i++) {

				TableColumn column = tableColumns.get(i);
				ImpactRelation relation = modelFactory.createImpactRelation();
				relation.setEffectType(EffectType.insert);
				relation.setTarget(new TableColumnRelationElement(column));

				for (int j = 0; j < objectNames.size(); j++) {
					TObjectName columnName = objectNames.get(j);
					Object model = modelManager.getModel(stmt);
					if (model instanceof SelectResultSet) {
						SelectResultSet queryTable = (SelectResultSet) model;
						List<ResultColumn> columns = queryTable.getColumns();
						for (int k = 0; k < columns.size(); k++) {
							ResultColumn resultColumn = columns.get(k);
							if (resultColumn.getAlias() != null
									&& columnName.toString().equalsIgnoreCase(resultColumn.getAlias())) {
								relation.addSource(
										new ResultColumnRelationElement(resultColumn, columnName.getLocation()));
							} else if (resultColumn.getName() != null
									&& columnName.toString().equalsIgnoreCase(resultColumn.getName())) {
								relation.addSource(
										new ResultColumnRelationElement(resultColumn, columnName.getLocation()));
							}
						}
					}
				}
			}
		}
	}

	private void analyzeUpdateStmt(TUpdateSqlStatement stmt) {
		if (stmt.getResultColumnList() == null)
			return;

		TTable table = stmt.getTargetTable();
		Table tableModel = modelFactory.createTable(table);

		for (int i = 0; i < stmt.tables.size(); i++) {
			TTable tableElement = stmt.tables.getTable(i);
			if (tableElement.getSubquery() != null) {
				QueryTable queryTable = modelFactory.createQueryTable(tableElement);
				TSelectSqlStatement subquery = tableElement.getSubquery();
				analyzeSelectStmt(subquery);

				if (subquery.getSetOperatorType() != ESetOperatorType.none) {
					SelectSetResultSet selectSetResultSetModel = (SelectSetResultSet) modelManager.getModel(subquery);
					for (int j = 0; j < selectSetResultSetModel.getColumns().size(); j++) {
						ResultColumn sourceColumn = selectSetResultSetModel.getColumns().get(j);
						ResultColumn targetColumn = modelFactory.createSelectSetResultColumn(queryTable, sourceColumn,
								j);
						DataFlowRelation selectSetRalation = modelFactory.createDataFlowRelation();
						selectSetRalation.setEffectType(EffectType.select);
						selectSetRalation.setTarget(new ResultColumnRelationElement(targetColumn));
						selectSetRalation.addSource(new ResultColumnRelationElement(sourceColumn));
					}
				}

				ResultSet resultSetModel = (ResultSet) modelManager.getModel(tableElement.getSubquery());
				if (resultSetModel != null && resultSetModel != queryTable
						&& !resultSetModel.getPseudoRows().getHoldRelations().isEmpty()) {
					ImpactRelation impactRelation = modelFactory.createImpactRelation();
					impactRelation.setEffectType(EffectType.update);
					impactRelation.addSource(
							new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSetModel.getPseudoRows()));
					impactRelation
							.setTarget(new PseudoRowsRelationElement<ResultSetPseudoRows>(queryTable.getPseudoRows()));
				}

			} else if (tableElement.getCTE() != null) {
				QueryTable queryTable = modelFactory.createQueryTable(tableElement);

				TObjectNameList cteColumns = tableElement.getCTE().getColumnList();
				if (cteColumns != null) {
					for (int j = 0; j < cteColumns.size(); j++) {
						modelFactory.createResultColumn(queryTable, cteColumns.getObjectName(j));
					}
				}
				TSelectSqlStatement subquery = tableElement.getCTE().getSubquery();
				if (subquery != null && !stmtStack.contains(subquery)) {
					analyzeSelectStmt(subquery);

					ResultSet resultSetModel = (ResultSet) modelManager.getModel(subquery);
					if (resultSetModel != null && resultSetModel != queryTable
							&& !resultSetModel.getPseudoRows().getHoldRelations().isEmpty()) {
						ImpactRelation impactRelation = modelFactory.createImpactRelation();
						impactRelation.setEffectType(EffectType.select);
						impactRelation.addSource(
								new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSetModel.getPseudoRows()));
						impactRelation.setTarget(
								new PseudoRowsRelationElement<ResultSetPseudoRows>(queryTable.getPseudoRows()));
					}

					if (subquery.getSetOperatorType() != ESetOperatorType.none) {
						SelectSetResultSet selectSetResultSetModel = (SelectSetResultSet) modelManager
								.getModel(subquery);
						for (int j = 0; j < selectSetResultSetModel.getColumns().size(); j++) {
							ResultColumn sourceColumn = selectSetResultSetModel.getColumns().get(j);
							ResultColumn targetColumn = null;
							if (cteColumns != null) {
								targetColumn = queryTable.getColumns().get(j);
							} else {
								targetColumn = modelFactory.createSelectSetResultColumn(queryTable, sourceColumn, j);
							}
							for (TObjectName starLinkColumn : sourceColumn.getStarLinkColumns()) {
								targetColumn.bindStarLinkColumn(starLinkColumn);
							}
							DataFlowRelation selectSetRalation = modelFactory.createDataFlowRelation();
							selectSetRalation.setEffectType(EffectType.select);
							selectSetRalation.setTarget(new ResultColumnRelationElement(targetColumn));
							selectSetRalation.addSource(new ResultColumnRelationElement(sourceColumn));
						}
					} else {
						for (int j = 0; j < resultSetModel.getColumns().size(); j++) {
							ResultColumn sourceColumn = resultSetModel.getColumns().get(j);
							ResultColumn targetColumn = null;
							if (cteColumns != null) {
								targetColumn = queryTable.getColumns().get(j);
							} else {
								targetColumn = modelFactory.createSelectSetResultColumn(queryTable, sourceColumn, j);
							}
							for (TObjectName starLinkColumn : sourceColumn.getStarLinkColumns()) {
								targetColumn.bindStarLinkColumn(starLinkColumn);
							}
							DataFlowRelation selectSetRalation = modelFactory.createDataFlowRelation();
							selectSetRalation.setEffectType(EffectType.select);
							selectSetRalation.setTarget(new ResultColumnRelationElement(targetColumn));
							selectSetRalation.addSource(new ResultColumnRelationElement(sourceColumn));
						}
					}
				} else if (tableElement.getCTE().getUpdateStmt() != null) {
					analyzeCustomSqlStmt(tableElement.getCTE().getUpdateStmt());
				} else if (tableElement.getCTE().getInsertStmt() != null) {
					analyzeCustomSqlStmt(tableElement.getCTE().getInsertStmt());
				} else if (tableElement.getCTE().getDeleteStmt() != null) {
					analyzeCustomSqlStmt(tableElement.getCTE().getDeleteStmt());
				}
			} else {
				modelFactory.createTable(stmt.tables.getTable(i));
			}
		}

		for (int i = 0; i < stmt.getResultColumnList().size(); i++) {
			TResultColumn field = stmt.getResultColumnList().getResultColumn(i);

			if (field.getExpr().getExpressionType() == EExpressionType.function_t) {
				continue;
			}

			TExpression expression = field.getExpr().getLeftOperand();
			if (expression == null) {
				System.err.println();
				System.err.println("Can't handle this case. Expression is ");
				System.err.println(field.getExpr().toString());
				continue;
			}
			if (expression.getExpressionType() == EExpressionType.list_t) {
				TExpression setExpression = field.getExpr().getRightOperand();
				if (setExpression != null && setExpression.getSubQuery() != null) {
					TSelectSqlStatement query = setExpression.getSubQuery();
					analyzeSelectStmt(query);

					SelectResultSet resultSetModel = (SelectResultSet) modelManager
							.getModel(query.getResultColumnList());

					if (!resultSetModel.getPseudoRows().getHoldRelations().isEmpty()) {
						ImpactRelation impactRelation = modelFactory.createImpactRelation();
						impactRelation.setEffectType(EffectType.update);
						impactRelation.addSource(
								new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSetModel.getPseudoRows()));
						impactRelation
								.setTarget(new PseudoRowsRelationElement<TablePseudoRows>(tableModel.getPseudoRows()));
					}

					TExpressionList columnList = expression.getExprList();
					for (int j = 0; j < columnList.size(); j++) {
						TObjectName column = columnList.getExpression(j).getObjectOperand();

						if (column.getDbObjectType() == EDbObjectType.variable) {
							continue;
						}

						ResultColumn resultColumn = resultSetModel.getColumns().get(j);
						TableColumn tableColumn = modelFactory.createTableColumn(tableModel, column, false);
						DataFlowRelation relation = modelFactory.createDataFlowRelation();
						relation.setEffectType(EffectType.update);
						relation.setTarget(new TableColumnRelationElement(tableColumn));
						relation.addSource(new ResultColumnRelationElement(resultColumn));

					}
				}
			} else if (expression.getExpressionType() == EExpressionType.simple_object_name_t) {
				TExpression setExpression = field.getExpr().getRightOperand();
				if (setExpression != null && setExpression.getSubQuery() != null) {
					TSelectSqlStatement query = setExpression.getSubQuery();
					analyzeSelectStmt(query);

					SelectResultSet resultSetModel = (SelectResultSet) modelManager
							.getModel(query.getResultColumnList());

					if (!resultSetModel.getPseudoRows().getHoldRelations().isEmpty()) {
						ImpactRelation impactRelation = modelFactory.createImpactRelation();
						impactRelation.setEffectType(EffectType.update);
						impactRelation.addSource(
								new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSetModel.getPseudoRows()));
						impactRelation
								.setTarget(new PseudoRowsRelationElement<TablePseudoRows>(tableModel.getPseudoRows()));
					}

					TObjectName column = expression.getObjectOperand();
					ResultColumn resultColumn = resultSetModel.getColumns().get(0);
					TableColumn tableColumn = modelFactory.createTableColumn(tableModel, column, false);
					DataFlowRelation relation = modelFactory.createDataFlowRelation();
					relation.setEffectType(EffectType.update);
					relation.setTarget(new TableColumnRelationElement(tableColumn));
					relation.addSource(new ResultColumnRelationElement(resultColumn));
				} else if (setExpression != null) {
					// ResultSet resultSet = modelFactory.createResultSet(stmt,
					// true);

					ResultSet resultSet = modelFactory.createResultSet(stmt, false);

					createPseudoImpactRelation(stmt, resultSet, EffectType.update);

					TObjectName columnObject = expression.getObjectOperand();

					ResultColumn updateColumn = modelFactory.createUpdateResultColumn(resultSet, columnObject);

					columnsInExpr visitor = new columnsInExpr();
					field.getExpr().getRightOperand().inOrderTraverse(visitor);

					List<TObjectName> objectNames = visitor.getObjectNames();
					List<TParseTreeNode> functions = visitor.getFunctions();
					List<TSelectSqlStatement> subquerys = visitor.getSubquerys();

					if (functions != null && !functions.isEmpty()) {
						analyzeFunctionDataFlowRelation(updateColumn, functions, EffectType.update);
					}
					
					if (subquerys!=null && !subquerys.isEmpty()) {
						analyzeSubqueryDataFlowRelation(updateColumn, subquerys, EffectType.update);
					}

					analyzeDataFlowRelation(updateColumn, objectNames, EffectType.update, functions);

					List<TConstant> constants = visitor.getConstants();
					analyzeConstantDataFlowRelation(updateColumn, constants, EffectType.update, functions);

					TableColumn tableColumn = modelFactory.createTableColumn(tableModel, columnObject, false);

					DataFlowRelation relation = modelFactory.createDataFlowRelation();
					relation.setEffectType(EffectType.update);
					relation.setTarget(new TableColumnRelationElement(tableColumn));
					relation.addSource(new ResultColumnRelationElement(updateColumn));
				}
			}
		}

		if (stmt.getJoins() != null && stmt.getJoins().size() > 0) {
			for (int i = 0; i < stmt.getJoins().size(); i++) {
				TJoin join = stmt.getJoins().getJoin(i);
				if (join.getJoinItems() != null) {
					for (int j = 0; j < join.getJoinItems().size(); j++) {
						TJoinItem joinItem = join.getJoinItems().getJoinItem(j);
						TExpression expr = joinItem.getOnCondition();
						analyzeFilterCondtion(expr, joinItem.getJoinType(), JoinClauseType.on, EffectType.update);
					}
				}
			}
		}

		if (stmt.getWhereClause() != null && stmt.getWhereClause().getCondition() != null) {
			analyzeFilterCondtion(stmt.getWhereClause().getCondition(), null, JoinClauseType.where, EffectType.update);
		}
		
		if (stmt.getOutputClause()!=null){
			TOutputClause outputClause = stmt.getOutputClause();
			if(outputClause.getSelectItemList()!=null){
				ResultSet resultSet = modelFactory.createResultSet(outputClause, false);
				for (int j = 0; j < outputClause.getSelectItemList().size(); j++) {
					TResultColumn sourceColumn = outputClause.getSelectItemList().getResultColumn(j);
					ResultColumn sourceColumnModel = modelFactory.createResultColumn(resultSet, sourceColumn);
					analyzeResultColumn(sourceColumn, EffectType.select);
					
					if(outputClause.getIntoTable()!=null){
						Table intoTableModel = modelFactory.createTableByName(outputClause.getIntoTable());
						if(outputClause.getIntoColumnList()!=null){
							TableColumn intoTableColumn = modelFactory.createInsertTableColumn(intoTableModel,
									outputClause.getIntoColumnList().getObjectName(j));
							
							DataFlowRelation relation = modelFactory.createDataFlowRelation();
							relation.setEffectType(EffectType.insert);
							relation.setTarget(new TableColumnRelationElement(intoTableColumn));
							relation.addSource(new ResultColumnRelationElement(sourceColumnModel));
						}
						else if(sourceColumn.getAliasClause()!=null || sourceColumn.getExpr().getObjectOperand()!=null){
							TObjectName tableColumnObject = null;
							if(sourceColumn.getAliasClause()!=null){
								tableColumnObject = sourceColumn.getAliasClause().getAliasName();
							}else{
								tableColumnObject = sourceColumn.getExpr().getObjectOperand();
							}
							
							TableColumn intoTableColumn = modelFactory.createInsertTableColumn(intoTableModel,
									tableColumnObject);
							DataFlowRelation relation = modelFactory.createDataFlowRelation();
							relation.setEffectType(EffectType.insert);
							relation.setTarget(new TableColumnRelationElement(intoTableColumn));
							relation.addSource(new ResultColumnRelationElement(sourceColumnModel));
						}
					}
				}
			}
		}
	}

	private void analyzeConstantDataFlowRelation(Object modelObject, List<TConstant> constants, EffectType effectType,
			List<TParseTreeNode> functions) {
		if (constants == null || constants.size() == 0)
			return;

		DataFlowRelation relation = modelFactory.createDataFlowRelation();
		relation.setEffectType(effectType);

		if (functions != null && !functions.isEmpty()) {
			relation.setFunction(getFunctionName(functions.get(0)));
		}

		if (modelObject instanceof ResultColumn) {
			relation.setTarget(new ResultColumnRelationElement((ResultColumn) modelObject));

		} else if (modelObject instanceof TableColumn) {
			relation.setTarget(new TableColumnRelationElement((TableColumn) modelObject));

		} else if (modelObject instanceof ViewColumn) {
			relation.setTarget(new ViewColumnRelationElement((ViewColumn) modelObject));

		} else {
			throw new UnsupportedOperationException();
		}

		for (int i = 0; i < constants.size(); i++) {
			TConstant constant = constants.get(i);
			relation.addSource(new ConstantRelationElement(new Constant(constant)));
		}

	}

	private String getFunctionName(TParseTreeNode parseTreeNode) {
		if (parseTreeNode instanceof TFunctionCall) {
			return ((TFunctionCall) parseTreeNode).getFunctionName().toString();
		}
		if (parseTreeNode instanceof TCaseExpression) {
			return "case-when";
		}
		return null;
	}

	private void analyzeCreateViewStmt(TCustomSqlStatement stmt, TSelectSqlStatement subquery,
			TViewAliasClause viewAlias, TObjectName viewName) {
		if (subquery != null) {
			analyzeSelectStmt(subquery);
		}

		if (viewAlias != null && viewAlias.getViewAliasItemList() != null) {
			TViewAliasItemList viewItems = viewAlias.getViewAliasItemList();
			View viewModel = modelFactory.createView(stmt, viewName);
			ResultSet resultSetModel = (ResultSet) modelManager.getModel(subquery);
			if (resultSetModel != null) {
				for (int i = 0; i < viewItems.size(); i++) {
					TObjectName alias = viewItems.getViewAliasItem(i).getAlias();
					ResultColumn resultColumn = null;
					if (resultSetModel != null) {
						if (resultSetModel.getColumns().size() <= i) {
							resultColumn = resultSetModel.getColumns().get(resultSetModel.getColumns().size() - 1);
						} else {
							resultColumn = resultSetModel.getColumns().get(i);
						}
					}
					if (alias != null) {
						ViewColumn viewColumn = modelFactory.createViewColumn(viewModel, alias, i);
						if (resultColumn != null) {
							DataFlowRelation relation = modelFactory.createDataFlowRelation();
							relation.setEffectType(EffectType.create_view);
							relation.setTarget(new ViewColumnRelationElement(viewColumn));
							relation.addSource(new ResultColumnRelationElement(resultColumn));
						}
					} else if (resultColumn.getColumnObject() instanceof TObjectName) {
						ViewColumn viewColumn = modelFactory.createViewColumn(viewModel,
								(TObjectName) resultColumn.getColumnObject(), i);
						if (resultColumn != null) {
							DataFlowRelation relation = modelFactory.createDataFlowRelation();
							relation.setEffectType(EffectType.create_view);
							relation.setTarget(new ViewColumnRelationElement(viewColumn));
							relation.addSource(new ResultColumnRelationElement(resultColumn));
						}
					} else if (resultColumn.getColumnObject() instanceof TResultColumn) {
						ViewColumn viewColumn = modelFactory.createViewColumn(viewModel,
								((TResultColumn) resultColumn.getColumnObject()).getFieldAttr(), i);
						ResultColumn column = (ResultColumn) modelManager.getModel(resultColumn.getColumnObject());
						if (column != null && !column.getStarLinkColumns().isEmpty()) {
							viewColumn.bindStarLinkColumns(column.getStarLinkColumns());
						}
						if (resultColumn != null) {
							DataFlowRelation relation = modelFactory.createDataFlowRelation();
							relation.setEffectType(EffectType.create_view);
							relation.setTarget(new ViewColumnRelationElement(viewColumn));
							relation.addSource(new ResultColumnRelationElement(resultColumn));
						}
					}
				}
				if (resultSetModel != null && !resultSetModel.getPseudoRows().getHoldRelations().isEmpty()) {
					ImpactRelation impactRelation = modelFactory.createImpactRelation();
					impactRelation.setEffectType(EffectType.create_view);
					impactRelation.addSource(
							new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSetModel.getPseudoRows()));
					impactRelation.setTarget(new PseudoRowsRelationElement<TablePseudoRows>(viewModel.getPseudoRows()));
				}
			}

			if (subquery.getResultColumnList() == null && subquery.getValueClause() != null
					&& subquery.getValueClause().getValueRows().size() == viewItems.size()) {
				for (int i = 0; i < viewItems.size(); i++) {
					TObjectName alias = viewItems.getViewAliasItem(i).getAlias();

					if (alias != null) {
						ViewColumn viewColumn = modelFactory.createViewColumn(viewModel, alias, i);

						TExpression expression = subquery.getValueClause().getValueRows().getValueRowItem(i).getExpr();

						columnsInExpr visitor = new columnsInExpr();
						expression.inOrderTraverse(visitor);
						List<TObjectName> objectNames = visitor.getObjectNames();
						List<TParseTreeNode> functions = visitor.getFunctions();

						if (functions != null && !functions.isEmpty()) {
							analyzeFunctionDataFlowRelation(viewColumn, functions, EffectType.select);

						}
						
						List<TSelectSqlStatement> subquerys = visitor.getSubquerys();
						if (subquerys!=null && !subquerys.isEmpty()) {
							analyzeSubqueryDataFlowRelation(viewColumn, subquerys, EffectType.select);
						}

						analyzeDataFlowRelation(viewColumn, objectNames, EffectType.select, functions);
						List<TConstant> constants = visitor.getConstants();
						analyzeConstantDataFlowRelation(viewColumn, constants, EffectType.select, functions);
					}
				}
			}

		} else {
			View viewModel = modelFactory.createView(stmt, viewName);
			if (subquery != null && !subquery.isCombinedQuery()) {
				SelectResultSet resultSetModel = (SelectResultSet) modelManager
						.getModel(subquery.getResultColumnList());
				for (int i = 0; i < resultSetModel.getColumns().size(); i++) {
					ResultColumn resultColumn = resultSetModel.getColumns().get(i);
					if (resultColumn.getColumnObject() instanceof TResultColumn) {
						TResultColumn columnObject = ((TResultColumn) resultColumn.getColumnObject());

						TAliasClause alias = ((TResultColumn) resultColumn.getColumnObject()).getAliasClause();
						if (alias != null && alias.getAliasName() != null) {
							ViewColumn viewColumn = modelFactory.createViewColumn(viewModel, alias.getAliasName(), i);
							DataFlowRelation relation = modelFactory.createDataFlowRelation();
							relation.setEffectType(EffectType.create_view);
							relation.setTarget(new ViewColumnRelationElement(viewColumn));
							relation.addSource(new ResultColumnRelationElement(resultColumn));
						} else if (columnObject.getFieldAttr() != null) {
							ViewColumn viewColumn = modelFactory.createViewColumn(viewModel,
									columnObject.getFieldAttr(), i);
							ResultColumn column = (ResultColumn) modelManager.getModel(resultColumn.getColumnObject());
							if (column != null && !column.getStarLinkColumns().isEmpty()) {
								viewColumn.bindStarLinkColumns(column.getStarLinkColumns());
							}
							DataFlowRelation relation = modelFactory.createDataFlowRelation();
							relation.setEffectType(EffectType.create_view);
							relation.setTarget(new ViewColumnRelationElement(viewColumn));
							relation.addSource(new ResultColumnRelationElement(resultColumn));
							if (sqlenv == null) {
								relation.setShowStarRelation(true);
								viewColumn.setShowStar(true);
								resultColumn.setShowStar(true);
								setSourceShowStar(resultColumn);
							}
						} else if (resultColumn.getAlias() != null && columnObject.getExpr()
								.getExpressionType() == EExpressionType.sqlserver_proprietary_column_alias_t) {
							ViewColumn viewColumn = modelFactory.createViewColumn(viewModel,
									columnObject.getExpr().getLeftOperand().getObjectOperand(), i);
							DataFlowRelation relation = modelFactory.createDataFlowRelation();
							relation.setEffectType(EffectType.create_view);
							relation.setTarget(new ViewColumnRelationElement(viewColumn));
							relation.addSource(new ResultColumnRelationElement(resultColumn));
						} else {
							TGSqlParser parser = columnObject.getGsqlparser();
							TObjectName viewColumnName = parser
									.parseObjectName(generateQuotedName(parser, columnObject.toString()));
							ViewColumn viewColumn = modelFactory.createViewColumn(viewModel, viewColumnName, i);
							DataFlowRelation relation = modelFactory.createDataFlowRelation();
							relation.setEffectType(EffectType.create_view);
							relation.setTarget(new ViewColumnRelationElement(viewColumn));
							relation.addSource(new ResultColumnRelationElement(resultColumn));
						}
					} else if (resultColumn.getColumnObject() instanceof TObjectName) {
						ViewColumn viewColumn = modelFactory.createViewColumn(viewModel,
								(TObjectName) resultColumn.getColumnObject(), i);
						DataFlowRelation relation = modelFactory.createDataFlowRelation();
						relation.setEffectType(EffectType.create_view);
						relation.setTarget(new ViewColumnRelationElement(viewColumn));
						relation.addSource(new ResultColumnRelationElement(resultColumn));
					}
				}
				if (!resultSetModel.getPseudoRows().getHoldRelations().isEmpty()) {
					ImpactRelation impactRelation = modelFactory.createImpactRelation();
					impactRelation.setEffectType(EffectType.create_view);
					impactRelation.addSource(
							new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSetModel.getPseudoRows()));
					impactRelation.setTarget(new PseudoRowsRelationElement<TablePseudoRows>(viewModel.getPseudoRows()));
				}
			} else if (subquery != null && subquery.isCombinedQuery()) {
				SelectSetResultSet resultSetModel = (SelectSetResultSet) modelManager.getModel(subquery);
				for (int i = 0; i < resultSetModel.getColumns().size(); i++) {
					ResultColumn resultColumn = resultSetModel.getColumns().get(i);

					if (resultColumn.getColumnObject() instanceof TResultColumn) {
						TResultColumn columnObject = ((TResultColumn) resultColumn.getColumnObject());

						TAliasClause alias = columnObject.getAliasClause();
						if (alias != null && alias.getAliasName() != null) {
							ViewColumn viewColumn = modelFactory.createViewColumn(viewModel, alias.getAliasName(), i);
							DataFlowRelation relation = modelFactory.createDataFlowRelation();
							relation.setEffectType(EffectType.create_view);
							relation.setTarget(new ViewColumnRelationElement(viewColumn));
							relation.addSource(new ResultColumnRelationElement(resultColumn));
						} else if (columnObject.getFieldAttr() != null) {
							ViewColumn viewColumn = modelFactory.createViewColumn(viewModel,
									columnObject.getFieldAttr(), i);
							ResultColumn column = (ResultColumn) modelManager.getModel(resultColumn.getColumnObject());
							if (column != null && !column.getStarLinkColumns().isEmpty()) {
								viewColumn.bindStarLinkColumns(column.getStarLinkColumns());
							}
							DataFlowRelation relation = modelFactory.createDataFlowRelation();
							relation.setEffectType(EffectType.create_view);
							relation.setTarget(new ViewColumnRelationElement(viewColumn));
							relation.addSource(new ResultColumnRelationElement(resultColumn));
						} else if (resultColumn.getAlias() != null && columnObject.getExpr()
								.getExpressionType() == EExpressionType.sqlserver_proprietary_column_alias_t) {
							ViewColumn viewColumn = modelFactory.createViewColumn(viewModel,
									columnObject.getExpr().getLeftOperand().getObjectOperand(), i);
							DataFlowRelation relation = modelFactory.createDataFlowRelation();
							relation.setEffectType(EffectType.create_view);
							relation.setTarget(new ViewColumnRelationElement(viewColumn));
							relation.addSource(new ResultColumnRelationElement(resultColumn));
						} else {
							TGSqlParser parser = columnObject.getGsqlparser();
							TObjectName viewColumnName = parser
									.parseObjectName(generateQuotedName(parser, columnObject.toString()));
							ViewColumn viewColumn = modelFactory.createViewColumn(viewModel, viewColumnName, i);
							DataFlowRelation relation = modelFactory.createDataFlowRelation();
							relation.setEffectType(EffectType.create_view);
							relation.setTarget(new ViewColumnRelationElement(viewColumn));
							relation.addSource(new ResultColumnRelationElement(resultColumn));
						}
					} else if (resultColumn.getColumnObject() instanceof TObjectName) {
						ViewColumn viewColumn = modelFactory.createViewColumn(viewModel,
								(TObjectName) resultColumn.getColumnObject(), i);
						DataFlowRelation relation = modelFactory.createDataFlowRelation();
						relation.setEffectType(EffectType.create_view);
						relation.setTarget(new ViewColumnRelationElement(viewColumn));
						relation.addSource(new ResultColumnRelationElement(resultColumn));
					}
				}
				if (!resultSetModel.getPseudoRows().getHoldRelations().isEmpty()) {
					ImpactRelation impactRelation = modelFactory.createImpactRelation();
					impactRelation.setEffectType(EffectType.create_view);
					impactRelation.addSource(
							new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSetModel.getPseudoRows()));
					impactRelation.setTarget(new PseudoRowsRelationElement<TablePseudoRows>(viewModel.getPseudoRows()));
				}
			}
		}
	}

	private void setSourceShowStar(Object resultColumn) {
		for (Relation relation : modelManager.getRelations()) {
			RelationElement<?>[] sources = relation.getSources();
			if (relation.getTarget().getElement() == resultColumn && sources != null) {

				((AbstractRelation) relation).setShowStarRelation(true);
				for (RelationElement<?> source : sources) {
					Object column = source.getElement();
					if (column instanceof TableColumn) {
						if (((TableColumn) column).isShowStar())
							continue;
						((TableColumn) column).setShowStar(true);
					}
					if (column instanceof ViewColumn) {
						if (((ViewColumn) column).isShowStar())
							continue;
						((ViewColumn) column).setShowStar(true);
					}
					if (column instanceof ResultColumn) {
						if (((ResultColumn) column).isShowStar())
							continue;
						((ResultColumn) column).setShowStar(true);
					}
					setSourceShowStar(column);
				}
			}
		}
	}

	private String generateQuotedName(TGSqlParser parser, String name) {
		return "\"" + name + "\"";
	}

	private void appendRelations(dataflow dataflow) {
		Relation[] relations = modelManager.getRelations();

		appendRelation(dataflow, relations, DataFlowRelation.class);
		appendRelation(dataflow, relations, IndirectImpactRelation.class);
		appendRecordSetRelation(dataflow, relations);
		appendRelation(dataflow, relations, ImpactRelation.class);
		appendRelation(dataflow, relations, JoinRelation.class);
	}

	private void appendRelation(dataflow dataflow, Relation[] relations, Class<? extends Relation> clazz) {
		for (int i = 0; i < relations.length; i++) {
			AbstractRelation relation = (AbstractRelation) relations[i];
			if (relation.getClass() == clazz) {
				if (relation.getSources() == null || relation.getTarget() == null) {
					continue;
				}
				Object targetElement = relation.getTarget().getElement();
				if (targetElement instanceof ResultColumn) {
					ResultColumn targetColumn = (ResultColumn) targetElement;
					
					if("*".equals(targetColumn.getName()) && targetColumn.getStarLinkColumns().isEmpty()){
						updateResultColumnStarLinks(dataflow, targetColumn, relation.getSources());
					}
					
					if (!targetColumn.getStarLinkColumns().isEmpty()) {
						for (int j = 0; j < targetColumn.getStarLinkColumns().size(); j++) {
							appendStarRelation(dataflow, relation, j);
						}
						if (!relation.isShowStarRelation()) {
							continue;
						}
					}
				} else if (targetElement instanceof TableColumn) {
					TableColumn targetColumn = (TableColumn) targetElement;
					
					if("*".equals(targetColumn.getName()) && targetColumn.getStarLinkColumns().isEmpty()){
						updateTableColumnStarLinks(dataflow, targetColumn, relation.getSources());
					}
					
					if (!targetColumn.getStarLinkColumns().isEmpty()) {
						for (int j = 0; j < targetColumn.getStarLinkColumns().size(); j++) {
							appendStarRelation(dataflow, relation, j);
						}
						if (!relation.isShowStarRelation()) {
							continue;
						}
					}
				}

				relation relationElement = new relation();
				relationElement.setType(relation.getRelationType().name());
				if (relation.getEffectType() != null) {
					relationElement.setEffectType(relation.getEffectType().name());
				}
				if (relation.getFunction() != null) {
					relationElement.setFunction(relation.getFunction());
				}
				relationElement.setId(String.valueOf(relation.getId()));
				if (relation instanceof JoinRelation) {
					relationElement.setCondition(((JoinRelation) relation).getJoinCondition());
					relationElement.setJoinType(((JoinRelation) relation).getJoinType().name());
					relationElement.setClause(((JoinRelation) relation).getJoinClauseType().name());
				}

				String targetName = null;
				Object columnObject = null;
				List<TObjectName> targetObjectNames = null;

				if (targetElement instanceof ResultSetPseudoRows) {
					ResultSetPseudoRows targetColumn = (ResultSetPseudoRows) targetElement;
					targetColumn target = new targetColumn();
					target.setId(String.valueOf(targetColumn.getId()));
					target.setColumn(targetColumn.getName());
					target.setParent_id(String.valueOf(targetColumn.getHolder().getId()));
					target.setParent_name(getResultSetName(targetColumn.getHolder()));
					if (targetColumn.getStartPosition() != null && targetColumn.getEndPosition() != null) {
						target.setCoordinate(targetColumn.getStartPosition() + "," + targetColumn.getEndPosition());
					}
					if (relation instanceof RecordSetRelation) {
						target.setFunction(((RecordSetRelation) relation).getAggregateFunction());
					}
					target.setSource("system");
					targetName = targetColumn.getName();
					relationElement.setTarget(target);
				} else if (targetElement instanceof TablePseudoRows) {
					TablePseudoRows targetColumn = (TablePseudoRows) targetElement;
					targetColumn target = new targetColumn();
					target.setId(String.valueOf(targetColumn.getId()));
					target.setColumn(targetColumn.getName());
					target.setParent_id(String.valueOf(targetColumn.getHolder().getId()));
					target.setParent_name(getTableName(targetColumn.getHolder()));
					if (targetColumn.getStartPosition() != null && targetColumn.getEndPosition() != null) {
						target.setCoordinate(targetColumn.getStartPosition() + "," + targetColumn.getEndPosition());
					}
					if (relation instanceof RecordSetRelation) {
						target.setFunction(((RecordSetRelation) relation).getAggregateFunction());
					}
					target.setSource("system");
					targetName = targetColumn.getName();
					relationElement.setTarget(target);
				} else if (targetElement instanceof ResultColumn) {
					ResultColumn targetColumn = (ResultColumn) targetElement;
					targetColumn target = new targetColumn();
					target.setId(String.valueOf(targetColumn.getId()));
					target.setColumn(targetColumn.getName());
					target.setParent_id(String.valueOf(targetColumn.getResultSet().getId()));
					target.setParent_name(getResultSetName(targetColumn.getResultSet()));
					if (targetColumn.getStartPosition() != null && targetColumn.getEndPosition() != null) {
						target.setCoordinate(targetColumn.getStartPosition() + "," + targetColumn.getEndPosition());
					}
					if (relation instanceof RecordSetRelation) {
						target.setFunction(((RecordSetRelation) relation).getAggregateFunction());
					}
					targetName = targetColumn.getName();
					if (((ResultColumn) targetColumn).getColumnObject() instanceof TResultColumn) {
						columnsInExpr visitor = new columnsInExpr();
						((TResultColumn) ((ResultColumn) targetColumn).getColumnObject()).getExpr()
								.inOrderTraverse(visitor);
						targetObjectNames = visitor.getObjectNames();
					}

					if (targetElement instanceof FunctionResultColumn) {
						columnObject = ((FunctionResultColumn) targetElement).getColumnObject();
					}

					relationElement.setTarget(target);
				} else if (targetElement instanceof TableColumn) {
					TableColumn targetColumn = (TableColumn) targetElement;
					targetColumn target = new targetColumn();
					target.setId(String.valueOf(targetColumn.getId()));
					target.setColumn(targetColumn.getName());
					target.setParent_id(String.valueOf(targetColumn.getTable().getId()));
					target.setParent_name(getTableName(targetColumn.getTable()));
					if (targetColumn.getStartPosition() != null && targetColumn.getEndPosition() != null) {
						target.setCoordinate(targetColumn.getStartPosition() + "," + targetColumn.getEndPosition());
					}
					if (relation instanceof RecordSetRelation) {
						target.setFunction(((RecordSetRelation) relation).getAggregateFunction());
					}
					targetName = targetColumn.getName();
					relationElement.setTarget(target);
				} else if (targetElement instanceof ViewColumn) {
					ViewColumn targetColumn = (ViewColumn) targetElement;
					targetColumn target = new targetColumn();
					target.setId(String.valueOf(targetColumn.getId()));
					target.setColumn(targetColumn.getName());
					target.setParent_id(String.valueOf(targetColumn.getView().getId()));
					target.setParent_name(targetColumn.getView().getFullName());
					if (targetColumn.getStartPosition() != null && targetColumn.getEndPosition() != null) {
						target.setCoordinate(targetColumn.getStartPosition() + "," + targetColumn.getEndPosition());
					}
					if (relation instanceof RecordSetRelation) {
						target.setFunction(((RecordSetRelation) relation).getAggregateFunction());
					}
					targetName = targetColumn.getName();
					relationElement.setTarget(target);
				} else if (targetElement instanceof Table) {
					Table table = (Table) targetElement;
					targetColumn target = new targetColumn();
					target.setTarget_id(String.valueOf(table.getId()));
					target.setTarget_name(getTableName(table));
					if (table.getStartPosition() != null && table.getEndPosition() != null) {
						target.setCoordinate(table.getStartPosition() + "," + table.getEndPosition());
					}
					relationElement.setTarget(target);
				} else {
					continue;
				}

				RelationElement<?>[] sourceElements = relation.getSources();
				if (sourceElements.length == 0) {
					continue;
				}

				boolean append = false;
				for (int j = 0; j < sourceElements.length; j++) {
					Object sourceElement = sourceElements[j].getElement();
					TObjectName sourceColumnName = null;
					if (sourceElements[j] instanceof ResultColumnRelationElement) {
						sourceColumnName = ((ResultColumnRelationElement) sourceElements[j]).getColumnName();
					}
					if (sourceElement instanceof ResultColumn) {
						ResultColumn sourceColumn = (ResultColumn) sourceElement;
						if (!sourceColumn.getStarLinkColumns().isEmpty() && !relation.isShowStarRelation()) {
							sourceColumn source = new sourceColumn();

							if (targetElement instanceof ViewColumn) {
								source.setId(String.valueOf(sourceColumn.getId()) + "_"
										+ ((ViewColumn) targetElement).getColumnIndex());
								source.setColumn(getColumnName(sourceColumn.getStarLinkColumns()
										.get(((ViewColumn) targetElement).getColumnIndex())));
								source.setParent_id(String.valueOf(sourceColumn.getResultSet().getId()));
								source.setParent_name(getResultSetName(sourceColumn.getResultSet()));
								if (sourceColumn.getStartPosition() != null && sourceColumn.getEndPosition() != null) {
									source.setCoordinate(
											sourceColumn.getStartPosition() + "," + sourceColumn.getEndPosition());
								}
								append = true;
								relationElement.getSources().add(source);
							} else {
								if (targetObjectNames != null && !targetObjectNames.isEmpty()) {
									for (int k = 0; k < targetObjectNames.size(); k++) {
										int index = getColumnIndex(sourceColumn.getStarLinkColumns(),
												targetObjectNames.get(k).getColumnNameOnly());
										if (index != -1) {
											source = new sourceColumn();
											source.setId(String.valueOf(sourceColumn.getId()) + "_" + index);
											source.setColumn(
													getColumnName(sourceColumn.getStarLinkColumns().get(index)));
											source.setParent_id(String.valueOf(sourceColumn.getResultSet().getId()));
											source.setParent_name(getResultSetName(sourceColumn.getResultSet()));
											if (sourceColumn.getStartPosition() != null
													&& sourceColumn.getEndPosition() != null) {
												source.setCoordinate(sourceColumn.getStartPosition() + ","
														+ sourceColumn.getEndPosition());
											}
											append = true;
											relationElement.getSources().add(source);
										} else {
											source = new sourceColumn();
											source.setId(String.valueOf(sourceColumn.getId()));
											source.setColumn(relationElement.getTarget().getColumn());
											source.setParent_id(String.valueOf(sourceColumn.getResultSet().getId()));
											source.setParent_name(getResultSetName(sourceColumn.getResultSet()));
											if (sourceColumn.getStartPosition() != null
													&& sourceColumn.getEndPosition() != null) {
												source.setCoordinate(sourceColumn.getStartPosition() + ","
														+ sourceColumn.getEndPosition());
											}
											append = true;
											relationElement.getSources().add(source);
										}
									}
								} else {
									if (columnObject instanceof TWhenClauseItemList) {
										TWhenClauseItemList list = (TWhenClauseItemList) columnObject;
										for (int k = 0; k < list.size(); k++) {
											TWhenClauseItem element = (TWhenClauseItem) list.getElement(k);
											columnsInExpr visitor = new columnsInExpr();
											element.getReturn_expr().inOrderTraverse(visitor);
											List<TObjectName> objectNames = visitor.getObjectNames();
											if (objectNames == null) {
												continue;
											}
											for (int x = 0; x < objectNames.size(); x++) {
												int index = getColumnIndex(sourceColumn.getStarLinkColumns(),
														objectNames.get(x).getColumnNameOnly());
												if (index != -1) {
													source.setId(String.valueOf(sourceColumn.getId()) + "_" + index);
													source.setColumn(getColumnName(
															sourceColumn.getStarLinkColumns().get(index)));
												} else {
													source.setId(String.valueOf(sourceColumn.getId()));
													source.setColumn(sourceColumn.getName());
												}
												source.setParent_id(
														String.valueOf(sourceColumn.getResultSet().getId()));
												source.setParent_name(getResultSetName(sourceColumn.getResultSet()));
												if (sourceColumn.getStartPosition() != null
														&& sourceColumn.getEndPosition() != null) {
													source.setCoordinate(sourceColumn.getStartPosition() + ","
															+ sourceColumn.getEndPosition());
												}
												append = true;
												relationElement.getSources().add(source);
											}
										}
									} else {
										int index = getColumnIndex(sourceColumn.getStarLinkColumns(), targetName);
										if (index != -1) {
											source.setId(String.valueOf(sourceColumn.getId()) + "_" + index);
											source.setColumn(
													getColumnName(sourceColumn.getStarLinkColumns().get(index)));
										}

										if (index == -1 && sourceColumnName != null) {
											index = getColumnIndex(sourceColumn.getStarLinkColumns(),
													sourceColumnName.getColumnNameOnly());
										}

										if (index != -1) {
											source.setId(String.valueOf(sourceColumn.getId()) + "_" + index);
											source.setColumn(
													getColumnName(sourceColumn.getStarLinkColumns().get(index)));
										} else {
											source.setId(String.valueOf(sourceColumn.getId()));
											source.setColumn(sourceColumn.getName());
										}
										source.setParent_id(String.valueOf(sourceColumn.getResultSet().getId()));
										source.setParent_name(getResultSetName(sourceColumn.getResultSet()));
										if (sourceColumn.getStartPosition() != null
												&& sourceColumn.getEndPosition() != null) {
											source.setCoordinate(sourceColumn.getStartPosition() + ","
													+ sourceColumn.getEndPosition());
										}
										append = true;
										relationElement.getSources().add(source);
									}
								}
							}
						} else {
							sourceColumn source = new sourceColumn();
							source.setId(String.valueOf(sourceColumn.getId()));
							source.setColumn(sourceColumn.getName());
							source.setParent_id(String.valueOf(sourceColumn.getResultSet().getId()));
							source.setParent_name(getResultSetName(sourceColumn.getResultSet()));
							if (sourceColumn.getStartPosition() != null && sourceColumn.getEndPosition() != null) {
								source.setCoordinate(
										sourceColumn.getStartPosition() + "," + sourceColumn.getEndPosition());
							}
							append = true;
							relationElement.getSources().add(source);
						}
					} else if (sourceElement instanceof TableColumn) {
						TableColumn sourceColumn = (TableColumn) sourceElement;
						sourceColumn source = new sourceColumn();
						source.setId(String.valueOf(sourceColumn.getId()));
						source.setColumn(sourceColumn.getName());
						source.setParent_id(String.valueOf(sourceColumn.getTable().getId()));
						source.setParent_name(getTableName(sourceColumn.getTable()));
						if (sourceColumn.getStartPosition() != null && sourceColumn.getEndPosition() != null) {
							source.setCoordinate(sourceColumn.getStartPosition() + "," + sourceColumn.getEndPosition());
						}
						append = true;
						relationElement.getSources().add(source);
					} else if (sourceElement instanceof TablePseudoRows) {
						TablePseudoRows sourceColumn = (TablePseudoRows) sourceElement;
						sourceColumn source = new sourceColumn();
						source.setId(String.valueOf(sourceColumn.getId()));
						source.setColumn(sourceColumn.getName());
						source.setParent_id(String.valueOf(sourceColumn.getHolder().getId()));
						source.setParent_name(getTableName(sourceColumn.getHolder()));
						if (sourceColumn.getStartPosition() != null && sourceColumn.getEndPosition() != null) {
							source.setCoordinate(sourceColumn.getStartPosition() + "," + sourceColumn.getEndPosition());
						}
						source.setSource("system");
						append = true;
						relationElement.getSources().add(source);
					} else if (sourceElement instanceof ResultSetPseudoRows) {
						ResultSetPseudoRows sourceColumn = (ResultSetPseudoRows) sourceElement;
						sourceColumn source = new sourceColumn();
						source.setId(String.valueOf(sourceColumn.getId()));
						source.setColumn(sourceColumn.getName());
						source.setParent_id(String.valueOf(sourceColumn.getHolder().getId()));
						source.setParent_name(getResultSetName(sourceColumn.getHolder()));
						if (sourceColumn.getStartPosition() != null && sourceColumn.getEndPosition() != null) {
							source.setCoordinate(sourceColumn.getStartPosition() + "," + sourceColumn.getEndPosition());
						}
						source.setSource("system");
						append = true;
						relationElement.getSources().add(source);
					} else if (sourceElement instanceof Constant) {
						Constant sourceColumn = (Constant) sourceElement;
						sourceColumn source = new sourceColumn();
						source.setId(String.valueOf(sourceColumn.getId()));
						source.setColumn(sourceColumn.getName());
						source.setColumn_type("constant");
						if (sourceColumn.getStartPosition() != null && sourceColumn.getEndPosition() != null) {
							source.setCoordinate(sourceColumn.getStartPosition() + "," + sourceColumn.getEndPosition());
						}
						append = true;
						relationElement.getSources().add(source);
					} else if (sourceElement instanceof Table) {
						Table table = (Table) sourceElement;
						sourceColumn source = new sourceColumn();
						source.setSource_id(String.valueOf(table.getId()));
						source.setSource_name(getTableName(table));
						if (table.getStartPosition() != null && table.getEndPosition() != null) {
							source.setCoordinate(table.getStartPosition() + "," + table.getEndPosition());
						}
						append = true;
						relationElement.getSources().add(source);
					}

					if (relation instanceof ImpactRelation) {
						ESqlClause clause = getSqlClause(sourceElements[j]);
						if (clause != null
								&& (relationElement.getSources() != null && !relationElement.getSources().isEmpty())) {
							relationElement.getSources().get(relationElement.getSources().size() - 1)
									.setClauseType(clause.name());
						}
					}
				}
				if (append)
					dataflow.getRelations().add(relationElement);
			}
		}
	}



	private void updateResultColumnStarLinks(dataflow dataflow, ResultColumn targetColumn,
			RelationElement<?>[] sourceElements) {
		if (sourceElements == null || sourceElements.length == 0)
			return;

		for (int j = 0; j < sourceElements.length; j++) {
			Object sourceElement = sourceElements[j].getElement();
			if (sourceElement instanceof ResultColumn) {
				ResultColumn source = (ResultColumn)sourceElement;
				if (source.getStarLinkColumns() != null && !source.getStarLinkColumns().isEmpty()) {
					targetColumn.getStarLinkColumns().addAll(source.getStarLinkColumns());
				}
				else if (!"*".equals(source.getName())) {
					if (source.getColumnObject() instanceof TObjectName) {
						targetColumn.getStarLinkColumns().add((TObjectName) source.getColumnObject());
					} else if (source.getColumnObject() instanceof TResultColumn) {
						TObjectName field = ((TResultColumn) source.getColumnObject()).getFieldAttr();
						if (field != null) {
							targetColumn.getStarLinkColumns().add(field);
						}
					}
				}
			} else if (sourceElement instanceof TableColumn) {
				TableColumn source = (TableColumn)sourceElement;
				if(source.getStarLinkColumns()!=null){
					targetColumn.getStarLinkColumns().addAll(source.getStarLinkColumns());
				}
				else if (!"*".equals(source.getName())) {
					targetColumn.getStarLinkColumns().add(source.getColumnObject());
				}
			}
		}
		
		if(!targetColumn.getStarLinkColumns().isEmpty()){
			table resultSetElement = dataflow.getResultsets().stream().filter(t->t.getId().equals(String.valueOf(targetColumn.getResultSet().getId()))).findFirst().get();
			for (int k = 0; k < targetColumn.getStarLinkColumns().size(); k++) {
				column columnElement = new column();
				columnElement.setId( String.valueOf(targetColumn.getId()) + "_" + k);
				TObjectName column = targetColumn.getStarLinkColumns().get(k);
				String columnName =  getColumnName(column);
				columnElement.setName(columnName);
				if(targetColumn.isFunction()){
					columnElement.setIsFunction(String.valueOf(targetColumn.isFunction()));
				}
				if (targetColumn.getStartPosition() != null && targetColumn.getEndPosition() != null) {
					columnElement.setCoordinate(
							targetColumn.getStartPosition() + "," + targetColumn.getEndPosition());
				}
				resultSetElement.getColumns().add(columnElement);
			}
		}
	}
	
	private void updateTableColumnStarLinks(dataflow dataflow, TableColumn targetColumn, RelationElement<?>[] sourceElements) {
		if (sourceElements == null || sourceElements.length == 0)
			return;

		for (int j = 0; j < sourceElements.length; j++) {
			Object sourceElement = sourceElements[j].getElement();
			if (sourceElement instanceof ResultColumn) {
				ResultColumn source = (ResultColumn)sourceElement;
				if (source.getStarLinkColumns() != null && !source.getStarLinkColumns().isEmpty()) {
					targetColumn.getStarLinkColumns().addAll(source.getStarLinkColumns());
				}
				else if (!"*".equals(source.getName())) {
					if (source.getColumnObject() instanceof TObjectName) {
						targetColumn.getStarLinkColumns().add((TObjectName) source.getColumnObject());
					} else if (source.getColumnObject() instanceof TResultColumn) {
						TObjectName field = ((TResultColumn) source.getColumnObject()).getFieldAttr();
						if (field != null) {
							targetColumn.getStarLinkColumns().add(field);
						}
					}
				}
			} else if (sourceElement instanceof TableColumn) {
				TableColumn source = (TableColumn)sourceElement;
				if(source.getStarLinkColumns()!=null){
					targetColumn.getStarLinkColumns().addAll(source.getStarLinkColumns());
				}
				else if (!"*".equals(source.getName())) {
					targetColumn.getStarLinkColumns().add(source.getColumnObject());
				}
			}
		}
		
		if (!targetColumn.getStarLinkColumns().isEmpty()) {
			Optional<table> tableElement = dataflow.getTables().stream()
					.filter(t -> t.getId().equals(String.valueOf(targetColumn.getTable().getId()))).findFirst();
			if (!tableElement.isPresent()) {
				tableElement = dataflow.getViews().stream()
						.filter(t -> t.getId().equals(String.valueOf(targetColumn.getTable().getId()))).findFirst();
			}
			if (tableElement.isPresent()) {
				for (int k = 0; k < targetColumn.getStarLinkColumns().size(); k++) {
					column columnElement = new column();
					columnElement.setId(String.valueOf(targetColumn.getId()) + "_" + k);
					TObjectName column = targetColumn.getStarLinkColumns().get(k);
					String columnName = getColumnName(column);
					columnElement.setName(columnName);
					if (targetColumn.getStartPosition() != null && targetColumn.getEndPosition() != null) {
						columnElement
								.setCoordinate(targetColumn.getStartPosition() + "," + targetColumn.getEndPosition());
					}
					tableElement.get().getColumns().add(columnElement);
				}
			}
		}
	}

	private ESqlClause getSqlClause(RelationElement<?> relationElement) {
		if (relationElement instanceof TableColumnRelationElement) {
			return ((TableColumnRelationElement) relationElement).getRelationLocation();
		} else if (relationElement instanceof ResultColumnRelationElement) {
			return ((ResultColumnRelationElement) relationElement).getRelationLocation();
		}
		return null;
	}

	private int getColumnIndex(List<TObjectName> starLinkColumns, String targetName) {
		for (int i = 0; i < starLinkColumns.size(); i++) {
			if (SQLUtil.getIdentifierNormalName(starLinkColumns.get(i).toString())
					.equalsIgnoreCase(SQLUtil.getIdentifierNormalName(targetName))
					|| getColumnName(starLinkColumns.get(i))
							.equalsIgnoreCase(SQLUtil.getIdentifierNormalName(targetName)))
				return i;
		}
		return -1;
	}

	private void appendStarRelation(dataflow dataflow, AbstractRelation relation, int index) {
		Object targetElement = relation.getTarget().getElement();

		relation relationElement = new relation();
		relationElement.setType(relation.getRelationType().name());
		if (relation.getEffectType() != null) {
			relationElement.setEffectType(relation.getEffectType().name());
		}
		relationElement.setId(String.valueOf(relation.getId()) + "_" + index);

		String targetName = "";

		if (targetElement instanceof ResultColumn) {
			ResultColumn targetColumn = (ResultColumn) targetElement;

			TObjectName linkTargetColumn = targetColumn.getStarLinkColumns().get(index);
			targetName = getColumnName(linkTargetColumn);

			targetColumn target = new targetColumn();
			target.setId(String.valueOf(targetColumn.getId()) + "_" + index);
			target.setColumn(targetName);

			target.setParent_id(String.valueOf(targetColumn.getResultSet().getId()));
			target.setParent_name(getResultSetName(targetColumn.getResultSet()));
			if (targetColumn.getStartPosition() != null && targetColumn.getEndPosition() != null) {
				target.setCoordinate(targetColumn.getStartPosition() + "," + targetColumn.getEndPosition());
			}
			relationElement.setTarget(target);
		} else if (targetElement instanceof ViewColumn) {
			ViewColumn targetColumn = (ViewColumn) targetElement;

			TObjectName linkTargetColumn = targetColumn.getStarLinkColumns().get(index);
			targetName = getColumnName(linkTargetColumn);

			targetColumn target = new targetColumn();
			target.setId(String.valueOf(targetColumn.getId()) + "_" + index);
			target.setColumn(targetName);
			target.setParent_id(String.valueOf(targetColumn.getView().getId()));
			target.setParent_name(targetColumn.getView().getName());
			if (targetColumn.getStartPosition() != null && targetColumn.getEndPosition() != null) {
				target.setCoordinate(targetColumn.getStartPosition() + "," + targetColumn.getEndPosition());
			}
			relationElement.setTarget(target);
		} else if (targetElement instanceof TableColumn) {
			TableColumn targetColumn = (TableColumn) targetElement;

			TObjectName linkTargetColumn = targetColumn.getStarLinkColumns().get(index);
			targetName = getColumnName(linkTargetColumn);

			targetColumn target = new targetColumn();
			target.setId(String.valueOf(targetColumn.getId()) + "_" + index);
			target.setColumn(targetName);
			target.setParent_id(String.valueOf(targetColumn.getTable().getId()));
			target.setParent_name(targetColumn.getTable().getName());
			if (targetColumn.getStartPosition() != null && targetColumn.getEndPosition() != null) {
				target.setCoordinate(targetColumn.getStartPosition() + "," + targetColumn.getEndPosition());
			}
			relationElement.setTarget(target);
		} else {
			return;
		}

		RelationElement<?>[] sourceElements = relation.getSources();
		if (sourceElements.length == 0) {
			return;
		}

		for (int j = 0; j < sourceElements.length; j++) {
			Object sourceElement = sourceElements[j].getElement();
			if (sourceElement instanceof ResultColumn) {
				ResultColumn sourceColumn = (ResultColumn) sourceElement;
				if (!sourceColumn.getStarLinkColumns().isEmpty()) {
					for (int k = 0; k < sourceColumn.getStarLinkColumns().size(); k++) {
						TObjectName sourceName = sourceColumn.getStarLinkColumns().get(k);
						sourceColumn source = new sourceColumn();
						source.setId(String.valueOf(sourceColumn.getId()) + "_" + k);
						source.setColumn(getColumnName(sourceName));
						source.setParent_id(String.valueOf(sourceColumn.getResultSet().getId()));
						source.setParent_name(getResultSetName(sourceColumn.getResultSet()));
						if (sourceColumn.getStartPosition() != null && sourceColumn.getEndPosition() != null) {
							source.setCoordinate(sourceColumn.getStartPosition() + "," + sourceColumn.getEndPosition());
						}
						if (relation.getRelationType() == RelationType.fdd) {
							if (!SQLUtil.getIdentifierNormalName(targetName).equalsIgnoreCase(getColumnName(sourceName))
									&& !"*".equals(getColumnName(sourceName)))
								continue;
						}
						relationElement.getSources().add(source);
					}
				} else {
					sourceColumn source = new sourceColumn();
					source.setId(String.valueOf(sourceColumn.getId()));
					source.setColumn(sourceColumn.getName());
					source.setParent_id(String.valueOf(sourceColumn.getResultSet().getId()));
					source.setParent_name(getResultSetName(sourceColumn.getResultSet()));
					if (sourceColumn.getStartPosition() != null && sourceColumn.getEndPosition() != null) {
						source.setCoordinate(sourceColumn.getStartPosition() + "," + sourceColumn.getEndPosition());
					}
					if (relation.getRelationType() == RelationType.fdd) {
						if (!SQLUtil.getIdentifierNormalName(targetName)
								.equalsIgnoreCase(SQLUtil.getIdentifierNormalName(sourceColumn.getName()))
								&& !"*".equals(sourceColumn.getName()))
							continue;
					}
					relationElement.getSources().add(source);
				}
			} else if (sourceElement instanceof TableColumn) {
				TableColumn sourceColumn = (TableColumn) sourceElement;
				if (!sourceColumn.getStarLinkColumns().isEmpty()) {
					for (int k = 0; k < sourceColumn.getStarLinkColumns().size(); k++) {
						TObjectName sourceName = sourceColumn.getStarLinkColumns().get(k);
						sourceColumn source = new sourceColumn();
						source.setId(String.valueOf(sourceColumn.getId()) + "_" + k);
						source.setColumn(getColumnName(sourceName));
						source.setParent_id(String.valueOf(sourceColumn.getTable().getId()));
						source.setParent_name(getTableName(sourceColumn.getTable()));
						if (sourceColumn.getStartPosition() != null && sourceColumn.getEndPosition() != null) {
							source.setCoordinate(sourceColumn.getStartPosition() + "," + sourceColumn.getEndPosition());
						}
						if (relation.getRelationType() == RelationType.fdd) {
							if (!SQLUtil.getIdentifierNormalName(targetName).equalsIgnoreCase(getColumnName(sourceName))
									&& !"*".equals(getColumnName(sourceName)))
								continue;
						}
						relationElement.getSources().add(source);
					}
				} else {
					sourceColumn source = new sourceColumn();
					source.setId(String.valueOf(sourceColumn.getId()));
					source.setColumn(sourceColumn.getName());
					source.setParent_id(String.valueOf(sourceColumn.getTable().getId()));
					source.setParent_name(getTableName(sourceColumn.getTable()));
					if (sourceColumn.getStartPosition() != null && sourceColumn.getEndPosition() != null) {
						source.setCoordinate(sourceColumn.getStartPosition() + "," + sourceColumn.getEndPosition());
					}
					if (relation.getRelationType() == RelationType.fdd) {
						if (!SQLUtil.getIdentifierNormalName(targetName)
								.equalsIgnoreCase(SQLUtil.getIdentifierNormalName(sourceColumn.getName()))
								&& !"*".equals(sourceColumn.getName()))
							continue;
					}
					relationElement.getSources().add(source);
				}
			}
		}

		dataflow.getRelations().add(relationElement);
	}

	private String getColumnName(TObjectName column) {
		if (column == null) {
			return null;
		}
		String name = column.getColumnNameOnly();
		if (name == null || "".equals(name.trim())) {
			return SQLUtil.getIdentifierNormalName(column.toString().trim());
		} else
			return SQLUtil.getIdentifierNormalName(name.trim());
	}
	
	private String getColumnName(String column) {
		if (column == null) {
			return null;
		}
		String name = column.substring(column.lastIndexOf(".") + 1);
		if (name == null || "".equals(name.trim())) {
			return SQLUtil.getIdentifierNormalName(column.toString().trim());
		} else
			return SQLUtil.getIdentifierNormalName(name.trim());
	}

	private void appendRecordSetRelation(dataflow dataflow, Relation[] relations) {
		for (int i = 0; i < relations.length; i++) {
			AbstractRelation relation = (AbstractRelation) relations[i];
			relation relationElement = new relation();
			relationElement.setType(relation.getRelationType().name());
			if (relation.getFunction() != null) {
				relationElement.setFunction(relation.getFunction());
			}
			if (relation.getEffectType() != null) {
				relationElement.setEffectType(relation.getEffectType().name());
			}
			relationElement.setId(String.valueOf(relation.getId()));

			if (relation instanceof RecordSetRelation) {
				RecordSetRelation recordCountRelation = (RecordSetRelation) relation;

				Object targetElement = recordCountRelation.getTarget().getElement();
				if (targetElement instanceof ResultColumn) {
					ResultColumn targetColumn = (ResultColumn) targetElement;
					targetColumn target = new targetColumn();
					target.setId(String.valueOf(targetColumn.getId()));
					target.setColumn(targetColumn.getName());
					target.setFunction(recordCountRelation.getAggregateFunction());
					target.setParent_id(String.valueOf(targetColumn.getResultSet().getId()));
					target.setParent_name(getResultSetName(targetColumn.getResultSet()));
					if (targetColumn.getStartPosition() != null && targetColumn.getEndPosition() != null) {
						target.setCoordinate(targetColumn.getStartPosition() + "," + targetColumn.getEndPosition());
					}
					relationElement.setTarget(target);
				} else if (targetElement instanceof TableColumn) {
					TableColumn targetColumn = (TableColumn) targetElement;
					targetColumn target = new targetColumn();
					target.setId(String.valueOf(targetColumn.getId()));
					target.setColumn(targetColumn.getName());
					target.setFunction(recordCountRelation.getAggregateFunction());
					target.setParent_id(String.valueOf(targetColumn.getTable().getId()));
					target.setParent_name(getTableName(targetColumn.getTable()));
					if (targetColumn.getStartPosition() != null && targetColumn.getEndPosition() != null) {
						target.setCoordinate(targetColumn.getStartPosition() + "," + targetColumn.getEndPosition());
					}
					relationElement.setTarget(target);
				} else if (targetElement instanceof ViewColumn) {
					ViewColumn targetColumn = (ViewColumn) targetElement;
					targetColumn target = new targetColumn();
					target.setId(String.valueOf(targetColumn.getId()));
					target.setColumn(targetColumn.getName());
					target.setFunction(recordCountRelation.getAggregateFunction());
					target.setParent_id(String.valueOf(targetColumn.getView().getId()));
					target.setParent_name(getTableName(targetColumn.getView()));
					if (targetColumn.getStartPosition() != null && targetColumn.getEndPosition() != null) {
						target.setCoordinate(targetColumn.getStartPosition() + "," + targetColumn.getEndPosition());
					}
					relationElement.setTarget(target);
				} else if (targetElement instanceof ResultSetPseudoRows) {
					ResultSetPseudoRows targetColumn = (ResultSetPseudoRows) targetElement;
					targetColumn target = new targetColumn();
					target.setId(String.valueOf(targetColumn.getId()));
					target.setColumn(targetColumn.getName());
					target.setParent_id(String.valueOf(targetColumn.getHolder().getId()));
					target.setParent_name(getResultSetName(targetColumn.getHolder()));
					if (targetColumn.getStartPosition() != null && targetColumn.getEndPosition() != null) {
						target.setCoordinate(targetColumn.getStartPosition() + "," + targetColumn.getEndPosition());
					}
					target.setSource("system");
					relationElement.setTarget(target);
				} else {
					continue;
				}

				RelationElement<?>[] sourceElements = recordCountRelation.getSources();
				if (sourceElements.length == 0) {
					continue;
				}

				boolean append = false;
				for (int j = 0; j < sourceElements.length; j++) {
					Object sourceElement = sourceElements[j].getElement();
					if (sourceElement instanceof Table) {
						Table table = (Table) sourceElement;
						sourceColumn source = new sourceColumn();
						source.setSource_id(String.valueOf(table.getId()));
						source.setSource_name(getTableName(table));
						if (table.getStartPosition() != null && table.getEndPosition() != null) {
							source.setCoordinate(table.getStartPosition() + "," + table.getEndPosition());
						}
						append = true;
						relationElement.getSources().add(source);
					} else if (sourceElement instanceof QueryTable) {
						QueryTable table = (QueryTable) sourceElement;
						sourceColumn source = new sourceColumn();
						source.setSource_id(String.valueOf(table.getId()));
						source.setSource_name(getResultSetName(table));
						if (table.getStartPosition() != null && table.getEndPosition() != null) {
							source.setCoordinate(table.getStartPosition() + "," + table.getEndPosition());
						}
						append = true;
						relationElement.getSources().add(source);
					} else if (sourceElement instanceof TablePseudoRows) {
						TablePseudoRows pseudoRows = (TablePseudoRows) sourceElement;
						sourceColumn source = new sourceColumn();
						source.setId(String.valueOf(pseudoRows.getId()));
						source.setColumn(pseudoRows.getName());
						source.setParent_id(String.valueOf(pseudoRows.getHolder().getId()));
						source.setParent_name(getTableName(pseudoRows.getHolder()));
						if (pseudoRows.getStartPosition() != null && pseudoRows.getEndPosition() != null) {
							source.setCoordinate(pseudoRows.getStartPosition() + "," + pseudoRows.getEndPosition());
						}
						source.setSource("system");
						append = true;
						relationElement.getSources().add(source);
					} else if (sourceElement instanceof ResultSetPseudoRows) {
						ResultSetPseudoRows pseudoRows = (ResultSetPseudoRows) sourceElement;
						sourceColumn source = new sourceColumn();
						source.setId(String.valueOf(pseudoRows.getId()));
						source.setColumn(pseudoRows.getName());
						source.setParent_id(String.valueOf(pseudoRows.getHolder().getId()));
						source.setParent_name(getResultSetName(pseudoRows.getHolder()));
						if (pseudoRows.getStartPosition() != null && pseudoRows.getEndPosition() != null) {
							source.setCoordinate(pseudoRows.getStartPosition() + "," + pseudoRows.getEndPosition());
						}
						source.setSource("system");
						append = true;
						relationElement.getSources().add(source);
					} else if (sourceElement instanceof TableColumn) {
						TableColumn sourceColumn = (TableColumn) sourceElement;
						sourceColumn source = new sourceColumn();
						source.setId(String.valueOf(sourceColumn.getId()));
						source.setColumn(sourceColumn.getName());
						source.setParent_id(String.valueOf(sourceColumn.getTable().getId()));
						source.setParent_name(getTableName(sourceColumn.getTable()));
						if (sourceColumn.getStartPosition() != null && sourceColumn.getEndPosition() != null) {
							source.setCoordinate(sourceColumn.getStartPosition() + "," + sourceColumn.getEndPosition());
						}
						append = true;
						relationElement.getSources().add(source);
					}
					if (sourceElement instanceof ResultColumn) {
						ResultColumn sourceColumn = (ResultColumn) sourceElement;
						sourceColumn source = new sourceColumn();
						source.setId(String.valueOf(sourceColumn.getId()));
						source.setColumn(sourceColumn.getName());
						source.setParent_id(String.valueOf(sourceColumn.getResultSet().getId()));
						source.setParent_name(getResultSetName(sourceColumn.getResultSet()));
						if (sourceColumn.getStartPosition() != null && sourceColumn.getEndPosition() != null) {
							source.setCoordinate(sourceColumn.getStartPosition() + "," + sourceColumn.getEndPosition());
						}
						append = true;
						relationElement.getSources().add(source);
					}
				}

				if (append)
					dataflow.getRelations().add(relationElement);
			}
		}
	}

	private void appendResultSets(dataflow dataflow) {
		List<TResultColumnList> selectResultSets = modelManager.getSelectResultSets();
		List<TObjectNameList> queryAliasTables = modelManager.getQueryAliasTables();
		List<TTable> tableWithSelectSetResultSets = modelManager.getTableWithSelectSetResultSets();
		List<TSelectSqlStatement> selectSetResultSets = modelManager.getSelectSetResultSets();
		List<TCTE> ctes = modelManager.getCTEs();
		List<TParseTreeNode> mergeResultSets = modelManager.getMergeResultSets();
		List<TParseTreeNode> outputResultSets = modelManager.getOutputResultSets();
		List<TParseTreeNode> updateResultSets = modelManager.getUpdateResultSets();
		List<TParseTreeNode> functionCalls = modelManager.getFunctoinCalls();
		List<TParseTreeNode> cursors = modelManager.getCursors();

		List<TParseTreeNode> resultSets = new ArrayList<TParseTreeNode>();
		resultSets.addAll(selectResultSets);
		resultSets.addAll(queryAliasTables);
		resultSets.addAll(tableWithSelectSetResultSets);
		resultSets.addAll(selectSetResultSets);
		resultSets.addAll(ctes);
		resultSets.addAll(mergeResultSets);
		resultSets.addAll(outputResultSets);
		resultSets.addAll(updateResultSets);
		resultSets.addAll(functionCalls);
		resultSets.addAll(cursors);

		for (int i = 0; i < resultSets.size(); i++) {
			ResultSet resultSetModel = (ResultSet) modelManager.getModel(resultSets.get(i));
			appendResultSet(dataflow, resultSetModel);
		}
	}

	private void appendResultSet(dataflow dataflow, ResultSet resultSetModel) {
		if (resultSetModel == null) {
			System.err.println("ResultSet Model should not be null.");
		}

		if (!appendResultSets.contains(resultSetModel)) {
			appendResultSets.add(resultSetModel);
		} else {
			return;
		}

		table resultSetElement = new table();
		resultSetElement.setId(String.valueOf(resultSetModel.getId()));
		if (!SQLUtil.isEmpty(resultSetModel.getDatabase())) {
			resultSetElement.setDatabase(resultSetModel.getDatabase());
		}
		if (!SQLUtil.isEmpty(resultSetModel.getSchema())) {
			resultSetElement.setSchema(resultSetModel.getSchema());
		}
		resultSetElement.setName(getResultSetName(resultSetModel));
		resultSetElement.setType(getResultSetType(resultSetModel));
		//if ((ignoreRecordSet || simpleOutput) && resultSetModel.isTarget()) {
			resultSetElement.setIsTarget(String.valueOf(resultSetModel.isTarget()));
		//}
		if (resultSetModel.getStartPosition() != null && resultSetModel.getEndPosition() != null) {
			resultSetElement.setCoordinate(resultSetModel.getStartPosition() + "," + resultSetModel.getEndPosition());
		}
		dataflow.getResultsets().add(resultSetElement);

		List<ResultColumn> columns = resultSetModel.getColumns();

		Map<String, Integer> columnCounts = new HashMap<String, Integer>();
		for (ResultColumn column : columns) {
			String columnName = SQLUtil.getIdentifierNormalName(column.getName());
			if (!columnCounts.containsKey(columnName)) {
				columnCounts.put(columnName, 0);
			}
			columnCounts.put(columnName, columnCounts.get(columnName) + 1);
			if (!column.getStarLinkColumns().isEmpty()) {
				for (int k = 0; k < column.getStarLinkColumns().size(); k++) {
					columnName = SQLUtil.getIdentifierNormalName(getColumnName(column.getStarLinkColumns().get(k)));
					if (!columnCounts.containsKey(columnName)) {
						columnCounts.put(columnName, 0);
					}
					columnCounts.put(columnName, columnCounts.get(columnName) + 1);
				}
			}
		}

		for (int j = 0; j < columns.size(); j++) {
			ResultColumn columnModel = columns.get(j);
			if (!columnModel.getStarLinkColumns().isEmpty()) {
				for (int k = 0; k < columnModel.getStarLinkColumns().size(); k++) {
					column columnElement = new column();
					columnElement.setId( String.valueOf(columnModel.getId()) + "_" + k);
					TObjectName column = columnModel.getStarLinkColumns().get(k);
					String columnName =  getColumnName(column);
					columnElement.setName(columnName);
					if(columnModel.isFunction()){
						columnElement.setIsFunction(String.valueOf(columnModel.isFunction()));
					}
					if (columnModel.getStartPosition() != null && columnModel.getEndPosition() != null) {
						columnElement.setCoordinate(
								columnModel.getStartPosition() + "," + columnModel.getEndPosition());
					}
					String identifier = SQLUtil.getIdentifierNormalName(columnName);
					if(columnCounts.containsKey(identifier) && columnCounts.get(identifier)>1){
						if(!SQLUtil.isEmpty(getQualifiedTable(column))){
							columnElement.setQualifiedTable(getQualifiedTable(column));
						}
					}
					resultSetElement.getColumns().add(columnElement);
				}
				if(columnModel.isShowStar()){
					column columnElement = new column();
					columnElement.setId( String.valueOf(columnModel.getId()));
					columnElement.setName( columnModel.getName());
					if(columnModel.isFunction()){
						columnElement.setIsFunction(String.valueOf(columnModel.isFunction()));
					}
					if (columnModel.getStartPosition() != null && columnModel.getEndPosition() != null) {
						columnElement.setCoordinate(
								columnModel.getStartPosition() + "," + columnModel.getEndPosition());
					}
					
					String identifier = SQLUtil.getIdentifierNormalName(columnModel.getName());
					if (columnCounts.containsKey(identifier) && columnCounts.get(identifier) > 1) {
						String qualifiedTable = getQualifiedTable(columnModel);
						if(!SQLUtil.isEmpty(qualifiedTable)) {
							columnElement.setQualifiedTable(qualifiedTable);
						}
					}
					resultSetElement.getColumns().add(columnElement);
				}
			} else {
				column columnElement = new column();
				columnElement.setId( String.valueOf(columnModel.getId()));
				columnElement.setName( columnModel.getName());
				if(columnModel.isFunction()){
					columnElement.setIsFunction(String.valueOf(columnModel.isFunction()));
				}
				if (columnModel.getStartPosition() != null && columnModel.getEndPosition() != null) {
					columnElement.setCoordinate(
							columnModel.getStartPosition() + "," + columnModel.getEndPosition());
				}
	

				String identifier = SQLUtil.getIdentifierNormalName(columnModel.getName());
				if (columnCounts.containsKey(identifier) && columnCounts.get(identifier) > 1) {
					String qualifiedTable = getQualifiedTable(columnModel);
					if(!SQLUtil.isEmpty(qualifiedTable)) {
						columnElement.setQualifiedTable(qualifiedTable);
					}
				}
			
				resultSetElement.getColumns().add(columnElement);
			}
		}

		ResultSetPseudoRows pseudoRows = resultSetModel.getPseudoRows();
		column pseudoRowsElement = new column();
		pseudoRowsElement.setId(String.valueOf(pseudoRows.getId()));
		pseudoRowsElement.setName(pseudoRows.getName());
		if (pseudoRows.getStartPosition() != null && pseudoRows.getEndPosition() != null) {
			pseudoRowsElement.setCoordinate(pseudoRows.getStartPosition() + "," + pseudoRows.getEndPosition());
		}
		pseudoRowsElement.setSource("system");
		resultSetElement.getColumns().add(pseudoRowsElement);
	}

	private String getQualifiedTable(ResultColumn columnModel) {
		if (columnModel.getColumnObject() instanceof TObjectName) {
			return getQualifiedTable((TObjectName) columnModel.getColumnObject());
		}
		if (columnModel.getColumnObject() instanceof TResultColumn) {
			TObjectName field = ((TResultColumn) columnModel.getColumnObject()).getFieldAttr();
			if (field != null) {
				return getQualifiedTable(field);
			}
		}
		return null;
	}

	private String getQualifiedTable(TObjectName column) {
		if (column == null)
			return null;
		String[] splits = column.toString().split("\\.");
		if (splits.length > 1) {
			return splits[splits.length - 2];
		}
		return null;
	}

	private String getResultSetType(ResultSet resultSetModel) {
		if (resultSetModel instanceof QueryTable) {
			QueryTable table = (QueryTable) resultSetModel;
			if (table.getTableObject().getCTE() != null) {
				return "with_cte";
			}
		}

		if (resultSetModel instanceof SelectSetResultSet) {
			ESetOperatorType type = ((SelectSetResultSet) resultSetModel).getSetOperatorType();
			return "select_" + type.name();
		}

		if (resultSetModel instanceof SelectResultSet) {
			if (((SelectResultSet) resultSetModel).getSelectStmt().getParentStmt() instanceof TInsertSqlStatement) {
				return "insert-select";
			}
			if (((SelectResultSet) resultSetModel).getSelectStmt().getParentStmt() instanceof TUpdateSqlStatement) {
				return "update-select";
			}
		}

		if (resultSetModel.getGspObject() instanceof TMergeUpdateClause) {
			return "merge-update";
		}
		
		if (resultSetModel.getGspObject() instanceof TOutputClause) {
			return "output";
		}

		if (resultSetModel.getGspObject() instanceof TMergeInsertClause) {
			return "merge-insert";
		}

		if (resultSetModel.getGspObject() instanceof TUpdateSqlStatement) {
			return "update-set";
		}

		if (resultSetModel.getGspObject() instanceof TFunctionCall) {
			return "function";
		}

		if (resultSetModel.getGspObject() instanceof TCaseExpression) {
			return "function";
		}

		if (resultSetModel.getGspObject() instanceof TCursorDeclStmt) {
			return "cursor";
		}

		return "select_list";
	}

	private String getTableName(Table tableModel) {
		String tableName;
		if (tableModel.getFullName() != null && tableModel.getFullName().trim().length() > 0) {
			return tableModel.getFullName();
		}
		if (tableModel.getAlias() != null && tableModel.getAlias().trim().length() > 0) {
			tableName = getResultSetWithId("RESULT_OF_" + tableModel.getAlias());

		} else {
			tableName = getResultSetDisplayId("RS");
		}
		return tableName;
	}

	private String getResultSetName(ResultSet resultSetModel) {

		if (modelManager.DISPLAY_NAME.containsKey(resultSetModel.getId())) {
			return modelManager.DISPLAY_NAME.get(resultSetModel.getId());
		}

		if (resultSetModel instanceof QueryTable) {
			QueryTable table = (QueryTable) resultSetModel;
			if (table.getAlias() != null && table.getAlias().trim().length() > 0) {
				String name = getResultSetWithId("RESULT_OF_" + table.getAlias().trim());
				if (table.getTableObject().getCTE() != null) {
					name = getResultSetWithId("RESULT_OF_" + table.getTableObject().getCTE().getTableName().toString()
							+ "_" + table.getAlias().trim());
				}
				modelManager.DISPLAY_NAME.put(resultSetModel.getId(), name);
				return name;
			} else if (table.getTableObject().getCTE() != null) {
				String name = "CTE-" + table.getTableObject().getCTE().getTableName().toString();
				modelManager.DISPLAY_NAME.put(table.getId(), name);
				return name;
			}
		}

		if (resultSetModel instanceof SelectResultSet) {
			if (((SelectResultSet) resultSetModel).getSelectStmt().getParentStmt() instanceof TInsertSqlStatement) {
				String name = getResultSetDisplayId("INSERT-SELECT");
				modelManager.DISPLAY_NAME.put(resultSetModel.getId(), name);
				return name;
			}

			if (((SelectResultSet) resultSetModel).getSelectStmt().getParentStmt() instanceof TUpdateSqlStatement) {
				String name = getResultSetDisplayId("UPDATE-SELECT");
				modelManager.DISPLAY_NAME.put(resultSetModel.getId(), name);
				return name;
			}
		}

		if (resultSetModel instanceof SelectSetResultSet) {
			ESetOperatorType type = ((SelectSetResultSet) resultSetModel).getSetOperatorType();
			String name = getResultSetDisplayId("RESULT_OF_" + type.name().toUpperCase());
			modelManager.DISPLAY_NAME.put(resultSetModel.getId(), name);
			return name;
		}

		if (resultSetModel.getGspObject() instanceof TMergeUpdateClause) {
			String name = getResultSetDisplayId("MERGE-UPDATE");
			modelManager.DISPLAY_NAME.put(resultSetModel.getId(), name);
			return name;
		}
		
		if (resultSetModel.getGspObject() instanceof TOutputClause) {
			String name = getResultSetDisplayId("OUTPUT");
			modelManager.DISPLAY_NAME.put(resultSetModel.getId(), name);
			return name;
		}

		if (resultSetModel.getGspObject() instanceof TMergeInsertClause) {
			String name = getResultSetDisplayId("MERGE-INSERT");
			modelManager.DISPLAY_NAME.put(resultSetModel.getId(), name);
			return name;
		}

		if (resultSetModel.getGspObject() instanceof TUpdateSqlStatement) {
			String name = getResultSetDisplayId("UPDATE-SET");
			modelManager.DISPLAY_NAME.put(resultSetModel.getId(), name);
			return name;
		}

		if (resultSetModel.getGspObject() instanceof TFunctionCall) {
			String name = getResultSetDisplayId("FUNCTION");
			modelManager.DISPLAY_NAME.put(resultSetModel.getId(), name);
			return name;
		}

		if (resultSetModel.getGspObject() instanceof TCaseExpression) {
			String name = getResultSetDisplayId("FUNCTION");
			modelManager.DISPLAY_NAME.put(resultSetModel.getId(), name);
			return name;
		}

		String name = getResultSetDisplayId("RS");
		modelManager.DISPLAY_NAME.put(resultSetModel.getId(), name);
		return name;
	}

	private String getResultSetWithId(String type) {
		type = SQLUtil.getIdentifierNormalName(type);
		if (!modelManager.DISPLAY_ID.containsKey(type)) {
			modelManager.DISPLAY_ID.put(type, 0);
			return type;
		} else {
			int id = modelManager.DISPLAY_ID.get(type);
			modelManager.DISPLAY_ID.put(type, id + 1);
			return type + "(" + (id + 1) + ")";
		}
	}

	private String getResultSetDisplayId(String type) {
		if (!modelManager.DISPLAY_ID.containsKey(type)) {
			modelManager.DISPLAY_ID.put(type, 1);
			return type + "-" + 1;
		} else {
			int id = modelManager.DISPLAY_ID.get(type);
			modelManager.DISPLAY_ID.put(type, id + 1);
			return type + "-" + (id + 1);
		}
	}

	private void appendViews(dataflow dataflow) {
		List<TCustomSqlStatement> views = modelManager.getViews();
		for (int i = 0; i < views.size(); i++) {
			View viewModel = (View) modelManager.getViewModel(views.get(i));
			table viewElement = new table();
			viewElement.setId(String.valueOf(viewModel.getId()));
			if (!SQLUtil.isEmpty(viewModel.getDatabase())) {
				viewElement.setDatabase(viewModel.getDatabase());
			}
			if (!SQLUtil.isEmpty(viewModel.getSchema())) {
				viewElement.setSchema(viewModel.getSchema());
			}
			viewElement.setName(viewModel.getName());
			viewElement.setType("view");
			if (viewModel.getStartPosition() != null && viewModel.getEndPosition() != null) {
				viewElement.setCoordinate(viewModel.getStartPosition() + "," + viewModel.getEndPosition());
			}
			dataflow.getViews().add(viewElement);

			List<TableColumn> columns = viewModel.getColumns();
			for (int j = 0; j < columns.size(); j++) {
				TableColumn columnModel = (TableColumn) columns.get(j);
				if (!columnModel.getStarLinkColumns().isEmpty()) {
					for (int k = 0; k < columnModel.getStarLinkColumns().size(); k++) {
						column columnElement = new column();
						columnElement.setId(String.valueOf(columnModel.getId()) + "_" + k);
						columnElement.setName(getColumnName(columnModel.getStarLinkColumns().get(k)));
						if (columnModel.getStartPosition() != null && columnModel.getEndPosition() != null) {
							columnElement
									.setCoordinate(columnModel.getStartPosition() + "," + columnModel.getEndPosition());
						}
						viewElement.getColumns().add(columnElement);
					}

					if (columnModel.isShowStar()) {
						column columnElement = new column();
						columnElement.setId(String.valueOf(columnModel.getId()));
						columnElement.setName(columnModel.getName());
						if (columnModel.getStartPosition() != null && columnModel.getEndPosition() != null) {
							columnElement
									.setCoordinate(columnModel.getStartPosition() + "," + columnModel.getEndPosition());
						}
						viewElement.getColumns().add(columnElement);
					}

				} else {
					column columnElement = new column();
					columnElement.setId(String.valueOf(columnModel.getId()));
					columnElement.setName(columnModel.getName());
					if (columnModel.getStartPosition() != null && columnModel.getEndPosition() != null) {
						columnElement
								.setCoordinate(columnModel.getStartPosition() + "," + columnModel.getEndPosition());
					}
					viewElement.getColumns().add(columnElement);
				}
			}

			TablePseudoRows pseudoRows = viewModel.getPseudoRows();
			column pseudoRowsElement = new column();
			pseudoRowsElement.setId(String.valueOf(pseudoRows.getId()));
			pseudoRowsElement.setName(pseudoRows.getName());
			if (pseudoRows.getStartPosition() != null && pseudoRows.getEndPosition() != null) {
				pseudoRowsElement.setCoordinate(pseudoRows.getStartPosition() + "," + pseudoRows.getEndPosition());
			}
			pseudoRowsElement.setSource("system");
			viewElement.getColumns().add(pseudoRowsElement);
		}
	}

	private void appendProcedures(dataflow dataflow) {
		List<TStoredProcedureSqlStatement> procedures = this.modelManager.getProcedures();

		for (int i = 0; i < procedures.size(); ++i) {
			Procedure model = (Procedure) this.modelManager.getModel(procedures.get(i));
			procedure procedure = new procedure();
			procedure.setId(String.valueOf(model.getId()));
			if (!SQLUtil.isEmpty(model.getDatabase())) {
				procedure.setDatabase(model.getDatabase());
			}
			if (!SQLUtil.isEmpty(model.getSchema())) {
				procedure.setSchema(model.getSchema());
			}
			procedure.setName(model.getName());
			procedure.setType(model.getType().name().replace("sst", ""));
			if (model.getStartPosition() != null && model.getEndPosition() != null) {
				procedure.setCoordinate(model.getStartPosition() + "," + model.getEndPosition());
			}

			dataflow.getProcedures().add(procedure);

			List<Argument> arguments = model.getArguments();

			for (int j = 0; j < arguments.size(); ++j) {
				Argument argumentModel = (Argument) arguments.get(j);
				argument argumentElement = new argument();
				argumentElement.setId(String.valueOf(argumentModel.getId()));
				argumentElement.setName(argumentModel.getName());
				if (argumentModel.getStartPosition() != null && argumentModel.getEndPosition() != null) {
					argumentElement
							.setCoordinate(argumentModel.getStartPosition() + "," + argumentModel.getEndPosition());
				}

				argumentElement.setDatatype(argumentModel.getDataType().getDataTypeName());
				argumentElement.setInout(argumentModel.getMode().name());
				procedure.getArguments().add(argumentElement);
			}
		}

	}

	private void appendTables(dataflow dataflow) {
		List<TTable> tables = modelManager.getBaseTables();
		for (int i = 0; i < tables.size(); i++) {
			Object model = modelManager.getModel(tables.get(i));
			if (model instanceof Table && !(model instanceof View)) {
				Table tableModel = (Table) model;
				if (!tableIds.contains(tableModel.getId())) {
					appendTableModel(dataflow, tableModel);
					tableIds.add(tableModel.getId());
				}
			} else if (model instanceof QueryTable) {
				QueryTable queryTable = (QueryTable) model;
				if (!tableIds.contains(queryTable.getId())) {
					appendResultSet(dataflow, queryTable);
					tableIds.add(queryTable.getId());
				}
			}
		}

		List<Table> tableNames = modelManager.getTablesByName();
		for (int i = 0; i < tableNames.size(); i++) {
			Table tableModel = tableNames.get(i);
			if (tableModel instanceof View) {
				continue;
			}
			if (!tableIds.contains(tableModel.getId())) {
				appendTableModel(dataflow, tableModel);
				tableIds.add(tableModel.getId());
			}
		}
	}

	private void appendTableModel(dataflow dataflow, Table tableModel) {
		table tableElement = new table();
		tableElement.setId(String.valueOf(tableModel.getId()));
		if (!SQLUtil.isEmpty(tableModel.getDatabase())) {
			tableElement.setDatabase(tableModel.getDatabase());
		}
		if (!SQLUtil.isEmpty(tableModel.getSchema())) {
			tableElement.setSchema(tableModel.getSchema());
		}
		tableElement.setName(tableModel.getName());
		tableElement.setType("table");

		if (tableModel.getTableType() != null) {
			tableElement.setTableType(tableModel.getTableType());
		}
		if (tableModel.getParent() != null) {
			tableElement.setParent(tableModel.getParent());
		}
		if (tableModel.getAlias() != null && tableModel.getAlias().trim().length() > 0) {
			tableElement.setAlias(tableModel.getAlias());
		}
		if (tableModel.getStartPosition() != null && tableModel.getEndPosition() != null) {
			tableElement.setCoordinate(tableModel.getStartPosition() + "," + tableModel.getEndPosition());
		}
		dataflow.getTables().add(tableElement);

		List<TableColumn> columns = tableModel.getColumns();
		for (int j = 0; j < columns.size(); j++) {
			TableColumn columnModel = columns.get(j);
			if (!columnModel.getStarLinkColumns().isEmpty()) {
				for (int k = 0; k < columnModel.getStarLinkColumns().size(); k++) {
					column columnElement = new column();
					columnElement.setId(String.valueOf(columnModel.getId()) + "_" + k);
					columnElement.setName(getColumnName(columnModel.getStarLinkColumns().get(k)));
					if (columnModel.getStartPosition() != null && columnModel.getEndPosition() != null) {
						columnElement
								.setCoordinate(columnModel.getStartPosition() + "," + columnModel.getEndPosition());
					}
					tableElement.getColumns().add(columnElement);
				}
				if (columnModel.isShowStar()) {
					column columnElement = new column();
					columnElement.setId(String.valueOf(columnModel.getId()));
					columnElement.setName(columnModel.getName());
					if (columnModel.getStartPosition() != null && columnModel.getEndPosition() != null) {
						columnElement
								.setCoordinate(columnModel.getStartPosition() + "," + columnModel.getEndPosition());
					}
					tableElement.getColumns().add(columnElement);
				}
			} else {
				column columnElement = new column();
				columnElement.setId(String.valueOf(columnModel.getId()));
				columnElement.setName(columnModel.getName());
				if (columnModel.getStartPosition() != null && columnModel.getEndPosition() != null) {
					columnElement.setCoordinate(columnModel.getStartPosition() + "," + columnModel.getEndPosition());
				}
				tableElement.getColumns().add(columnElement);
			}
		}

		TablePseudoRows pseudoRows = tableModel.getPseudoRows();
		column pseudoRowsElement = new column();
		pseudoRowsElement.setId(String.valueOf(pseudoRows.getId()));
		pseudoRowsElement.setName(pseudoRows.getName());
		if (pseudoRows.getStartPosition() != null && pseudoRows.getEndPosition() != null) {
			pseudoRowsElement.setCoordinate(pseudoRows.getStartPosition() + "," + pseudoRows.getEndPosition());
		}
		pseudoRowsElement.setSource("system");
		tableElement.getColumns().add(pseudoRowsElement);
	}

	private void analyzeSelectStmt(TSelectSqlStatement stmt) {
		if (stmt.getSetOperatorType() != ESetOperatorType.none) {

			if (!accessedStatements.contains(stmt.getLeftStmt())) {
				accessedStatements.add(stmt.getLeftStmt());
				analyzeSelectStmt(stmt.getLeftStmt());
			}

			if (!accessedStatements.contains(stmt.getRightStmt())) {
				accessedStatements.add(stmt.getRightStmt());
				analyzeSelectStmt(stmt.getRightStmt());
			}

			stmtStack.push(stmt);
			SelectSetResultSet resultSet = modelFactory.createSelectSetResultSet(stmt);

			ResultSet leftResultSetModel = (ResultSet) modelManager.getModel(stmt.getLeftStmt());
			if (leftResultSetModel != null && leftResultSetModel != resultSet
					&& !leftResultSetModel.getPseudoRows().getHoldRelations().isEmpty()) {
				ImpactRelation impactRelation = modelFactory.createImpactRelation();
				impactRelation.setEffectType(EffectType.select);
				impactRelation.addSource(
						new PseudoRowsRelationElement<ResultSetPseudoRows>(leftResultSetModel.getPseudoRows()));
				impactRelation.setTarget(new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSet.getPseudoRows()));
			}

			ResultSet rightResultSetModel = (ResultSet) modelManager.getModel(stmt.getRightStmt());
			if (rightResultSetModel != null && rightResultSetModel != resultSet
					&& !rightResultSetModel.getPseudoRows().getHoldRelations().isEmpty()) {
				ImpactRelation impactRelation = modelFactory.createImpactRelation();
				impactRelation.setEffectType(EffectType.select);
				impactRelation.addSource(
						new PseudoRowsRelationElement<ResultSetPseudoRows>(rightResultSetModel.getPseudoRows()));
				impactRelation.setTarget(new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSet.getPseudoRows()));
			}

			if (resultSet.getColumns() == null || resultSet.getColumns().isEmpty()) {
				if (getResultColumnList(stmt.getLeftStmt()) != null) {
					createSelectSetResultColumns(resultSet, stmt.getLeftStmt());
				} else if (getResultColumnList(stmt.getRightStmt()) != null) {
					createSelectSetResultColumns(resultSet, stmt.getRightStmt());
				}
			}

			List<ResultColumn> columns = resultSet.getColumns();
			for (int i = 0; i < columns.size(); i++) {
				DataFlowRelation relation = modelFactory.createDataFlowRelation();
				relation.setEffectType(EffectType.select);
				relation.setTarget(new ResultColumnRelationElement(columns.get(i)));

				if (!stmt.getLeftStmt().isCombinedQuery()) {
					ResultSet sourceResultSet = (ResultSet) modelManager
							.getModel(stmt.getLeftStmt().getResultColumnList());
					if (sourceResultSet.getColumns().size() > i) {
						relation.addSource(new ResultColumnRelationElement(sourceResultSet.getColumns().get(i)));
					}
				} else {
					ResultSet sourceResultSet = (ResultSet) modelManager.getModel(stmt.getLeftStmt());
					if (sourceResultSet != null && sourceResultSet.getColumns().size() > i) {
						relation.addSource(new ResultColumnRelationElement(sourceResultSet.getColumns().get(i)));
					}
				}

				if (!stmt.getRightStmt().isCombinedQuery()) {
					ResultSet sourceResultSet = (ResultSet) modelManager
							.getModel(stmt.getRightStmt().getResultColumnList());
					if (sourceResultSet != null && sourceResultSet.getColumns().size() > i) {
						relation.addSource(new ResultColumnRelationElement(sourceResultSet.getColumns().get(i)));
					}
				} else {
					ResultSet sourceResultSet = (ResultSet) modelManager.getModel(stmt.getRightStmt());
					if (sourceResultSet != null && sourceResultSet.getColumns().size() > i) {
						relation.addSource(new ResultColumnRelationElement(sourceResultSet.getColumns().get(i)));
					}
				}
			}

			stmtStack.pop();
		} else {
			
			if(stmt.getResultColumnList() == null){
				return;
			}
			
			stmtStack.push(stmt);

			TTableList fromTables = stmt.tables;
			for (int i = 0; i < fromTables.size(); i++) {
				TTable table = fromTables.getTable(i);

				if (table.getSubquery() != null) {
					QueryTable queryTable = modelFactory.createQueryTable(table);
					TSelectSqlStatement subquery = table.getSubquery();
					analyzeSelectStmt(subquery);

					ResultSet resultSetModel = (ResultSet) modelManager.getModel(subquery);
					
					
					if (resultSetModel != null && resultSetModel != queryTable
							&& !resultSetModel.getPseudoRows().getHoldRelations().isEmpty()) {
						ImpactRelation impactRelation = modelFactory.createImpactRelation();
						impactRelation.setEffectType(EffectType.select);
						impactRelation.addSource(
								new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSetModel.getPseudoRows()));
						impactRelation.setTarget(
								new PseudoRowsRelationElement<ResultSetPseudoRows>(queryTable.getPseudoRows()));
					}

					if(resultSetModel != null && resultSetModel != queryTable 
							&& queryTable.getTableObject().getAliasClause()!=null
							&& queryTable.getTableObject().getAliasClause().getColumns()!=null){
						for (int j = 0; j < queryTable.getColumns().size() && j < resultSetModel.getColumns().size(); j++) {
							ResultColumn sourceColumn = resultSetModel.getColumns().get(j);
							ResultColumn targetColumn = queryTable.getColumns().get(j);
							
							DataFlowRelation queryRalation = modelFactory.createDataFlowRelation();
							queryRalation.setEffectType(EffectType.select);
							queryRalation.setTarget(new ResultColumnRelationElement(targetColumn));
							queryRalation.addSource(new ResultColumnRelationElement(sourceColumn));
						}
					}
					else if (subquery.getSetOperatorType() != ESetOperatorType.none) {
						SelectSetResultSet selectSetResultSetModel = (SelectSetResultSet) modelManager
								.getModel(subquery);
						for (int j = 0; j < selectSetResultSetModel.getColumns().size(); j++) {
							ResultColumn sourceColumn = selectSetResultSetModel.getColumns().get(j);
							ResultColumn targetColumn = modelFactory.createSelectSetResultColumn(queryTable,
									sourceColumn, j);
							for (TObjectName starLinkColumn : sourceColumn.getStarLinkColumns()) {
								targetColumn.bindStarLinkColumn(starLinkColumn);
							}
							DataFlowRelation selectSetRalation = modelFactory.createDataFlowRelation();
							selectSetRalation.setEffectType(EffectType.select);
							selectSetRalation.setTarget(new ResultColumnRelationElement(targetColumn));
							selectSetRalation.addSource(new ResultColumnRelationElement(sourceColumn));
						}
					}
				} else if (table.getCTE() != null) {
					QueryTable queryTable = modelFactory.createQueryTable(table);

					TObjectNameList cteColumns = table.getCTE().getColumnList();
					if (cteColumns != null) {
						for (int j = 0; j < cteColumns.size(); j++) {
							modelFactory.createResultColumn(queryTable, cteColumns.getObjectName(j));
						}
					}
					TSelectSqlStatement subquery = table.getCTE().getSubquery();
					if (subquery != null && !stmtStack.contains(subquery) && subquery.getResultColumnList()!=null) {
						analyzeSelectStmt(subquery);

						ResultSet resultSetModel = (ResultSet) modelManager.getModel(subquery);
						if (resultSetModel != null && resultSetModel != queryTable
								&& !resultSetModel.getPseudoRows().getHoldRelations().isEmpty()) {
							ImpactRelation impactRelation = modelFactory.createImpactRelation();
							impactRelation.setEffectType(EffectType.select);
							impactRelation.addSource(
									new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSetModel.getPseudoRows()));
							impactRelation.setTarget(
									new PseudoRowsRelationElement<ResultSetPseudoRows>(queryTable.getPseudoRows()));
						}

						if (subquery.getSetOperatorType() != ESetOperatorType.none) {
							SelectSetResultSet selectSetResultSetModel = (SelectSetResultSet) modelManager
									.getModel(subquery);
							for (int j = 0; j < selectSetResultSetModel.getColumns().size(); j++) {
								ResultColumn sourceColumn = selectSetResultSetModel.getColumns().get(j);
								ResultColumn targetColumn = null;
								if (cteColumns != null) {
									targetColumn = queryTable.getColumns().get(j);
								} else {
									targetColumn = modelFactory.createSelectSetResultColumn(queryTable, sourceColumn,
											j);
								}
								for (TObjectName starLinkColumn : sourceColumn.getStarLinkColumns()) {
									targetColumn.bindStarLinkColumn(starLinkColumn);
								}
								DataFlowRelation selectSetRalation = modelFactory.createDataFlowRelation();
								selectSetRalation.setEffectType(EffectType.select);
								selectSetRalation.setTarget(new ResultColumnRelationElement(targetColumn));
								selectSetRalation.addSource(new ResultColumnRelationElement(sourceColumn));
							}
						} else {
							for (int j = 0; j < resultSetModel.getColumns().size(); j++) {
								ResultColumn sourceColumn = resultSetModel.getColumns().get(j);
								ResultColumn targetColumn = null;
								if (cteColumns != null) {
									targetColumn = queryTable.getColumns().get(j);
								} else {
									targetColumn = modelFactory.createSelectSetResultColumn(queryTable, sourceColumn,
											j);
								}
								for (TObjectName starLinkColumn : sourceColumn.getStarLinkColumns()) {
									targetColumn.bindStarLinkColumn(starLinkColumn);
								}
								DataFlowRelation selectSetRalation = modelFactory.createDataFlowRelation();
								selectSetRalation.setEffectType(EffectType.select);
								selectSetRalation.setTarget(new ResultColumnRelationElement(targetColumn));
								selectSetRalation.addSource(new ResultColumnRelationElement(sourceColumn));
							}
						}
					} else if (table.getCTE().getUpdateStmt() != null) {
						analyzeCustomSqlStmt(table.getCTE().getUpdateStmt());
					} else if (table.getCTE().getInsertStmt() != null) {
						analyzeCustomSqlStmt(table.getCTE().getInsertStmt());
					} else if (table.getCTE().getDeleteStmt() != null) {
						analyzeCustomSqlStmt(table.getCTE().getDeleteStmt());
					}
				} else if (table.getTableType().name().startsWith("open")) {
					continue;
				} else if (table.getLinkedColumns() != null && table.getLinkedColumns().size() > 0) {
					Table tableModel = modelFactory.createTable(table);
					for (int j = 0; j < table.getLinkedColumns().size(); j++) {
						TObjectName object = table.getLinkedColumns().getObjectName(j);

						if (object.getDbObjectType() == EDbObjectType.variable) {
							continue;
						}

						if (isFunctionName(object) && isFromFunction(object)) {
							continue;
						}

						if (object.getSourceTable() == null || object.getSourceTable() == table
								|| "*".equals(object.toString())) {
							if (!"*".equals(object.toString()) || table.getLinkedColumns().size() == 1) {
								modelFactory.createTableColumn(tableModel, object, false);
							}
						}

					}
				}
			}

			if (!stmt.isCombinedQuery()) {
				Object queryModel = modelManager.getModel(stmt.getResultColumnList());

				if (queryModel == null) {
					TSelectSqlStatement parentStmt = getParentSetSelectStmt(stmt);
					if (stmt.getParentStmt() == null || parentStmt == null) {
						SelectResultSet resultSetModel = modelFactory.createResultSet(stmt,
								stmt.getParentStmt() == null);

						createPseudoImpactRelation(stmt, resultSetModel, EffectType.select);

						for (int i = 0; i < stmt.getResultColumnList().size(); i++) {
							TResultColumn column = stmt.getResultColumnList().getResultColumn(i);

							if (column.getExpr().getComparisonType() == EComparisonType.equals
									&& column.getExpr().getLeftOperand().getObjectOperand() != null) {
								TObjectName columnObject = column.getExpr().getLeftOperand().getObjectOperand();

								ResultColumn resultColumn = modelFactory.createResultColumn(resultSetModel,
										columnObject);

								columnsInExpr visitor = new columnsInExpr();
								column.getExpr().getRightOperand().inOrderTraverse(visitor);

								List<TObjectName> objectNames = visitor.getObjectNames();
								List<TParseTreeNode> functions = visitor.getFunctions();

								if (functions != null && !functions.isEmpty()) {
									analyzeFunctionDataFlowRelation(resultColumn, functions, EffectType.select);

								}
								
								List<TSelectSqlStatement> subquerys = visitor.getSubquerys();
								if (subquerys!=null && !subquerys.isEmpty()) {
									analyzeSubqueryDataFlowRelation(resultColumn, subquerys, EffectType.select);
								}

								analyzeDataFlowRelation(resultColumn, objectNames, EffectType.select, functions);

								List<TConstant> constants = visitor.getConstants();
								analyzeConstantDataFlowRelation(resultColumn, constants, EffectType.select, functions);
							} else {
								ResultColumn resultColumn = modelFactory.createResultColumn(resultSetModel, column);

								if ("*".equals(column.getColumnNameOnly())) {
									TObjectName columnObject = column.getFieldAttr();
									TTable sourceTable = columnObject.getSourceTable();
									if (columnObject.getTableToken() != null && sourceTable != null) {
										TObjectName[] columns = modelManager.getTableColumns(sourceTable);
										for (int j = 0; j < columns.length; j++) {
											TObjectName columnName = columns[j];
											if (columnName == null || "*".equals(getColumnName(columnName))) {
												continue;
											}
											resultColumn.bindStarLinkColumn(columnName);
										}
									} else {
										TTableList tables = stmt.getTables();
										for (int k = 0; k < tables.size(); k++) {
											TTable table = tables.getTable(k);
											TObjectName[] columns = modelManager.getTableColumns(table);
											for (int j = 0; j < columns.length; j++) {
												TObjectName columnName = columns[j];
												if (columnName == null) {
													continue;
												}
												if ("*".equals(getColumnName(columnName))) {
													if (modelManager.getModel(table) instanceof Table) {
														Table tableModel = (Table) modelManager.getModel(table);
														if (tableModel != null && !tableModel.getColumns().isEmpty()) {
															for (int z = 0; z < tableModel.getColumns().size(); z++) {
																resultColumn.bindStarLinkColumn(tableModel.getColumns()
																		.get(z).getColumnObject());
																if (table.getSubquery() == null
																		&& table.getCTE() == null
																		&& !tableModel.isCreateTable()) {
																	resultColumn.setShowStar(true);
																}
															}
														}
													} else if (modelManager.getModel(table) instanceof QueryTable) {
														QueryTable tableModel = (QueryTable) modelManager
																.getModel(table);
														if (tableModel != null && !tableModel.getColumns().isEmpty()) {
															for (int z = 0; z < tableModel.getColumns().size(); z++) {
																if (!tableModel.getColumns().get(z).getStarLinkColumns()
																		.isEmpty()) {
																	for (TObjectName starLinkColumn : tableModel
																			.getColumns().get(z).getStarLinkColumns()) {
																		resultColumn.bindStarLinkColumn(starLinkColumn);
																	}
																} else if (tableModel.getColumns().get(z)
																		.getColumnObject() instanceof TObjectName) {
																	resultColumn.bindStarLinkColumn(
																			(TObjectName) tableModel.getColumns().get(z)
																					.getColumnObject());
																} else if (tableModel.getColumns().get(z)
																		.getColumnObject() instanceof TResultColumn) {
																	TResultColumn queryTableColumn = (TResultColumn) tableModel
																			.getColumns().get(z).getColumnObject();
																	TObjectName tableColumnObject = queryTableColumn
																			.getFieldAttr();
																	if (tableColumnObject != null) {
																		resultColumn
																				.bindStarLinkColumn(tableColumnObject);
																	} else if (queryTableColumn
																			.getAliasClause() != null) {
																		resultColumn.bindStarLinkColumn(queryTableColumn
																				.getAliasClause().getAliasName());
																	}
																}
															}
														}
													}
													continue;
												}
												resultColumn.bindStarLinkColumn(columnName);
											}
										}
									}
								}
								analyzeResultColumn(column, EffectType.select);

							}
						}
					}

					TSelectSqlStatement parent = getParentSetSelectStmt(stmt);
					if (parent != null && parent.getSetOperatorType() != ESetOperatorType.none) {
						SelectResultSet resultSetModel = modelFactory.createResultSet(stmt, false);

						createPseudoImpactRelation(stmt, resultSetModel, EffectType.select);

						for (int i = 0; i < stmt.getResultColumnList().size(); i++) {
							TResultColumn column = stmt.getResultColumnList().getResultColumn(i);
							ResultColumn resultColumn = modelFactory.createResultColumn(resultSetModel, column);
							if ("*".equals(column.getColumnNameOnly())) {
								TObjectName columnObject = column.getFieldAttr();
								TTable sourceTable = columnObject.getSourceTable();
								if (columnObject.getTableToken() != null && sourceTable != null) {
									TObjectName[] columns = modelManager.getTableColumns(sourceTable);
									for (int j = 0; j < columns.length; j++) {
										TObjectName columnName = columns[j];
										if (columnName == null || "*".equals(getColumnName(columnName))) {
											continue;
										}
										resultColumn.bindStarLinkColumn(columnName);
									}

									if (modelManager.getModel(sourceTable) instanceof Table) {
										Table tableModel = (Table) modelManager.getModel(sourceTable);
										if (tableModel != null && !tableModel.getColumns().isEmpty()) {
											for (int z = 0; z < tableModel.getColumns().size(); z++) {
												if ("*".equals(getColumnName(
														tableModel.getColumns().get(z).getColumnObject()))) {
													continue;
												}
												resultColumn.bindStarLinkColumn(
														tableModel.getColumns().get(z).getColumnObject());
											}
										}
									} else if (modelManager.getModel(sourceTable) instanceof QueryTable) {
										QueryTable tableModel = (QueryTable) modelManager.getModel(sourceTable);
										if (tableModel != null && !tableModel.getColumns().isEmpty()) {
											for (int z = 0; z < tableModel.getColumns().size(); z++) {
												if (!tableModel.getColumns().get(z).getStarLinkColumns().isEmpty()) {
													for (TObjectName starLinkColumn : tableModel.getColumns().get(z)
															.getStarLinkColumns()) {
														if ("*".equals(getColumnName(starLinkColumn))) {
															continue;
														}
														resultColumn.bindStarLinkColumn(starLinkColumn);
													}
												} else if (tableModel.getColumns().get(z)
														.getColumnObject() instanceof TObjectName) {
													TObjectName starLinkColumn = (TObjectName) tableModel.getColumns()
															.get(z).getColumnObject();
													if ("*".equals(getColumnName(starLinkColumn))) {
														continue;
													}
													resultColumn.bindStarLinkColumn(starLinkColumn);
												}
											}
										}
									}

								} else {
									TTableList tables = stmt.getTables();
									for (int k = 0; k < tables.size(); k++) {
										TTable table = tables.getTable(k);
										TObjectName[] columns = modelManager.getTableColumns(table);
										for (int j = 0; j < columns.length; j++) {
											TObjectName columnName = columns[j];
											if (columnName == null) {
												continue;
											}
											if ("*".equals(getColumnName(columnName))) {
												if (modelManager.getModel(table) instanceof Table) {
													Table tableModel = (Table) modelManager.getModel(table);
													if (tableModel != null && !tableModel.getColumns().isEmpty()) {
														for (int z = 0; z < tableModel.getColumns().size(); z++) {
															resultColumn.bindStarLinkColumn(
																	tableModel.getColumns().get(z).getColumnObject());
														}
													}
												} else if (modelManager.getModel(table) instanceof QueryTable) {
													QueryTable tableModel = (QueryTable) modelManager.getModel(table);
													if (tableModel != null && !tableModel.getColumns().isEmpty()) {
														for (int z = 0; z < tableModel.getColumns().size(); z++) {
															if (!tableModel.getColumns().get(z).getStarLinkColumns()
																	.isEmpty()) {
																for (TObjectName starLinkColumn : tableModel
																		.getColumns().get(z).getStarLinkColumns()) {
																	resultColumn.bindStarLinkColumn(starLinkColumn);
																}
															} else if (tableModel.getColumns().get(z)
																	.getColumnObject() instanceof TObjectName) {
																resultColumn.bindStarLinkColumn((TObjectName) tableModel
																		.getColumns().get(z).getColumnObject());
															} else if (tableModel.getColumns().get(z)
																	.getColumnObject() instanceof TResultColumn) {
																TResultColumn queryTableColumn = (TResultColumn) tableModel
																		.getColumns().get(z).getColumnObject();
																TObjectName tableColumnObject = queryTableColumn
																		.getFieldAttr();
																if (tableColumnObject != null) {
																	resultColumn.bindStarLinkColumn(tableColumnObject);
																} else if (queryTableColumn.getAliasClause() != null) {
																	resultColumn.bindStarLinkColumn(queryTableColumn
																			.getAliasClause().getAliasName());
																}
															}
														}
													}
												}

												continue;
											}
											resultColumn.bindStarLinkColumn(columnName);
										}
									}
								}
							}
							analyzeResultColumn(column, EffectType.select);

						}
					}
				} else {
					for (int i = 0; i < stmt.getResultColumnList().size(); i++) {
						TResultColumn column = stmt.getResultColumnList().getResultColumn(i);

						ResultColumn resultColumn;

						if (queryModel instanceof QueryTable) {
							resultColumn = modelFactory.createResultColumn((QueryTable) queryModel, column);
						} else if (queryModel instanceof ResultSet) {
							resultColumn = modelFactory.createResultColumn((ResultSet) queryModel, column);
						} else {
							continue;
						}

						if ("*".equals(column.getColumnNameOnly())) {
							TObjectName columnObject = column.getFieldAttr();
							TTable sourceTable = columnObject.getSourceTable();
							if (columnObject.getTableToken() != null && sourceTable != null) {
								if (modelManager.getModel(sourceTable) instanceof Table) {
									Table tableModel = (Table) modelManager.getModel(sourceTable);
									if (tableModel != null) {
										modelFactory.createTableColumn(tableModel, columnObject, false);
									}
									TObjectName[] columns = modelManager.getTableColumns(sourceTable);
									for (int j = 0; j < columns.length; j++) {
										TObjectName columnName = columns[j];
										if (columnName == null || "*".equals(getColumnName(columnName))) {
											continue;
										}
										resultColumn.bindStarLinkColumn(columnName);
									}

									if (tableModel.getColumns() != null) {
										for (int j = 0; j < tableModel.getColumns().size(); j++) {
											TableColumn tableColumn = tableModel.getColumns().get(j);
											TObjectName columnName = tableColumn.getColumnObject();
											if (columnName == null || "*".equals(getColumnName(columnName))) {
												continue;
											}
											resultColumn.bindStarLinkColumn(columnName);
										}
									}
								} else if (modelManager.getModel(sourceTable) instanceof QueryTable) {
									QueryTable tableModel = (QueryTable) modelManager.getModel(sourceTable);
									if (tableModel != null && !tableModel.getColumns().isEmpty()) {
										for (int z = 0; z < tableModel.getColumns().size(); z++) {
											if (!tableModel.getColumns().get(z).getStarLinkColumns().isEmpty()) {
												for (TObjectName starLinkColumn : tableModel.getColumns().get(z)
														.getStarLinkColumns()) {
													resultColumn.bindStarLinkColumn(starLinkColumn);
												}
											} else if (tableModel.getColumns().get(z)
													.getColumnObject() instanceof TObjectName) {
												resultColumn.bindStarLinkColumn(
														(TObjectName) tableModel.getColumns().get(z).getColumnObject());
											} else if (tableModel.getColumns().get(z)
													.getColumnObject() instanceof TResultColumn) {
												TResultColumn queryTableColumn = (TResultColumn) tableModel.getColumns()
														.get(z).getColumnObject();
												TObjectName tableColumnObject = queryTableColumn.getFieldAttr();
												if (tableColumnObject != null) {
													resultColumn.bindStarLinkColumn(tableColumnObject);
												} else if (queryTableColumn.getAliasClause() != null) {
													resultColumn.bindStarLinkColumn(
															queryTableColumn.getAliasClause().getAliasName());
												}
											}
										}
									}
								}
							} else {
								TTableList tables = stmt.getTables();
								for (int k = 0; k < tables.size(); k++) {
									TTable table = tables.getTable(k);
									TObjectName[] columns = modelManager.getTableColumns(table);
									for (int j = 0; j < columns.length; j++) {
										TObjectName columnName = columns[j];
										if (columnName == null) {
											continue;
										}
										if ("*".equals(getColumnName(columnName))) {
											if (modelManager.getModel(table) instanceof Table) {
												Table tableModel = (Table) modelManager.getModel(table);
												if (tableModel != null) {
													modelFactory.createTableColumn(tableModel, columnName, false);
												}
												if (tableModel != null && !tableModel.getColumns().isEmpty()) {
													for (int z = 0; z < tableModel.getColumns().size(); z++) {
														resultColumn.bindStarLinkColumn(
																tableModel.getColumns().get(z).getColumnObject());
													}
												}
											} else if (modelManager.getModel(table) instanceof QueryTable) {
												QueryTable tableModel = (QueryTable) modelManager.getModel(table);
												if (tableModel != null && !tableModel.getColumns().isEmpty()) {
													for (int z = 0; z < tableModel.getColumns().size(); z++) {
														if (!tableModel.getColumns().get(z).getStarLinkColumns()
																.isEmpty()) {
															for (TObjectName starLinkColumn : tableModel.getColumns()
																	.get(z).getStarLinkColumns()) {
																resultColumn.bindStarLinkColumn(starLinkColumn);
															}
														} else if (tableModel.getColumns().get(z)
																.getColumnObject() instanceof TObjectName) {
															resultColumn.bindStarLinkColumn((TObjectName) tableModel
																	.getColumns().get(z).getColumnObject());
														} else if (tableModel.getColumns().get(z)
																.getColumnObject() instanceof TResultColumn) {
															TResultColumn queryTableColumn = (TResultColumn) tableModel
																	.getColumns().get(z).getColumnObject();
															TObjectName tableColumnObject = queryTableColumn
																	.getFieldAttr();
															if (tableColumnObject != null) {
																resultColumn.bindStarLinkColumn(tableColumnObject);
															} else if (queryTableColumn.getAliasClause() != null) {
																resultColumn.bindStarLinkColumn(queryTableColumn
																		.getAliasClause().getAliasName());
															}
														}
													}
												}
											}
											continue;
										}
										resultColumn.bindStarLinkColumn(columnName);
									}
								}
							}
						}

						analyzeResultColumn(column, EffectType.select);

					}
				}
			}

			if (stmt.getIntoClause() != null) {
				List<TObjectName> tableNames = new ArrayList<TObjectName>();
				if (stmt.getIntoClause().getIntoName() != null) {
					tableNames.add(stmt.getIntoClause().getIntoName());
				} else if (stmt.getIntoClause().getExprList() != null) {
					for (int j = 0; j < stmt.getIntoClause().getExprList().size(); j++) {
						TObjectName tableName = stmt.getIntoClause().getExprList().getExpression(j).getObjectOperand();
						if (tableName != null) {
							tableNames.add(tableName);
						}
					}
				}

				Object queryModel = modelManager.getModel(stmt.getResultColumnList());
				for (int j = 0; j < tableNames.size(); j++) {
					TObjectName tableName = tableNames.get(j);
					if (tableName.getDbObjectType() == EDbObjectType.variable) {
						continue;
					}
					Table tableModel = modelFactory.createTableByName(tableName);

					for (int i = 0; i < stmt.getResultColumnList().size(); i++) {
						TResultColumn column = stmt.getResultColumnList().getResultColumn(i);

						if ("*".equals(column.getColumnNameOnly()) && column.getFieldAttr() != null
								&& column.getFieldAttr().getSourceTable() != null) {
							ResultColumn resultColumn = (ResultColumn) modelManager.getModel(column);
							List<TObjectName> columns = resultColumn.getStarLinkColumns();
							if (columns.size() > 0) {
								for (int k = 0; k < columns.size(); k++) {

									TableColumn tableColumn = modelFactory.createInsertTableColumn(tableModel,
											columns.get(k));

									if (SQLUtil.isTempTable(tableModel, vendor) && sqlenv != null
											&& tableModel.getDatabase() != null && tableModel.getSchema() != null) {
										TSQLSchema schema = sqlenv.getSQLSchema(
												tableModel.getDatabase() + "." + tableModel.getSchema(), true);
										if (schema != null) {
											TSQLTable tempTable = schema.createTable(tableModel.getName());
											tempTable.addColumn(tableColumn.getName());
										}
									}

									DataFlowRelation relation = modelFactory.createDataFlowRelation();
									relation.setEffectType(EffectType.insert);
									relation.setTarget(new TableColumnRelationElement(tableColumn));
									// if (columns.get(k).getSourceTable() !=
									// null) {
									// TTable souceTable =
									// columns.get(k).getSourceTable();
									// Object model =
									// modelManager.getModel(souceTable);
									// if (model instanceof Table) {
									// Table sourceTableModel = (Table) model;
									// if (sourceTableModel.getColumns().size()
									// > k) {
									// relation.addSource(new
									// TableColumnRelationElement(
									// sourceTableModel.getColumns().get(k)));
									// }
									// } else if (model instanceof QueryTable) {
									// QueryTable sourceTableModel =
									// (QueryTable) model;
									// if (sourceTableModel.getColumns().size()
									// > k) {
									// relation.addSource(new
									// ResultColumnRelationElement(
									// sourceTableModel.getColumns().get(k)));
									// }
									// }
									// } else {
									relation.addSource(new ResultColumnRelationElement(resultColumn));
									// }
								}
							} else {
								TObjectName columnObject = column.getFieldAttr();
								if (columnObject != null) {
									TableColumn tableColumn = modelFactory.createInsertTableColumn(tableModel,
											columnObject);
									DataFlowRelation relation = modelFactory.createDataFlowRelation();
									relation.setEffectType(EffectType.insert);
									relation.setTarget(new TableColumnRelationElement(tableColumn));
									relation.addSource(new ResultColumnRelationElement(resultColumn));
								} else if (!SQLUtil.isEmpty(column.getColumnAlias())) {
									TableColumn tableColumn = modelFactory.createInsertTableColumn(tableModel,
											column.getAliasClause().getAliasName());
									DataFlowRelation relation = modelFactory.createDataFlowRelation();
									relation.setEffectType(EffectType.insert);
									relation.setTarget(new TableColumnRelationElement(tableColumn));
									relation.addSource(new ResultColumnRelationElement(resultColumn));
								}
							}
						} else {
							ResultColumn resultColumn = null;

							if (queryModel instanceof QueryTable) {
								resultColumn = (ResultColumn) modelManager.getModel(column);
							} else if (queryModel instanceof ResultSet) {
								resultColumn = (ResultColumn) modelManager.getModel(column);
							} else {
								continue;
							}

							if (resultColumn != null) {
								TObjectName columnObject = column.getFieldAttr();
								if (columnObject != null) {
									TableColumn tableColumn = modelFactory.createInsertTableColumn(tableModel,
											columnObject);
									DataFlowRelation relation = modelFactory.createDataFlowRelation();
									relation.setEffectType(EffectType.insert);
									relation.setTarget(new TableColumnRelationElement(tableColumn));
									relation.addSource(new ResultColumnRelationElement(resultColumn));
								} else if (!SQLUtil.isEmpty(column.getColumnAlias())) {
									TableColumn tableColumn = modelFactory.createInsertTableColumn(tableModel,
											column.getAliasClause().getAliasName());
									DataFlowRelation relation = modelFactory.createDataFlowRelation();
									relation.setEffectType(EffectType.insert);
									relation.setTarget(new TableColumnRelationElement(tableColumn));
									relation.addSource(new ResultColumnRelationElement(resultColumn));
								}
							}
						}
					}
				}

				// TInsertIntoValue value = stmt.getIntoTableClause(
				// ).getTableName()
				// .getElement( j );
				// TTable table = value.getTable( );
				// Table tableModel = modelFactory.createTable( table );
				//
				// inserTables.add( tableModel );
				// List<TObjectName> keyMap = new ArrayList<TObjectName>( );
				// List<TResultColumn> valueMap = new ArrayList<TResultColumn>(
				// );
				// insertTableKeyMap.put( tableModel, keyMap );
				// insertTableValueMap.put( tableModel, valueMap );
				//
				// List<TableColumn> tableColumns = bindInsertTableColumn(
				// tableModel,
				// value,
				// keyMap,
				// valueMap );
				// if ( tableColumnMap.get( table.getName( ) ) == null
				// && !tableColumns.isEmpty( ) )
				// {
				// tableColumnMap.put( tableModel.getName( ), tableColumns );
				// }
			}

			if (stmt.getJoins() != null && stmt.getJoins().size() > 0) {
				for (int i = 0; i < stmt.getJoins().size(); i++) {
					TJoin join = stmt.getJoins().getJoin(i);
					analyzeJoin(join, EffectType.select);
				}
			}

			if (stmt.getWhereClause() != null) {
				TExpression expr = stmt.getWhereClause().getCondition();
				if (expr != null) {
					analyzeFilterCondtion(expr, null, JoinClauseType.where, EffectType.select);
				}
			}

			if (stmt.getGroupByClause() != null) {
				TGroupByItemList groupByList = stmt.getGroupByClause().getItems();
				for (int i = 0; i < groupByList.size(); i++) {
					TGroupByItem groupBy = groupByList.getGroupByItem(i);
					TExpression expr = groupBy.getExpr();
					analyzeAggregate(expr, EffectType.select);
				}

				if (stmt.getGroupByClause().getHavingClause() != null) {
					analyzeAggregate(stmt.getGroupByClause().getHavingClause(), EffectType.select);
				}
			}

			stmtStack.pop();
		}
	}

	private boolean isFromFunction(TObjectName object) {

		Stack<TParseTreeNode> nodes = object.getStartToken().getNodesStartFromThisToken();
		if(nodes!=null){
			for(int i=0;i<nodes.size();i++){
				if(nodes.get(i) instanceof TFunctionCall){
					return true;
				}
			}
		}
		return false;
	}

	private TResultColumnList getResultColumnList(TSelectSqlStatement stmt) {
		if (stmt.isCombinedQuery()) {
			TResultColumnList columns = getResultColumnList(stmt.getLeftStmt());
			if (columns != null) {
				return columns;
			}
			return getResultColumnList(stmt.getRightStmt());
		} else {
			return stmt.getResultColumnList();
		}
	}

	private void createPseudoImpactRelation(TCustomSqlStatement stmt, ResultSet resultSetModel, EffectType effectType) {
		if (stmt.getTables() != null) {
			for (int i = 0; i < stmt.getTables().size(); i++) {
				TTable table = stmt.getTables().getTable(i);
				if (modelManager.getModel(table) instanceof ResultSet) {
					ResultSet tableModel = (ResultSet) modelManager.getModel(table);
					if (tableModel != resultSetModel && !tableModel.getPseudoRows().getHoldRelations().isEmpty()) {
						ImpactRelation impactRelation = modelFactory.createImpactRelation();
						impactRelation.setEffectType(effectType);
						impactRelation.addSource(
								new PseudoRowsRelationElement<ResultSetPseudoRows>(tableModel.getPseudoRows()));
						impactRelation.setTarget(
								new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSetModel.getPseudoRows()));
					}
				} else if (modelManager.getModel(table) instanceof Table) {
					Table tableModel = (Table) modelManager.getModel(table);
					if (!tableModel.getPseudoRows().getHoldRelations().isEmpty()) {
						ImpactRelation impactRelation = modelFactory.createImpactRelation();
						impactRelation.setEffectType(effectType);
						impactRelation
								.addSource(new PseudoRowsRelationElement<TablePseudoRows>(tableModel.getPseudoRows()));
						impactRelation.setTarget(
								new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSetModel.getPseudoRows()));
					}
				} else if (modelManager.getModel(table) instanceof View) {
					View tableModel = (View) modelManager.getModel(table);
					if (!tableModel.getPseudoRows().getHoldRelations().isEmpty()) {
						ImpactRelation impactRelation = modelFactory.createImpactRelation();
						impactRelation.setEffectType(effectType);
						impactRelation
								.addSource(new PseudoRowsRelationElement<TablePseudoRows>(tableModel.getPseudoRows()));
						impactRelation.setTarget(
								new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSetModel.getPseudoRows()));
					}
				}
			}
		}
	}

	private void analyzeFunctionDataFlowRelation(Object gspObject, List<TParseTreeNode> functions,
			EffectType effectType) {

		Object modelObject = modelManager.getModel(gspObject);
		if (modelObject == null) {
			if (gspObject instanceof ResultColumn || gspObject instanceof TableColumn
					|| gspObject instanceof ViewColumn) {
				modelObject = gspObject;
			}
		}

		DataFlowRelation relation = modelFactory.createDataFlowRelation();
		relation.setEffectType(effectType);

		if (modelObject instanceof ResultColumn) {
			relation.setTarget(new ResultColumnRelationElement((ResultColumn) modelObject));

		} else if (modelObject instanceof TableColumn) {
			relation.setTarget(new TableColumnRelationElement((TableColumn) modelObject));

		} else if (modelObject instanceof ViewColumn) {
			relation.setTarget(new ViewColumnRelationElement((ViewColumn) modelObject));

		} else {
			throw new UnsupportedOperationException();
		}

		for (int i = 0; i < functions.size(); i++) {
			TParseTreeNode functionCall = functions.get(i);
			createFunction(functionCall);
			if (functionCall instanceof TFunctionCall) {
				relation.addSource(new ResultColumnRelationElement((FunctionResultColumn) modelManager
						.getModel(((TFunctionCall) functionCall).getFunctionName())));
			} else if (functionCall instanceof TCaseExpression) {
				relation.addSource(new ResultColumnRelationElement((FunctionResultColumn) modelManager
						.getModel(((TCaseExpression) functionCall).getWhenClauseItemList())));
			}

		}

	}
	
	private void analyzeSubqueryDataFlowRelation(Object gspObject, List<TSelectSqlStatement> subquerys,
			EffectType effectType) {

		Object modelObject = modelManager.getModel(gspObject);
		if (modelObject == null) {
			if (gspObject instanceof ResultColumn || gspObject instanceof TableColumn
					|| gspObject instanceof ViewColumn) {
				modelObject = gspObject;
			}
		}

		DataFlowRelation relation = modelFactory.createDataFlowRelation();
		relation.setEffectType(effectType);

		if (modelObject instanceof ResultColumn) {
			relation.setTarget(new ResultColumnRelationElement((ResultColumn) modelObject));

		} else if (modelObject instanceof TableColumn) {
			relation.setTarget(new TableColumnRelationElement((TableColumn) modelObject));

		} else if (modelObject instanceof ViewColumn) {
			relation.setTarget(new ViewColumnRelationElement((ViewColumn) modelObject));

		} else {
			throw new UnsupportedOperationException();
		}

		for (int i = 0; i < subquerys.size(); i++) {
			TSelectSqlStatement subquery = subquerys.get(i);
			ResultSet resultSetModel = (ResultSet) modelManager.getModel(subquery);
			if(resultSetModel!=null && resultSetModel.getColumns()!=null){
				for(ResultColumn column: resultSetModel.getColumns()){
					relation.addSource(new ResultColumnRelationElement(column));
				}
			}
		}

	}

	private void createFunction(TParseTreeNode functionCall) {
		if (functionCall instanceof TFunctionCall) {
			Function function = modelFactory.createFunction((TFunctionCall) functionCall);
			ResultColumn column = modelFactory.createFunctionResultColumn(function,
					((TFunctionCall) functionCall).getFunctionName());
			analyzeFunctionArgumentsDataFlowRelation(column, functionCall);
		} else if (functionCall instanceof TCaseExpression) {
			Function function = modelFactory.createFunction((TCaseExpression) functionCall);
			ResultColumn column = modelFactory.createFunctionResultColumn(function,
					((TCaseExpression) functionCall).getWhenClauseItemList());
			analyzeFunctionArgumentsDataFlowRelation(column, functionCall);
		}
	}

	private void analyzeFunctionArgumentsDataFlowRelation(ResultColumn resultColumn, TParseTreeNode gspObject) {
		List<TExpression> expressions = new ArrayList<TExpression>();
		if (gspObject instanceof TFunctionCall) {
			TFunctionCall functionCall = (TFunctionCall) gspObject;

			if (functionCall.getArgs() != null) {
				for (int k = 0; k < functionCall.getArgs().size(); k++) {
					TExpression expr = functionCall.getArgs().getExpression(k);
					if (expr != null) {
						expressions.add(expr);
					}
				}
			}
			if (functionCall.getTrimArgument() != null) {
				TTrimArgument args = functionCall.getTrimArgument();
				TExpression expr = args.getStringExpression();
				if (expr != null) {
					expressions.add(expr);
				}
				expr = args.getTrimCharacter();
				if (expr != null) {
					expressions.add(expr);
				}
			}

			if (functionCall.getAgainstExpr() != null) {
				expressions.add(functionCall.getAgainstExpr());
			}
			if (functionCall.getBetweenExpr() != null) {
				expressions.add(functionCall.getBetweenExpr());
			}
			if (functionCall.getExpr1() != null) {
				expressions.add(functionCall.getExpr1());
			}
			if (functionCall.getExpr2() != null) {
				expressions.add(functionCall.getExpr2());
			}
			if (functionCall.getExpr3() != null) {
				expressions.add(functionCall.getExpr3());
			}
			if (functionCall.getParameter() != null) {
				expressions.add(functionCall.getParameter());
			}
			if (functionCall.getWindowDef() != null && functionCall.getWindowDef().getPartitionClause() != null) {
				TExpressionList args = functionCall.getWindowDef().getPartitionClause().getExpressionList();
				if (args != null) {
					for (int k = 0; k < args.size(); k++) {
						TExpression expr = args.getExpression(k);
						if (expr != null) {
							expressions.add(expr);
						}
					}
				}
			}
			if (functionCall.getWindowDef() != null && functionCall.getWindowDef().getOrderBy() != null) {
				TOrderByItemList orderByList = functionCall.getWindowDef().getOrderBy().getItems();
				for (int i = 0; i < orderByList.size(); i++) {
					TOrderByItem element = orderByList.getOrderByItem(i);
					TExpression expression = element.getSortKey();
					expressions.add(expression);
				}
			}
			if(functionCall.getWithinGroup() !=null && functionCall.getWithinGroup().getOrderBy()!=null){
				TOrderByItemList orderByList = functionCall.getWithinGroup().getOrderBy().getItems();
				for (int i = 0; i < orderByList.size(); i++) {
					TOrderByItem element = orderByList.getOrderByItem(i);
					TExpression expression = element.getSortKey();
					expressions.add(expression);
				}
			}
			if(functionCall.getCallTarget()!=null){
				expressions.add(functionCall.getCallTarget().getExpr());
			}
		} else if (gspObject instanceof TCaseExpression) {
			TCaseExpression expr = (TCaseExpression) gspObject;
			TExpression inputExpr = expr.getInput_expr();
			if (inputExpr != null) {
				expressions.add(inputExpr);
			}
			TExpression defaultExpr = expr.getElse_expr();
			if (defaultExpr != null) {
				expressions.add(defaultExpr);
			}
			TWhenClauseItemList list = expr.getWhenClauseItemList();
			for (int i = 0; i < list.size(); i++) {
				TWhenClauseItem element = list.getWhenClauseItem(i);
				expressions.add(element.getComparison_expr());
				expressions.add(element.getReturn_expr());
			}
		}

		for (int j = 0; j < expressions.size(); j++) {
			columnsInExpr visitor = new columnsInExpr();
			expressions.get(j).inOrderTraverse(visitor);
			
		
				List<TObjectName> objectNames = visitor.getObjectNames();
				List<TParseTreeNode> functions = visitor.getFunctions();
				
				if (functions != null && !functions.isEmpty()) {
					analyzeFunctionDataFlowRelation(resultColumn, functions, EffectType.function);
				}
				
				List<TSelectSqlStatement> subquerys = visitor.getSubquerys();
				if (subquerys!=null && !subquerys.isEmpty()) {
					analyzeSubqueryDataFlowRelation(resultColumn, subquerys, EffectType.function);
				}
	
				analyzeDataFlowRelation(resultColumn, objectNames, EffectType.function, functions);
	
				List<TConstant> constants = visitor.getConstants();
				analyzeConstantDataFlowRelation(resultColumn, constants, EffectType.function, functions);
			
		}
	}

	private void analyzeJoin(TJoin join, EffectType effectType) {
		if (join.getJoinItems() != null) {
			for (int j = 0; j < join.getJoinItems().size(); j++) {
				TJoinItem joinItem = join.getJoinItems().getJoinItem(j);
				TExpression expr = joinItem.getOnCondition();
				if (expr != null) {
					analyzeFilterCondtion(expr, joinItem.getJoinType(), JoinClauseType.on, effectType);
				}
			}
		}

		if (join.getJoin() != null) {
			analyzeJoin(join.getJoin(), effectType);
		}
	}

	private TSelectSqlStatement getParentSetSelectStmt(TSelectSqlStatement stmt) {
		TCustomSqlStatement parent = stmt.getParentStmt();
		if (parent == null)
			return null;
		if (parent.getStatements() != null) {
			for (int i = 0; i < parent.getStatements().size(); i++) {
				TCustomSqlStatement temp = parent.getStatements().get(i);
				if (temp instanceof TSelectSqlStatement) {
					TSelectSqlStatement select = (TSelectSqlStatement) temp;
					if (select.getLeftStmt() == stmt || select.getRightStmt() == stmt)
						return select;
				}
			}
		}
		if (parent instanceof TSelectSqlStatement) {
			TSelectSqlStatement select = (TSelectSqlStatement) parent;
			if (select.getLeftStmt() == stmt || select.getRightStmt() == stmt)
				return select;
		}
		return null;
	}

	private void createSelectSetResultColumns(SelectSetResultSet resultSet, TSelectSqlStatement stmt) {
		if (stmt.getSetOperatorType() != ESetOperatorType.none) {
			createSelectSetResultColumns(resultSet, stmt.getLeftStmt());
		} else {
			TResultColumnList columnList = stmt.getResultColumnList();
			for (int i = 0; i < columnList.size(); i++) {
				TResultColumn column = columnList.getResultColumn(i);
				ResultColumn resultColumn = modelFactory.createSelectSetResultColumn(resultSet, column, i);

				if (resultColumn.getColumnObject() instanceof TResultColumn) {
					TResultColumn columnObject = (TResultColumn) resultColumn.getColumnObject();
					if (columnObject.getFieldAttr() != null) {
						if ("*".equals(getColumnName(columnObject.getFieldAttr()))) {
							TObjectName fieldAttr = columnObject.getFieldAttr();
							TTable sourceTable = fieldAttr.getSourceTable();
							if (fieldAttr.getTableToken() != null && sourceTable != null) {
								TObjectName[] columns = modelManager.getTableColumns(sourceTable);
								for (int j = 0; j < columns.length; j++) {
									TObjectName columnName = columns[j];
									if (columnName == null) {
										continue;
									}
									if ("*".equals(getColumnName(columnName))) {
										continue;
									}
									resultColumn.bindStarLinkColumn(columnName);
								}

								if (modelManager.getModel(sourceTable) instanceof Table) {
									Table tableModel = (Table) modelManager.getModel(sourceTable);
									if (tableModel != null && !tableModel.getColumns().isEmpty()) {
										for (int z = 0; z < tableModel.getColumns().size(); z++) {
											if ("*".equals(
													getColumnName(tableModel.getColumns().get(z).getColumnObject()))) {
												continue;
											}
											resultColumn.bindStarLinkColumn(
													tableModel.getColumns().get(z).getColumnObject());
										}
									}
								} else if (modelManager.getModel(sourceTable) instanceof QueryTable) {
									QueryTable tableModel = (QueryTable) modelManager.getModel(sourceTable);
									if (tableModel != null && !tableModel.getColumns().isEmpty()) {
										for (int z = 0; z < tableModel.getColumns().size(); z++) {
											if (!tableModel.getColumns().get(z).getStarLinkColumns().isEmpty()) {
												for (TObjectName starLinkColumn : tableModel.getColumns().get(z)
														.getStarLinkColumns()) {
													if ("*".equals(getColumnName(starLinkColumn))) {
														continue;
													}
													resultColumn.bindStarLinkColumn(starLinkColumn);
												}
											} else if (tableModel.getColumns().get(z)
													.getColumnObject() instanceof TObjectName) {
												TObjectName starLinkColumn = (TObjectName) tableModel.getColumns()
														.get(z).getColumnObject();
												if ("*".equals(getColumnName(starLinkColumn))) {
													continue;
												}
												resultColumn.bindStarLinkColumn(starLinkColumn);
											}
										}
									}
								}

							} else {
								TTableList tables = stmt.getTables();
								for (int k = 0; k < tables.size(); k++) {
									TTable tableElement = tables.getTable(k);
									TObjectName[] columns = modelManager.getTableColumns(tableElement);
									for (int j = 0; j < columns.length; j++) {
										TObjectName columnName = columns[j];
										if (columnName == null) {
											continue;
										}
										if ("*".equals(getColumnName(columnName))) {
											if (modelManager.getModel(tableElement) instanceof Table) {
												Table tableModel = (Table) modelManager.getModel(tableElement);
												if (tableModel != null && !tableModel.getColumns().isEmpty()) {
													for (int z = 0; z < tableModel.getColumns().size(); z++) {
														resultColumn.bindStarLinkColumn(
																tableModel.getColumns().get(z).getColumnObject());
													}
												}
											} else if (modelManager.getModel(tableElement) instanceof QueryTable) {
												QueryTable tableModel = (QueryTable) modelManager
														.getModel(tableElement);
												if (tableModel != null && !tableModel.getColumns().isEmpty()) {
													for (int z = 0; z < tableModel.getColumns().size(); z++) {
														if (!tableModel.getColumns().get(z).getStarLinkColumns()
																.isEmpty()) {
															for (TObjectName starLinkColumn : tableModel.getColumns()
																	.get(z).getStarLinkColumns())
																resultColumn.bindStarLinkColumn(starLinkColumn);
														} else if (tableModel.getColumns().get(z)
																.getColumnObject() instanceof TObjectName) {
															resultColumn.bindStarLinkColumn((TObjectName) tableModel
																	.getColumns().get(z).getColumnObject());
														}
													}
												}
											}
											continue;
										}
										resultColumn.bindStarLinkColumn(columnName);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private void analyzeResultColumn(TResultColumn column, EffectType effectType) {
		TExpression expression = column.getExpr();
		columnsInExpr visitor = new columnsInExpr();
		expression.inOrderTraverse(visitor);
		List<TObjectName> objectNames = visitor.getObjectNames();

		List<TParseTreeNode> functions = visitor.getFunctions();

		if (functions != null && !functions.isEmpty()) {
			analyzeFunctionDataFlowRelation(column, functions, effectType);
		}
		
		List<TSelectSqlStatement> subquerys = visitor.getSubquerys();
		if (subquerys!=null && !subquerys.isEmpty()) {
			analyzeSubqueryDataFlowRelation(column, subquerys, effectType);
		}

		analyzeDataFlowRelation(column, objectNames, effectType, functions);

		List<TConstant> constants = visitor.getConstants();
		Object columnObject = modelManager.getModel(column);
		analyzeConstantDataFlowRelation(columnObject, constants, effectType, functions);

		analyzeRecordSetRelation(column, functions, effectType);
		// analyzeResultColumnImpact( column, effectType, functions);
	}

	/**
	 * private void analyzeResultColumnImpact( TResultColumn column, EffectType
	 * effectType, List<TParseTreeNode> functions ) { TExpression expression =
	 * column.getExpr( ); EExpressionType type = expression.getExpressionType(
	 * ); List<TObjectName> objectNames = new ArrayList<TObjectName>( ); if (
	 * type == EExpressionType.case_t ) { TWhenClauseItemList list =
	 * expression.getCaseExpression( ) .getWhenClauseItemList( ); for ( int i =
	 * 0; i < list.size( ); i++ ) { TExpression condition =
	 * list.getWhenClauseItem( i ) .getComparison_expr( ); columnsInExpr visitor
	 * = new columnsInExpr( ); condition.inOrderTraverse( visitor );
	 * objectNames.addAll( visitor.getObjectNames( ) ); } } else if ( type ==
	 * EExpressionType.function_t ) { String functionName =
	 * expression.getFunctionCall( ) .getFunctionName( ) .toString( ) .trim( );
	 * if ( "NVL".equalsIgnoreCase( functionName ) || "IF".equalsIgnoreCase(
	 * functionName ) || "IFNULL".equalsIgnoreCase( functionName ) ||
	 * "DECODE".equalsIgnoreCase( functionName ) ) { TExpression condition =
	 * expression.getFunctionCall( ) .getArgs( ) .getExpression( 0 );
	 * columnsInExpr visitor = new columnsInExpr( ); condition.inOrderTraverse(
	 * visitor ); objectNames.addAll( visitor.getObjectNames( ) ); } }
	 * analyzeImpactRelation( column, objectNames, effectType, functions ); }
	 * 
	 * private void analyzeImpactRelation( TResultColumn column,
	 * List<TObjectName> objectNames, EffectType effectType,
	 * List<TParseTreeNode> functions ) { if ( objectNames == null ||
	 * objectNames.size( ) == 0 ) return;
	 * 
	 * IndirectImpactRelation relation =
	 * modelFactory.createIndirectImpactRelation( ); relation.setEffectType(
	 * effectType );
	 * 
	 * if(functions!=null && !functions.isEmpty()) {
	 * relation.setFunction(getFunctionName(functions.get(0))); }
	 * 
	 * relation.setTarget( new ResultColumnRelationElement( (ResultColumn)
	 * modelManager.getModel( column ) ) );
	 * 
	 * TCustomSqlStatement stmt = stmtStack.peek( );
	 * 
	 * for ( int j = 0; j < objectNames.size( ); j++ ) { TObjectName columnName
	 * = objectNames.get( j );
	 * 
	 * if(columnName.getDbObjectType() == EDbObjectType.variable) { continue; }
	 * 
	 * TTable table = modelManager.getTable( stmt, columnName ); if ( table !=
	 * null ) { if ( modelManager.getModel( table ) instanceof Table ) { Table
	 * tableModel = (Table) modelManager.getModel( table ); if ( tableModel !=
	 * null ) { TableColumn columnModel = modelFactory.createTableColumn(
	 * tableModel, columnName ); relation.addSource( new
	 * TableColumnRelationElement( columnModel ) ); } } else if (
	 * modelManager.getModel( table ) instanceof QueryTable ) { ResultColumn
	 * resultColumn = (ResultColumn) modelManager.getModel(
	 * columnName.getSourceColumn( ) ); if ( resultColumn != null ) {
	 * relation.addSource( new ResultColumnRelationElement( resultColumn ) ); }
	 * } } } }
	 **/

	private void analyzeRecordSetRelation(TResultColumn column, List<TParseTreeNode> functions, EffectType effectType) {
		if (functions == null || functions.size() == 0)
			return;

		List<TFunctionCall> aggregateFunctions = new ArrayList<TFunctionCall>();
		for (TParseTreeNode function : functions) {
			if (function instanceof TFunctionCall && isAggregateFunction((TFunctionCall) function)) {
				aggregateFunctions.add((TFunctionCall) function);
			}
		}

		if (aggregateFunctions.size() == 0)
			return;

		// RecordSetRelation relation = modelFactory.createRecordSetRelation();
		// relation.setEffectType(effectType);
		// relation.setTarget(new ResultColumnRelationElement((ResultColumn)
		// modelManager.getModel(column)));
		// relation.setFunction(aggregateFunctions.get(0).getFunctionName().toString());
		for (int i = 0; i < aggregateFunctions.size(); i++) {
			TFunctionCall function = aggregateFunctions.get(i);
			RecordSetRelation functionRelation = modelFactory.createRecordSetRelation();
			functionRelation.setEffectType(EffectType.function);
			functionRelation.setFunction(function.getFunctionName().toString());
			functionRelation.setTarget(
					new ResultColumnRelationElement((ResultColumn) modelManager.getModel(function.getFunctionName())));

			if (stmtStack.peek().getTables().size() == 1) {
				Object tableObject = modelManager.getModel(stmtStack.peek().getTables().getTable(0));
				if (tableObject instanceof Table) {
					Table tableModel = (Table) tableObject;
					// relation.addSource(new
					// PseudoRowsRelationElement<TablePseudoRows>(tableModel.getPseudoRows()));
					// relation.setAggregateFunction(function.getFunctionName().toString());
					functionRelation
							.addSource(new PseudoRowsRelationElement<TablePseudoRows>(tableModel.getPseudoRows()));
					functionRelation.setAggregateFunction(function.getFunctionName().toString());
				} else if (tableObject instanceof View) {
					View tableModel = (View) tableObject;
					// relation.addSource(new
					// PseudoRowsRelationElement<TablePseudoRows>(tableModel.getPseudoRows()));
					// relation.setAggregateFunction(function.getFunctionName().toString());
					functionRelation
							.addSource(new PseudoRowsRelationElement<TablePseudoRows>(tableModel.getPseudoRows()));
					functionRelation.setAggregateFunction(function.getFunctionName().toString());
				} else if (tableObject instanceof QueryTable) {
					QueryTable tableModel = (QueryTable) tableObject;
					// relation.addSource(new
					// PseudoRowsRelationElement<ResultSetPseudoRows>(tableModel.getPseudoRows()));
					// relation.setAggregateFunction(function.getFunctionName().toString());
					functionRelation
							.addSource(new PseudoRowsRelationElement<ResultSetPseudoRows>(tableModel.getPseudoRows()));
					functionRelation.setAggregateFunction(function.getFunctionName().toString());
				}
			}
		}
	}

	private void analyzeDataFlowRelation(TParseTreeNode gspObject, List<TObjectName> objectNames, EffectType effectType,
			List<TParseTreeNode> functions) {
		Object columnObject = modelManager.getModel(gspObject);
		analyzeDataFlowRelation(columnObject, objectNames, effectType, functions);
	}

	private void analyzeDataFlowRelation(Object modelObject, List<TObjectName> objectNames, EffectType effectType,
			List<TParseTreeNode> functions) {
		if (objectNames == null || objectNames.size() == 0)
			return;

		boolean isStar = false;
		boolean showStar = false;

		DataFlowRelation relation = modelFactory.createDataFlowRelation();
		relation.setEffectType(effectType);

		if (functions != null && !functions.isEmpty()) {
			relation.setFunction(getFunctionName(functions.get(0)));
		}

		int columnIndex = -1;

		if (modelObject instanceof ResultColumn) {
			relation.setTarget(new ResultColumnRelationElement((ResultColumn) modelObject));

			if ("*".equals(((ResultColumn) modelObject).getName())) {
				isStar = true;
				showStar = ((ResultColumn) modelObject).isShowStar();
			}

			if (((ResultColumn) modelObject).getResultSet() != null) {
				columnIndex = ((ResultColumn) modelObject).getResultSet().getColumns().indexOf(modelObject);
			}
		} else if (modelObject instanceof TableColumn) {
			relation.setTarget(new TableColumnRelationElement((TableColumn) modelObject));

			if ("*".equals(((TableColumn) modelObject).getName())) {
				isStar = true;
			}

			if (((TableColumn) modelObject).getTable() != null) {
				columnIndex = ((TableColumn) modelObject).getTable().getColumns().indexOf(modelObject);
			}
		} else if (modelObject instanceof ViewColumn) {
			relation.setTarget(new ViewColumnRelationElement((ViewColumn) modelObject));

			if ("*".equals(((ViewColumn) modelObject).getName())) {
				isStar = true;
			}

			if (((ViewColumn) modelObject).getView() != null) {
				columnIndex = ((ViewColumn) modelObject).getView().getColumns().indexOf(modelObject);
			}
		} else {
			throw new UnsupportedOperationException();
		}

		for (int i = 0; i < objectNames.size(); i++) {
			TObjectName columnName = objectNames.get(i);

			if (columnName.getDbObjectType() == EDbObjectType.variable) {
				continue;
			}

			List<TTable> tables = new ArrayList<TTable>();
			{
				TCustomSqlStatement stmt = stmtStack.peek();

				TTable table = columnName.getSourceTable();

				if (table == null) {
					table = modelManager.getTable(stmt, columnName);
				}

				if (table == null) {
					if (columnName.getTableToken() != null || !"*".equals(getColumnName(columnName))) {
						table = columnName.getSourceTable();
					}
				}

				if (table == null) {
					if (stmt.tables != null) {
						for (int j = 0; j < stmt.tables.size(); j++) {
							if (table != null)
								break;

							TTable tTable = stmt.tables.getTable(j);
							if (tTable.getTableType().name().startsWith("open")) {
								continue;
							} else if (tTable.getLinkedColumns() != null && tTable.getLinkedColumns().size() > 0) {
								for (int z = 0; z < tTable.getLinkedColumns().size(); z++) {
									TObjectName refer = tTable.getLinkedColumns().getObjectName(z);
									if ("*".equals(getColumnName(refer)))
										continue;
									if (SQLUtil.getIdentifierNormalName(getColumnName(refer)).equals(SQLUtil.getIdentifierNormalName(getColumnName(columnName)))) {
										table = tTable;
										break;
									}
								}
							} else if (columnName.getTableToken() != null && (columnName.getTableToken().astext
									.equalsIgnoreCase(tTable.getName())
									|| columnName.getTableToken().astext.equalsIgnoreCase(tTable.getAliasName()))) {
								table = tTable;
								break;
							}
						}
					}
					
					if(table == null){
						for (int j = 0; j < stmt.tables.size(); j++) {
							if (table != null)
								break;

							TTable tTable = stmt.tables.getTable(j);
							Object model = ModelBindingManager.get().getModel(tTable);
							if(model instanceof Table){
								Table tableModel = (Table)model;
								for (int z = 0; tableModel.getColumns()!=null && z < tableModel.getColumns().size(); z++) {
									TableColumn refer = tableModel.getColumns().get(z);
									if (SQLUtil.getIdentifierNormalName(refer.getName()).equals(SQLUtil.getIdentifierNormalName(getColumnName(columnName)))) {
										table = tTable;
										break;
									}
								}
							}
							else if(model instanceof QueryTable){
								QueryTable tableModel = (QueryTable)model;
								for (int z = 0; tableModel.getColumns()!=null && z < tableModel.getColumns().size(); z++) {
									ResultColumn refer = tableModel.getColumns().get(z);
									if (SQLUtil.getIdentifierNormalName(refer.getName()).equals(SQLUtil.getIdentifierNormalName(getColumnName(columnName)))) {
										table = tTable;
										break;
									}
								}
							}
						}
					}
				}

				if (columnName.getTableToken() == null && "*".equals(getColumnName(columnName))) {
					if (stmt.tables != null) {
						for (int j = 0; j < stmt.tables.size(); j++) {
							tables.add(stmt.tables.getTable(j));
						}
					}
				} else if (table != null) {
					tables.add(table);
				}

				// 此处特殊处理，多表关联无法找到 column 所属的 Table, tTable.getLinkedColumns
				// 也找不到，退而求其次采用第一个表
				if (stmt.tables != null && stmt.tables.size() != 0 && tables.size() == 0
						&& (stmt.getGsqlparser().getSqlEnv() == null
								|| SQLUtil.isTempTable(stmt.tables.getTable(0), vendor))
						&& !(isFunctionName(columnName) && isFromFunction(columnName))) {
					tables.add(stmt.tables.getTable(0));
					System.err.println("guessing orphan column [" + columnName.toString() + "] table is:"
							+ stmt.tables.getTable(0).getFullNameWithAliasString());
				}
			}

			for (int k = 0; k < tables.size(); k++) {
				TTable table = tables.get(k);
				if (table != null) {
					if (modelManager.getModel(table) instanceof Table) {
						Table tableModel = (Table) modelManager.getModel(table);
						if (tableModel != null) {
							if (!isStar && "*".equals(getColumnName(columnName))) {
								TObjectName[] columns = modelManager.getTableColumns(table);
								for (int j = 0; j < columns.length; j++) {
									TObjectName objectName = columns[j];
									if (objectName == null || "*".equals(getColumnName(objectName))) {
										continue;
									}
									TableColumn columnModel = modelFactory.createTableColumn(tableModel, objectName,
											false);
									relation.addSource(new TableColumnRelationElement(columnModel));
								}
							} else {
								if ("*".equals(getColumnName(columnName)) && !tableModel.getColumns().isEmpty()) {

									for (int j = 0; j < tableModel.getColumns().size(); j++) {
										;
										TableColumn columnModel = tableModel.getColumns().get(j);
										relation.addSource(new TableColumnRelationElement(columnModel));
									}

									if (isStar && showStar) {
										TableColumn columnModel = modelFactory.createTableColumn(tableModel, columnName,
												false);
										relation.addSource(new TableColumnRelationElement(columnModel));
									}
								} else {
									TableColumn columnModel = modelFactory.createTableColumn(tableModel, columnName,
											false);
									relation.addSource(new TableColumnRelationElement(columnModel));
								}
							}
						}
					} else if (modelManager.getModel(table) instanceof QueryTable) {
						QueryTable queryTable = (QueryTable) modelManager.getModel(table);

						TObjectNameList cteColumns = null;
						TSelectSqlStatement subquery = null;
						if (queryTable.getTableObject().getCTE() != null) {
							subquery = queryTable.getTableObject().getCTE().getSubquery();
							cteColumns = queryTable.getTableObject().getCTE().getColumnList();
						} else if (queryTable.getTableObject().getAliasClause()!=null && queryTable.getTableObject().getAliasClause().getColumns()!=null){
							
						}else {
							subquery = queryTable.getTableObject().getSubquery();
						}

						if (cteColumns != null) {
							for (int j = 0; j < cteColumns.size(); j++) {
								modelFactory.createResultColumn(queryTable, cteColumns.getObjectName(j));
							}
						}

						if (subquery != null && subquery.isCombinedQuery()) {
							SelectSetResultSet selectSetResultSetModel = (SelectSetResultSet) modelManager
									.getModel(subquery);

							if (selectSetResultSetModel != null
									&& !selectSetResultSetModel.getPseudoRows().getHoldRelations().isEmpty()) {
								ImpactRelation impactRelation = modelFactory.createImpactRelation();
								impactRelation.setEffectType(EffectType.select);
								impactRelation.addSource(new PseudoRowsRelationElement<ResultSetPseudoRows>(
										selectSetResultSetModel.getPseudoRows()));
								impactRelation.setTarget(
										new PseudoRowsRelationElement<ResultSetPseudoRows>(queryTable.getPseudoRows()));
							}

							if (selectSetResultSetModel != null) {
								if (getColumnName(columnName).equals("*")) {
									for (int j = 0; j < selectSetResultSetModel.getColumns().size(); j++) {
										ResultColumn sourceColumn = selectSetResultSetModel.getColumns().get(j);

										ResultColumn targetColumn = modelFactory.createSelectSetResultColumn(queryTable,
												sourceColumn, j);

										relation.addSource(new ResultColumnRelationElement(targetColumn));
									}
									break;
								} else {
									boolean flag = false;

									for (int j = 0; j < selectSetResultSetModel.getColumns().size(); j++) {
										SelectSetResultColumn sourceColumn = (SelectSetResultColumn)selectSetResultSetModel.getColumns().get(j);

										if (getColumnName(sourceColumn.getName()).equalsIgnoreCase(getColumnName(columnName))) {
											ResultColumn targetColumn = modelFactory
													.createSelectSetResultColumn(queryTable, sourceColumn, j);

											relation.addSource(new ResultColumnRelationElement(targetColumn));
											flag = true;
											break;
										} else if(sourceColumn.getAliasSet().size()>1){
											for(String alias: sourceColumn.getAliasSet()){
												if (getColumnName(alias).equalsIgnoreCase(getColumnName(columnName))) {
													ResultColumn targetColumn = modelFactory
															.createSelectSetResultColumn(queryTable, sourceColumn, j);
													relation.addSource(new ResultColumnRelationElement(targetColumn));
													flag = true;
													break;
												} 
											}
											if(flag){
												break;
											}
										}	
									}

									if (flag) {
										break;
									} else if (columnIndex != -1) {
										for (int j = 0; j < selectSetResultSetModel.getColumns().size(); j++) {
											ResultColumn sourceColumn = selectSetResultSetModel.getColumns().get(j);
											if (!sourceColumn.getStarLinkColumns().isEmpty()) {
												ResultColumn targetColumn = modelFactory.createSelectSetResultColumn(
														queryTable, selectSetResultSetModel.getColumns().get(j),
														columnIndex);

												relation.addSource(new ResultColumnRelationElement(targetColumn));
												flag = true;
												break;
											}
										}
									}

									if (flag) {
										break;
									} else if (columnIndex < selectSetResultSetModel.getColumns().size()
											&& columnIndex != -1) {
										ResultColumn targetColumn = modelFactory.createSelectSetResultColumn(queryTable,
												selectSetResultSetModel.getColumns().get(columnIndex), columnIndex);
										relation.addSource(new ResultColumnRelationElement(targetColumn));
										flag = true;
										break;
									}

									if (flag) {
										break;
									}
								}
							} else if (cteColumns != null) {
								if (getColumnName(columnName).equals("*")) {
									for (int j = 0; j < cteColumns.size(); j++) {
										ResultColumn targetColumn = queryTable.getColumns().get(j);

										relation.addSource(new ResultColumnRelationElement(targetColumn));
									}
									break;
								} else {
									boolean flag = false;

									for (int j = 0; j < cteColumns.size(); j++) {
										TObjectName sourceColumn = cteColumns.getObjectName(j);

										if (getColumnName(sourceColumn).equalsIgnoreCase(getColumnName(columnName))) {
											ResultColumn targetColumn = queryTable.getColumns().get(j);

											relation.addSource(new ResultColumnRelationElement(targetColumn));
											flag = true;
											break;
										}
									}

									if (flag) {
										break;
									}
								}
							} 

							if (columnName.getSourceColumn() != null) {
								Object model = modelManager.getModel(columnName.getSourceColumn());
								if (model instanceof ResultColumn) {
									ResultColumn resultColumn = (ResultColumn) model;
									relation.addSource(new ResultColumnRelationElement(resultColumn));
								}
							} else if (columnName.getSourceTable() != null) {
								Object tablModel = modelManager.getModel(columnName.getSourceTable());
								if (tablModel instanceof Table) {
									Object model = modelManager
											.getModel(new Pair<Table, TObjectName>((Table) tablModel, columnName));
									if (model instanceof TableColumn) {
										relation.addSource(new TableColumnRelationElement((TableColumn) model));
									}
								}
							}
						} else {
							List<ResultColumn> columns = queryTable.getColumns();
							if (getColumnName(columnName).equals("*")) {
								for (int j = 0; j < queryTable.getColumns().size(); j++) {
									relation.addSource(new ResultColumnRelationElement(queryTable.getColumns().get(j)));
								}
							} else {
								if (table.getCTE() != null) {
									for (k = 0; k < columns.size(); k++) {
										ResultColumn column = columns.get(k);
										if ("*".equals(column.getName())) {
											if (!containsStarColumn(column, columnName)) {
												column.bindStarLinkColumn(columnName);
											}
											relation.addSource(new ResultColumnRelationElement(column, columnName));
										} else if (SQLUtil.compareIdentifier(getColumnName(columnName),
												SQLUtil.getIdentifierNormalName(column.getName()))) {
											if (!column.equals(modelObject)) {
												relation.addSource(new ResultColumnRelationElement(column, columnName));
											}
											break;
										}
									}
								} else if (table.getAliasClause() != null && table.getAliasClause().getColumns()!=null) {
									for (k = 0; k < columns.size(); k++) {
										ResultColumn column = columns.get(k);
										if ("*".equals(column.getName())) {
											if (!containsStarColumn(column, columnName)) {
												column.bindStarLinkColumn(columnName);
											}
											relation.addSource(new ResultColumnRelationElement(column, columnName));
										} else if (SQLUtil.compareIdentifier(getColumnName(columnName),
												SQLUtil.getIdentifierNormalName(column.getName()))) {
											if (!column.equals(modelObject)) {
												relation.addSource(new ResultColumnRelationElement(column, columnName));
											}
											break;
										}
									}
								} else if (table.getSubquery() != null) {
									if (columnName.getSourceColumn() != null) {
										Object model = modelManager.getModel(columnName.getSourceColumn());
										if (model instanceof ResultColumn) {
											ResultColumn resultColumn = (ResultColumn) model;
											if ("*".equals(resultColumn.getName())
													&& !containsStarColumn(resultColumn, columnName)) {
												resultColumn.bindStarLinkColumn(columnName);
											}
											relation.addSource(
													new ResultColumnRelationElement(resultColumn, columnName));
										}
									} else if (columnName.getSourceTable() != null) {
										Object tableModel = modelManager.getModel(columnName.getSourceTable());
										if (tableModel instanceof Table) {
											Object model = modelManager.getModel(
													new Pair<Table, TObjectName>((Table) tableModel, columnName));
											if (model instanceof TableColumn) {
												relation.addSource(new TableColumnRelationElement((TableColumn) model));
											}
										} else if (tableModel instanceof QueryTable) {
											List<ResultColumn> queryColumns = ((QueryTable) tableModel).getColumns();
											boolean flag = false;
											for (int l = 0; l < queryColumns.size(); l++) {
												ResultColumn column = queryColumns.get(l);
												if ("*".equals(column.getName())) {
													if (!containsStarColumn(column, columnName)) {
														column.bindStarLinkColumn(columnName);
													}
													relation.addSource(
															new ResultColumnRelationElement(column, columnName));
													flag = true;
													break;
												} else if (SQLUtil.compareIdentifier(getColumnName(columnName),
														SQLUtil.getIdentifierNormalName(column.getName()))) {
													if (!column.equals(modelObject)) {
														relation.addSource(
																new ResultColumnRelationElement(column, columnName));
														flag = true;
													}
													break;
												}
											}
											if (!flag && columnIndex < queryColumns.size() && columnIndex != -1) {
												relation.addSource(new ResultColumnRelationElement(
														queryColumns.get(columnIndex), columnName));
											}
										}
									}
								}
							}
						}
					}
				}
			}
			if (relation.getSources().length == 0 && isKeyword(columnName)) {
				relation.addSource(new ConstantRelationElement(new Constant(columnName)));
			}

			if (relation.getSources().length > 0) {
				for (int j = 0; j < relation.getSources().length; j++) {
					Object source = relation.getSources()[j].getElement();
					ImpactRelation impactRelation = null;
					if (source instanceof ResultColumn
							&& !((ResultColumn) source).getResultSet().getPseudoRows().getHoldRelations().isEmpty()) {
						impactRelation = modelFactory.createImpactRelation();
						impactRelation.addSource(new PseudoRowsRelationElement<ResultSetPseudoRows>(
								((ResultColumn) source).getResultSet().getPseudoRows()));
					} else if (source instanceof TableColumn
							&& !((TableColumn) source).getTable().getPseudoRows().getHoldRelations().isEmpty()) {
						impactRelation = modelFactory.createImpactRelation();
						impactRelation.addSource(new PseudoRowsRelationElement<TablePseudoRows>(
								((TableColumn) source).getTable().getPseudoRows()));
					} else if (source instanceof ViewColumn
							&& !((ViewColumn) source).getView().getPseudoRows().getHoldRelations().isEmpty()) {
						impactRelation = modelFactory.createImpactRelation();
						impactRelation.addSource(new PseudoRowsRelationElement<TablePseudoRows>(
								((ViewColumn) source).getView().getPseudoRows()));
					}

					if (impactRelation == null) {
						continue;
					}

					Object target = relation.getTarget().getElement();
					if (target instanceof ResultColumn) {
						impactRelation.setTarget(new PseudoRowsRelationElement<ResultSetPseudoRows>(
								((ResultColumn) target).getResultSet().getPseudoRows()));
					} else if (source instanceof TableColumn) {
						impactRelation.setTarget(new PseudoRowsRelationElement<TablePseudoRows>(
								((TableColumn) target).getTable().getPseudoRows()));
					} else if (source instanceof ViewColumn) {
						impactRelation.setTarget(new PseudoRowsRelationElement<TablePseudoRows>(
								((ViewColumn) target).getView().getPseudoRows()));
					}
				}
			}
		}
	}

	private boolean containsStarColumn(ResultColumn resultColumn, TObjectName columnName) {
		List<TObjectName> columns = resultColumn.getStarLinkColumns();
		if (columns != null) {
			String targetColumnName = SQLUtil.getIdentifierNormalName(columnName.getColumnNameOnly());
			for (TObjectName item : columns) {
				String itemName = item.getColumnNameOnly();
				if (SQLUtil.getIdentifierNormalName(itemName).equals(targetColumnName)) {
					return true;
				}
			}
		}
		return false;
	}

	private void analyzeAggregate(TExpression expr, EffectType effectType) {
		if (expr == null) {
			return;
		}

		TCustomSqlStatement stmt = stmtStack.peek();
		columnsInExpr visitor = new columnsInExpr();
		expr.inOrderTraverse(visitor);
		List<TObjectName> objectNames = visitor.getObjectNames();
		ResultSet resultSet = (ResultSet) modelManager.getModel(stmt.getResultColumnList());
		if (resultSet == null) {
			return;
		}

		for (int j = 0; j < objectNames.size(); j++) {
			TObjectName columnName = objectNames.get(j);

			if (columnName.getDbObjectType() == EDbObjectType.variable) {
				continue;
			}
			RecordSetRelation relation = modelFactory.createRecordSetRelation();
			relation.setEffectType(effectType);
			relation.setTarget(new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSet.getPseudoRows()));
			TTable table = modelManager.getTable(stmt, columnName);
			if (table != null) {
				if (modelManager.getModel(table) instanceof Table) {
					Table tableModel = (Table) modelManager.getModel(table);
					if (tableModel != null) {
						TableColumn columnModel = modelFactory.createTableColumn(tableModel, columnName, false);
						relation.addSource(new TableColumnRelationElement(columnModel, columnName.getLocation()));
					}
				} else if (modelManager.getModel(table) instanceof QueryTable) {
					ResultColumn resultColumn = (ResultColumn) modelManager.getModel(columnName.getSourceColumn());
					if (resultColumn != null) {
						relation.addSource(new ResultColumnRelationElement(resultColumn, columnName.getLocation()));
					}
				}
			}
		}

		List<TParseTreeNode> functions = visitor.getFunctions();
		for (int j = 0; j < functions.size(); j++) {
			TParseTreeNode functionObj = functions.get(j);
			if (modelManager.getModel(functionObj) == null) {
				createFunction(functionObj);
			}
			if (modelManager.getModel(functionObj) instanceof Function) {

				RecordSetRelation relation = modelFactory.createRecordSetRelation();
				relation.setEffectType(effectType);
				relation.setTarget(new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSet.getPseudoRows()));

				if (functionObj instanceof TFunctionCall) {
					ResultColumn resultColumn = (ResultColumn) modelManager
							.getModel(((TFunctionCall) functionObj).getFunctionName());
					if (resultColumn != null) {
						ResultColumnRelationElement element = new ResultColumnRelationElement(resultColumn,
								((TFunctionCall) functionObj).getFunctionName().getLocation());
						relation.addSource(element);
					}
				}
				if (functionObj instanceof TCaseExpression) {
					ResultColumn resultColumn = (ResultColumn) modelManager
							.getModel(((TCaseExpression) functionObj).getWhenClauseItemList());
					if (resultColumn != null) {
						ResultColumnRelationElement element = new ResultColumnRelationElement(resultColumn);
						relation.addSource(element);
					}
				}
			}
		}
	}

	private void analyzeFilterCondtion(TExpression expr, EJoinType joinType, JoinClauseType joinClauseType,
			EffectType effectType) {
		if (expr == null) {
			return;
		}

		TCustomSqlStatement stmt = stmtStack.peek();

		columnsInExpr visitor = new columnsInExpr();
		expr.inOrderTraverse(visitor);

		List<TObjectName> objectNames = visitor.getObjectNames();
		List<TParseTreeNode> functions = visitor.getFunctions();

		ResultSet resultSet = (ResultSet) modelManager.getModel(stmt.getResultColumnList());
		if (resultSet != null) {
			ImpactRelation relation = modelFactory.createImpactRelation();
			relation.setEffectType(effectType);
			relation.setTarget(new PseudoRowsRelationElement<ResultSetPseudoRows>(resultSet.getPseudoRows()));

			for (int j = 0; j < objectNames.size(); j++) {
				TObjectName columnName = objectNames.get(j);
				if (columnName.getDbObjectType() == EDbObjectType.variable) {
					continue;
				}

				TTable table = modelManager.getTable(stmt, columnName);

				if (table == null && stmt.tables != null && stmt.tables.size() != 0
						&& stmt.getGsqlparser().getSqlEnv() == null
						&& !(isFunctionName(columnName) && isFromFunction(columnName))) {
					table = stmt.tables.getTable(0);
					System.err.println("guessing orphan column [" + columnName.toString() + "] table is:"
							+ stmt.tables.getTable(0).getFullNameWithAliasString());
				}

				if (table != null) {
					if (modelManager.getModel(table) instanceof Table) {
						Table tableModel = (Table) modelManager.getModel(table);
						if (tableModel != null) {
							TableColumn columnModel = modelFactory.createTableColumn(tableModel, columnName, false);
							TableColumnRelationElement element = new TableColumnRelationElement(columnModel,
									columnName.getLocation());
							relation.addSource(element);
						}
					} else if (modelManager.getModel(table) instanceof QueryTable) {
						if (table.getSubquery() != null && table.getSubquery().isCombinedQuery()) {
							TSelectSqlStatement subquery = table.getSubquery();
							List<ResultSet> resultSets = new ArrayList<>();
							if (!subquery.getLeftStmt().isCombinedQuery()) {
								ResultSet sourceResultSet = (ResultSet) modelManager
										.getModel(subquery.getLeftStmt().getResultColumnList());
								resultSets.add(sourceResultSet);
							} else {
								ResultSet sourceResultSet = (ResultSet) modelManager.getModel(subquery.getLeftStmt());
								resultSets.add(sourceResultSet);
							}

							if (!subquery.getRightStmt().isCombinedQuery()) {
								ResultSet sourceResultSet = (ResultSet) modelManager
										.getModel(subquery.getRightStmt().getResultColumnList());
								resultSets.add(sourceResultSet);
							} else {
								ResultSet sourceResultSet = (ResultSet) modelManager.getModel(subquery.getRightStmt());
								resultSets.add(sourceResultSet);
							}

							for (ResultSet sourceResultSet : resultSets) {
								if (sourceResultSet != null && columnName.getSourceColumn() != null) {
									for (int k = 0; k < sourceResultSet.getColumns().size(); k++) {
										if (sourceResultSet.getColumns().get(k).getName()
												.equalsIgnoreCase(columnName.getSourceColumn().getColumnNameOnly())) {
											List<TObjectName> starLinkColumns = sourceResultSet.getColumns().get(k)
													.getStarLinkColumns();
											if (!starLinkColumns.isEmpty()) {
												for (int x = 0; x < starLinkColumns.size(); x++) {
													if (starLinkColumns.get(x).getColumnNameOnly()
															.equalsIgnoreCase(columnName.getColumnNameOnly())) {
														ResultColumn column = modelFactory.createResultColumn(
																sourceResultSet, starLinkColumns.get(x), true);
														relation.addSource(new ResultColumnRelationElement(column));
														break;
													}
												}
											} else {
												relation.addSource(new ResultColumnRelationElement(
														sourceResultSet.getColumns().get(k)));
											}
										}
									}
								}
							}
						} else {
							ResultColumn resultColumn = (ResultColumn) modelManager
									.getModel(columnName.getSourceColumn());
							if (resultColumn != null) {
								List<TObjectName> starLinkColumns = resultColumn.getStarLinkColumns();
								if (!starLinkColumns.isEmpty()) {
									for (int x = 0; x < starLinkColumns.size(); x++) {
										if (starLinkColumns.get(x).getColumnNameOnly()
												.equalsIgnoreCase(columnName.getColumnNameOnly())) {
											ResultColumn column = modelFactory.createResultColumn(
													resultColumn.getResultSet(), starLinkColumns.get(x), true);
											relation.addSource(new ResultColumnRelationElement(column));
											break;
										}
									}

								} else {
									ResultColumnRelationElement element = new ResultColumnRelationElement(resultColumn,
											columnName.getLocation());
									relation.addSource(element);
								}
							}
						}
					}
				}
			}

			for (int j = 0; j < functions.size(); j++) {
				TParseTreeNode functionObj = functions.get(j);
				if (modelManager.getModel(functionObj) == null) {
					createFunction(functionObj);
				}
				if (modelManager.getModel(functionObj) instanceof Function) {
					if (functionObj instanceof TFunctionCall) {
						ResultColumn resultColumn = (ResultColumn) modelManager
								.getModel(((TFunctionCall) functionObj).getFunctionName());
						if (resultColumn != null) {
							ResultColumnRelationElement element = new ResultColumnRelationElement(resultColumn,
									((TFunctionCall) functionObj).getFunctionName().getLocation());
							relation.addSource(element);
						}
					}
					if (functionObj instanceof TCaseExpression) {
						ResultColumn resultColumn = (ResultColumn) modelManager
								.getModel(((TCaseExpression) functionObj).getWhenClauseItemList());
						if (resultColumn != null) {
							ResultColumnRelationElement element = new ResultColumnRelationElement(resultColumn);
							relation.addSource(element);
						}
					}
				}
			}

			// TResultColumnList columns = stmt.getResultColumnList();
			// if (columns != null) {
			// for (int i = 0; i < columns.size(); i++) {
			// TResultColumn column = columns.getResultColumn(i);
			//
			// AbstractRelation relation;
			// if (isAggregateFunction(column.getExpr().getFunctionCall())) {
			// relation = modelFactory.createRecordSetRelation();
			// relation.setEffectType(effectType);
			// relation.setFunction(column.getExpr().getFunctionCall().getFunctionName().toString());
			// relation.setTarget(new ResultColumnRelationElement((ResultColumn)
			// modelManager.getModel(column)));
			// ((RecordSetRelation) relation)
			// .setAggregateFunction(column.getExpr().getFunctionCall().getFunctionName().toString());
			// } else {
			// relation = modelFactory.createImpactRelation();
			// relation.setEffectType(effectType);
			// if (column.getExpr().getFunctionCall() != null) {
			// relation.setFunction(column.getExpr().getFunctionCall().getFunctionName().toString());
			// } else if (column.getExpr().getCaseExpression() != null) {
			// relation.setFunction("case-when");
			// }
			// if (column.getExpr().getExpressionType() ==
			// EExpressionType.assignment_t) {
			// relation.setTarget(new ResultColumnRelationElement((ResultColumn)
			// modelManager
			// .getModel(column.getExpr().getLeftOperand().getObjectOperand())));
			// } else {
			// relation.setTarget(
			// new ResultColumnRelationElement((ResultColumn)
			// modelManager.getModel(column)));
			// }
			// }
			//
			// for (int j = 0; j < objectNames.size(); j++) {
			// TObjectName columnName = objectNames.get(j);
			// if (columnName.getDbObjectType() == EDbObjectType.variable) {
			// continue;
			// }
			//
			// TTable table = modelManager.getTable(stmt, columnName);
			// if (table != null) {
			// if (modelManager.getModel(table) instanceof Table) {
			// Table tableModel = (Table) modelManager.getModel(table);
			// if (tableModel != null) {
			// TableColumn columnModel =
			// modelFactory.createTableColumn(tableModel, columnName);
			// TableColumnRelationElement element = new
			// TableColumnRelationElement(columnModel,
			// columnName.getLocation());
			// relation.addSource(element);
			// }
			// } else if (modelManager.getModel(table) instanceof QueryTable) {
			// ResultColumn resultColumn = (ResultColumn) modelManager
			// .getModel(columnName.getSourceColumn());
			// if (resultColumn != null) {
			// ResultColumnRelationElement element = new
			// ResultColumnRelationElement(resultColumn,
			// columnName.getLocation());
			// relation.addSource(element);
			// }
			// }
			// }
			// }
			//
			// for (int j = 0; j < functions.size(); j++) {
			// TParseTreeNode functionObj = functions.get(j);
			// if (modelManager.getModel(functionObj) == null) {
			// createFunction(functionObj);
			// }
			// if (modelManager.getModel(functionObj) instanceof Function) {
			// if (functionObj instanceof TFunctionCall) {
			// ResultColumn resultColumn = (ResultColumn) modelManager
			// .getModel(((TFunctionCall) functionObj).getFunctionName());
			// if (resultColumn != null) {
			// ResultColumnRelationElement element = new
			// ResultColumnRelationElement(resultColumn,
			// ((TFunctionCall) functionObj).getFunctionName().getLocation());
			// relation.addSource(element);
			// }
			// }
			// if (functionObj instanceof TCaseExpression) {
			// ResultColumn resultColumn = (ResultColumn) modelManager
			// .getModel(((TCaseExpression)
			// functionObj).getWhenClauseItemList());
			// if (resultColumn != null) {
			// ResultColumnRelationElement element = new
			// ResultColumnRelationElement(resultColumn);
			// relation.addSource(element);
			// }
			// }
			// }
			// }
			// }
			//
			// }
		}

		if (isShowJoin()) {
			joinInExpr joinVisitor = new joinInExpr(joinType, joinClauseType, effectType);
			expr.inOrderTraverse(joinVisitor);
		}
	}

	public void dispose() {
		ModelBindingManager.remove();
	}

	class columnsInExpr implements IExpressionVisitor {

		private List<TConstant> constants = new ArrayList<TConstant>();
		private List<TObjectName> objectNames = new ArrayList<TObjectName>();
		private List<TParseTreeNode> functions = new ArrayList<TParseTreeNode>();
		private List<TSelectSqlStatement> subquerys = new ArrayList<TSelectSqlStatement>();
		private boolean skipFunction = false;

		public void setSkipFunction(boolean skipFunction) {
			this.skipFunction = skipFunction;
		}

		public List<TParseTreeNode> getFunctions() {
			return functions;
		}
		
		public List<TSelectSqlStatement> getSubquerys() {
			return subquerys;
		}

		public List<TConstant> getConstants() {
			return constants;
		}

		public List<TObjectName> getObjectNames() {
			return objectNames;
		}

		@Override
		public boolean exprVisit(TParseTreeNode pNode, boolean isLeafNode) {
			TExpression lcexpr = (TExpression) pNode;
			if (lcexpr.getExpressionType() == EExpressionType.simple_constant_t) {
				if (lcexpr.getConstantOperand() != null) {
					constants.add(lcexpr.getConstantOperand());
				}
			} else if (lcexpr.getExpressionType() == EExpressionType.simple_object_name_t) {
				if (lcexpr.getObjectOperand() != null
						&& !(isFunctionName(lcexpr.getObjectOperand()) && isFromFunction(lcexpr.getObjectOperand()))) {
					objectNames.add(lcexpr.getObjectOperand());
				}
			} else if (lcexpr.getExpressionType() == EExpressionType.between_t) {
				if (lcexpr.getBetweenOperand() != null && lcexpr.getBetweenOperand().getObjectOperand() != null) {
					objectNames.add(lcexpr.getBetweenOperand().getObjectOperand());
				}
			} else if (lcexpr.getExpressionType() == EExpressionType.function_t) {
				TFunctionCall func = lcexpr.getFunctionCall();
				if (skipFunction) {
					if (func.getArgs() != null) {
						for (int k = 0; k < func.getArgs().size(); k++) {
							TExpression expr = func.getArgs().getExpression(k);
							if (expr != null)
								expr.inOrderTraverse(this);
						}
					}

					if (func.getTrimArgument() != null) {
						TTrimArgument args = func.getTrimArgument();
						TExpression expr = args.getStringExpression();
						if (expr != null) {
							expr.inOrderTraverse(this);
						}
						expr = args.getTrimCharacter();
						if (expr != null) {
							expr.inOrderTraverse(this);
						}
					}

					if (func.getAgainstExpr() != null) {
						func.getAgainstExpr().inOrderTraverse(this);
					}
					if (func.getBetweenExpr() != null) {
						func.getBetweenExpr().inOrderTraverse(this);
					}
					if (func.getExpr1() != null) {
						func.getExpr1().inOrderTraverse(this);
					}
					if (func.getExpr2() != null) {
						func.getExpr2().inOrderTraverse(this);
					}
					if (func.getExpr3() != null) {
						func.getExpr3().inOrderTraverse(this);
					}
					if (func.getParameter() != null) {
						func.getParameter().inOrderTraverse(this);
					}
				} else {
					functions.add(func);
				}

			} else if (lcexpr.getExpressionType() == EExpressionType.case_t) {
				TCaseExpression expr = lcexpr.getCaseExpression();
				if (skipFunction) {
					TExpression defaultExpr = expr.getElse_expr();
					if (defaultExpr != null) {
						defaultExpr.inOrderTraverse(this);
					}
					TWhenClauseItemList list = expr.getWhenClauseItemList();
					for (int i = 0; i < list.size(); i++) {
						TWhenClauseItem element = (TWhenClauseItem) list.getElement(i);
						(((TWhenClauseItem) element).getReturn_expr()).inOrderTraverse(this);

					}
				} else {
					functions.add(expr);
				}
			} else if (lcexpr.getSubQuery() != null) {
				TSelectSqlStatement select = lcexpr.getSubQuery();
				analyzeSelectStmt(select);
				subquerys.add(select);
				//inOrderTraverse(select, this);
				return false;
			}
			return true;
		}
	}

	class joinInExpr implements IExpressionVisitor {

		private EJoinType joinType;
		private JoinClauseType joinClauseType;
		private EffectType effectType;

		public joinInExpr(EJoinType joinType, JoinClauseType joinClauseType, EffectType effectType) {
			this.joinType = joinType;
			this.joinClauseType = joinClauseType;
			this.effectType = effectType;
		}

		boolean is_compare_condition(EExpressionType t) {
			return ((t == EExpressionType.simple_comparison_t) || (t == EExpressionType.group_comparison_t)
					|| (t == EExpressionType.in_t) || (t == EExpressionType.pattern_matching_t)
					|| (t == EExpressionType.left_join_t) || (t == EExpressionType.right_join_t));
		}

		@Override
		public boolean exprVisit(TParseTreeNode pNode, boolean isLeafNode) {
			TExpression expr = (TExpression) pNode;
			if (is_compare_condition(expr.getExpressionType())) {
				TExpression leftExpr = expr.getLeftOperand();
				columnsInExpr leftVisitor = new columnsInExpr();
				leftExpr.inOrderTraverse(leftVisitor);
				List<TObjectName> leftObjectNames = leftVisitor.getObjectNames();

				TExpression rightExpr = expr.getRightOperand();
				columnsInExpr rightVisitor = new columnsInExpr();
				rightExpr.inOrderTraverse(rightVisitor);
				List<TObjectName> rightObjectNames = rightVisitor.getObjectNames();

				if (!leftObjectNames.isEmpty() && !rightObjectNames.isEmpty()) {
					TCustomSqlStatement stmt = stmtStack.peek();

					for (int i = 0; i < leftObjectNames.size(); i++) {
						TObjectName leftObjectName = leftObjectNames.get(i);

						if (leftObjectName.getDbObjectType() == EDbObjectType.variable) {
							continue;
						}

						TTable leftTable = modelManager.getTable(stmt, leftObjectName);

						if (leftTable == null) {
							leftTable = leftObjectName.getSourceTable();
						}

						if (leftTable == null) {
							leftTable = modelManager.guessTable(stmt, leftObjectName);
						}

						if (leftTable != null) {
							for (int j = 0; j < rightObjectNames.size(); j++) {
								JoinRelation joinRelation = modelFactory.createJoinRelation();
								joinRelation.setEffectType(effectType);
								if (joinType != null) {
									joinRelation.setJoinType(joinType);
								} else {
									if (expr.getLeftOperand().isOracleOuterJoin()) {
										joinRelation.setJoinType(EJoinType.right);
									} else if (expr.getRightOperand().isOracleOuterJoin()) {
										joinRelation.setJoinType(EJoinType.left);
									} else if (expr.getExpressionType() == EExpressionType.left_join_t) {
										joinRelation.setJoinType(EJoinType.left);
									} else if (expr.getExpressionType() == EExpressionType.right_join_t) {
										joinRelation.setJoinType(EJoinType.right);
									} else {
										joinRelation.setJoinType(EJoinType.inner);
									}
								}

								joinRelation.setJoinClauseType(joinClauseType);
								joinRelation.setJoinCondition(expr.toScript());

								if (modelManager.getModel(leftTable) instanceof Table) {
									Table tableModel = (Table) modelManager.getModel(leftTable);
									if (tableModel != null) {
										TableColumn columnModel = modelFactory.createTableColumn(tableModel,
												leftObjectName, false);
										joinRelation.addSource(new TableColumnRelationElement(columnModel));
									}
								} else if (modelManager.getModel(leftTable) instanceof QueryTable) {
									if (leftObjectName.getSourceColumn() != null) {
										ResultColumn resultColumn = (ResultColumn) modelManager
												.getModel(leftObjectName.getSourceColumn());
										if (resultColumn != null) {
											joinRelation.addSource(new ResultColumnRelationElement(resultColumn));
										}
									} else {
										QueryTable table = (QueryTable) modelManager.getModel(leftTable);
										ResultColumn resultColumn = matchResultColumn(table.getColumns(),
												leftObjectName);
										if (resultColumn != null) {
											joinRelation.setTarget(new ResultColumnRelationElement(resultColumn));
										}
									}
								}

								TObjectName rightObjectName = rightObjectNames.get(j);

								if (rightObjectName.getDbObjectType() == EDbObjectType.variable) {
									continue;
								}

								TTable rightTable = modelManager.getTable(stmt, rightObjectName);
								if (rightTable == null) {
									rightTable = rightObjectName.getSourceTable();
								}

								if (rightTable == null) {
									rightTable = modelManager.guessTable(stmt, rightObjectName);
								}

								if (modelManager.getModel(rightTable) instanceof Table) {
									Table tableModel = (Table) modelManager.getModel(rightTable);
									if (tableModel != null) {
										TableColumn columnModel = modelFactory.createTableColumn(tableModel,
												rightObjectName, false);
										joinRelation.setTarget(new TableColumnRelationElement(columnModel));
									}
								} else if (modelManager.getModel(rightTable) instanceof QueryTable) {
									if (rightObjectName.getSourceColumn() != null) {
										ResultColumn resultColumn = (ResultColumn) modelManager
												.getModel(rightObjectName.getSourceColumn());
										if (resultColumn != null) {
											joinRelation.setTarget(new ResultColumnRelationElement(resultColumn));
										}
									} else {
										QueryTable table = (QueryTable) modelManager.getModel(rightTable);
										ResultColumn resultColumn = matchResultColumn(table.getColumns(),
												rightObjectName);
										if (resultColumn != null) {
											joinRelation.setTarget(new ResultColumnRelationElement(resultColumn));
										}
									}
								}
							}
						}
					}
				}
			}
			return true;
		}
	}

	public static String getVersion() {
		return "1.4.0";
	}

	public static String getReleaseDate() {
		return "2020-10-08";
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println(
					"Usage: java DataFlowAnalyzer [/f <path_to_sql_file>] [/d <path_to_directory_includes_sql_files>] [/s [/text]] [/traceView] [/t <database type>] [/o <output file path>][/version]");
			System.out.println("/f: Option, specify the sql file path to analyze fdd relation.");
			System.out.println("/d: Option, specify the sql directory path to analyze fdd relation.");
			System.out.println("/j: Option, analyze the join relation.");
			System.out.println("/s: Option, simple output, ignore the intermediate results.");
			System.out.println("/i: Option, ignore all result sets.");
			System.out.println("/traceView: Option, analyze the source tables of views.");
			System.out.println("/text: Option, print the plain text format output.");
			System.out.println(
					"/t: Option, set the database type. Support oracle, mysql, mssql, db2, netezza, teradata, informix, sybase, postgresql, hive, greenplum and redshift, the default type is oracle");
			System.out.println("/o: Option, write the output stream to the specified file.");
			System.out.println("/log: Option, generate a dataflow.log file to log information.");
			return;
		}

		File sqlFiles = null;

		List<String> argList = Arrays.asList(args);
		
		if(argList.indexOf("/version")!=-1){
			System.out.println("Version: "+DataFlowAnalyzer.getVersion());
			System.out.println("Release Date: "+DataFlowAnalyzer.getReleaseDate());
			return;
		}

		if (argList.indexOf("/f") != -1 && argList.size() > argList.indexOf("/f") + 1) {
			sqlFiles = new File(args[argList.indexOf("/f") + 1]);
			if (!sqlFiles.exists() || !sqlFiles.isFile()) {
				System.out.println(sqlFiles + " is not a valid file.");
				return;
			}
		} else if (argList.indexOf("/d") != -1 && argList.size() > argList.indexOf("/d") + 1) {
			sqlFiles = new File(args[argList.indexOf("/d") + 1]);
			if (!sqlFiles.exists() || !sqlFiles.isDirectory()) {
				System.out.println(sqlFiles + " is not a valid directory.");
				return;
			}
		} else {
			System.out.println("Please specify a sql file path or directory path to analyze dlineage.");
			return;
		}

		EDbVendor vendor = EDbVendor.dbvoracle;

		int index = argList.indexOf("/t");

		if (index != -1 && args.length > index + 1) {
			vendor = TGSqlParser.getDBVendorByName(args[index + 1]);
		}

		String outputFile = null;

		index = argList.indexOf("/o");

		if (index != -1 && args.length > index + 1) {
			outputFile = args[index + 1];
		}

		FileOutputStream writer = null;
		if (outputFile != null) {
			try {
				writer = new FileOutputStream(outputFile);
				System.setOut(new PrintStream(writer));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		boolean simple = argList.indexOf("/s") != -1;
		boolean ignoreResultSets = argList.indexOf("/i") != -1;
		boolean showJoin = argList.indexOf("/j") != -1;
		boolean textFormat = false;
		if (simple) {
			textFormat = argList.indexOf("/text") != -1;
		}
		
		boolean traceView = argList.indexOf("/traceView") != -1;
		if(traceView){
			simple = true;
		}

		DataFlowAnalyzer dlineage = new DataFlowAnalyzer(sqlFiles, vendor, simple);

		dlineage.setShowJoin(showJoin);
		dlineage.setIgnoreRecordSet(ignoreResultSets);

		if (simple) {
			dlineage.setTextFormat(textFormat);
		}

		StringBuffer errorBuffer = new StringBuffer();
		String result = dlineage.generateDataFlow(errorBuffer);
		
		if(traceView){
			result = dlineage.traceView();
		}

		if (result != null) {
			System.out.println(result);

			if (writer != null && result.length() < 1024 * 1024) {
				System.err.println(result);
			}
		}

		try {
			if (writer != null) {
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		boolean log = argList.indexOf("/log") != -1;
		PrintStream pw = null;
		ByteArrayOutputStream sw = null;
		PrintStream systemSteam = System.err;

		try {
			sw = new ByteArrayOutputStream();
			pw = new PrintStream(sw);
			System.setErr(pw);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (errorBuffer.length() > 0) {
			System.err.println("Error log:\n" + errorBuffer);
		}

		if (sw != null) {
			String errorMessage = sw.toString().trim();
			if (errorMessage.length() > 0) {
				if (log) {
					try {
						pw = new PrintStream(new File(".", "dataflow.log"));
						pw.print(errorMessage);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}

				System.setErr(systemSteam);
				System.err.println(errorMessage);
			}
		}
	}
	
	public String traceView() {
		StringBuilder buffer = new StringBuilder();
		dataflow dataflow = this.getDataFlow();
		Map<table, Set<table>> traceViewMap = new LinkedHashMap<>();
		if (dataflow != null && dataflow.getViews()!=null) {
			List<relation> relations = dataflow.getRelations();
			Map<String, table> viewMap = new HashMap<>();
			Map<String, table> tableMap = new HashMap<>();
			for (table view : dataflow.getViews()) {
				viewMap.put(view.getId(), view);
				tableMap.put(view.getId(), view);
			}
			for (table table : dataflow.getTables()) {
				tableMap.put(table.getId(), table);
			}
			for (relation relation : relations) {
				if(!RelationType.fdd.name().equals(relation.getType())){
					continue;
				}
				String parentId = relation.getTarget().getParent_id();
				if(viewMap.containsKey(parentId)){
					traceViewMap.putIfAbsent(viewMap.get(parentId), new LinkedHashSet<>());
					for(sourceColumn sourceColumn:relation.getSources()){
						traceViewMap.get(viewMap.get(parentId)).add(tableMap.get(sourceColumn.getParent_id()));
					}
				}
			}
			
			Map<table, Set<table>> viewTableMap = new LinkedHashMap<>();
			for(table view: traceViewMap.keySet()){
				Set<table> tables = new LinkedHashSet<>();
				traverseViewSourceTables(tables, view, traceViewMap);
				viewTableMap.put(view, tables);
			}
			
			for(table view: viewTableMap.keySet()){
				buffer.append(view.getFullName());
				for(table table: viewTableMap.get(view)){
					buffer.append(",").append(table.getFullName());
				}
				buffer.append(System.getProperty("line.separator"));
			}
		}
		return buffer.toString().trim();
	}

	private void traverseViewSourceTables(Set<table> tables, table view, Map<table, Set<table>> traceViewMap) {
		Set<table> sourceTables = traceViewMap.get(view);
		for(table sourceTable: sourceTables){
			if(sourceTable.isTable()){
				tables.add(sourceTable);
			}
			else if(sourceTable.isView()){
				traverseViewSourceTables(tables, sourceTable, traceViewMap);
			}
		}
	}

	protected static List<SqlInfo> convertSQL(String json) {
        List<SqlInfo> sqlInfos = new ArrayList<>();
        JSONArray sqlContents = JSONArray.parseArray(json);
        for (int j = 0; j < sqlContents.size(); j++) {
            JSONObject sqlContent = sqlContents.getJSONObject(j);
            String sql = sqlContent.getString("sql");
            String fileName = sqlContent.getString("fileName");
            if (sql != null && sql.trim().startsWith("{")) {
                JSONObject queryObject = JSON.parseObject(sql);
                JSONArray querys = queryObject.getJSONArray("queries");
                if (querys != null) {
                    for (int i = 0; i < querys.size(); i++) {
                        JSONObject object = querys.getJSONObject(i);
                        SqlInfo info = new SqlInfo();
                        info.setSql(object.toJSONString());
                        info.setFileName(fileName);
                        info.setOriginIndex(i);
                        sqlInfos.add(info);
                    }
                } else {
                    SqlInfo info = new SqlInfo();
                    info.setSql(queryObject.toJSONString());
                    info.setFileName(fileName);
                    info.setOriginIndex(0);
                    sqlInfos.add(info);
                }
            } else if (sql != null) {
                SqlInfo info = new SqlInfo();
                info.setSql(sql);
                info.setFileName(fileName);
                info.setOriginIndex(0);
                sqlInfos.add(info);
            }
        }
        return sqlInfos;
    }

	private void setTextFormat(boolean textFormat) {
		this.textFormat = textFormat;
	}

	public boolean isFunctionName(TObjectName object) {
		if (object == null || object.getGsqlparser() == null)
			return false;
		try {
			EDbVendor vendor = object.getGsqlparser().getDbVendor();
			if (vendor == EDbVendor.dbvteradata) {
				boolean result = TERADATA_BUILTIN_FUNCTIONS.contains(object.toString());
				if (result) {
					return true;
				}
			}

			List<String> versions = functionChecker.getAvailableDbVersions(vendor);
			if (versions != null && versions.size() > 0) {
				for (int i = 0; i < versions.size(); i++) {
					boolean result = functionChecker.isBuiltInFunction(object.toString(),
							object.getGsqlparser().getDbVendor(), versions.get(i));
					if (result) {
						return result;
					}
				}

				// boolean result =
				// TERADATA_BUILTIN_FUNCTIONS.contains(object.toString());
				// if (result) {
				// return true;
				// }
			}
		} catch (Exception e) {
		}

		return false;
	}

	public boolean isKeyword(TObjectName object) {
		if (object == null || object.getGsqlparser() == null)
			return false;
		try {
			EDbVendor vendor = object.getGsqlparser().getDbVendor();

			List<String> versions = keywordChecker.getAvailableDbVersions(vendor);
			if (versions != null && versions.size() > 0) {
				for (int i = 0; i < versions.size(); i++) {
					boolean result = keywordChecker.isKeyword(object.toString(), object.getGsqlparser().getDbVendor(),
							versions.get(i), false);
					if (result) {
						return result;
					}
				}
			}
		} catch (Exception e) {
		}

		return false;
	}

	public boolean isAggregateFunction(TFunctionCall func) {
		if (func == null)
			return false;
		return Arrays
				.asList(new String[] { "AVG", "COUNT", "MAX", "MIN", "SUM", "COLLECT", "CORR", "COVAR_POP",
						"COVAR_SAMP", "CUME_DIST", "DENSE_RANK", "FIRST", "GROUP_ID", "GROUPING", "GROUPING_ID", "LAST",
						"LISTAGG", "MEDIAN", "PERCENT_RANK", "PERCENTILE_CONT", "PERCENTILE_DISC", "RANK",
						"STATS_BINOMIAL_TEST", "STATS_CROSSTAB", "STATS_F_TEST", "STATS_KS_TEST", "STATS_MODE",
						"STATS_MW_TEST", "STATS_ONE_WAY_ANOVA", "STATS_WSR_TEST", "STDDEV", "STDDEV_POP", "STDDEV_SAMP",
						"SYS_XMLAGG", "VAR_ POP", "VAR_ SAMP", "VARI ANCE", "XMLAGG" })
				.contains(func.getFunctionName().toString().toUpperCase());
	}
}
