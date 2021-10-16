package demos.sqlenv;

import gudusoft.gsqlparser.EDbVendor;

public class TNetezzaSQLDataSource extends DbSchemaSQLDataSource {

	public TNetezzaSQLDataSource(String hostName, String port, String account, String password, String database) {
		super(EDbVendor.dbvnetezza, hostName, port, account, password, database);
		setSystemDbsSchemas("SYSTEM/ADMIN");
	}

}