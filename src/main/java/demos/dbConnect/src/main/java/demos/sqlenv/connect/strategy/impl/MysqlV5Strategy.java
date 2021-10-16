package demos.sqlenv.connect.strategy.impl;

import demos.sqlenv.connect.Connector;
import demos.sqlenv.connect.impl.MysqlV5Connector;
import demos.sqlenv.connect.strategy.DbStrategy;

public class MysqlV5Strategy implements DbStrategy {

    @Override
    public Connector getDbConnect() {
        return new MysqlV5Connector();
    }

}
