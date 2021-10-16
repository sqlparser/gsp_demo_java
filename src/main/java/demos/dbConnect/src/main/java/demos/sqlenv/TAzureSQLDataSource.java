package demos.sqlenv;

import gudusoft.gsqlparser.EDbVendor;

public class TAzureSQLDataSource extends DbSchemaSQLDataSource {

	public TAzureSQLDataSource(String hostName, String port, String account, String password, String database) {
		super(EDbVendor.dbvazuresql, hostName, port, account, password, database);
		setSystemDbsSchemas("master/dbo", "master/sys", "*/INFORMATION_SCHEMA");
	}
	
}
