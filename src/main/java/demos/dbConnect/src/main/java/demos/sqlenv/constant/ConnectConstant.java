package demos.sqlenv.constant;

/**
 * CemB
 */
public enum ConnectConstant {
	MYSQLV5("jdbc:mysql://%s/%s?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT&socketTimeout=5000",
			"com.mysql.jdbc.Driver"),

	MYSQLV8("jdbc:mysql://%s/%s?useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT&socketTimeout=5000",
			"com.mysql.cj.jdbc.Driver"),

	ORACLE("jdbc:oracle:thin:@%s:%s", "oracle.jdbc.driver.OracleDriver"),

	DB2("jdbc:db2://%s/%s", "com.ibm.db2.jcc.DB2Driver"),

	PostgreSQL("jdbc:postgresql://%s/%s?socketTimeout=5", "org.postgresql.Driver"),

	SQLServer("jdbc:sqlserver://%s;DatabaseName=%s", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
	
	AzureSQL("jdbc:sqlserver://%s;DatabaseName=%s", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),

	Snowflake("jdbc:snowflake://%s?db=%s&role=%s&private_key_file=%s&private_key_file_pwd=%s&loginTimeout=5", "net.snowflake.client.jdbc.SnowflakeDriver"),

	Sybase("JDBC:sybase:Tds:%s/%s", "com.sybase.JDBC.SybDriver"),

	informix("JDBC:informix-sqli:%s/%s:INFORMIXSERVER=myserver", "com.informix.JDBC.ifxDriver"),

	Netezza("jdbc:netezza://%s/%s", "org.netezza.Driver"),
	
	Hive("jdbc:hive2://%s/%s", "org.apache.hive.jdbc.HiveDriver"),

	Teradata("jdbc:teradata://%s/%s", "com.teradata.jdbc.TeraDriver"),

	Greenplum("jdbc:pivotal:greenplum://%s;DatabaseName=%s", "com.pivotal.jdbc.GreenplumDriver"),
	
	Redshift("jdbc:redshift://%s/%s", "com.amazon.redshift.jdbc42.Driver");

	private String url;
	private String driver;

	ConnectConstant(String url, String driver) {
		this.url = url;
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public String getDriver() {
		return driver;
	}
}
