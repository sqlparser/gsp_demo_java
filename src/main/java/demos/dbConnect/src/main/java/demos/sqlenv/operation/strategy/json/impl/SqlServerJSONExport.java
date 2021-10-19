package demos.sqlenv.operation.strategy.json.impl;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.ESQLDataObjectType;
import demos.sqlenv.TSQLDataSource;
import demos.sqlenv.operation.strategy.json.AbstractJSONExport;
import gudusoft.gsqlparser.sqlenv.util.SQLFileUtil;
import gudusoft.gsqlparser.util.Identifier;

/**
 * CemB
 */
public class SqlServerJSONExport extends AbstractJSONExport {

    @Override
    protected String dbLinksSql(TSQLDataSource datasource, String database) {
        return String.format(SQLFileUtil.readSqlContent(datasource.getDbCategory(), "dblink.sql"));
    }

    @Override
    protected String synonymsSql(TSQLDataSource datasource, String database, String catalog) {
        return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "synonyms.sql").replace("%s", new Identifier(EDbVendor.dbvmssql, ESQLDataObjectType.dotCatalog, database)
				.getNormalizeIdentifier());
    }

    @Override
    protected String databasesSql(TSQLDataSource datasource) {
        return String.format(SQLFileUtil.readSqlContent(datasource.getDbCategory(), "database.sql"));
    }

    @Override
    protected String tablesSql(TSQLDataSource datasource, String database, String catalog) {
        return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "schema.sql").replace("%s", new Identifier(EDbVendor.dbvmssql, ESQLDataObjectType.dotCatalog, database)
				.getNormalizeIdentifier());
    }

    @Override
    protected String queriesSql(TSQLDataSource datasource, String database, String catalog) {
        return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "query.sql").replace("%s", new Identifier(EDbVendor.dbvmssql, ESQLDataObjectType.dotCatalog, database)
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
}
