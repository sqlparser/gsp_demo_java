package demos.sqlenv.operation.strategy.json.impl;

import gudusoft.gsqlparser.sqlenv.*;
import demos.sqlenv.constant.SystemConstant;
import demos.sqlenv.metadata.Metadata;
import demos.sqlenv.metadata.model.Column;
import demos.sqlenv.metadata.model.Database;
import demos.sqlenv.metadata.model.Query;
import demos.sqlenv.metadata.model.Table;
import demos.sqlenv.operation.DbOperationService;
import gudusoft.gsqlparser.util.CollectionUtil;
import gudusoft.gsqlparser.util.Identifier;
import gudusoft.gsqlparser.util.SQLUtil;
import gudusoft.gsqlparser.util.json.JSON;
import demos.sqlenv.TSQLDataSource;
import demos.sqlenv.DbSchemaSQLDataSource;
import demos.sqlenv.DatabaseSQLDataSource;
import demos.sqlenv.SchemaSQLDataSource;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class HiveMetadataJSONExport implements DbOperationService<String> {

	private static final String SHOW_CREATE_VIEW = "show create table %s.%s";

	@Override
	public String operate(TSQLDataSource datasource) {
		long startTime = System.currentTimeMillis();
		Statement statement = null;
		try {
			try {
				statement = datasource.getConnection().createStatement();
			} finally {
			}
			TSQLDataSource dataSource = datasource;

			Metadata exportMetadataModel = new Metadata();
			exportMetadataModel.setCreatedBy(SystemConstant.name + " " + SystemConstant.version);
			exportMetadataModel.setDialect(datasource.getDbType());
			exportMetadataModel.setUserAccountId(dataSource.getAccount());
			exportMetadataModel.setExportId(UUID.randomUUID().toString());
			exportMetadataModel.setPhysicalInstance(dataSource.getHostName());

			List<String> databases = exportDatabases(datasource, statement, exportMetadataModel);
			if (databases != null) {
				exportMetadataModel.appendDatabases(new ArrayList<Database>());
				for (String database : databases) {
					if (!acceptDatabase(datasource, database)) {
						continue;
					}
					if (!SQLUtil.isEmpty(database)) {
						exportTables(datasource, database, database, exportMetadataModel);
						exportQueries(datasource, database, database, exportMetadataModel);
					}
				}
			}
			exportMetadataModel.setExportTime(System.currentTimeMillis() - startTime);
			return JSON.toJSONString(exportMetadataModel).replace((char) 160, (char) 32);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(statement);
		}
		return null;
	}

	protected void exportTables(TSQLDataSource datasource, String database, String catalog,
			Metadata exportMetadataModel) {
		Statement statement = null;
		Connection connection = null;
		try {
			connection = datasource.getConnection();
			connection.setSchema(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, catalog)
					.getNormalizeIdentifier());
			statement = connection.createStatement();
			Database sqlDatabase = new Database();
			sqlDatabase.setName(database);
			List<String> tableNames = exportTables(datasource, statement, exportMetadataModel);
			List<String> viewNames = exportViews(datasource, statement, exportMetadataModel);
			List<Table> tables = new CopyOnWriteArrayList<Table>();
			for (String tableName : tableNames) {
				Map<String, String> columnMap = exportTableColumns(datasource, statement, tableName,
						exportMetadataModel);
				Table table = new Table();
				table.setDatabase(database);
				table.setSchema(catalog);
				table.setName(tableName);
				table.setIsView(String.valueOf(viewNames.contains(tableName)));
				List<Column> columns = new ArrayList<Column>();
				for (String columnName : columnMap.keySet()) {
					Column column = new Column();
					column.setName(columnName);
					column.setDataType(columnMap.get(columnName));
					column.setComment("");
					columns.add(column);
				}
				table.setColumns(columns);
				tables.add(table);
			}
			sqlDatabase.setTables(tables);
			appendDatabase(exportMetadataModel, datasource, sqlDatabase);
		} catch (Exception e) {
			if (e.getMessage() != null) {
				exportMetadataModel.appendErrorMessage(e.getMessage());
			}
			e.printStackTrace();
		} finally {
			close(statement);
		}
	}

	protected void exportQueries(TSQLDataSource datasource, String database, String catalog,
			Metadata exportMetadataModel) {
		Statement statement = null;
		Connection connection = null;
		try {
			connection = datasource.getConnection();
			connection.setSchema(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, catalog)
					.getNormalizeIdentifier());
			statement = connection.createStatement();
			List<String> viewNames = exportViews(datasource, statement, exportMetadataModel);
			List<Query> queries = new ArrayList<Query>();
			for (String viewName : viewNames) {
				ResultSet resultSet = statement.executeQuery(String.format(SHOW_CREATE_VIEW, catalog, viewName));
				Map<String, List<Query>> queryGroups = new LinkedHashMap<String, List<Query>>();
				while (resultSet.next()) {
					Query query = new Query();
					query.setSourceCode(detectResult(1, resultSet));
					query.setType("view");
					query.setName(viewName);
					query.setGroupName("");
					query.setDatabase(database);
					query.setSchema(catalog);
					String key = query.getSchema() + "." + query.getName();
					if (!queryGroups.containsKey(key)) {
						queryGroups.put(key, new ArrayList<Query>());
					}
					queryGroups.get(key).add(query);
				}
				Iterator<String> keyIter = queryGroups.keySet().iterator();
				while (keyIter.hasNext()) {
					List<Query> queryGroup = queryGroups.get(keyIter.next());
					Query query = mergeQuery(datasource, queryGroup);
					if (acceptDataSourceQuery(datasource, query)) {
						queries.add(query);
					}
				}
			}
			exportMetadataModel.appendQueries(queries);
		} catch (Exception e) {
			if (e.getMessage() != null) {
				exportMetadataModel.appendErrorMessage(e.getMessage());
			}
			e.printStackTrace();
		} finally {
			close(statement);
		}
	}
	
	protected Query mergeQuery(TSQLDataSource datasource, List<Query> queryGroup) {
		if (queryGroup == null || queryGroup.isEmpty())
			return null;
		Query query = queryGroup.get(0);
		for (int i = 1; i < queryGroup.size(); i++) {
			query.setSourceCode(query.getSourceCode() + queryGroup.get(i).getSourceCode());
		}
		return query;
	}

	protected boolean acceptDataSourceQuery(TSQLDataSource datasource, Query query) {
		if (datasource.getExtractedStoredProcedures().length == 0 && datasource.getExtractedViews().length == 0)
			return true;
		if (datasource.getExtractedViews().length > 0 && query.getType().toLowerCase().indexOf("view") != -1) {
			for (String view : datasource.getExtractedViews()) {
				String[] segments = view.split("\\.");
				boolean result = acceptDataSourceQuery(datasource, query, segments);
				if (result) {
					return result;
				}
			}
		}

		if (datasource.getExtractedStoredProcedures().length > 0
				&& (query.getType().toLowerCase().indexOf("procedure") != -1
						|| query.getType().toLowerCase().indexOf("trigger") != -1
						|| query.getType().toLowerCase().indexOf("function") != -1)) {
			for (String procedures : datasource.getExtractedStoredProcedures()) {
				String[] segments = procedures.split("\\.");
				boolean result = acceptDataSourceQuery(datasource, query, segments);
				if (result) {
					return result;
				}
			}
		}

		return false;
	}

	private boolean acceptDataSourceQuery(TSQLDataSource datasource, Query query, String[] segments) {
		if (datasource instanceof DbSchemaSQLDataSource && (segments.length == 2 || segments.length == 3)) {
			String database;
			String schema;
			String table;
			if (segments.length == 3) {
				database = segments[0].trim();
				schema = segments[1].trim();
				table = segments[2].trim();
			} else {
				database = segments[0].trim();
				schema = segments[0].trim();
				table = segments[1].trim();
			}

			String queryDatabase = query.getDatabase();
			String querySchema = query.getSchema();
			String queryName = query.getName();

			if (matchIdentifier(datasource, database, queryDatabase, ESQLDataObjectType.dotCatalog)
					&& matchIdentifier(datasource, schema, querySchema, ESQLDataObjectType.dotSchema)
					&& matchIdentifier(datasource, table, queryName, ESQLDataObjectType.dotTable)) {
				return true;
			}
		} else if (datasource instanceof DatabaseSQLDataSource && (segments.length == 2 || segments.length == 3)) {
			String database;
			String table;
			if (segments.length == 3) {
				database = segments[0].trim();
				table = segments[2].trim();
			} else {
				database = segments[0].trim();
				table = segments[1].trim();
			}

			String queryDatabase = query.getDatabase();
			String queryName = query.getName();

			if (matchIdentifier(datasource, database, queryDatabase, ESQLDataObjectType.dotCatalog)
					&& matchIdentifier(datasource, table, queryName, ESQLDataObjectType.dotTable)) {
				return true;
			}
		} else if (datasource instanceof SchemaSQLDataSource && (segments.length == 2 || segments.length == 3)) {
			String schema;
			String table;
			if (segments.length == 3) {
				schema = segments[1];
				table = segments[2];
			} else {
				schema = segments[0];
				table = segments[1];
			}

			String querySchema = query.getDatabase();
			String queryName = query.getName();

			if (matchIdentifier(datasource, schema, querySchema, ESQLDataObjectType.dotSchema)
					&& matchIdentifier(datasource, table, queryName, ESQLDataObjectType.dotTable)) {
				return true;
			}
		}

		return false;
	}

	private boolean matchIdentifier(TSQLDataSource datasource, String identifierPattern, String identifier,
			ESQLDataObjectType dataObjectType) {
		if (identifierPattern.endsWith("*")) {
			int index = identifierPattern.indexOf("*");
			if (index == 0) {
				return true;
			}

			String patternIdentifier = new Identifier(datasource.getVendor(), dataObjectType, identifierPattern)
					.getNormalizeIdentifier();
			String normalizeIdentifier = new Identifier(datasource.getVendor(), dataObjectType, identifier)
					.getNormalizeIdentifier();

			if (patternIdentifier.substring(0, index)
					.equals(normalizeIdentifier.length() > index ? normalizeIdentifier.substring(0, index)
							: normalizeIdentifier)) {
				return true;
			}
		} else {
			if (new Identifier(datasource.getVendor(), dataObjectType, identifierPattern)
					.equals(new Identifier(datasource.getVendor(), dataObjectType, identifier))) {
				return true;
			}
		}

		return false;
	}

	protected void appendDatabase(Metadata exportMetadataModel, TSQLDataSource datasource, Database sqlDatabase) {
		Database database = null;

		for (Database item : exportMetadataModel.getDatabases()) {
			if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, item.getName()).equals(
					new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, sqlDatabase.getName()))) {
				database = item;
				break;
			}
		}

		if (database == null) {
			exportMetadataModel.getDatabases().add(sqlDatabase);
		} else {
			if (sqlDatabase.getTables() != null) {
				database.getTables().addAll(sqlDatabase.getTables());
			}
		}
	}

	protected boolean acceptDatabase(TSQLDataSource datasource, String databaseName) {
		DatabaseSQLDataSource ds = (DatabaseSQLDataSource) datasource;
		if (ds.isSystemDatabase(databaseName)) {
			return false;
		}
		if (!CollectionUtil.isEmpty(ds.getExtractedDatabases())) {
			for (String database : ds.getExtractedDatabases()) {
				if ("*".equals(database)) {
					return true;
				}
				if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, database)
						.equals(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, databaseName))) {
					return true;
				}
			}
			return false;
		} else if (!CollectionUtil.isEmpty(ds.getExcludedDatabases())) {
			for (String database : ds.getExcludedDatabases()) {
				if ("*".equals(database)) {
					return false;
				}
				if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, database)
						.equals(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, databaseName))) {
					return false;
				}
			}
			return true;
		} else {
			return true;
		}
	}

	protected List<String> exportDatabases(TSQLDataSource datasource, Statement statement,
			Metadata exportMetadataModel) {
		try {
			String databasesSql = "show databases";
			ResultSet resultSet = statement.executeQuery(databasesSql);
			List<String> dbNames = new LinkedList<String>();
			while (resultSet.next()) {
				dbNames.add(resultSet.getString(1).trim());
			}
			return dbNames;
		} catch (Exception e) {
			if (e.getMessage() != null) {
				exportMetadataModel.appendErrorMessage(e.getMessage());
			}
			e.printStackTrace();
			return null;
		}
	}

	protected List<String> exportViews(TSQLDataSource datasource, Statement statement, Metadata exportMetadataModel) {
		try {
			String sql = "show views";
			ResultSet resultSet = statement.executeQuery(sql);
			List<String> viewNames = new LinkedList<String>();
			while (resultSet.next()) {
				viewNames.add(resultSet.getString(1).trim());
			}
			return viewNames;
		} catch (Exception e) {
			if (e.getMessage() != null) {
				exportMetadataModel.appendErrorMessage(e.getMessage());
			}
			e.printStackTrace();
			return null;
		}
	}

	protected List<String> exportTables(TSQLDataSource datasource, Statement statement, Metadata exportMetadataModel) {
		try {
			String sql = "show tables";
			ResultSet resultSet = statement.executeQuery(sql);
			List<String> viewNames = new LinkedList<String>();
			while (resultSet.next()) {
				viewNames.add(resultSet.getString(1).trim());
			}
			return viewNames;
		} catch (Exception e) {
			if (e.getMessage() != null) {
				exportMetadataModel.appendErrorMessage(e.getMessage());
			}
			e.printStackTrace();
			return null;
		}
	}

	protected Map<String, String> exportTableColumns(TSQLDataSource datasource, Statement statement, String tableName,
			Metadata exportMetadataModel) {
		try {
			String databasesSql = "describe " + tableName;
			ResultSet resultSet = statement.executeQuery(databasesSql);
			Map<String, String> tableColumns = new LinkedHashMap<String, String>();
			while (resultSet.next()) {
				tableColumns.put(resultSet.getString(1).trim(), resultSet.getString(2).trim());
			}
			return tableColumns;
		} catch (Exception e) {
			if (e.getMessage() != null) {
				exportMetadataModel.appendErrorMessage(e.getMessage());
			}
			e.printStackTrace();
			return null;
		}
	}

	protected void close(Object colseable) {
		try {
			if (colseable instanceof Closeable) {
				((Closeable) colseable).close();
			}
			if (colseable instanceof Statement) {
				((Statement) colseable).close();
			}
			if (colseable instanceof ResultSet) {
				((ResultSet) colseable).close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected String detectResult(int index, ResultSet resultSet) throws SQLException {
		String var = resultSet.getString(index);
		if (!SQLUtil.isEmpty(var)) {
			return var.trim();
		}
		return "";
	}

}
