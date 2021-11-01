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
public class TeradataConnector implements Connector {
	@Override
	public Connection toConnect(TSQLDataSource datasource) throws Exception {
		String address = SQLUtil.isEmpty(datasource.getPort()) ? datasource.getHostName()
				: datasource.getHostName() + ":" + datasource.getPort();
		String[] segment = address.split(":");
		StringBuilder buffer = new StringBuilder();
		buffer.append("jdbc:teradata://" + segment[0].trim() + "/CHARSET=UTF8,");
		if (segment.length == 2 && segment[1].trim().length() > 0) {
			buffer.append("DBS_PORT=" + segment[1].trim() + ",");
		}
		buffer.append("DATABASE=" + datasource.getDatabase());
		return ConnectUtil.getConnection(datasource.getAccount(), datasource.getPassword(), ConnectConstant.Teradata.getDriver(), buffer.toString(),
				 datasource.getTimeout());
	}

}
