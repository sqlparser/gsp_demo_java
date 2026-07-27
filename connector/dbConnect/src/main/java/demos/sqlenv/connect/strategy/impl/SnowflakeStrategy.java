package demos.sqlenv.connect.strategy.impl;

import demos.sqlenv.connect.Connector;
import demos.sqlenv.connect.impl.SnowflakeConnector;
import demos.sqlenv.connect.strategy.DbStrategy;

/**
 * CemB
 */
public class SnowflakeStrategy implements DbStrategy {
    @Override
    public Connector getDbConnect() {
        return new SnowflakeConnector();
    }
}
