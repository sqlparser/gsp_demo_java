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
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * CemB
 */
public class NetezzaJSONExport extends AbstractJSONExport {

	@Override
	protected String dbLinksSql(TSQLDataSource datasource, String database) {
		return null;
	}

	@Override
	protected String synonymsSql(TSQLDataSource datasource, String database, String schema) {
		return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "synonyms.sql").replace("%s",
				new Identifier(EDbVendor.dbvnetezza, ESQLDataObjectType.dotCatalog, database).getNormalizeIdentifier());
	}

	@Override
	protected String databasesSql(TSQLDataSource datasource) {
		return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "database.sql");
	}

	@Override
	protected String tablesSql(TSQLDataSource datasource, String database, String catalog) {
		return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "schema.sql").replace("%s",
				new Identifier(EDbVendor.dbvnetezza, ESQLDataObjectType.dotCatalog, catalog).getNormalizeIdentifier());
	}

	@Override
	protected String queriesSql(TSQLDataSource datasource, String database, String catalog) {
		return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "query_view.sql").replace("%s",
				new Identifier(EDbVendor.dbvnetezza, ESQLDataObjectType.dotCatalog, catalog).getNormalizeIdentifier());
	}

	@Override
	protected String queriesPSql(TSQLDataSource datasource, String database, String catalog) {
		return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "query_procedure.sql").replace("%s",
				new Identifier(EDbVendor.dbvnetezza, ESQLDataObjectType.dotCatalog, catalog).getNormalizeIdentifier());
	}

	protected List<String> exportDatabases(TSQLDataSource datasource, Statement statement,
			Metadata exportMetadataModel) {
		try {
			String databasesSql = databasesSql(datasource);
			ResultSet resultSet = statement.executeQuery(databasesSql);
			List<String> dbNames = new ArrayList<String>();
			while (resultSet.next()) {
				dbNames.add("\"" + resultSet.getString(1).trim() + "\"");
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

	protected void exportQueries(TSQLDataSource datasource, String database, String catalog,
			Metadata exportMetadataModel) {
		Statement statement = null;
		ResultSet resultSet = null;
		Connection connection = null;
		try {
			connection = datasource.getConnection();
			if (enableSetCatalog()) {
				connection.setCatalog(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, catalog)
						.getNormalizeIdentifier());
			}
			String queriesSql = queriesSql(datasource, database, catalog);

			List<Query> queries = new ArrayList<Query>();

			String[] querySqls = queriesSql.trim().split("\\s*;\\s*");
			for (int i = 0; i < querySqls.length; i++) {
				try {
					statement = connection.createStatement();
					resultSet = statement.executeQuery(querySqls[i]);
					Map<String, List<Query>> queryGroups = new LinkedHashMap<String, List<Query>>();
					while (resultSet.next()) {
						Query query = new Query();
						query.setSourceCode(detectResult(1, resultSet));
						query.setType(detectResult(2, resultSet));
						query.setName(detectResult(3, resultSet));
						query.setGroupName(detectResult(4, resultSet));
						query.setDatabase(detectResult(5, resultSet));
						String schema = detectResult(6, resultSet);

						if (SQLUtil.isEmpty(query.getSourceCode())) {
							continue;
						}

						if (!acceptSchema(datasource, query.getDatabase(), schema)) {
							continue;
						}
						query.setSchema(schema);
						String key = query.getSchema() + "." + query.getName();
						if (!queryGroups.containsKey(key)) {
							queryGroups.put(key, new ArrayList<Query>());
						}
						queryGroups.get(key).add(query);
					}
					resultSet.close();
					statement.close();
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

			String queriespSql = queriesPSql(datasource, database, catalog);
			if (!SQLUtil.isEmpty(queriespSql)) {
				String[] querypSqls = queriespSql.trim().split("\\s*;\\s*");
				for (int i = 0; i < querypSqls.length; i++) {
					try {
					statement = connection.createStatement();
					resultSet = statement.executeQuery(querypSqls[i]);
					Map<String, List<Query>> queryGroups = new LinkedHashMap<String, List<Query>>();
					while (resultSet.next()) {
						Query query = new Query();
						query.setSourceCode(computeProcedureSource(datasource, resultSet));
						query.setType(detectResult(2, resultSet));
						query.setName(detectResult(3, resultSet));
						query.setGroupName(detectResult(4, resultSet));
						query.setDatabase(detectResult(5, resultSet));
						String schema = detectResult(6, resultSet);
						if (!acceptSchema(datasource, query.getDatabase(), schema)) {
							continue;
						}
						query.setSchema(schema);

						String key = query.getSchema() + "." + query.getName();
						if (!queryGroups.containsKey(key)) {
							queryGroups.put(key, new ArrayList<Query>());
						}
						queryGroups.get(key).add(query);
					}
					resultSet.close();
					statement.close();
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

	private String computeProcedureSource(TSQLDataSource datasource, ResultSet resultSet) throws SQLException {
		StringBuilder buffer = new StringBuilder();
		buffer.append("CREATE OR REPLACE PROCEDURE ");
		buffer.append(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotProcedure,
				detectResult("procedureName", resultSet)).getNormalizeIdentifier());
		buffer.append(" RETURNS ").append(detectResult("procedureReturn", resultSet));
		buffer.append(" LANGUAGE NZPLSQL AS\nBEGIN_PROC\n");
		buffer.append(detectResult("procedureSource", resultSet).trim());
		buffer.append("\nEND_PROC;");
		return buffer.toString();
	}
}
