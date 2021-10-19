package demos.sqlenv.connect;

import demos.sqlenv.TSQLDataSource;

import java.sql.Connection;

public interface Connector {

	Connection toConnect(TSQLDataSource datasource) throws Exception;

}
