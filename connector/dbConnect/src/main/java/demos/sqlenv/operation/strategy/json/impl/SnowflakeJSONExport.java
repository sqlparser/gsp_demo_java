package demos.sqlenv.operation.strategy.json.impl;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.ESQLDataObjectType;
import demos.sqlenv.TSQLDataSource;
import demos.sqlenv.metadata.Metadata;
import demos.sqlenv.metadata.model.Query;
import demos.sqlenv.operation.strategy.json.AbstractJSONExport;
import gudusoft.gsqlparser.sqlenv.util.SQLFileUtil;
import gudusoft.gsqlparser.util.Identifier;
import gudusoft.gsqlparser.util.SQLUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CemB
 */
public class SnowflakeJSONExport extends AbstractJSONExport {
	public static final Logger logger = Logger.getLogger(gudusoft.gsqlparser.sqlenv.operation.strategy.json.impl.SnowflakeJSONExport.class.getName());

	@Override
	protected String dbLinksSql(TSQLDataSource datasource, String database) {
		return null;
	}

	@Override
	protected String synonymsSql(TSQLDataSource datasource, String database, String catalog) {
		return null;
	}

	@Override
	protected String databasesSql(TSQLDataSource datasource) {
		return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "database.sql");
	}

	@Override
	protected String tablesSql(TSQLDataSource datasource, String database, String catalog) {
		return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "schema.sql").replace("%s", new Identifier(EDbVendor.dbvsnowflake, ESQLDataObjectType.dotCatalog, database).getNormalizeIdentifier());
	}

	@Override
	protected String queriesSql(TSQLDataSource datasource, String database, String catalog) {
		return null;
	}

	@Override
	protected String queriesPSql(TSQLDataSource datasource, String database, String catalog) {
		return null;
	}
	
	protected List<String> exportDatabases(TSQLDataSource datasource, Statement statement,
			Metadata exportMetadataModel) {
		try {
			String databasesSql = databasesSql(datasource);
			ResultSet resultSet = statement.executeQuery(databasesSql);
			List<String> dbNames = new ArrayList<String>();
			while (resultSet.next()) {
				dbNames.add("\"" + resultSet.getString(2).trim() + "\"");
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

	protected void exportQueries(TSQLDataSource datasource, String database, String catalog, Metadata exportMetadataModel) {
		Statement statement = null;
		Connection connection = null;
		try {
			connection = datasource.getConnection();
			connection.setCatalog(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, catalog)
					.getNormalizeIdentifier());
			statement = connection.createStatement();
			statement.executeQuery(String.format("USE DATABASE %s",
					new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, database)
							.getNormalizeIdentifier()));
			ResultSet schemasSet = statement.executeQuery("SHOW SCHEMAS");
			List<String> databaseSchemaNames = new ArrayList<String>();
			while (schemasSet.next()) {
				String schemaName = "\"" + schemasSet.getString(2) + "\"";
				String databaseSchema = database + "." + schemaName;
				if (acceptSchema(datasource, database, schemaName)) {
					databaseSchemaNames.add(databaseSchema);
				}
			}
			close(schemasSet);

			Map<String, List<Query>> queryGroups = new LinkedHashMap<String, List<Query>>();

			for (String databaseSchema : databaseSchemaNames) {
				ResultSet resultSet = null;
				
				try {
					String databaseName = databaseSchema.split("\\.")[0];
					String schemaName = databaseSchema.split("\\.")[1];
					resultSet = statement.executeQuery(String.format("SHOW VIEWS IN %s.%s",
							new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, databaseName)
									.getNormalizeIdentifier(),
							new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, schemaName)
									.getNormalizeIdentifier()));
					while (resultSet.next()) {
						String viewName = "\"" + resultSet.getString(2) + "\"";
						try {
							String sourceCode = generateViewDDL(datasource, viewName, databaseSchema, exportMetadataModel);
							if (!SQLUtil.isEmpty(sourceCode)) {
								Query query = new Query();
								query.setSourceCode(sourceCode);
								query.setType("view");
								query.setName(viewName);
								query.setDatabase(databaseName);
								query.setSchema(schemaName);
								if (!acceptSchema(datasource, query.getDatabase(), query.getSchema())) {
									continue;
								}
								String key = query.getSchema() + "." + query.getName();
								if (!queryGroups.containsKey(key)) {
									queryGroups.put(key, new ArrayList<Query>());
								}
								queryGroups.get(key).add(query);
							}
						} catch (Exception e) {
							if (e.getMessage() != null) {
								exportMetadataModel.appendErrorMessage(e.getMessage());
							}
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					if (e.getMessage() != null) {
						exportMetadataModel.appendErrorMessage(e.getMessage());
					}
					e.printStackTrace();
				} finally {
					close(resultSet);
				}

				try {
					String databaseName1 = databaseSchema.split("\\.")[0];
					String schemaName1 = databaseSchema.split("\\.")[1];
					resultSet = statement.executeQuery(String.format("SHOW PROCEDURES IN %s.%s",
							new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, databaseName1)
									.getNormalizeIdentifier(),
							new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, schemaName1)
									.getNormalizeIdentifier()));
					while (resultSet.next()) {
						String schemaName = resultSet.getString("schema_name");
						if (schemaName == null || !new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema,
								databaseSchema.split("\\.")[1])
										.equals(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema,
												schemaName))) {
							continue;
						}

						String arguments = resultSet.getString("arguments");

						int returnIndex = arguments.toUpperCase().indexOf("RETURN");
						if (returnIndex != -1) {
							arguments = arguments.substring(0, returnIndex).trim();
						}

						String procedureName = arguments;

						try {
							String sourceCode = generateProcedureDDL(datasource, procedureName, databaseSchema, exportMetadataModel);
							if (!SQLUtil.isEmpty(sourceCode)) {
								Query query = new Query();
								query.setSourceCode(sourceCode);
								query.setType("procedure");
								query.setName("\""
										+ (procedureName.indexOf("(") == -1 ? procedureName
												: procedureName.substring(0, procedureName.indexOf("(")).trim())
										+ "\"");
								query.setDatabase(database);
								query.setSchema(schemaName);
								if (!acceptSchema(datasource, query.getDatabase(), query.getSchema())) {
									continue;
								}
								String key = query.getSchema() + "." + query.getName();
								if (!queryGroups.containsKey(key)) {
									queryGroups.put(key, new ArrayList<Query>());
								}
								queryGroups.get(key).add(query);
							}
						} catch (Exception e) {
							if (e.getMessage() != null) {
								exportMetadataModel.appendErrorMessage(e.getMessage());
							}
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					if (e.getMessage() != null) {
						exportMetadataModel.appendErrorMessage(e.getMessage());
					}
					e.printStackTrace();
				} finally {
					close(resultSet);
				}

				try {
					String databaseName1 = databaseSchema.split("\\.")[0];
					String schemaName1 = databaseSchema.split("\\.")[1];
					resultSet = statement.executeQuery(String.format("SHOW FUNCTIONS IN %s.%s",
							new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, databaseName1)
									.getNormalizeIdentifier(),
							new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, schemaName1)
									.getNormalizeIdentifier()));
					while (resultSet.next()) {
						String schemaName = resultSet.getString("schema_name");
						if (schemaName == null || !new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema,
								databaseSchema.split("\\.")[1])
										.equals(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema,
												schemaName))) {
							continue;
						}

						String arguments = resultSet.getString("arguments");

						int returnIndex = arguments.toUpperCase().indexOf("RETURN");
						if (returnIndex != -1) {
							arguments = arguments.substring(0, returnIndex).trim();
						}

						String functionName = arguments;

						try {
							String sourceCode = generateFunctionDDL(datasource, functionName, databaseSchema, exportMetadataModel);
							if (!SQLUtil.isEmpty(sourceCode)) {
								Query query = new Query();
								query.setSourceCode(sourceCode);
								query.setType("function");
								query.setName("\"" + (functionName.indexOf("(") == -1 ? functionName
										: functionName.substring(0, functionName.indexOf("(")).trim()) + "\"");
								query.setDatabase(database);
								query.setSchema(schemaName);
								if (!acceptSchema(datasource, query.getDatabase(), query.getSchema())) {
									continue;
								}
								String key = query.getSchema() + "." + query.getName();
								if (!queryGroups.containsKey(key)) {
									queryGroups.put(key, new ArrayList<Query>());
								}
								queryGroups.get(key).add(query);
							}
						} catch (Exception e) {
							if (e.getMessage() != null) {
								exportMetadataModel.appendErrorMessage(e.getMessage());
							}
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					if (e.getMessage() != null) {
						exportMetadataModel.appendErrorMessage(e.getMessage());
					}
					e.printStackTrace();
				} finally {
					close(resultSet);
				}
			}

			List<Query> queries = new ArrayList<Query>();
			Iterator<String> keyIter = queryGroups.keySet().iterator();
			while (keyIter.hasNext()) {
				List<Query> queryGroup = queryGroups.get(keyIter.next());
				if (needMergeQuery()) {
					Query query = mergeQuery(datasource, queryGroup);
					if(acceptDataSourceQuery(datasource,query)){
						queries.add(query);
					}
				} else {
					for(Query query:queryGroup){
						if(acceptDataSourceQuery(datasource,query)){
							queries.add(query);
						}
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

	protected String generateViewDDL(TSQLDataSource datasource, String viewName, String databaseSchema, Metadata exportMetadataModel) {
		Statement stmt = null;
		ResultSet rs = null;
		String database = databaseSchema.split("\\.")[0];
		String schemaName = databaseSchema.split("\\.")[1];
		try {
			stmt = datasource.getConnection().createStatement();
			rs = stmt.executeQuery(String.format("SELECT GET_DDL('VIEW', '%s.%s.%s')",
					new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, database)
							.getNormalizeIdentifier(),
					new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, schemaName)
							.getNormalizeIdentifier(),
					new Identifier(datasource.getVendor(), ESQLDataObjectType.dotTable, viewName)
							.getNormalizeIdentifier()));
			if (!rs.next()) {
				return null;
			}
			String createDDL = rs.getString(1).replaceFirst(viewName,
					String.format("%s.%s.%s", database, schemaName, viewName));
			return createDDL;
		} catch (Exception e) {
			if (e.getMessage() != null) {
				exportMetadataModel.appendErrorMessage(e.getMessage());
			}
			logger.log(Level.SEVERE, "Can't get view " + database + "." + schemaName + "." + viewName + " ddl.", e);
			return null;
		} finally {
			close(rs);
			close(stmt);
		}
	}

	protected String generateProcedureDDL(TSQLDataSource datasource, String procedureName, String databaseSchema, Metadata exportMetadataModel) {
		Statement stmt = null;
		ResultSet rs = null;
		String database = databaseSchema.split("\\.")[0];
		String schemaName = databaseSchema.split("\\.")[1];
		try {

			stmt = datasource.getConnection().createStatement();
			rs = stmt.executeQuery(String.format("SELECT GET_DDL('PROCEDURE', '%s.%s.%s')",
					new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, database)
							.getNormalizeIdentifier(),
					new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, schemaName)
							.getNormalizeIdentifier(),
					new Identifier(datasource.getVendor(), ESQLDataObjectType.dotProcedure, procedureName)
							.getNormalizeIdentifier()));
			if (!rs.next()) {
				return null;
			}

			String createDDL = rs.getString(1);
			createDDL = String.format(
					"CREATE OR REPLACE PROCEDURE %s.%s.%s" + createDDL.substring(createDDL.indexOf("(")), database,
					schemaName, procedureName.substring(0, procedureName.indexOf("(")).trim());
			return createDDL;
		} catch (Exception e) {
			if (e.getMessage() != null) {
				exportMetadataModel.appendErrorMessage(e.getMessage());
			}
			logger.log(Level.SEVERE,
					"Can't get procedure " + database + "." + schemaName + "." + procedureName + " ddl.", e);
			return null;
		} finally {
			close(rs);
			close(stmt);
		}
	}

	protected String generateFunctionDDL(TSQLDataSource datasource, String functionName, String databaseSchema, Metadata exportMetadataModel) {
		Statement stmt = null;
		ResultSet rs = null;
		String database = databaseSchema.split("\\.")[0];
		String schemaName = databaseSchema.split("\\.")[1];
		try {

			stmt = datasource.getConnection().createStatement();
			rs = stmt.executeQuery(String.format("SELECT GET_DDL('FUNCTION', '%s.%s.%s')",
					new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, database)
							.getNormalizeIdentifier(),
					new Identifier(datasource.getVendor(), ESQLDataObjectType.dotSchema, schemaName)
							.getNormalizeIdentifier(),
					new Identifier(datasource.getVendor(), ESQLDataObjectType.dotFunction, functionName)
							.getNormalizeIdentifier()));
			if (!rs.next()) {
				return null;
			}
			String createDDL = rs.getString(1);
			createDDL = String.format(
					"CREATE OR REPLACE FUNCTION %s.%s.%s" + createDDL.substring(createDDL.indexOf("(")), database,
					schemaName, functionName.substring(0, functionName.indexOf("(")).trim());

			return createDDL;
		} catch (Exception e) {
			if (e.getMessage() != null) {
				exportMetadataModel.appendErrorMessage(e.getMessage());
			}
			logger.log(Level.SEVERE, "Can't get function " + database + "." + schemaName + "." + functionName + " ddl.",
					e);
			return null;
		} finally {
			close(rs);
			close(stmt);
		}
	}
}
