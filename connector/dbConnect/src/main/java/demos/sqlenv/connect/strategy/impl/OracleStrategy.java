package demos.sqlenv.connect.strategy.impl;

import demos.sqlenv.connect.Connector;
import demos.sqlenv.connect.impl.OracleConnector;
import demos.sqlenv.connect.strategy.DbStrategy;

public class OracleStrategy implements DbStrategy {
    @Override
    public Connector getDbConnect() {
        return new OracleConnector();
    }
}
