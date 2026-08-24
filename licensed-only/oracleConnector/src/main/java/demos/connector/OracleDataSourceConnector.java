package demos.connector;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import gudusoft.gsqlparser.sqlenv.TOracleSQLDataSource;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.sqlenv.metadata.DDL;

public class OracleDataSourceConnector {

	public static void main(String[] args) {
		TOracleSQLDataSource datasource = new TOracleSQLDataSource("IP", "1521", "username", "password",
				"orcl");

		// Only extract schema BIGKING
		datasource.setExtractedSchemas("BIGKING");

		if (datasource.testConnection()) {
			System.out.println("==============================Test SQL JDBC Connection============================");
			Connection connection = datasource.getConnection();
			try {
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("select * from v$version");
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
			System.err.println("Connect oracle database failed.");
		}
	}

}
