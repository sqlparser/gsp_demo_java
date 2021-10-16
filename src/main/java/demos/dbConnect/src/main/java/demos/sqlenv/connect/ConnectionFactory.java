package demos.sqlenv.connect;

import demos.sqlenv.TSQLDataSource;
import demos.sqlenv.connect.strategy.DbStrategyFactory;

import java.sql.Connection;

public class ConnectionFactory {

	public static Connection getConnection(TSQLDataSource datasource) throws Exception {
		return DbStrategyFactory.getInstance().getDbStrategy(datasource.getDbType()).getDbConnect()
				.toConnect(datasource);
	}
}
