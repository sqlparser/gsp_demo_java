package demos.sqlenv.connect.strategy.impl;

import demos.sqlenv.connect.Connector;
import demos.sqlenv.connect.impl.NetezzaConnector;
import demos.sqlenv.connect.strategy.DbStrategy;

public class NetezzaStrategy implements DbStrategy {
    @Override
    public Connector getDbConnect() {
        return new NetezzaConnector();
    }
}
