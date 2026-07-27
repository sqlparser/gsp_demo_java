package demos.sqlenv;

import gudusoft.gsqlparser.EDbVendor;
import demos.sqlenv.connect.ConnectionFactory;
import demos.sqlenv.constant.DbOperation;
import demos.sqlenv.constant.DbTypeConstant;
import demos.sqlenv.metadata.DDL;
import demos.sqlenv.operation.DbOperationFactory;
import demos.sqlenv.operation.DbOperationService;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import demos.sqlenv.util.JdbcUrlParser;
import gudusoft.gsqlparser.sqlenv.parser.TJSONSQLEnvParser;
import gudusoft.gsqlparser.util.SQLUtil;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TSQLDataSource {

    public static final Logger logger = Logger.getLogger(TSQLDataSource.class.getName());

    private final String account;
    private final String password;
    private final String port;
    private final String hostName;
    protected String dbCategory;
    private final EDbVendor vendor;
    private String database;
    private long timeout = 30000;

    protected Connection connection;
    protected String dbType;

    private String[] extractedStoredProcedures;
    private String[] extractedViews;

    {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        logger.log(Level.SEVERE, "Close database connection failed.", e);
                    }
                }
            }
        });
    }

    public TSQLDataSource(EDbVendor vendor, String hostName, String port, String account, String password) {
        this(vendor, hostName, port, account, password, null);
    }

    public TSQLDataSource(EDbVendor vendor, String hostName, String port, String account, String password, String database) {
        this.vendor = vendor;
        this.account = account;
        this.password = password;
        this.port = port;
        this.database = database;
        this.hostName = hostName;
        this.dbCategory = DbTypeConstant.vendorTypes.get(vendor);
        this.dbType = this.dbCategory;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getPort() {
        return port;
    }

    public String getDatabase() {
        return database;
    }

    public String getHostName() {
        return hostName;
    }

    public String getDbCategory() {
        return dbCategory;
    }

    public EDbVendor getVendor() {
        return vendor;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public Connection getConnection() {
        try {
            if (connection != null && (connection.isClosed() || !connection.isValid(0))) {
                connection = null;
            }
            if (connection == null) {
                toConnect();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Connection is closed.", e);
        }
        return connection;
    }

    public boolean testConnection() {
        if (getConnection() != null) {
            return true;
        }

        return false;
    }

    public DDL exportDDL() {
        DbOperationService<DDL> exporter = DbOperationFactory.getDbOperationService(dbCategory, DbOperation.TODDL);
        return exporter.operate(this);
    }

    public String exportJSON() {
        DbOperationService<String> exporter = DbOperationFactory.getDbOperationService(dbCategory, DbOperation.TOJSON);
        return exporter.operate(this);
    }

    public TSQLEnv exportSQLEnv() {
        String json = exportJSON();
        if (!SQLUtil.isEmpty(json)) {
            TSQLEnv[] envs =  new TJSONSQLEnvParser(null,null,null).parseSQLEnv(vendor, json);
            if(envs!=null && envs.length>0) {
                return envs[0];
            }
        }
        return null;
    }

    protected void toConnect() {
        try {
            if (connection == null) {
                this.connection = ConnectionFactory.getConnection(this);
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Connect data source failed.", ex);
        }
    }

    public String getDbType() {
        return dbType;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String[] getExtractedStoredProcedures() {
        return extractedStoredProcedures == null ? new String[0] : extractedStoredProcedures;
    }

    public void setExtractedStoredProcedures(String... extractedStoredProcedures) {
        List<String> storedProcedures = new ArrayList<String>();
        if (extractedStoredProcedures != null && extractedStoredProcedures.length > 0) {
            for (String storedProcedure : extractedStoredProcedures) {
                storedProcedure = storedProcedure.replace("/", ".").trim();
                if (storedProcedure.length() > 0) {
                    storedProcedures.add(storedProcedure);
                }
            }
            if (storedProcedures.size() > 0) {
                this.extractedStoredProcedures = storedProcedures.toArray(new String[0]);
            }
        }
    }

    public String[] getExtractedViews() {
        return extractedViews == null ? new String[0] : extractedViews;
    }

    public void setExtractedViews(String... extractedViews) {
        List<String> views = new ArrayList<String>();
        if (extractedViews != null && extractedViews.length > 0) {
            for (String view : extractedViews) {
                view = view.replace("/", ".").trim();
                if (view.length() > 0) {
                    views.add(view);
                }
            }
            if (views.size() > 0) {
                this.extractedViews = views.toArray(new String[0]);
            }
        }
    }

    public static TSQLDataSource createSQLDataSource(String jdbcUrl, String account, String password) {
        if (jdbcUrl == null) {
            return null;
        }
        return JdbcUrlParser.generateSQLDataSource(jdbcUrl, account, password);
    }

    @SuppressWarnings("unchecked")
    public static <T extends TSQLDataSource> T createSQLDataSource(EDbVendor vendor, String jdbcUrl, String account,
                                                                                              String password) {
        if (jdbcUrl == null) {
            return null;
        }
        return (T) JdbcUrlParser.generateSQLDataSource(vendor, jdbcUrl, account, password);
    }

    public static void main(String[] args) {
        TMssqlSQLDataSource datasource = TSQLDataSource.createSQLDataSource(EDbVendor.dbvmssql,
                "jdbc:sqlserver://aaa:123;DatabaseName=dbf", "account", "password");
        System.out.println(datasource);

        gudusoft.gsqlparser.sqlenv.TSQLDataSource datasource1 = gudusoft.gsqlparser.sqlenv.TSQLDataSource.createSQLDataSource("jdbc:sqlserver://aaa:123;DatabaseName=dbf",
                "account", "password");
        System.out.println(datasource1);
    }
}
