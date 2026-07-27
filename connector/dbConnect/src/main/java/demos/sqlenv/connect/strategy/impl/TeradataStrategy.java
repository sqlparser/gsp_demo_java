package demos.sqlenv.connect.strategy.impl;

import demos.sqlenv.connect.Connector;
import demos.sqlenv.connect.impl.TeradataConnector;
import demos.sqlenv.connect.strategy.DbStrategy;

public class TeradataStrategy implements DbStrategy {
    @Override
    public Connector getDbConnect() {
        return new TeradataConnector();
    }
}
