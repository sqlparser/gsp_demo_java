package demos.sqlenv;

import gudusoft.gsqlparser.EDbVendor;
import demos.sqlenv.connect.ConnectionFactory;
import demos.sqlenv.constant.DbConstant;

import java.sql.Connection;
import java.util.logging.Level;

public class TMysqlSQLDataSource extends DbSchemaSQLDataSource {

	public TMysqlSQLDataSource(String hostName, String port, String account, String password, String database) {
		super(EDbVendor.dbvmysql, hostName, port, account, password, database);
		setSystemDbsSchemas("mysql", "information_schema", "performance_schema", "sys", "*/information_schema");
		this.dbType = DbConstant.MySQLV5;
	}

	public TMysqlSQLDataSource(String hostName, String port, String account, String password) {
		super(EDbVendor.dbvmysql, hostName, port, account, password);
		setSystemDbsSchemas("mysql", "information_schema", "performance_schema", "sys", "*/information_schema");
		this.dbType = DbConstant.MySQLV5;
	}

	private void getMysqlV8Connector() {
		if (DbConstant.MySQLV5.equals(dbType) || DbConstant.MySQL.equals(dbType)) {
			try {
				dbType = DbConstant.MySQLV8;
				Connection sqlConnection = ConnectionFactory.getConnection(this);
				if (null != sqlConnection) {
					this.connection = sqlConnection;
				}
			} catch (Exception ex) {
				logger.log(Level.SEVERE, "Connect data source failed.", ex);
			}
		}
	}

	@Override
	protected void toConnect() {
		Connection sqlConnection = null;
		try {
			if (connection == null) {
				sqlConnection = ConnectionFactory.getConnection(this);
			}
			if (null == sqlConnection) {
				getMysqlV8Connector();
			} else {
				this.connection = sqlConnection;
			}
		} catch (Exception ex) {
			if (DbConstant.MySQLV5.equals(dbType) || DbConstant.MySQL.equals(dbType)) {
				getMysqlV8Connector();
			} else {
				logger.log(Level.SEVERE, "Connect data source failed.", ex);
			}
		}
	}
}
