package demos.sqlenv.operation.strategy.json.impl;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.ESQLDataObjectType;
import demos.sqlenv.TSQLDataSource;
import demos.sqlenv.operation.strategy.json.AbstractJSONExport;
import gudusoft.gsqlparser.sqlenv.util.SQLFileUtil;
import gudusoft.gsqlparser.util.Identifier;

public class RedshiftJSONExport extends AbstractJSONExport {

    @Override
    protected String dbLinksSql(TSQLDataSource datasource, String database) {
        return null;
    }

    @Override
    protected String synonymsSql(TSQLDataSource datasource, String database, String schema) {
        return null;
    }

    @Override
    protected String databasesSql(TSQLDataSource datasource) {
        return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "database.sql");
    }

    @Override
    protected String tablesSql(TSQLDataSource datasource, String database, String schema) {
        return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "schema.sql").replace("%s", new Identifier(EDbVendor.dbvredshift, ESQLDataObjectType.dotCatalog, database)
				.getNormalizeIdentifier());
    }

    @Override
    protected String queriesSql(TSQLDataSource datasource, String database, String schema) {
        return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "query.sql").replace("%s", new Identifier(EDbVendor.dbvredshift, ESQLDataObjectType.dotCatalog, database)
				.getNormalizeIdentifier());
    }

    @Override
    protected String queriesPSql(TSQLDataSource datasource, String database, String schema) {
        return null;
    }
}
