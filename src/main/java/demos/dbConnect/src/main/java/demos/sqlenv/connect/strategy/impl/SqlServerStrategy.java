package demos.sqlenv.connect.strategy.impl;

import demos.sqlenv.connect.Connector;
import demos.sqlenv.connect.impl.SQLServerConnector;
import demos.sqlenv.connect.strategy.DbStrategy;

public class SqlServerStrategy implements DbStrategy {
    @Override
    public Connector getDbConnect() {
        return new SQLServerConnector();
    }
}
