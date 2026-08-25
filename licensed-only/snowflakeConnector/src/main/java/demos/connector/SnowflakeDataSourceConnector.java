package demos.connector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.sqlenv.TSnowflakeSQLDataSource;
import gudusoft.gsqlparser.sqlenv.metadata.DDL;

public class SnowflakeDataSourceConnector {

	public static void main(String[] args) {
		TSnowflakeSQLDataSource datasource = new TSnowflakeSQLDataSource("URL", "443", "username", "password");
		
		// Extract database sunny
		datasource.setExtractedDatabaseSchemas("sunny");

		if (datasource.testConnection()) {
			System.out.println("==============================Test SQL JDBC Connection============================");
			Connection connection = datasource.getConnection();
			try {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT CURRENT_VERSION()");
				while (rs.next()) {
					System.out.println(rs.getString(1));
				}
				rs.close();
				stmt.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

			System.out.println();

			System.out.println("==============================Test DDL Exporter============================");
			DDL ddl = datasource.exportDDL();
			System.out.println(ddl.toString());

			System.out.println();

			System.out.println("==============================Test SQLEnv Exporter============================");
			TSQLEnv sqlenv = TSQLEnv.valueOf(datasource);
			System.out.println(sqlenv.toString());

			System.out.println();

			System.out.println("==============================Test JSON Exporter============================");
			String json = datasource.exportJSON();
			System.out.println(json);
		} else {
			System.err.println("Connect snowflake database failed.");
		}
	}

}
