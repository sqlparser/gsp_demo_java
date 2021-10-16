package demos.sqlenv;

import gudusoft.gsqlparser.EDbVendor;

public class TRedshiftSQLDataSource extends DbSchemaSQLDataSource {

	public TRedshiftSQLDataSource(String hostName, String port, String account, String password) {
		super(EDbVendor.dbvredshift, hostName, port, account, password, "postgres");
		setSystemDbsSchemas("*/information_schema", "*/pg_catalog");
	}
	
	public TRedshiftSQLDataSource(String hostName, String port, String account, String password, String database) {
		super(EDbVendor.dbvredshift, hostName, port, account, password, database);
		setSystemDbsSchemas("*/information_schema", "*/pg_catalog");
	}
	
}