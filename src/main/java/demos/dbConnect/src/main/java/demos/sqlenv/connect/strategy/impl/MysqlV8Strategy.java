package demos.sqlenv.connect.strategy.impl;

import demos.sqlenv.connect.Connector;
import demos.sqlenv.connect.impl.MysqlV8Connector;
import demos.sqlenv.connect.strategy.DbStrategy;

public class MysqlV8Strategy implements DbStrategy {
    @Override
    public Connector getDbConnect() {
        return new MysqlV8Connector();
    }
}
