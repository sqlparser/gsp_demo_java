package demos.sqlenv.operation.strategy.json.impl;

import demos.sqlenv.operation.strategy.json.AbstractJSONExport;
import demos.sqlenv.TSQLDataSource;
import demos.sqlenv.util.SQLFileUtil;

public class Db2JSONExport extends AbstractJSONExport {

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
        return SQLFileUtil.readSql(datasource.getDbCategory(), "databases");
    }

    @Override
    protected String tablesSql(TSQLDataSource datasource, String database, String catalog) {
        return SQLFileUtil.readSql(datasource.getDbCategory(), "tables").replace("%s", database);
    }

    @Override
    protected String queriesSql(TSQLDataSource datasource, String database, String catalog) {
        return SQLFileUtil.readSql(datasource.getDbCategory(), "queries").replace("%s", database);
    }

    @Override
    protected String queriesPSql(TSQLDataSource datasource, String database, String catalog) {
        return SQLFileUtil.readSql(datasource.getDbCategory(), "queries-p").replace("%s", database);
    }

}
