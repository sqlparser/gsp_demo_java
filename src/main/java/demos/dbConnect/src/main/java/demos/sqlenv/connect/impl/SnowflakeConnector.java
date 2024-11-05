package demos.sqlenv.connect.impl;

import demos.sqlenv.TSQLDataSource;
import demos.sqlenv.TSnowflakeSQLDataSource;
import demos.sqlenv.connect.Connector;
import demos.sqlenv.constant.ConnectConstant;
import gudusoft.gsqlparser.sqlenv.util.ConnectUtil;
import gudusoft.gsqlparser.util.SQLUtil;

import java.sql.Connection;

/**
 * CemB
 */
public class SnowflakeConnector implements Connector {
	@Override
	public Connection toConnect(TSQLDataSource datasource) throws Exception {

		String address = SQLUtil.isEmpty(datasource.getPort()) ? removeSlash(datasource.getHostName())
				: removeSlash(datasource.getHostName()) + ":" + datasource.getPort();

		TSnowflakeSQLDataSource snowflakeDataSource = (TSnowflakeSQLDataSource) datasource;

		return ConnectUtil.getConnection(datasource.getAccount(), datasource.getPassword(),
				ConnectConstant.Snowflake.getDriver(),
				String.format(ConnectConstant.Snowflake.getUrl(), address, datasource.getDatabase(),
						snowflakeDataSource.getDefaultRole(), snowflakeDataSource.getPrivateKeyFile(),
						snowflakeDataSource.getPrivateKeyFilePwd()).replace("db=null&", "").replace("role=null&", "")
						.replace("private_key_file=null&", "").replace("private_key_file_pwd=null&", ""),
				datasource.getTimeout());
	}

	private String removeSlash(String hostName) {
		if (hostName.endsWith("/")) {
			hostName = hostName.substring(0, hostName.length() - 1);
		}
		return hostName;
	}

}
