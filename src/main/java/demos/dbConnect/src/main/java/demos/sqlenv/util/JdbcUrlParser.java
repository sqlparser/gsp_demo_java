package demos.sqlenv.util;

import demos.sqlenv.*;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.util.SQLUtil;

public class JdbcUrlParser {

	public static TSQLDataSource generateSQLDataSource(EDbVendor vendor, String jdbcUrl, String account,
													   String password) {
		if (jdbcUrl == null) {
			throw new IllegalArgumentException("Jdbc url can't be null.");
		}

		jdbcUrl = jdbcUrl.trim();

		if (!jdbcUrl.toLowerCase().startsWith("jdbc:")) {
			throw new IllegalArgumentException("Invalid " + vendor.name().replace("dbv", "") + " jdbc url: " + jdbcUrl);
		}

		String trimJdbcUrl = jdbcUrl.toLowerCase();

		switch (vendor) {
		case dbvgreenplum: {
			if (trimJdbcUrl.startsWith("jdbc:pivotal:greenplum://")) {
				String address = jdbcUrl.substring("jdbc:pivotal:greenplum://".length());
				if (address.indexOf(";") != -1) {
					address = address.substring(0, address.indexOf(";"));
				}
				String database = null;
				if (trimJdbcUrl.indexOf("databasename=") != -1) {
					database = jdbcUrl.substring(trimJdbcUrl.indexOf("databasename=") + "databasename=".length());
					if (database.indexOf(";") != -1) {
						database = database.substring(0, database.indexOf(";"));
					}
				}

				String host = address;
				int index = address.indexOf(":");
				String port = "5432";
				if (index != -1) {
					host = address.substring(0, index);
					port = address.substring(index + 1);
				}

				if (SQLUtil.isEmpty(database)) {
					return new TGreenplumSQLDataSource(host, port, account, password);
				} else {
					return new TGreenplumSQLDataSource(host, port, account, password, database);
				}
			} else if (trimJdbcUrl.startsWith("jdbc:postgresql://")) {
				String address = jdbcUrl.substring("jdbc:postgresql://".length());
				String database = null;
				if (address.indexOf("/") != -1) {
					if (address.indexOf("?") != -1) {
						database = address.substring(address.indexOf("/") + 1, address.indexOf("?"));
					} else {
						database = address.substring(address.indexOf("/") + 1);
					}
					address = address.substring(0, address.indexOf("/"));
				}
				String host = address;
				int index = address.indexOf(":");
				String port = "5432";
				if (index != -1) {
					host = address.substring(0, index);
					port = address.substring(index + 1);
				}

				if (SQLUtil.isEmpty(database)) {
					return new TGreenplumSQLDataSource(host, port, account, password);
				} else {
					return new TGreenplumSQLDataSource(host, port, account, password, database);
				}
			}
		}
		case dbvmysql: {
			if (trimJdbcUrl.startsWith("jdbc:mysql://")) {
				String address = jdbcUrl.substring("jdbc:mysql://".length());
				String database = null;
				if (address.indexOf("/") != -1) {
					if (address.indexOf("?") != -1) {
						database = address.substring(address.indexOf("/") + 1, address.indexOf("?"));
					} else {
						database = address.substring(address.indexOf("/") + 1);
					}
					address = address.substring(0, address.indexOf("/"));
				}
				String host = address;
				int index = address.indexOf(":");
				String port = "3306";
				if (index != -1) {
					host = address.substring(0, index);
					port = address.substring(index + 1);
				}

				if (SQLUtil.isEmpty(database)) {
					return new TMysqlSQLDataSource(host, port, account, password);
				} else {
					return new TMysqlSQLDataSource(host, port, account, password, database);
				}
			}
		}
		case dbvhive: {
			if (trimJdbcUrl.startsWith("jdbc:hive2://")) {
				String address = jdbcUrl.substring("jdbc:hive2://".length());
				String database = null;
				if (address.indexOf("/") != -1) {
					if (address.indexOf("?") != -1) {
						database = address.substring(address.indexOf("/") + 1, address.indexOf("?"));
					} else {
						database = address.substring(address.indexOf("/") + 1);
					}
					address = address.substring(0, address.indexOf("/"));
				}
				String host = address;
				int index = address.indexOf(":");
				String port = "10000";
				if (index != -1) {
					host = address.substring(0, index);
					port = address.substring(index + 1);
				}

				if (SQLUtil.isEmpty(database)) {
					return new THiveMetadataDataSource(host, port, account, password);
				} else {
					return new THiveMetadataDataSource(host, port, account, password, database);
				}
			}
		}
		case dbvmssql: {
			if (trimJdbcUrl.startsWith("jdbc:sqlserver://")) {
				String address = jdbcUrl.substring("jdbc:sqlserver://".length());
				if (address.indexOf(";") != -1) {
					address = address.substring(0, address.indexOf(";"));
				}
				String database = null;
				if (trimJdbcUrl.indexOf("databasename=") != -1) {
					database = jdbcUrl.substring(trimJdbcUrl.indexOf("databasename=") + "databasename=".length());
					if (database.indexOf(";") != -1) {
						database = database.substring(0, database.indexOf(";"));
					}
				}

				String host = address;
				int index = address.indexOf(":");
				String port = "1433";
				if (index != -1) {
					host = address.substring(0, index);
					port = address.substring(index + 1);
				}

				if (SQLUtil.isEmpty(database)) {
					return new TMssqlSQLDataSource(host, port, account, password);
				} else {
					return new TMssqlSQLDataSource(host, port, account, password, database);
				}
			}
		}
		case dbvazuresql: {
			if (trimJdbcUrl.startsWith("jdbc:sqlserver://")) {
				String address = jdbcUrl.substring("jdbc:sqlserver://".length());
				if (address.indexOf(";") != -1) {
					address = address.substring(0, address.indexOf(";"));
				}
				String database = null;
				if (trimJdbcUrl.indexOf("databasename=") != -1) {
					database = jdbcUrl.substring(trimJdbcUrl.indexOf("databasename=") + "databasename=".length());
					if (database.indexOf(";") != -1) {
						database = database.substring(0, database.indexOf(";"));
					}
				}

				String host = address;
				int index = address.indexOf(":");
				String port = "1433";
				if (index != -1) {
					host = address.substring(0, index);
					port = address.substring(index + 1);
				}

				if (SQLUtil.isEmpty(database)) {
					return new TMssqlSQLDataSource(host, port, account, password);
				} else {
					return new TAzureSQLDataSource(host, port, account, password, database);
				}
			}
		}
		case dbvnetezza: {
			if (trimJdbcUrl.startsWith("jdbc:netezza://")) {
				String address = jdbcUrl.substring("jdbc:netezza://".length());
				String database = null;
				if (address.indexOf("/") != -1) {
					if (address.indexOf("?") != -1) {
						database = address.substring(address.indexOf("/") + 1, address.indexOf("?"));
					} else {
						database = address.substring(address.indexOf("/") + 1);
					}
					address = address.substring(0, address.indexOf("/"));
				}
				String host = address;
				int index = address.indexOf(":");
				String port = "5480";
				if (index != -1) {
					host = address.substring(0, index);
					port = address.substring(index + 1);
				}

				if (!SQLUtil.isEmpty(database)) {
					return new TNetezzaSQLDataSource(host, port, account, password, database);
				}
			}
		}
		case dbvoracle: {
			if (trimJdbcUrl.startsWith("jdbc:oracle:thin:@")) {
				String address = jdbcUrl.substring("jdbc:oracle:thin:@".length());

				if (address.indexOf("?") != -1) {
					address = address.substring(0, address.indexOf("?"));
				}

				String host = address;
				int index = address.indexOf(":");
				if (index != -1) {
					host = address.substring(0, index);
					address = address.substring(index + 1);
				}

				String port = "1521";
				index = address.indexOf(":");
				if (index != -1) {
					port = address.substring(0, index);
					address = address.substring(index + 1);
				} else {
					index = address.indexOf("/");
				}
				if (index != -1) {
					port = address.substring(0, index);
					address = address.substring(index + 1);
				}

				String database = address;

				if (SQLUtil.isEmpty(database)) {
					return new TOracleSQLDataSource(host, port, account, password);
				} else {
					return new TOracleSQLDataSource(host, port, account, password, database);
				}
			}
		}
		case dbvpostgresql: {
			if (trimJdbcUrl.startsWith("jdbc:postgresql://")) {
				String address = jdbcUrl.substring("jdbc:postgresql://".length());
				String database = null;
				if (address.indexOf("/") != -1) {
					if (address.indexOf("?") != -1) {
						database = address.substring(address.indexOf("/") + 1, address.indexOf("?"));
					} else {
						database = address.substring(address.indexOf("/") + 1);
					}
					address = address.substring(0, address.indexOf("/"));
				}
				String host = address;
				int index = address.indexOf(":");
				String port = "5432";
				if (index != -1) {
					host = address.substring(0, index);
					port = address.substring(index + 1);
				}

				if (SQLUtil.isEmpty(database)) {
					return new TPostgreSQLDataSource(host, port, account, password);
				} else {
					return new TPostgreSQLDataSource(host, port, account, password, database);
				}
			}
		}
		case dbvredshift: {
			if (trimJdbcUrl.startsWith("jdbc:redshift://")) {
				String address = jdbcUrl.substring("jdbc:redshift://".length());
				String database = null;
				if (address.indexOf("/") != -1) {
					if (address.indexOf("?") != -1) {
						database = address.substring(address.indexOf("/") + 1, address.indexOf("?"));
					} else {
						database = address.substring(address.indexOf("/") + 1);
					}
					address = address.substring(0, address.indexOf("/"));
				}
				String host = address;
				int index = address.indexOf(":");
				String port = "5439";
				if (index != -1) {
					host = address.substring(0, index);
					port = address.substring(index + 1);
				}
				if (SQLUtil.isEmpty(database)) {
					return new TRedshiftSQLDataSource(host, port, account, password);
				} else {
					return new TRedshiftSQLDataSource(host, port, account, password, database);
				}
			} else if (trimJdbcUrl.startsWith("jdbc:postgresql://")) {
				String address = jdbcUrl.substring("jdbc:postgresql://".length());
				String database = null;
				if (address.indexOf("/") != -1) {
					if (address.indexOf("?") != -1) {
						database = address.substring(address.indexOf("/") + 1, address.indexOf("?"));
					} else {
						database = address.substring(address.indexOf("/") + 1);
					}
					address = address.substring(0, address.indexOf("/"));
				}
				String host = address;
				int index = address.indexOf(":");
				String port = "5439";
				if (index != -1) {
					host = address.substring(0, index);
					port = address.substring(index + 1);
				}

				if (SQLUtil.isEmpty(database)) {
					return new TRedshiftSQLDataSource(host, port, account, password);
				} else {
					return new TRedshiftSQLDataSource(host, port, account, password, database);
				}
			}
		}
		case dbvsnowflake: {
			if (trimJdbcUrl.startsWith("jdbc:snowflake://")) {
				String address = jdbcUrl.substring("jdbc:snowflake://".length());
				if (address.indexOf("?") != -1) {
					address = address.substring(0, address.indexOf("?"));
				}
				String database = null;
				if (trimJdbcUrl.indexOf("db=") != -1) {
					database = jdbcUrl.substring(trimJdbcUrl.indexOf("db=") + "db=".length());
					if (database.indexOf("&") != -1) {
						database = database.substring(0, database.indexOf("&"));
					}
				}

				String private_key_file = null;
				if (trimJdbcUrl.indexOf("private_key_file=") != -1) {
					private_key_file = jdbcUrl
							.substring(trimJdbcUrl.indexOf("private_key_file=") + "private_key_file=".length());
					if (private_key_file.indexOf("&") != -1) {
						private_key_file = private_key_file.substring(0, private_key_file.indexOf("&"));
					}
				}

				String private_key_file_pwd = null;
				if (trimJdbcUrl.indexOf("private_key_file_pwd=") != -1) {
					private_key_file_pwd = jdbcUrl
							.substring(trimJdbcUrl.indexOf("private_key_file_pwd=") + "private_key_file_pwd=".length());
					if (private_key_file_pwd.indexOf("&") != -1) {
						private_key_file_pwd = private_key_file_pwd.substring(0, private_key_file_pwd.indexOf("&"));
					}
				}

				String role = null;
				if (trimJdbcUrl.indexOf("role=") != -1) {
					role = jdbcUrl.substring(trimJdbcUrl.indexOf("role=") + "role=".length());
					if (role.indexOf("&") != -1) {
						role = role.substring(0, role.indexOf("&"));
					}
				}

				String schema = null;
				if (trimJdbcUrl.indexOf("schema=") != -1) {
					schema = jdbcUrl.substring(trimJdbcUrl.indexOf("schema=") + "schema=".length());
					if (schema.indexOf("&") != -1) {
						schema = schema.substring(0, schema.indexOf("&"));
					}
				}

				String host = address;
				int index = address.indexOf(":");
				String port = "443";
				if (index != -1) {
					host = address.substring(0, index);
					port = address.substring(index + 1);
				}

				TSnowflakeSQLDataSource snowflakeSQLDataSource = null;
				if (SQLUtil.isEmpty(database)) {
					snowflakeSQLDataSource = new TSnowflakeSQLDataSource(host, port, account, password);
				} else {
					snowflakeSQLDataSource = new TSnowflakeSQLDataSource(host, port, account, password, database);
				}

				if (!SQLUtil.isEmpty(role)) {
					snowflakeSQLDataSource.setDefaultRole(role);
				}

				if (!SQLUtil.isEmpty(private_key_file)) {
					snowflakeSQLDataSource.setPrivateKeyFile(private_key_file);
				}

				if (!SQLUtil.isEmpty(role)) {
					snowflakeSQLDataSource.setPrivateKeyFilePwd(private_key_file_pwd);
				}

				if (SQLUtil.isEmpty(database) && !SQLUtil.isEmpty(schema)) {
					snowflakeSQLDataSource.setExtractedDbsSchemas(database + "/" + schema);
				}
				return snowflakeSQLDataSource;
			}
		}
		default:
			throw new UnsupportedOperationException(
					"Unsupport the " + vendor.name().replace("dbv", "") + " jdbc url: " + jdbcUrl);
		}
	}

	public static TSQLDataSource generateSQLDataSource(String jdbcUrl, String account, String password) {
		if (jdbcUrl == null) {
			throw new IllegalArgumentException("Jdbc url can't be null.");
		}

		jdbcUrl = jdbcUrl.trim();

		if (!jdbcUrl.toLowerCase().startsWith("jdbc:")) {
			throw new IllegalArgumentException("Invalid jdbc url: " + jdbcUrl);
		}

		String trimJdbcUrl = jdbcUrl.toLowerCase();
		if (trimJdbcUrl.indexOf("greenplum") != -1) {
			return generateSQLDataSource(EDbVendor.dbvgreenplum, jdbcUrl, account, password);
		}
		if (trimJdbcUrl.indexOf("redshift") != -1) {
			return generateSQLDataSource(EDbVendor.dbvredshift, jdbcUrl, account, password);
		}
		if (trimJdbcUrl.indexOf("postgresql") != -1) {
			return generateSQLDataSource(EDbVendor.dbvpostgresql, jdbcUrl, account, password);
		}
		if (trimJdbcUrl.indexOf("mysql") != -1) {
			return generateSQLDataSource(EDbVendor.dbvmysql, jdbcUrl, account, password);
		}
		if (trimJdbcUrl.indexOf("sqlserver") != -1) {
			return generateSQLDataSource(EDbVendor.dbvmssql, jdbcUrl, account, password);
		}
		if (trimJdbcUrl.indexOf("netezza") != -1) {
			return generateSQLDataSource(EDbVendor.dbvnetezza, jdbcUrl, account, password);
		}
		if (trimJdbcUrl.indexOf("oracle") != -1) {
			return generateSQLDataSource(EDbVendor.dbvoracle, jdbcUrl, account, password);
		}
		if (trimJdbcUrl.indexOf("snowflake") != -1) {
			return generateSQLDataSource(EDbVendor.dbvsnowflake, jdbcUrl, account, password);
		}
		if (trimJdbcUrl.indexOf("hive") != -1) {
			return generateSQLDataSource(EDbVendor.dbvhive, jdbcUrl, account, password);
		}
		throw new UnsupportedOperationException("Unsupport the jdbc url: " + jdbcUrl);
	}
}
