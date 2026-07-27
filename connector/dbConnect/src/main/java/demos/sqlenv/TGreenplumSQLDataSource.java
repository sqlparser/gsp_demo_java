package demos.sqlenv;

import gudusoft.gsqlparser.EDbVendor;

public class TGreenplumSQLDataSource extends DbSchemaSQLDataSource {

	public TGreenplumSQLDataSource(String hostName, String port, String account, String password) {
		super(EDbVendor.dbvgreenplum, hostName, port, account, password, "postgres");
		setSystemDbsSchemas("*/information_schema", "*/pg_catalog");
	}
	
	public TGreenplumSQLDataSource(String hostName, String port, String account, String password, String database) {
		super(EDbVendor.dbvgreenplum, hostName, port, account, password, database);
		setSystemDbsSchemas("*/information_schema", "*/pg_catalog");
	}
}
