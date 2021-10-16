package demos.sqlenv.connect.strategy;

import demos.sqlenv.connect.strategy.impl.*;
import demos.sqlenv.constant.DbConstant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DbStrategyFactory {

    private static final Map<String, DbStrategy> strategyMap = new ConcurrentHashMap<>();

    static {
        strategyMap.put(DbConstant.MySQLV5, new MysqlV5Strategy());
        strategyMap.put(DbConstant.MySQLV8, new MysqlV8Strategy());
        strategyMap.put(DbConstant.Oracle, new OracleStrategy());
        strategyMap.put(DbConstant.SQLServer, new SqlServerStrategy());
        strategyMap.put(DbConstant.PostgreSQL, new PostgreSQLStrategy());
        strategyMap.put(DbConstant.Snowflake, new SnowflakeStrategy());
        strategyMap.put(DbConstant.DB2, new Db2Strategy());
        strategyMap.put(DbConstant.Netezza, new NetezzaStrategy());
        strategyMap.put(DbConstant.Teradata, new TeradataStrategy());
        strategyMap.put(DbConstant.AzureSQL, new AzureSQLStrategy());
        strategyMap.put(DbConstant.Greenplum, new GreenplumStrategy());
        strategyMap.put(DbConstant.Redshift, new RedshiftStrategy());
        strategyMap.put(DbConstant.Hive, new HiveStrategy());
    }

    private DbStrategyFactory() {
    }

    public static DbStrategyFactory getInstance() {
        return LazyHolder.factory;
    }

    public DbStrategy getDbStrategy(String dbType) {
        return strategyMap.get(dbType);
    }

    private static class LazyHolder {
        private final static DbStrategyFactory factory = new DbStrategyFactory();
    }
}
