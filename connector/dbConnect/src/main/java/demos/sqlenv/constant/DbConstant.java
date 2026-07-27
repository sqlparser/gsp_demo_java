package demos.sqlenv.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * CemB
 */
public interface DbConstant {

	String MySQL = "mysql";
	String MySQLV5 = "mySQLV5";
	String MySQLV8 = "mySQLV8";
	String Oracle = "oracle";
	String DB2 = "db2";
	String PostgreSQL = "postgresql";
	String SQLServer = "mssql";
	String Snowflake = "snowflake";
	String Couchbase = "couchbase";
	String Dax = "dax";
	String Greenplum = "greenplum";
	String Hana = "hana";
	String Hive = "hive";
	String Impala = "impala";
	String Informix = "informix";
	String Mdx = "mdx";
	String Netezza = "netezza";
	String Openedge = "openedge";
	String Redshift = "redshift";
	String Sybase = "sybase";
	String Teradata = "teradata";
	String Vertica = "vertica";
	String AzureSQL = "azuresql";

	List<String> dbs = new ArrayList<String>() {
		private static final long serialVersionUID = 2584465401841956521L;
		{
			add(Couchbase);
			add(Dax);
			add(DB2);
			add(Greenplum);
			add(Hana);
			add(Hive);
			add(Impala);
			add(Informix);
			add(Mdx);
			add(MySQL);
			add(Netezza);
			add(Openedge);
			add(Oracle);
			add(PostgreSQL);
			add(Redshift);
			add(Snowflake);
			add(SQLServer);
			add(Sybase);
			add(Teradata);
			add(Vertica);
			add(AzureSQL);
		}
	};

}
