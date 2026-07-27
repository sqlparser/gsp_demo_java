package demos.sqlenv.operation.strategy.json.impl;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.ESQLDataObjectType;
import demos.sqlenv.TSQLDataSource;
import demos.sqlenv.operation.strategy.json.AbstractJSONExport;
import gudusoft.gsqlparser.sqlenv.util.SQLFileUtil;
import gudusoft.gsqlparser.util.Identifier;

public class AzureSQLJSONExport extends AbstractJSONExport {

	@Override
	protected String dbLinksSql(TSQLDataSource datasource, String database) {
		return null;
	}

	@Override
	protected String synonymsSql(TSQLDataSource datasource, String database, String catalog) {
        return SQLFileUtil.readSqlContent(datasource.getDbType(), "synonyms.sql").replace("%s", new Identifier(EDbVendor.dbvmssql, ESQLDataObjectType.dotCatalog, database)
				.getNormalizeIdentifier());
	}

	@Override
	protected String databasesSql(TSQLDataSource datasource) {
		return String.format(SQLFileUtil.readSqlContent(datasource.getDbType(), "database.sql"));
	}

	@Override
	protected String tablesSql(TSQLDataSource datasource, String database, String catalog) {
        return SQLFileUtil.readSqlContent(datasource.getDbType(), "schema.sql").replace("%s", new Identifier(EDbVendor.dbvmssql, ESQLDataObjectType.dotCatalog, database)
				.getNormalizeIdentifier());
	}

	@Override
	protected String queriesSql(TSQLDataSource datasource, String database, String catalog) {
        return SQLFileUtil.readSqlContent(datasource.getDbType(), "query.sql").replace("%s", new Identifier(EDbVendor.dbvmssql, ESQLDataObjectType.dotCatalog, database)
				.getNormalizeIdentifier());
	}

	@Override
	protected String queriesPSql(TSQLDataSource datasource, String database, String catalog) {
		return null;
	}

	@Override
	protected boolean needMergeQuery() {
		return true;
	}

	@Override
	protected boolean enableSetCatalog() {
		return false;
	}
	
	protected boolean acceptDatabase(TSQLDataSource datasource, String databaseName) {
		boolean accept = super.acceptDatabase(datasource, databaseName);
		if(accept){
			if (new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, datasource.getDatabase())
					.equals(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, databaseName))) {
				return true;
			}
		}
		return false;
	}
}
