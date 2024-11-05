package demos.sqlenv.connect.strategy;

import demos.sqlenv.connect.Connector;

public interface DbStrategy {

    Connector getDbConnect();

}

