package demos.sqlenv;

import gudusoft.gsqlparser.EDbVendor;

public class TPostgreSQLDataSource extends DbSchemaSQLDataSource {

	public TPostgreSQLDataSource(String hostName, String port, String account, String password) {
		super(EDbVendor.dbvpostgresql, hostName, port, account, password, "postgres");
		setSystemDbsSchemas("*/information_schema", "*/pg_catalog");
	}
	
	public TPostgreSQLDataSource(String hostName, String port, String account, String password, String database) {
		super(EDbVendor.dbvpostgresql, hostName, port, account, password, database);
		setSystemDbsSchemas("*/information_schema", "*/pg_catalog");
	}
}
