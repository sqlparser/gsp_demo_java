package demos.columnDetail;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.IMetaDatabase;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.nodes.TAlterTableOption;
import gudusoft.gsqlparser.nodes.TColumnDefinition;
import gudusoft.gsqlparser.nodes.TColumnWithSortOrder;
import gudusoft.gsqlparser.nodes.TConstraint;
import gudusoft.gsqlparser.nodes.TObjectName;
import gudusoft.gsqlparser.nodes.TObjectNameList;
import gudusoft.gsqlparser.nodes.TPTNodeList;
import gudusoft.gsqlparser.nodes.TTable;
import gudusoft.gsqlparser.nodes.TTableList;
import gudusoft.gsqlparser.nodes.TTypeName;
import gudusoft.gsqlparser.stmt.TAlterTableStatement;
import gudusoft.gsqlparser.stmt.TCommentOnSqlStmt;
import gudusoft.gsqlparser.stmt.TCreateIndexSqlStatement;
import gudusoft.gsqlparser.stmt.TCreateTableSqlStatement;
import gudusoft.gsqlparser.stmt.TSelectSqlStatement;

public class ColumnDetail {
	private Map<TableMetaData, List<ColumnMetaData>> tableColumns = new HashMap<TableMetaData, List<ColumnMetaData>>();
	private List<ColumnMetaData> columnMetadatas = new ArrayList<ColumnMetaData>();

	public List<ColumnMetaData> getTableColumns() {
		return columnMetadatas;
	}

	class MetaDB implements IMetaDatabase {

		private String columns[][];

		public MetaDB(Map<TableMetaData, List<ColumnMetaData>> metaMap) {
			List<String[]> columnList = new ArrayList<String[]>();
			if (metaMap != null) {
				Iterator<TableMetaData> tableIter = metaMap.keySet().iterator();
				while (tableIter.hasNext()) {
					TableMetaData table = tableIter.next();
					List<ColumnMetaData> columnMetadatas = metaMap.get(table);
					for (int i = 0; i < columnMetadatas.size(); i++) {
						ColumnMetaData columnMetadata = columnMetadatas.get(i);
						String[] column = new String[5];
						column[0] = "";
						column[1] = columnMetadata.getCatalogName();
						column[2] = columnMetadata.getSchemaName();
						column[3] = columnMetadata.getTableName();
						column[4] = columnMetadata.getName();
						columnList.add(column);
					}
				}
			}
			columns = columnList.toArray(new String[columnList.size()][5]);
		}

		// = {
		// { "server", "db", "DW", "AcctInfo_PT", "ACCT_ID" },
		// { "server", "db", "DW", "ImSysInfo_BC", "ACCT_ID" },
		// { "server", "db", "DW", "AcctInfo_PT", "SystemOfRec" },
		// { "server", "db", "DW", "ImSysInfo_BC", "SystemOfRec" },
		// { "server", "db", "DW", "AcctInfo_PT", "OfficerCode" },
		// { "server", "db", "DW", "ImSysInfo_BC", "OpeningDate" }, };

		public boolean checkColumn(String server, String database,
				String schema, String table, String column) {
			boolean bServer, bDatabase, bSchema, bTable, bColumn, bRet = false;
			for (int i = 0; i < columns.length; i++) {
				if (strict) {
					if ((server == null) || (server.length() == 0)) {
						bServer = true;
					} else {
						bServer = columns[i][0].equalsIgnoreCase(server);
					}
					if (!bServer)
						continue;

					if ((database == null) || (database.length() == 0)) {
						bDatabase = true;
					} else {
						bDatabase = columns[i][1].equalsIgnoreCase(database);
					}
					if (!bDatabase)
						continue;

					if ((schema == null) || (schema.length() == 0)) {
						bSchema = true;
					} else {
						bSchema = columns[i][2].equalsIgnoreCase(schema);
					}

					if (!bSchema)
						continue;
				}

				bTable = columns[i][3].equalsIgnoreCase(table);
				if (!bTable)
					continue;

				bColumn = columns[i][4].equalsIgnoreCase(column);
				if (!bColumn)
					continue;

				bRet = true;
				break;

			}

			return bRet;
		}

	}

	private boolean strict = false;

	public ColumnDetail(File sqlFiles, EDbVendor vendor, boolean strict) {
		this.strict = strict;

		tableColumns.clear();

		File[] children = sqlFiles.listFiles();
		for (int i = 0; i < children.length; i++) {
			File child = children[i];
			if (child.isDirectory())
				continue;
			String content = getContent(child);
			String[] sqls = content.split(";\\s*\\n");
			for (int j = 0; j < sqls.length; j++) {
				TGSqlParser sqlparser = new TGSqlParser(vendor);
				sqlparser.sqltext = sqls[j].toUpperCase() + ";";
				checkDDL(sqlparser);
			}
		}

		MetaDB metaDB = new MetaDB(tableColumns);

		for (int i = 0; i < children.length; i++) {
			File child = children[i];
			if (child.isDirectory())
				continue;
			String content = getContent(child);
			String[] sqls = content.split(";\\s*\\n");
			for (int j = 0; j < sqls.length; j++) {
				TGSqlParser sqlparser = new TGSqlParser(vendor);
				// sqlparser.setMetaDatabase(metaDB);
				sqlparser.sqltext = sqls[j].toUpperCase() + ";";
				columnImpact(sqlparser);
			}
		}
	}

	private void columnImpact(TGSqlParser sqlparser) {
		try {
			int ret = sqlparser.parse();
			if (ret == 0) {
				TStatementList stmts = sqlparser.sqlstatements;
				for (int i = 0; i < stmts.size(); i++) {
					TCustomSqlStatement stmt = stmts.get(i);
					if (stmt instanceof TSelectSqlStatement) {
						columnImpact((TSelectSqlStatement) stmt);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void columnImpact(TSelectSqlStatement select) {
		if (select.getSetOperator() != TSelectSqlStatement.SET_OPERATOR_NONE) {
			columnImpact(select.getLeftStmt());
			columnImpact(select.getRightStmt());
		} else {
			TStatementList stmts = select.getStatements();
			if (stmts != null) {
				for (int i = 0; i < stmts.size(); i++) {
					TCustomSqlStatement stmt = stmts.get(i);
					if (stmt instanceof TSelectSqlStatement) {
						columnImpact((TSelectSqlStatement) stmt);
					}
				}
			}

			TTableList tables = select.tables;
			for (int i = 0; i < tables.size(); i++) {
				TTable table = tables.getTable(i);
				if (table.isBaseTable() || table.isLinkTable()) {
					TObjectNameList columns = table.getLinkedColumns();
					for (int j = 0; j < columns.size(); j++) {
						TObjectName column = columns.getObjectName(j);

						ColumnMetaData columnMetaData = new ColumnMetaData(
								strict);
						String columnName = column.getColumnNameOnly();
						if (columnName.startsWith(":"))
							continue;
						columnMetaData.setName(columnName);

						String tableName = table.getTableName()
								.getTableString();
						String tableSchema = table.getTableName()
								.getSchemaString();
						columnMetaData.setTableName(tableName);
						columnMetaData.setSchemaName(tableSchema);

						TableMetaData tableMetaData = new TableMetaData(strict);
						tableMetaData.setName(columnMetaData.getTableName());
						tableMetaData.setSchemaName(columnMetaData
								.getSchemaName());

						if (tableColumns.containsKey(tableMetaData)) {
							List<ColumnMetaData> list = tableColumns
									.get(tableMetaData);
							if (list.contains(columnMetaData)) {
								ColumnMetaData data = list.get(list
										.indexOf(columnMetaData));
								if (!columnMetadatas.contains(data))
									columnMetadatas.add(data);
							} else {
								columnMetadatas.add(columnMetaData);
							}
						} else {
							columnMetadatas.add(columnMetaData);
						}
					}
				}
			}
		}
	}

	private String getContent(File file) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
			byte[] tmp = new byte[4096];
			InputStream is = new BufferedInputStream(new FileInputStream(file));
			while (true) {
				int r = is.read(tmp);
				if (r == -1)
					break;
				out.write(tmp, 0, r);
			}
			byte[] bytes = out.toByteArray();
			is.close();
			out.close();
			String content = new String(bytes);
			return content.trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ColumnDetail(String sql, EDbVendor vendor) {
		tableColumns.clear();
		TGSqlParser sqlparser = new TGSqlParser(vendor);
		sqlparser.sqltext = sql.toUpperCase();
		checkDDL(sqlparser);
	}

	private void checkDDL(TGSqlParser sqlparser) {
		int ret = sqlparser.parse();
		if (ret == 0) {
			TStatementList stmts = sqlparser.sqlstatements;
			for (int i = 0; i < stmts.size(); i++) {
				TCustomSqlStatement stmt = stmts.get(i);
				parseStatement(stmt);
			}
		}
	}

	private void parseStatement(TCustomSqlStatement stmt) {
		if (stmt instanceof TCreateTableSqlStatement) {
			TCreateTableSqlStatement createTable = (TCreateTableSqlStatement) stmt;
			parseCreateTable(createTable);
		}
		if (stmt instanceof TCommentOnSqlStmt) {
			TCommentOnSqlStmt commentOn = (TCommentOnSqlStmt) stmt;
			parseCommentOn(commentOn);
		}
		if (stmt instanceof TAlterTableStatement) {
			TAlterTableStatement alterTable = (TAlterTableStatement) stmt;
			TableMetaData tableMetaData = new TableMetaData(strict);
			tableMetaData.setName(alterTable.getTableName().getTableString());
			tableMetaData.setSchemaName(alterTable.getTableName()
					.getSchemaString());
			if (!tableColumns.containsKey(tableMetaData)) {
				tableColumns
						.put(tableMetaData, new ArrayList<ColumnMetaData>());
			}
			parseAlterTable(alterTable, tableMetaData);
		}
		if (stmt instanceof TCreateIndexSqlStatement) {
			TCreateIndexSqlStatement createIndex = (TCreateIndexSqlStatement) stmt;
			parseCreateIndex(createIndex);
		}
	}

	private void parseCreateIndex(TCreateIndexSqlStatement createIndex) {
		switch (createIndex.getIndexType()) {
		case itUnique:
			// Can't get information from TCreateIndexSqlStatement
			break;
		default:
		}

	}

	private void parseAlterTable(TAlterTableStatement alterTable,
			TableMetaData tableMetaData) {
		for (int i = 0; i < alterTable.getAlterTableOptionList().size(); i++) {
			parseAlterTableOption(alterTable.getAlterTableOptionList()
					.getAlterTableOption(i), tableMetaData);
		}
	}

	private void parseAlterTableOption(TAlterTableOption alterTableOption,
			TableMetaData tableMetaData) {
		switch (alterTableOption.getOptionType()) {
		case AddColumn:
			for (int i = 0; i < alterTableOption.getColumnDefinitionList()
					.size(); i++) {
				parseColumnDefinition(alterTableOption
						.getColumnDefinitionList().getColumn(i), tableMetaData);
			}
			break;
		case ModifyColumn:
			for (int i = 0; i < alterTableOption.getColumnDefinitionList()
					.size(); i++) {
				parseColumnDefinition(alterTableOption
						.getColumnDefinitionList().getColumn(i), tableMetaData);
			}
			break;
		case AddConstraint:
			for (int i = 0; i < alterTableOption.getConstraintList().size(); i++) {
				parseTableConstraint(alterTableOption.getConstraintList()
						.getConstraint(i), tableMetaData);
			}
		default:

		}
	}

	private static boolean isNotEmpty(String str) {
		return str != null && str.trim().length() > 0;
	}

	private void parseCreateTable(TCreateTableSqlStatement createTable) {
		if (createTable.getTableName() != null) {
			String tableName = createTable.getTableName().getTableString();
			String tableSchema = createTable.getTableName().getSchemaString();
			TableMetaData tableMetaData = new TableMetaData(strict);
			tableMetaData.setName(tableName);
			tableMetaData.setSchemaName(tableSchema);
			if (!tableColumns.containsKey(tableMetaData)) {
				tableColumns
						.put(tableMetaData, new ArrayList<ColumnMetaData>());
			}
			if (createTable.getTableComment() != null) {
				tableMetaData.setComment(createTable.getTableComment()
						.toString());
			}
			if (createTable.getColumnList() != null) {
				for (int i = 0; i < createTable.getColumnList().size(); i++) {
					TColumnDefinition columnDef = createTable.getColumnList()
							.getColumn(i);
					parseColumnDefinition(columnDef, tableMetaData);
				}
			}

			if (createTable.getTableConstraints() != null) {
				for (int i = 0; i < createTable.getTableConstraints().size(); i++) {
					TConstraint constraint = createTable.getTableConstraints()
							.getConstraint(i);
					parseTableConstraint(constraint, tableMetaData);
				}
			}
		}
	}

	private void parseTableConstraint(TConstraint constraint,
			TableMetaData tableMetaData) {
		if (constraint.getColumnList() == null)
			return;
		switch (constraint.getConstraint_type()) {
		case primary_key:
			if (constraint.getColumnList().size() == 1) {
				setColumnMetaDataPrimaryKey(tableMetaData,
						constraint.getColumnList());
			}
			break;
		case unique:
			if (constraint.getColumnList().size() == 1) {
				setColumnMetaDataUnique(tableMetaData,
						constraint.getColumnList());
			}
			break;
		case foreign_key:
			if (constraint.getColumnList().size() == 1) {
				setColumnMetaDataForeignKey(tableMetaData,
						constraint.getColumnList());
			}
			break;
		default:
			break;
		}
	}

	private void setColumnMetaDataForeignKey(TableMetaData tableMetaData,
											 TPTNodeList<TColumnWithSortOrder> columnList) {
		for (int i = 0; i < columnList.size(); i++) {
			TObjectName object = columnList.getElement(i).getColumnName();
			ColumnMetaData columnMetaData = getColumnMetaData(tableMetaData,
					object);
			columnMetaData.setForeignKey(true);
		}
	}

	private void setColumnMetaDataUnique(TableMetaData tableMetaData,
										 TPTNodeList<TColumnWithSortOrder> columnList) {
		for (int i = 0; i < columnList.size(); i++) {
			TObjectName object = columnList.getElement(i).getColumnName();
			ColumnMetaData columnMetaData = getColumnMetaData(tableMetaData,
					object);
			columnMetaData.setUnique(true);
		}
	}

	private void setColumnMetaDataPrimaryKey(TableMetaData tableMetaData,
			TPTNodeList<TColumnWithSortOrder> columnList) {
		for (int i = 0; i < columnList.size(); i++) {
			TObjectName object = columnList.getElement(i).getColumnName();
			ColumnMetaData columnMetaData = getColumnMetaData(tableMetaData,
					object);
			columnMetaData.setPrimaryKey(true);
		}
	}

	private void parseColumnDefinition(TColumnDefinition columnDef,
			TableMetaData tableMetaData) {
		if (columnDef.getColumnName() != null) {
			TObjectName object = columnDef.getColumnName();
			ColumnMetaData columnMetaData = getColumnMetaData(tableMetaData,
					object);

			if (object.getCommentString() != null) {
				String columnComment = object.getCommentString().toString();
				columnMetaData.setComment(columnComment);
			}

			if (columnDef.getDefaultExpression() != null) {
				columnMetaData.setDefaultVlaue(columnDef.getDefaultExpression()
						.toString());
			}

			if (columnDef.getDatatype() != null) {
				TTypeName type = columnDef.getDatatype();
				String typeName = type.toString();
				int typeNameIndex = typeName.indexOf("(");
				if (typeNameIndex != -1)
					typeName = typeName.substring(0, typeNameIndex);
				columnMetaData.setTypeName(typeName);
				if (type.getScale() != null) {
					try {
						columnMetaData.setScale(Integer.parseInt(type
								.getScale().toString()));
					} catch (NumberFormatException e1) {
					}
				}
				if (type.getPrecision() != null) {
					try {
						columnMetaData.setPrecision(Integer.parseInt(type
								.getPrecision().toString()));
					} catch (NumberFormatException e) {
					}
				}
				if (type.getLength() != null) {
					try {
						columnMetaData.setColumnDisplaySize(Integer
								.parseInt(type.getLength().toString()));
					} catch (NumberFormatException e) {
					}
				}
			}

			if (columnDef.isNull()) {
				columnMetaData.setNull(true);
			}

			if (columnDef.getConstraints() != null) {
				for (int i = 0; i < columnDef.getConstraints().size(); i++) {
					TConstraint constraint = columnDef.getConstraints()
							.getConstraint(i);
					switch (constraint.getConstraint_type()) {
					case notnull:
						columnMetaData.setNotNull(true);
						break;
					case primary_key:
						columnMetaData.setPrimaryKey(true);
						break;
					case unique:
						columnMetaData.setUnique(true);
						break;
					case check:
						columnMetaData.setCheck(true);
						break;
					case foreign_key:
						columnMetaData.setForeignKey(true);
						break;
					case fake_auto_increment:
						columnMetaData.setAutoIncrement(true);
						break;
					case fake_comment:
						// Can't get comment information.
					default:
						break;
					}
				}
			}
		}
	}

	private ColumnMetaData getColumnMetaData(TableMetaData tableMetaData,
			TObjectName object) {
		ColumnMetaData columnMetaData = new ColumnMetaData(strict);
		String columnName = object.getColumnNameOnly();
		columnMetaData.setName(columnName);

		if (isNotEmpty(object.getTableString())) {
			columnMetaData.setTableName(object.getTableString());
		} else {
			columnMetaData.setTableName(tableMetaData.getName());
		}

		if (isNotEmpty(object.getSchemaString())) {
			columnMetaData.setSchemaName(object.getSchemaString());
		} else {
			columnMetaData.setSchemaName(tableMetaData.getSchemaName());
		}

		int index = tableColumns.get(tableMetaData).indexOf(columnMetaData);
		if (index != -1) {
			columnMetaData = tableColumns.get(tableMetaData).get(index);
		} else {
			tableColumns.get(tableMetaData).add(columnMetaData);
		}
		return columnMetaData;
	}

	private void parseCommentOn(TCommentOnSqlStmt commentOn) {
		if (commentOn.getDbObjType() == TObjectName.ttobjTable) {
			String tableName = commentOn.getObjectName().getPartString();
			String tableSchema = commentOn.getObjectName().getObjectString();
			TableMetaData tableMetaData = new TableMetaData(strict);
			tableMetaData.setName(tableName);
			tableMetaData.setSchemaName(tableSchema);
			if (!tableColumns.containsKey(tableMetaData)) {
				tableColumns
						.put(tableMetaData, new ArrayList<ColumnMetaData>());
			}
			tableMetaData.setComment(commentOn.getMessage().toString());
		} else if (commentOn.getDbObjType() == TObjectName.ttobjColumn) {
			ColumnMetaData columnMetaData = new ColumnMetaData(strict);
			String columnName = commentOn.getObjectName().getColumnNameOnly();
			columnMetaData.setName(columnName);

			if (isNotEmpty(commentOn.getObjectName().getTableString())) {
				columnMetaData.setTableName(commentOn.getObjectName()
						.getTableString());
			}

			if (isNotEmpty(commentOn.getObjectName().getSchemaString())) {
				columnMetaData.setSchemaName(commentOn.getObjectName()
						.getSchemaString());
			}

			TableMetaData tableMetaData = new TableMetaData(strict);
			tableMetaData.setName(columnMetaData.getTableName());
			tableMetaData.setSchemaName(columnMetaData.getSchemaName());
			if (!tableColumns.containsKey(tableMetaData)) {
				tableColumns
						.put(tableMetaData, new ArrayList<ColumnMetaData>());
			}
			int index = tableColumns.get(tableMetaData).indexOf(columnMetaData);
			if (index != -1) {
				tableColumns.get(tableMetaData).get(index)
						.setComment(commentOn.getMessage().toString());
			} else {
				columnMetaData.setComment(commentOn.getMessage().toString());
				tableColumns.get(tableMetaData).add(columnMetaData);
			}
		}
	}

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out
					.println("Usage: java ColumnDetail <path_to_directory_includes_sql_files> [/t <database type>] [/s]");
			System.out
					.println("/t: Option, set the database type. Support oracle, mysql, mssql and db2, the default type is oracle");
			System.out
					.println("/s: Option, set the strict match mode. It will match the catalog name and schema name.");

			return;
		}

		File sqlFiles = new File(args[0]);
		if (!sqlFiles.exists() || !sqlFiles.isDirectory()) {
			System.out.println(sqlFiles + " is not a valid directory.");
			return;
		}

		List<String> argList = Arrays.asList(args);

		EDbVendor vendor = EDbVendor.dbvoracle;

		int index = argList.indexOf("/t");

		if (index != -1 && args.length > index + 1) {
			if (args[index + 1].equalsIgnoreCase("mssql")) {
				vendor = EDbVendor.dbvmssql;
			} else if (args[index + 1].equalsIgnoreCase("db2")) {
				vendor = EDbVendor.dbvdb2;
			} else if (args[index + 1].equalsIgnoreCase("mysql")) {
				vendor = EDbVendor.dbvmysql;
			} else if (args[index + 1].equalsIgnoreCase("mssql")) {
				vendor = EDbVendor.dbvmssql;
			} else if (args[index + 1].equalsIgnoreCase("netezza")) {
				vendor = EDbVendor.dbvnetezza;
			} else if (args[index + 1].equalsIgnoreCase("teradata")) {
				vendor = EDbVendor.dbvteradata;
			}
		}

		boolean strict = argList.indexOf("/s") != -1;

		ColumnDetail parser = new ColumnDetail(sqlFiles, vendor, strict);

		try {
			JSONObject object = new JSONObject();
			JSONObject metaData = new JSONObject();
			object.put("meta-data", metaData);
			JSONArray columns = new JSONArray();
			metaData.put("columns", columns);

			List<ColumnMetaData> columnList = parser.getTableColumns();
			if (columnList != null) {
				for (int i = 0; i < columnList.size(); i++) {
					ColumnMetaData column = columnList.get(i);
					JSONObject columnObj = new JSONObject();
					columnObj.put(column.getName(), column);
					columns.add(columnObj);
				}
			}
			System.out.println(JSONObject.toJSONString(object, true).replace("\t", "  "));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
