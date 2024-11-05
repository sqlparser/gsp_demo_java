package demos.sqlenv.operation.strategy.json.impl;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.ESQLDataObjectType;
import demos.sqlenv.TSQLDataSource;
import demos.sqlenv.metadata.Metadata;
import demos.sqlenv.metadata.model.Database;
import demos.sqlenv.metadata.model.Query;
import demos.sqlenv.metadata.model.Sequence;
import demos.sqlenv.operation.strategy.json.AbstractJSONExport;
import gudusoft.gsqlparser.sqlenv.util.SQLFileUtil;
import gudusoft.gsqlparser.util.Identifier;
import gudusoft.gsqlparser.util.SQLUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * CemB
 */
public class OracleJSONExport extends AbstractJSONExport {

	@Override
	protected String dbLinksSql(TSQLDataSource datasource, String database) {
		return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "dblink.sql");
	}

	@Override
	protected String synonymsSql(TSQLDataSource datasource, String database, String catalog) {
		return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "synonyms.sql")
				.replace("%database",
						new Identifier(EDbVendor.dbvoracle, ESQLDataObjectType.dotCatalog, datasource.getDatabase())
								.getNormalizeIdentifier())
				.replace("%schema", new Identifier(EDbVendor.dbvoracle, ESQLDataObjectType.dotSchema, catalog)
						.getNormalizeIdentifier());
	}
	
	protected String sequencesSql(TSQLDataSource datasource, String database, String catalog) {
		return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "sequences.sql")
				.replace("%database",
						new Identifier(EDbVendor.dbvoracle, ESQLDataObjectType.dotCatalog, datasource.getDatabase())
								.getNormalizeIdentifier())
				.replace("%schema", new Identifier(EDbVendor.dbvoracle, ESQLDataObjectType.dotSchema, catalog)
						.getNormalizeIdentifier());
	}

	@Override
	protected String databasesSql(TSQLDataSource datasource) {
		return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "database.sql");
	}

	@Override
	protected String tablesSql(TSQLDataSource datasource, String database, String catalog) {
		return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "schema.sql")
				.replace("%database",
						new Identifier(EDbVendor.dbvoracle, ESQLDataObjectType.dotCatalog, datasource.getDatabase())
								.getNormalizeIdentifier())
				.replace("%schema", new Identifier(EDbVendor.dbvoracle, ESQLDataObjectType.dotSchema, catalog)
						.getNormalizeIdentifier());
	}

	@Override
	protected String queriesSql(TSQLDataSource datasource, String database, String catalog) {
		return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "query.sql")
				.replace("%database",
						new Identifier(EDbVendor.dbvoracle, ESQLDataObjectType.dotCatalog, datasource.getDatabase())
								.getNormalizeIdentifier())
				.replace("%schema", new Identifier(EDbVendor.dbvoracle, ESQLDataObjectType.dotSchema, catalog)
						.getNormalizeIdentifier());
	}

	@Override
	protected String queriesPSql(TSQLDataSource datasource, String database, String catalog) {
		return null;
	}

	protected Query mergeQuery(TSQLDataSource datasource, List<Query> queryGroup) {
		if (queryGroup == null || queryGroup.isEmpty())
			return null;
		Query query = queryGroup.get(0);

		if ("view".equals(query.getType())) {
			query.setSourceCode(
					"CREATE VIEW " + query.getSchema() + "." + query.getName() + " as " + query.getSourceCode());
		} else if ("materialized view".equals(query.getType())) {
			query.setSourceCode("CREATE MATERIALIZED VIEW " + query.getSchema() + "." + query.getName() + " as "
					+ query.getSourceCode());
		} else {
			query.setSourceCode("CREATE OR REPLACE " + query.getSourceCode());
		}

		for (int i = 1; i < queryGroup.size(); i++) {
			if (!query.getSourceCode().endsWith("\n") && i < queryGroup.size() - 1) {
				query.setSourceCode(query.getSourceCode() + "\n" + queryGroup.get(i).getSourceCode());
			} else {
				query.setSourceCode(query.getSourceCode() + queryGroup.get(i).getSourceCode());
			}
		}
		return query;
	}

	@Override
	protected boolean needMergeQuery() {
		return true;
	}
	
	protected List<Sequence> exportSequences(TSQLDataSource datasource, String database, String catalog,
			Metadata exportMetadataModel) {
		Statement statement = null;
		List<Sequence> sequenceModelItems = new ArrayList<Sequence>();
		Connection connection = null;

		try {
			connection = datasource.getConnection();
			if (enableSetCatalog()) {
				connection.setCatalog(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, catalog)
						.getNormalizeIdentifier());
			}
			statement = connection.createStatement();
			String sequencesSql = sequencesSql(datasource, database, catalog);
			if (!SQLUtil.isEmpty(sequencesSql)) {
				ResultSet resultSet = statement.executeQuery(sequencesSql);
				while (resultSet.next()) {
					Sequence item = new Sequence();
					item.setDatabase(detectResult(1, resultSet));
					String schema = detectResult(2, resultSet);
					if (!acceptSchema(datasource, item.getDatabase(), schema)) {
						continue;
					}
					item.setSchema(schema);
					item.setName(detectResult(3, resultSet));
					item.setIncrementBy(detectResult(4, resultSet));
					sequenceModelItems.add(item);
				}
			}
		} catch (Exception e) {
			if (e.getMessage() != null) {
				exportMetadataModel.appendErrorMessage(e.getMessage());
			}
			e.printStackTrace();
		} finally {
			close(statement);
		}
		return sequenceModelItems;
	}

	protected void appendDatabase(Metadata exportMetadataModel, TSQLDataSource datasource, Database sqlDatabase) {
		sqlDatabase.setName(
				"\"" + new Identifier(EDbVendor.dbvoracle, ESQLDataObjectType.dotCatalog, datasource.getDatabase())
						.getNormalizeIdentifier() + "\"");

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
			if (sqlDatabase.getSynonyms() != null) {
				database.getSynonyms().addAll(sqlDatabase.getSynonyms());
			}
		}
	}
}
