package demos.sqlenv.operation.strategy;

import demos.sqlenv.constant.DbOperation;
import demos.sqlenv.constant.JSONExportOperation;
import demos.sqlenv.operation.DbOperationService;
import demos.sqlenv.operation.strategy.json.impl.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DbOperationStrategyFactory {

    @SuppressWarnings("rawtypes")
	private static final Map<String, DbOperationService> strategyMap = new ConcurrentHashMap<String, DbOperationService>();

    static {
        strategyMap.put(JSONExportOperation.DB2, new Db2JSONExport());
        strategyMap.put(JSONExportOperation.PostgreSQL, new PostgreSQLJSONExport());
        strategyMap.put(JSONExportOperation.Snowflake, new SnowflakeJSONExport());
        strategyMap.put(JSONExportOperation.Netezza, new NetezzaJSONExport());
        strategyMap.put(JSONExportOperation.MySQL, new MySQLJSONExport());
        strategyMap.put(JSONExportOperation.Oracle, new OracleJSONExport());
        strategyMap.put(JSONExportOperation.SQLServer, new SqlServerJSONExport());
        strategyMap.put(JSONExportOperation.Greenplum, new GreenplumJSONExport());
        strategyMap.put(JSONExportOperation.Redshift, new RedshiftJSONExport());
        strategyMap.put(JSONExportOperation.Teradata, new TeradataJSONExport());
        
        strategyMap.put(JSONExportOperation.AzureSQL, new AzureSQLJSONExport());
        strategyMap.put(JSONExportOperation.HIVE, new HiveMetadataJSONExport());
    }

    private DbOperationStrategyFactory() {
    }

    public static DbOperationStrategyFactory getInstance() {
        return LazyHolder.factory;
    }

    @SuppressWarnings("unchecked")
	public <T> DbOperationService<T> getDbOperationStrategy(String dbType, DbOperation operation) {
		return strategyMap.get(dbType + operation.getOperation());
    }

    private static class LazyHolder {
        private final static DbOperationStrategyFactory factory = new DbOperationStrategyFactory();
    }
}
