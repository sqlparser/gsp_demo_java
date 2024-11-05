package demos.sqlenv.connect.strategy.impl;

import demos.sqlenv.connect.Connector;
import demos.sqlenv.connect.impl.GreenplumConnector;
import demos.sqlenv.connect.strategy.DbStrategy;

public class GreenplumStrategy implements DbStrategy {
	@Override
	public Connector getDbConnect() {
		return new  GreenplumConnector();
	}
}
