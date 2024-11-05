package demos.sqlenv;

import gudusoft.gsqlparser.EDbVendor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class THiveMetadataDataSource extends DatabaseSQLDataSource {

	public THiveMetadataDataSource(String hostName, String port, String account, String password) {
		super(EDbVendor.dbvhive, hostName, port, account, password, "default");
	}

	public THiveMetadataDataSource(String hostName, String port, String account, String password, String database) {
		super(EDbVendor.dbvhive, hostName, port, account, password, database);
	}

	@Override
	public Connection getConnection() {
		try {
			if (connection != null && (connection.isClosed())) {
				connection = null;
			}
			if (connection == null) {
				toConnect();
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "Connection is closed.", e);
		}
		return connection;
	}
}
