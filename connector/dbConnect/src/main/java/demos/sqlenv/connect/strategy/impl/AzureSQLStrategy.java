package demos.sqlenv.connect.strategy.impl;

import demos.sqlenv.connect.Connector;
import demos.sqlenv.connect.impl.AzureSQLConnector;
import demos.sqlenv.connect.strategy.DbStrategy;

/**
 * CemB
 */
public class AzureSQLStrategy implements DbStrategy {
    @Override
    public Connector getDbConnect() {
        return new AzureSQLConnector();
    }
}
