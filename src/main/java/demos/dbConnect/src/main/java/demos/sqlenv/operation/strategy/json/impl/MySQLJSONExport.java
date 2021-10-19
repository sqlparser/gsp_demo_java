package demos.sqlenv.operation.strategy.json.impl;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.ESQLDataObjectType;
import demos.sqlenv.TSQLDataSource;
import demos.sqlenv.metadata.Metadata;
import demos.sqlenv.metadata.model.Query;
import demos.sqlenv.operation.strategy.json.AbstractJSONExport;
import gudusoft.gsqlparser.sqlenv.util.SQLFileUtil;
import gudusoft.gsqlparser.util.Identifier;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * CemB
 */
public class MySQLJSONExport extends AbstractJSONExport {

	private static final String SHOW_CREATE_VIEW = "show create view %s.%s";
	private static final String SHOW_CREATE_TRIGGER = "show create trigger %s.%s";
	private static final String SHOW_CREATE_FUNCTION = "show create function %s.%s";
	private static final String SHOW_CREATE_PROCEDURE = "show create procedure %s.%s";

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
		return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "schema.sql").replace("%s",
				new Identifier(EDbVendor.dbvmysql, ESQLDataObjectType.dotCatalog, database).getNormalizeIdentifier());
	}

	@Override
	protected String queriesSql(TSQLDataSource datasource, String database, String catalog) {
		return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "query.sql").replace("%s",
				new Identifier(EDbVendor.dbvmysql, ESQLDataObjectType.dotCatalog, database).getNormalizeIdentifier());
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
				dbNames.add("`" + resultSet.getString(1).trim() + "`");
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

	@Override
	protected void exportQueries(TSQLDataSource datasource, String database, String catalog,
								 Metadata exportMetadataModel) {
		Statement statement = null;
		ResultSet resultSet = null;
		Connection connection = null;
		try {
			connection = datasource.getConnection();
			connection.setCatalog(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, catalog)
					.getNormalizeIdentifier());

			String queriesSql = queriesSql(datasource, database, catalog);
			String[] querySqls = queriesSql.trim().split("\\s*;\\s*");

			List<Query> queries = new ArrayList<Query>();

			for (int i = 0; i < querySqls.length; i++) {
				try {
					statement = connection.createStatement();
					resultSet = statement.executeQuery(querySqls[i].replace("%s", new Identifier(EDbVendor.dbvmysql, ESQLDataObjectType.dotSchema, database)
							.getNormalizeIdentifier()));
					Map<String, List<Query>> queryGroups = new LinkedHashMap<String, List<Query>>();
					while (resultSet.next()) {
						Query query = new Query();
						query.setSourceCode(detectResult(1, resultSet));
						query.setType(detectResult(2, resultSet));
						query.setName(detectResult(3, resultSet));
						query.setGroupName(detectResult(4, resultSet));
						query.setDatabase(detectResult(5, resultSet));
						String schema = detectResult(6, resultSet);
						if (!acceptSchema(datasource, query.getDatabase(), schema)) {
							continue;
						}
						query.setSchema(schema);
						connection.setCatalog(query.getDatabase());
						updateSourceCode(connection, query, exportMetadataModel);

						String key = query.getSchema() + "." + query.getName();
						if (!queryGroups.containsKey(key)) {
							queryGroups.put(key, new ArrayList<Query>());
						}
						queryGroups.get(key).add(query);
					}
					Iterator<String> keyIter = queryGroups.keySet().iterator();
					while (keyIter.hasNext()) {
						List<Query> queryGroup = queryGroups.get(keyIter.next());
						if (needMergeQuery()) {
							Query query = mergeQuery(datasource, queryGroup);
							if (acceptDataSourceQuery(datasource, query)) {
								queries.add(query);
							}
						} else {
							for (Query query : queryGroup) {
								if (acceptDataSourceQuery(datasource, query)) {
									queries.add(query);
								}
							}
						}
					}
				} catch (Exception e) {
					if (e.getMessage() != null) {
						exportMetadataModel.appendErrorMessage(e.getMessage());
					}
					e.printStackTrace();
				} finally {
					close(resultSet);
					close(statement);
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

	private void updateSourceCode(Connection connection, Query query, Metadata exportMetadataModel){
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			if ("view".equals(query.getType())) {
				resultSet = statement.executeQuery(String.format(SHOW_CREATE_VIEW, query.getSchema(), query.getName()));
				while (resultSet.next()) {
					query.setSourceCode(detectResult(2, resultSet));
				}
			} else if ("trigger".equals(query.getType())) {
				resultSet = statement
						.executeQuery(String.format(SHOW_CREATE_TRIGGER, query.getSchema(), query.getName()));
				while (resultSet.next()) {
					query.setSourceCode(detectResult(3, resultSet));
				}
			} else if ("function".equals(query.getType())) {
				resultSet = statement
						.executeQuery(String.format(SHOW_CREATE_FUNCTION, query.getSchema(), query.getName()));
				while (resultSet.next()) {
					query.setSourceCode(detectResult(3, resultSet));
				}
			} else if ("procedure".equals(query.getType())) {
				resultSet = statement
						.executeQuery(String.format(SHOW_CREATE_PROCEDURE, query.getSchema(), query.getName()));
				while (resultSet.next()) {
					query.setSourceCode(detectResult(3, resultSet));
				}
			} else {
				throw new UnsupportedOperationException("unsupport type " + query.getType());
			}
		} catch (Exception e) {
			if (e.getMessage() != null) {
				exportMetadataModel.appendErrorMessage(e.getMessage());
			}
			e.printStackTrace();
		} finally {
			close(resultSet);
			close(statement);
		}
	}
}
