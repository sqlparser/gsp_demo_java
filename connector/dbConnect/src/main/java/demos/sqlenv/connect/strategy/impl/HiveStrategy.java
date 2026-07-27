package demos.sqlenv.connect.strategy.impl;

import demos.sqlenv.connect.Connector;
import demos.sqlenv.connect.impl.HiveConnector;
import demos.sqlenv.connect.strategy.DbStrategy;

public class HiveStrategy implements DbStrategy {
    @Override
    public Connector getDbConnect() {
        return new HiveConnector();
    }
}
