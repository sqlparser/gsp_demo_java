package demos.sqlenv;

import gudusoft.gsqlparser.EDbVendor;

public class TOracleSQLDataSource extends SchemaSQLDataSource {

	public TOracleSQLDataSource(String hostName, String port, String account, String password) {
		super(EDbVendor.dbvoracle, hostName, port, account, password, "orcl");
		setSystemSchemas("SYS", "SYSTEM");
	}

	public TOracleSQLDataSource(String hostName, String port, String account, String password, String database) {
		super(EDbVendor.dbvoracle, hostName, port, account, password, database);
		setSystemSchemas("SYS", "SYSTEM");
	}

}
