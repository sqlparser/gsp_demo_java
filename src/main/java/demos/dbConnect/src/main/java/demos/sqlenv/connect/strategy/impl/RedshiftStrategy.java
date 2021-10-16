package demos.sqlenv.connect.strategy.impl;

import demos.sqlenv.connect.Connector;
import demos.sqlenv.connect.impl.RedshiftConnector;
import demos.sqlenv.connect.strategy.DbStrategy;

/**
 * CemB
 */
public class RedshiftStrategy implements DbStrategy {
    @Override
    public Connector getDbConnect() {
        return new RedshiftConnector();
    }
}
