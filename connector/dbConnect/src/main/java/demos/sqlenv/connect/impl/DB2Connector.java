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
public class DB2Connector implements Connector {
	@Override
	public Connection toConnect(TSQLDataSource datasource) throws Exception {
		
		String address = SQLUtil.isEmpty(datasource.getPort()) ? datasource.getHostName()
				: datasource.getHostName() + ":" + datasource.getPort();
		
		return ConnectUtil.getConnection(datasource.getAccount(), datasource.getPassword(),
				ConnectConstant.DB2.getDriver(),
				String.format(ConnectConstant.DB2.getUrl(), address, datasource.getDatabase()).replace("/null", ""),
				datasource.getTimeout());
	}

}
