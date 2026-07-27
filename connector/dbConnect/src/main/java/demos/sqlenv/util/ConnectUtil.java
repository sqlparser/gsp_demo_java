package demos.sqlenv.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class ConnectUtil {

    @SuppressWarnings({ "unchecked", "rawtypes" })
	public static Connection getConnection(final String name, final String password, final String driver, final String url, final long timeout) throws Exception {
    	FutureTask future = new FutureTask(new Callable<Connection>() {
			@Override
			public Connection call() throws Exception {
				  Class.forName(driver);
		          return DriverManager.getConnection(url, name, password);
			}
		});
    	new Thread(future).start();
        return (Connection)future.get(timeout, TimeUnit.MILLISECONDS);
    }
    
}
