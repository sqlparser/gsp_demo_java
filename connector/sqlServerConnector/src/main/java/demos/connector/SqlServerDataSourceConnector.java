package demos.connector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import gudusoft.gsqlparser.sqlenv.TMssqlSQLDataSource;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.sqlenv.metadata.DDL;

public class SqlServerDataSourceConnector {

	public static void main(String[] args) {
		TMssqlSQLDataSource datasource = new TMssqlSQLDataSource("IP", "1433", "username", "password");

		// Extract database AdventureWorksDW2019, but exclude schema dbo
		datasource.setExtractedDatabaseSchemas("AdventureWorksDW2019");
		datasource.setExcludedDatabaseSchemas("AdventureWorksDW2019/dbo");

		if (datasource.testConnection()) {
			System.out.println("==============================Test SQL JDBC Connection============================");
			Connection connection = datasource.getConnection();
			try {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT @@VERSION");
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
			System.err.println("Connect sqlserver database failed.");
		}
	}

}
