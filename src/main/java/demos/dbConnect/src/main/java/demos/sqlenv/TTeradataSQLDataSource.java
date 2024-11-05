package demos.sqlenv;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.DatabaseSQLDataSource;

public class TTeradataSQLDataSource extends DatabaseSQLDataSource {

	public TTeradataSQLDataSource(String hostName, String port, String account, String password, String database) {
		super(EDbVendor.dbvteradata, hostName, port, account, password, database);
		setSystemDatabases("DBC");
	}
}
