package demos.sqlenv.connect.impl;

import demos.sqlenv.TSQLDataSource;
import demos.sqlenv.connect.Connector;
import demos.sqlenv.constant.ConnectConstant;
import gudusoft.gsqlparser.sqlenv.util.ConnectUtil;
import gudusoft.gsqlparser.util.SQLUtil;

import java.sql.Connection;

/**
 * CemB
 */
public class RedshiftConnector implements Connector {
	
	@Override
	public Connection toConnect(TSQLDataSource datasource)
			throws Exception {
		
		String address = SQLUtil.isEmpty(datasource.getPort()) ? datasource.getHostName()
				: datasource.getHostName() + ":" + datasource.getPort();
		
		try {
			return ConnectUtil.getConnection(datasource.getAccount(), datasource.getPassword(), ConnectConstant.PostgreSQL.getDriver(), String
					.format(ConnectConstant.PostgreSQL.getUrl(), address, datasource.getDatabase()).replace("/null", ""),
					 datasource.getTimeout());
		} catch (Exception e) {
			return ConnectUtil.getConnection(datasource.getAccount(), datasource.getPassword(), ConnectConstant.Redshift.getDriver(), String
					.format(ConnectConstant.Redshift.getUrl(), address, datasource.getDatabase()).replace("/null", ""),
					 datasource.getTimeout());
		}
	}

}
