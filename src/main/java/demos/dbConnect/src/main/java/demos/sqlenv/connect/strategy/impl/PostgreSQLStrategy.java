package demos.sqlenv.connect.strategy.impl;

import demos.sqlenv.connect.Connector;
import demos.sqlenv.connect.impl.PostgreSQLConnector;
import demos.sqlenv.connect.strategy.DbStrategy;
public class PostgreSQLStrategy implements DbStrategy {
    @Override
    public Connector getDbConnect() {
        return new PostgreSQLConnector();
    }
}
