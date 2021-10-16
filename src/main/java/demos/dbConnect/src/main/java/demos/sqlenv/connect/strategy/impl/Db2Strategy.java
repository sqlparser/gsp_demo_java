package demos.sqlenv.connect.strategy.impl;

import demos.sqlenv.connect.Connector;
import demos.sqlenv.connect.impl.DB2Connector;
import demos.sqlenv.connect.strategy.DbStrategy;

/**
 * CemB
 */
public class Db2Strategy implements DbStrategy {
    @Override
    public Connector getDbConnect() {
        return new DB2Connector();
    }
}
